/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.util;

import java.util.Arrays;

/**
 * 本クラスはタイプIntegerのArrayListとほぼ同じですが、32-bitの整数を限定した簡単でかつ効率良いものです。
 * <p></p>
 * 効率上の問題で、本クラスではIteratorが実装されませんので、foreachが使えません。
 */
public class DkIntArrayList {
    // デフォルト：配列が一杯になった時、この定数で長さを増加する
    private static final int DEFAULT_GROW_NUMBER_ITEM = 10;

    // 現在の要素数 (この位置には要素が入ってない)
    private int size;

    // 配列の総長
    private int capacity;

    // 内部の配列
    private int[] arr;

    // 配列が一杯になった時、この数値で長さを増加する
    private int growNumberItem;

    public DkIntArrayList() {
        this(DEFAULT_GROW_NUMBER_ITEM, DEFAULT_GROW_NUMBER_ITEM);
    }

    public DkIntArrayList(int capacity) {
        this(capacity, DEFAULT_GROW_NUMBER_ITEM);
    }

    public DkIntArrayList(int capacity, int growNumberItem) {
        if (capacity < 1) {
            capacity = DEFAULT_GROW_NUMBER_ITEM;
        }
        this.capacity = capacity;
        this.growNumberItem = growNumberItem;
        arr = new int[capacity];
    }

    public int size() {
        return size;
    }

    private void growArray(int more) {
        int newLength = capacity + more;
        int[] newArr = new int[newLength];
        System.arraycopy(arr, 0, newArr, 0, size);
        arr = newArr;
        capacity = newLength;
    }

    /**
     * @param growNumberItem 配列が一杯になった時点に、どのような程度で長さを増やすか決める数値
     */
    public void setGrowNumberItem(int growNumberItem) {
        if (growNumberItem < 1) {
            growNumberItem = 1;
        }
        this.growNumberItem = growNumberItem;
    }

    public int getGrowNumberItem() {
        return growNumberItem;
    }

    /**
     * @return 左側からvalueと等しい最初に見つかった要素の位置
     */
    public int indexOf(int value) {
        int[] arr = this.arr;
        for (int i = 0, N = size; i < N; ++i) {
            if (arr[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return 右側からvalueと等しい最初に見つかった要素の位置
     */
    public int lastIndexOf(int value) {
        int[] arr = this.arr;
        for (int i = size - 1; i >= 0; --i) {
            if (arr[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void add(int value) {
        if (size >= capacity) {
            growArray(growNumberItem);
        }
        arr[size++] = value;
    }

    public void add(int index, int value) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        final int SIZE = size;
        if (index >= SIZE) {
            index = SIZE;
        }

        if (size >= capacity) {
            growArray(growNumberItem);
        }
        final int[] arr = this.arr;
        // 効率の向上で同じ配列の場合、System.arraycopy()は使用しないべき
        for (int i = SIZE; i > index; --i) {
            arr[i] = arr[i - 1];
        }
        arr[index] = value;
        ++size;
    }

    public boolean addIfAbsence(int value) {
        if (indexOf(value) < 0) {
            add(value);
            return true;
        }
        return false;
    }

    public void addAll(int[] values) {
        final int SIZE = size;
        final int[] arr = this.arr;

        size += values.length;
        if (size >= capacity) {
            growArray(size + growNumberItem);
        }

        for (int i = size - 1; i >= SIZE; --i) {
            arr[i] = values[i - SIZE];
        }
    }

    public void addAll(int index, int[] values) {
        final int SIZE = size;
        final int[] arr = this.arr;
        final int more = values.length;

        size += more;
        if (size >= capacity) {
            growArray(size + growNumberItem);
        }

        for (int i = SIZE - 1; i >= index; --i) {
            arr[i + more] = arr[i];
        }

        for (int i = index + more - 1; i >= index; --i) {
            arr[i] = values[i - index];
        }
    }

    /**
     * 注意：この関数を使うとindexとlastIndexの要素が交換され、lastIndexの要素が削除されます。
     * そのため、各要素の順序が乱れてしまい、それでも気にしないなら、使ってください。
     */
    public void fastRemove(int index) {
        int lastIndex = size - 1;
        if (index >= 0 && index <= lastIndex) {
            if (index < lastIndex) {
                arr[index] = arr[lastIndex];
            }
            --size;
        }
    }

    /**
     * 注意：この関数を使うと各要素の順序が乱れてしまい、それでも気にしないなら、使ってください。
     */
    public void fastRemoveValue(int value) {
        fastRemove(indexOf(value));
    }

    public void remove(int index) {
        int lastIndex = size - 1;
        if (index >= 0 && index <= lastIndex) {
            int[] arr = this.arr;
            // 効率の向上で同じ配列の場合、System.arraycopy()は使用しないべき
            for (int i = index; i < lastIndex; ++i) {
                arr[i] = arr[i + 1];
            }
            --size;
        }
    }

    public void removeValue(int value) {
        remove(indexOf(value));
    }

    public void clear() {
        size = 0;
    }

    /**
     * 例外をスローする関数なので、必ず範囲内のindexを与えてください。
     * 例外がスローされたくない場合は、safeGet()をご利用ください。
     */
    public int get(int index) {
        if (index >= 0 && index < size) {
            return arr[index];
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * 例外をスローしない関数なので、値が見つからない場合はNULLを返します。
     * しかし、自動ボックシングの仕組みが実装されるので、効率上で良くはありません。
     */
    public Integer safeGet(int index) {
        if (index >= 0 && index < size) {
            return arr[index];
        }
        return null;
    }

    /**
     * 例外をスローする関数なので、範囲内のindexを与えてください。
     * 例外がスローされたくない場合は、safeSet()をご利用ください。
     */
    public boolean set(int index, int value) {
        if (index >= 0 && index < size) {
            arr[index] = value;
            return true;
        }
        throw new IndexOutOfBoundsException();
    }

    public void safeSet(int index, int value) {
        if (index >= 0 && index < size) {
            arr[index] = value;
        }
    }

    /**
     * 本クラスがサポートしてない機能を実装したく、効率よくしたい場合に役に立ちます。
     * <p></p>
     * 注意：内部の配列の長さは一般にsize()で返された値より大きいですので、
     * 反復したとき、size()を使ってください。
     *
     * @return 本クラスが使用している内部の配列
     */
    public int[] getInternalArray() {
        return arr;
    }

    public boolean contains(int value) {
        return indexOf(value) >= 0;
    }

    public void sort() {
        Arrays.sort(arr, 0, size);
    }
}
