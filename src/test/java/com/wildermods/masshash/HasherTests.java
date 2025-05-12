package com.wildermods.masshash;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import com.wildermods.masshash.exception.IntegrityException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HasherTests {

	static final String VERSION = "thrixlVaultTest";
	static Path sourceDir = Paths.get("./src", "test", "resources");
	static Stream<Path> sources;
	
	static Hasher hasher;
	
	@BeforeEach
	void setup() throws IOException {
		sources = Files.walk(sourceDir);
	}
	
	@Test
	@Order(1)
	void HasherTest() throws IOException {
		System.out.println("Test is running...");
		hasher = new Hasher(sources) {};
	}
	
	@Test
	@Order(2)
	void verifyTest() throws IntegrityException, IOException {
		System.out.println("Verification test:");
		hasher = new Hasher(sources, (f,b) -> {
			try {
				b.verify();
			} catch (IntegrityException e) {
				throw new RuntimeException(e);
			}
		}) {};
	}
	
}
