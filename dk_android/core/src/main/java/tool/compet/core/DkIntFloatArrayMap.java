/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import androidx.annotation.NonNull;

import java.util.Arrays;

/**
 * This mapping int vs float.
 * We make this since Android has not SparseFloatArray.
 */
public class DkIntFloatArrayMap implements Cloneable {
	private int mSize;
	private int[] mKeys;
	private float[] mValues;

	public DkIntFloatArrayMap() {
		this(10);
	}

	/**
	 * Creates a new SparseIntArray containing no mappings that will not
	 * require any additional memory allocation to store the specified
	 * number of mappings. If you supply an initial capacity of 0, the
	 * sparse array will be initialized with a light-weight representation
	 * not requiring any additional array allocations.
	 */
	public DkIntFloatArrayMap(int initialCapacity) {
		if (initialCapacity <= 0) {
			mKeys = DkEmptyArray.INT;
			mValues = DkEmptyArray.FLOAT;
		}
		else {
			mKeys = new int[initialCapacity];
			mValues = new float[mKeys.length];
		}
		mSize = 0;
	}

	@NonNull
	@Override
	public DkIntFloatArrayMap clone() {
		DkIntFloatArrayMap clone = null;

		try {
			clone = (DkIntFloatArrayMap) super.clone();
			clone.mKeys = mKeys.clone();
			clone.mValues = mValues.clone();
		}
		catch (CloneNotSupportedException cnse) {
			// ignore
		}
		return clone;
	}

	/**
	 * Gets the int mapped from the specified key, or <code>0</code>
	 * if no such mapping has been made.
	 */
	public double get(int key) {
		return get(key, 0);
	}

	/**
	 * Gets the int mapped from the specified key, or the specified value
	 * if no such mapping has been made.
	 */
	public double get(int key, int valueIfKeyNotFound) {
		int i = MySearchHelper.binarySearchWithoutRangeCheck(mKeys, mSize, key);

		if (i < 0) {
			return valueIfKeyNotFound;
		}
		else {
			return mValues[i];
		}
	}

	/**
	 * Removes the mapping from the specified key, if there was any.
	 */
	public void delete(int key) {
		int i = MySearchHelper.binarySearchWithoutRangeCheck(mKeys, mSize, key);

		if (i >= 0) {
			removeAt(i);
		}
	}

	/**
	 * Removes the mapping at the given index.
	 */
	public void removeAt(int index) {
		System.arraycopy(mKeys, index + 1, mKeys, index, mSize - (index + 1));
		System.arraycopy(mValues, index + 1, mValues, index, mSize - (index + 1));
		mSize--;
	}

	/**
	 * Adds a mapping from the specified key to the specified value,
	 * replacing the previous mapping from the specified key if there
	 * was one.
	 */
	public void put(int key, float value) {
		int i = MySearchHelper.binarySearchWithoutRangeCheck(mKeys, mSize, key);

		if (i >= 0) {
			mValues[i] = value;
		}
		else {
			i = ~i;

			mKeys = MyArrayHelper.insert(mKeys, mSize, i, key);
			mValues = MyArrayHelper.insert(mValues, mSize, i, value);
			mSize++;
		}
	}

	/**
	 * Returns the number of key-value mappings that this SparseIntArray
	 * currently stores.
	 */
	public int size() {
		return mSize;
	}

	/**
	 * Given an index in the range <code>0...size()-1</code>, returns
	 * the key from the <code>index</code>th key-value mapping that this
	 * SparseIntArray stores.
	 *
	 * <p>The keys corresponding to indices in ascending order are guaranteed to
	 * be in ascending order, e.g., <code>keyAt(0)</code> will return the
	 * smallest key and <code>keyAt(size()-1)</code> will return the largest
	 * key.</p>
	 */
	public int keyAt(int index) {
		return mKeys[index];
	}

	/**
	 * Given an index in the range <code>0...size()-1</code>, returns
	 * the value from the <code>index</code>th key-value mapping that this
	 * SparseIntArray stores.
	 *
	 * <p>The values corresponding to indices in ascending order are guaranteed
	 * to be associated with keys in ascending order, e.g.,
	 * <code>valueAt(0)</code> will return the value associated with the
	 * smallest key and <code>valueAt(size()-1)</code> will return the value
	 * associated with the largest key.</p>
	 */
	public double valueAt(int index) {
		return mValues[index];
	}

	/**
	 * Directly set the value at a particular index.
	 */
	public void setValueAt(int index, int value) {
		mValues[index] = value;
	}

	/**
	 * Returns the index for which {@link #keyAt} would return the
	 * specified key, or a negative number if the specified
	 * key is not mapped.
	 */
	public int indexOfKey(int key) {
		return MySearchHelper.binarySearchWithoutRangeCheck(mKeys, mSize, key);
	}

	/**
	 * Returns an index for which {@link #valueAt} would return the
	 * specified key, or a negative number if no keys map to the
	 * specified value.
	 * Beware that this is a linear search, unlike lookups by key,
	 * and that multiple keys can map to the same value and this will
	 * find only one of them.
	 */
	public int indexOfValue(int value) {
		for (int i = 0; i < mSize; i++)
			if (mValues[i] == value) {
				return i;
			}

		return -1;
	}

	/**
	 * Removes all key-value mappings from this SparseIntArray.
	 */
	public void clear() {
		mSize = 0;
	}

	/**
	 * Puts a key/value pair into the array, optimizing for the case where
	 * the key is greater than all existing keys in the array.
	 */
	public void append(int key, int value) {
		if (mSize != 0 && key <= mKeys[mSize - 1]) {
			put(key, value);
			return;
		}

		mKeys = MyArrayHelper.append(mKeys, mSize, key);
		mValues = MyArrayHelper.append(mValues, mSize, value);
		mSize++;
	}

	/**
	 * Provides a copy of keys.
	 */
	public int[] copyKeys() {
		if (size() == 0) {
			return null;
		}
		return Arrays.copyOf(mKeys, size());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>This implementation composes a string by iterating over its mappings.</p>
	 */
	@Override
	public String toString() {
		if (size() <= 0) {
			return "{}";
		}

		StringBuilder buffer = new StringBuilder(mSize * 28);
		buffer.append('{');
		for (int i = 0; i < mSize; i++) {
			if (i > 0) {
				buffer.append(", ");
			}
			int key = keyAt(i);
			buffer.append(key);
			buffer.append('=');
			double value = valueAt(i);
			buffer.append(value);
		}
		buffer.append('}');
		return buffer.toString();
	}
}
