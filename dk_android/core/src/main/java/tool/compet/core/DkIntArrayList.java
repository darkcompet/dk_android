/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

/**
 * This class is performance-better version of ArrayList for Integer type.
 */
public class DkIntArrayList {
	// Max size that array can be reach to
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	// Current snapshot of element data (changed if perform insert/delete...)
	private int[] arr;

	// Current element count (changed if perform insert/delete...)
	private int size;

	public DkIntArrayList() {
		this(10);
	}

	public DkIntArrayList(int capacity) {
		if (capacity <= 0) {
			capacity = 10;
		}
		this.arr = new int[capacity];
	}

	public int size() {
		return size;
	}

	/**
	 * @return Left-most element which equals to given `value`.
	 */
	public int indexOf(int element) {
		int[] arr = this.arr;
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
	public int lastIndexOf(int element) {
		int[] arr = this.arr;
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
	public void add(int element) {
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
	public void add(int index, int element) {
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
	public boolean addIfAbsence(int element) {
		if (indexOf(element) < 0) {
			add(element);
			return true;
		}
		return false;
	}

	public void addAll(int[] elements) {
		addAll(size, elements);
	}

	/**
	 * Add (insert at) new `elements` from given `index` of array list.
	 * Note that, caller must pass valid `index` in range [0, size].
	 */
	public void addAll(int index, int[] elements) {
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

	public void fastRemoveElement(int element) {
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
	public void removeElement(int element) {
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
	public int get(int index) {
		return arr[index];
	}

	/**
	 * Set value to element at given `index`.
	 * Note that, caller must pass valid `index` in range [0, size - 1].
	 */
	public void set(int index, int element) {
		arr[index] = element;
	}

	/**
	 * Check existence of element which equals to given `value`.
	 */
	public boolean contains(int element) {
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
	public int[] getCurrentArray() {
		return arr;
	}

	public int[] toArray() {
		int[] result = new int[size];
		System.arraycopy(arr, 0, result, 0, size);
		return result;
	}

	// Grow array to size at least given `minCapacity`, normally it is 1.5 times of current-capacity.
	private void growCapacity(int minCapacity) {
		// Calculate new capacity
		int oldCapacity = arr.length;
		int newCapacity = oldCapacity + (oldCapacity >> 1);

		// Check over-flow (must subtract, NOT compare)
		if (newCapacity - minCapacity < 0) {
			newCapacity = minCapacity;
		}
		if (newCapacity - MAX_ARRAY_SIZE > 0) {
			newCapacity = MAX_ARRAY_SIZE;
		}

		// Make new array and then copy from old array.
		int[] newArr = new int[newCapacity];
		System.arraycopy(arr, 0, newArr, 0, size);

		this.arr = newArr;
	}
}
