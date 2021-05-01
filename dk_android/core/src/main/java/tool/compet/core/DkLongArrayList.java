/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

/**
 * This class is performance-better version of ArrayList for Integer type.
 */
public class DkLongArrayList {
	// Current snapshot of element data (changed if perform insert/delete...)
	private long[] arr;

	// Current element count (changed if perform insert/delete...)
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
	 * @return Left-most element which equals to given `value`.
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
	 * @return Right-most element which equals to given `value`.
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
	 * Note that, caller must pass valid `index` in range [0, size].
	 */
	public void add(int index, long element) {
		int newSize = size + 1;
		if (newSize >= arr.length) {
			growCapacity(newSize);
		}
		if (index < size) { // only copy if it is insertion (not append to last)
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
		addAll(size, elements);
	}

	/**
	 * Add (insert at) new `elements` from given `index` of array list.
	 * Note that, caller must pass valid `index` in range [0, size].
	 */
	public void addAll(int index, long[] elements) {
		final int oldSize = size;
		final int more = elements.length;
		final int newSize = oldSize + more;

		if (newSize >= arr.length) {
			growCapacity(newSize);
		}

		// Move elements in [index, oldSize -1] to `more` steps
		if (oldSize > index) {
			System.arraycopy(arr, index, arr, index + more, oldSize - index);
		}

		// Insert `more` elements to [index, index + more - 1]
		System.arraycopy(elements, 0, arr, index, more);

		size = newSize;
	}

	public void fastRemoveElement(long element) {
		fastRemove(indexOf(element));
	}

	/**
	 * This copy last element into element at given `index`.
	 * Use it if you do NOT care order of elements after remove.
	 * Note that, caller must pass valid `index` in range [0, size - 1].
	 */
	public void fastRemove(int index) {
		int lastIndex = size - 1;
		if (index < lastIndex) {
			arr[index] = arr[lastIndex];
		}
		--size;
	}

	/**
	 * Remove left-most element which equals to given `value`.
	 */
	public void removeElement(long element) {
		remove(indexOf(element));
	}

	/**
	 * Remove element at given `index`.
	 * Note that, caller must pass valid `index` in range [0, size - 1].
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
	 * Note that, caller must pass valid `index` in range [0, size - 1].
	 */
	public long get(int index) {
		return arr[index];
	}

	/**
	 * Set value to element at given `index`.
	 * Note that, caller must pass valid `index` in range [0, size - 1].
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

	public void ensureCapacity(int minCapacity) {
		if (arr.length <= minCapacity) {
			growCapacity(minCapacity);
		}
	}

	/**
	 * Get current snapshot of internal array. Since capacity of internal array maybe bigger than actual size,
	 * so caller should also use `size()` of current array to handle with number of elements.
	 */
	public long[] getCurrentArray() {
		return arr;
	}

	public long[] toArray() {
		long[] result = new long[size];
		System.arraycopy(arr, 0, result, 0, size);
		return result;
	}

	public Iterable<?> toIterable() {
		return DkArrays.asList(toArray());
	}

	private void growCapacity(int minCapacity) {
		int newCapacity = MyArrayHelper.calcNextCapacity(arr.length, minCapacity, Integer.MAX_VALUE - 8);

		// Make new array and then copy from old array.
		long[] newArr = new long[newCapacity];
		System.arraycopy(arr, 0, newArr, 0, size);

		this.arr = newArr;
	}
}
