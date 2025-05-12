package com.wildermods.masshash.utils;

import java.util.Objects;

public class Reference<T> {

	private T obj;
	
	public Reference(T obj) {
		set(obj);
	}
	
	public T get() {
		return obj;
	}
	
	public void set(T obj) {
		Objects.requireNonNull(obj);
		this.obj = obj;
	}
	
}
