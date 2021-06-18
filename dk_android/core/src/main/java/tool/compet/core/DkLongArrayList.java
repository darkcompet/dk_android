/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

/**
 * This class is performance-better version of ArrayList for long type.
 */
public class DkLongArrayList {
	// Current snapshot of elements (changed if perform insert/delete...)
	private long[] arr;

	// Current elements count (changed if perform insert/delete...)
	private int size;

	public DkLongArrayList() {
		this(10);
	}

	public DkLongArrayList(int capacity) {
		if (capacity <= 0) {
			capacity = 10;
		}
		this.arr = new long[capacity];
	}

	public int size() {
		return size;
	}

	/**
	 * @return Left-most element which equals to given `element`.
	 */
	public int indexOf(long element) {
		long[] arr = this.arr;
		for (int index = 0, N = size; index < N; ++index) {
			if (arr[index] == element) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * @return Right-most element which equals to given `element`.
	 */
	public int lastIndexOf(long element) {
		long[] arr = this.arr;
		for (int index = size - 1; index >= 0; --index) {
			if (arr[index] == element) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Add new `element` to array list.
	 */
	public void add(long element) {
		int newSize = size + 1;
		if (newSize >= arr.length) {
			growCapacity(newSize);
		}
		arr[size++] = element;
	}

	/**
	 * Add (insert at) new `element` at given `index` of array list.
	 *
	 * @param index Must in range [0, size].
	 */
	public void add(int index, long element) {
		int newSize = size + 1;
		if (newSize >= arr.length) {
			growCapacity(newSize);
		}
		// Shift right elements at `index`
		if (index < size) {
			System.arraycopy(arr, index, arr, index + 1, size - index);
		}
		arr[index] = element;
		++size;
	}

	/**
	 * Add new element if not exists.
	 */
	public boolean addIfAbsence(long element) {
		if (indexOf(element) < 0) {
			add(element);
			return true;
		}
		return false;
	}

	public void addAll(long[] elements) {
		addRange(size, elements, 0, elements.length);
	}

	public void addAll(int index, long[] elements) {
		addRange(index, elements, 0, elements.length);
	}

	public void addRange(long[] elements, int startIndex, int endIndex) {
		addRange(size, elements, startIndex, endIndex);
	}

	/**
	 * Add a range of `elements` at given `index`.
	 * Caller must pass valid `index` in range [0, size].
	 *
	 * @param index Insert position. Must in range [0, size).
	 * @param elements Data to copy.
	 * @param startIndex Start-index of copy-range inclusive.
	 * @param endIndex End-index of copy-range exclusive.
	 */
	public void addRange(int index, long[] elements, int startIndex, int endIndex) {
		final int oldSize = size;
		final int addMore = endIndex - startIndex;
		final int newSize = oldSize + addMore;

		if (newSize >= arr.length) {
			growCapacity(newSize);
		}

		// Move elements in [index, oldSize) to `addMore` steps
		if (oldSize > index) {
			System.arraycopy(arr, index, arr, index + addMore, oldSize - index);
		}

		// Insert `addMore` elements to [index, index + addMore)
		System.arraycopy(elements, startIndex, arr, index, addMore);

		size = newSize;
	}

	public void fastRemoveElement(long element) {
		fastRemove(indexOf(element));
	}

	/**
	 * This copy last element into element at given `index`.
	 * Use it if you do NOT care about order of elements after removed.
	 *
	 * @param index Must in range [0, size).
	 */
	public void fastRemove(int index) {
		int lastIndex = size - 1;
		if (index < lastIndex) {
			arr[index] = arr[lastIndex];
		}
		--size;
	}

	/**
	 * Remove left-most element which equals to given `element`.
	 */
	public void removeElement(long element) {
		remove(indexOf(element));
	}

	/**
	 * Remove element at given `index`.
	 *
	 * @param index Must in range [0, size).
	 */
	public void remove(int index) {
		System.arraycopy(arr, index + 1, arr, index, size - 1 - index);
		--size;
	}

	/**
	 * Clear elements by set size to 0.
	 */
	public void clear() {
		size = 0;
	}

	/**
	 * Get value of element at given `index`.
	 *
	 * @param index Must in range [0, size).
	 */
	public long get(int index) {
		return arr[index];
	}

	/**
	 * Set value to element at given `index`.
	 *
	 * @param index Must in range [0, size).
	 */
	public void set(int index, long element) {
		arr[index] = element;
	}

	/**
	 * Check existence of element which equals to given `value`.
	 */
	public boolean contains(long element) {
		return indexOf(element) >= 0;
	}

	/**
	 * Grows up if current internal array length is smaller than given `minCapacity`.
	 */
	public void ensureCapacity(int minCapacity) {
		if (arr.length <= minCapacity) {
			growCapacity(minCapacity);
		}
	}

	/**
	 * Get current snapshot of internal array. Because capacity of internal array maybe bigger than actual size of it,
	 * so caller take care of checking iteration-index with `size()` of result-array when take an action.
	 */
	public long[] getCurrentArray() {
		return arr;
	}

	/**
	 * @return Clone new array from internal array in range [0, size).
	 */
	public long[] toArray() {
		long[] result = new long[size];
		System.arraycopy(arr, 0, result, 0, size);
		return result;
	}

	/**
	 * This takes high cost for boxing-unboxing primitive value with object, so should not use as possible.
	 *
	 * @return Iterable over array in range [0, size).
	 */
	public Iterable<?> toIterable() {
		return DkArrays.asList(toArray());
	}

	// Make internal array bigger, and assure its capacity greater than given `minCapacity`.
	private void growCapacity(int minCapacity) {
		int newCapacity = MyArrayHelper.calcNextCapacity(arr.length, minCapacity, Integer.MAX_VALUE - 8);

		// Make new array and then copy from old array.
		long[] newArr = new long[newCapacity];
		System.arraycopy(arr, 0, newArr, 0, size);

		arr = newArr;
	}
}
