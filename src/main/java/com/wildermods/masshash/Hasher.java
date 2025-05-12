package com.wildermods.masshash;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;
import com.wildermods.masshash.utils.Reference;

/**
 * Abstract base class for hashing a collection of files in parallel.
 * 
 * <p>
 * Files are processed into {@link Blob} objects, hashed, and grouped by their
 * content hashes. Results are stored in a sorted, thread-safe {@link SetMultimap}.
 * </p>
 * 
 * <p>
 * {@code Hasher} is designed for large file sets (e.g., 40,000+ files) and
 * automatically parallelizes hashing using all available CPU cores.
 * </p>
 * 
 * <p>
 * Subclasses can access the resulting hash-to-path mappings via {@link #results()}.
 * </p>
 */
public abstract class Hasher {

	/**
	 * A multimap that stores computed hashes and their associated file paths.
	 * <p>
	 * If this {@code Hasher} instance was created using any non-default constructor,
	 * this multimap is guaranteed to be thread-safe.
	 * </p>
	 * <p>
	 * Subclasses that deserialize or otherwise initialize {@code blobs} must ensure
	 * it remains thread-safe. Failing to do so is a violation of the intended contract,
	 * although this cannot be strictly enforced at runtime.
	 * </p>
	 */
	protected SetMultimap<Hash, Path> blobs;
	protected Logger logger = LogManager.getLogger();
	
	/**
	 * Protected no-argument constructor for subclass serialization.
	 */
	protected Hasher() {
		//NO-OP
	}
	
	/**
	 * Constructs a {@code Hasher} that processes all regular files from the given stream.
	 * 
	 * @param files a stream of {@link Path} objects to hash
	 * @throws IOException if an I/O error occurs during hashing
	 */
	public Hasher(final Stream<Path> files) throws IOException {
		this(files, (p, b) -> {});
	}
	
	/**
	 * Constructs a {@code Hasher} that processes all regular files from the given stream,
	 * invoking the specified consumer for each {@link Blob} as it is created.
	 * 
	 * @param files a stream of {@link Path} objects to hash
	 * @param forEachBlob a {@link Consumer} invoked with each {@link Blob} before its data is dropped
	 * @param forEachBlob a consumer invoked with each {@link Blob} and a {@link Reference}&lt;Path&gt; 
	 *        that wraps the original file path. This allows the path to be modified (e.g., to relativize or normalize it)
	 *        before being added to the result map. The updated reference value will be associated with the computed hash.
	 * @throws IOException if an I/O error occurs during hashing
	 */
	public Hasher(final Stream<Path> files, final BiConsumer<Reference<Path>, Blob> forEachBlob) throws IOException {
		this(files, (p) -> true, forEachBlob);
	}
	
	/**
	 * Constructs a {@code Hasher} that processes all files matching the given predicate
	 * from the provided stream.
	 * 
	 * See the constructor below for more details.
	 *
	 * @param files a stream of {@link Path} objects to hash
	 * @param predicate a {@link Predicate} to filter which files should be hashed
	 * @param forEachBlob a {@link Consumer} invoked with each {@link Blob} before its data is dropped
	 * @param forEachBlob a consumer invoked with each {@link Blob} and a {@link Reference}&lt;Path&gt; 
	 *        that wraps the original file path. This allows the path to be modified (e.g., to relativize or normalize it)
	 *        before being added to the result map. The updated reference value will be associated with the computed hash.
	 * 
	 * @throws IOException if an I/O error occurs during hashing
	 * @throws IllegalArgumentException if no files match the predicate
	 */
	public Hasher(final Stream<Path> files, final Predicate<Path> predicate, final BiConsumer<Reference<Path>, Blob> forEachBlob) throws IOException {
		this(files, Runtime.getRuntime().availableProcessors(), predicate, forEachBlob);
	}
	
	/**
	 * Constructs a {@code Hasher} that processes all files matching the given predicate
	 * from the provided stream, using a specified number of threads.
	 * 
	 * <p>
	 * Each matching file is converted into a {@link Blob}, passed to the provided {@link BiConsumer},
	 * and then discarded (i.e., its data is dropped) to conserve memory.
	 * </p>
	 * 
	 * <p>
	 * Hashing is performed in parallel using a thread pool sized according to {@code threads}, unless
	 * the specified thread count is less than 1 or exceeds available processors, in which case it is adjusted
	 * to the nearest valid value.
	 * </p>
	 * 
	 * <p>
	 * The final result is a sorted, thread-safe {@link SetMultimap} mapping content hashes to their
	 * corresponding file paths. Results can be accessed via {@link #results()}.
	 * </p>
	 * 
	 * @param files a stream of file paths to be hashed
	 * @param threads the number of threads to use for parallel hashing (auto-adjusted if invalid)
	 * @param predicate a predicate to filter files before processing (e.g., by extension or size)
	 * @param forEachBlob a consumer invoked with each {@link Blob} and a {@link Reference}&lt;Path&gt; 
	 *        that wraps the original file path. This allows the path to be modified (e.g., to relativize or normalize it)
	 *        before being added to the result map. The updated reference value will be associated with the computed hash.
	 * 
	 * @throws IOException if an error occurs while reading files or during thread execution
	 * @throws IllegalArgumentException if no files matched the provided predicate
	 */
	public Hasher(final Stream<Path> files, int threads, final Predicate<Path> predicate, final BiConsumer<Reference<Path>,Blob> forEachBlob) throws IOException {
		final int processors = Runtime.getRuntime().availableProcessors();
		Objects.requireNonNull(files);
		Objects.requireNonNull(predicate);
		Objects.requireNonNull(forEachBlob);
		if(threads > processors) {
			logger.warn("[MassHash/WARN]: Requested thread count (" + threads + ") greather than the amount of availalbe processors (" + processors + "). Using " + processors + " threads instead.");
			threads = processors;
		}
		if(threads < 1) {
			logger.warn("[MassHash/WARN]: Thread count less than 1. Using 1 thread instead.");
			threads = 1;
		}

		
		final List<Path> allFiles = files.parallel()
			.filter(predicate.and(p -> Files.isRegularFile((Path) p)))
			.collect(Collectors.toList());
		
		//Fail fast if there's nothing to process - no point spinning up threads
		if (allFiles.isEmpty()) {
			throw logger.throwing(new IllegalArgumentException("No Files."));
		}

		//Use one thread per available processor core for efficient CPU usage
		//This allows parallel hashing of files and drastically speeds up processing on large sets (We're expecting ~40k files)
		int numThreads = Runtime.getRuntime().availableProcessors();

		//Chunk the files evenly among threads, leaving some flexibility for the final chunk
		//This keeps each thread busy with roughly equal work
		int chunkSize = allFiles.size() / numThreads + 1;

		ExecutorService pool = Executors.newFixedThreadPool(numThreads);
		List<Future<Map<Hash, Set<Path>>>> futures = new ArrayList<>();

		/*
		 * PERFORMANCE NOTE:
		 * 
		 * Inserting directly into a TreeMultimap offers automatic sorting,
		 * but the performance cost is too high.
		 *
		 * A faster approach is for each thread to first collect results in its own
		 * local, unsorted HashMap.
		 *
		 * Only after all threads finish do we merge the results into a single TreeMultimap.
		 * This final TreeMultimap handles all the sorting in one pass, avoiding synchronization.
		 *
		 * This approach is nearly **7x faster** than inserting directly into the 
		 * TreeMultimap during hashing.
		 *
		 * Entries are sorted by
		 * - hash value
		 * - then by file path
		 * 
		 * Entries are sorted to ensure a consistent and debuggable output.
		 */
		
		//Submit a hashing task for each chunk of files
		for (int i = 0; i < allFiles.size(); i += chunkSize) {
			List<Path> sublist = allFiles.subList(i, Math.min(i + chunkSize, allFiles.size()));

			futures.add(pool.submit(() -> {
				//Each thread uses a local map to avoid synchronization
				Map<Hash, Set<Path>> local = new HashMap<>();
				for (Path file : sublist) {
					Reference<Path> newFile = new Reference<>(file);
					//Read and hash the file into a Blob, then discard the Blob’s data to conserve memory
					Hash blob = new Blob(file);
					forEachBlob.accept(newFile, (Blob) blob);
					((Blob) blob).dropData();

					//Group files by their content hash. Files with the same hash will share the same key
					local.computeIfAbsent(blob, k -> new HashSet<>()).add(newFile.get());
				}
				return local;
			}));
		}

		/*
		 * Use a TreeMultimap to keep results sorted.
		 * Keys are sorted by Hash, and the paths under each key are sorted by their natural ordering.
		 */
		TreeMultimap<Hash, Path> sorted = TreeMultimap.create(
			Comparator.comparing(Hash::hash),
			Ordering.natural()
		);

		//Merge the results from all threads into the multimap, this will sort the hashes
		//as descrived above.
		for (Future<Map<Hash, Set<Path>>> future : futures) {
			try {
				Map<Hash, Set<Path>> partial = future.get();

				//Add all file-path sets for each hash to the final multimap
				for (Map.Entry<Hash, Set<Path>> entry : partial.entrySet()) {
					sorted.putAll(entry.getKey(), entry.getValue());
				}
			} catch (Throwable t) {
				//Shut down early if anything goes wrong in a thread
				pool.shutdownNow();
				throw logger.throwing(new IOException("Thread pool failed", t));
			}
		}

		pool.shutdown();

		//Wrap the result in a synchronized structure for thread safe access later
		blobs = Multimaps.synchronizedSetMultimap(sorted);

		logger.info("Blob calculation complete");
	}
	
	/**
	 * Returns the resulting multimap of content hashes to file paths.
	 * 
	 * @return a thread-safe {@link SetMultimap} mapping each {@link Hash} to one or more {@link Path}s
	 */
	public SetMultimap<Hash, Path> results() {
		return blobs;
	}
	
}
