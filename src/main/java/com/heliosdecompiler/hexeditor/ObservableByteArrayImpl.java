/*
 * Copyright 2017 Sam Sun <github-contact@samczsun.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.heliosdecompiler.hexeditor;

import javafx.collections.ObservableArrayBase;

import java.util.Arrays;

public class ObservableByteArrayImpl extends ObservableArrayBase<ObservableByteArray> implements ObservableByteArray {

    private static final byte[] INITIAL = new byte[0];

    private byte[] array = INITIAL;
    private int size = 0;

    /**
     * Creates empty observable integer array
     */
    public ObservableByteArrayImpl() {
    }

    /**
     * Creates observable integer array with copy of given initial values
     *
     * @param elements initial values to copy to observable integer array
     */
    public ObservableByteArrayImpl(byte... elements) {
        setAll(elements);
    }

    /**
     * Creates observable integer array with copy of given observable integer array
     *
     * @param src observable integer array to copy
     */
    public ObservableByteArrayImpl(ObservableByteArray src) {
        setAll(src);
    }

    @Override
    public void clear() {
        resize(0);
    }

    @Override
    public int size() {
        return size;
    }

    private void addAllInternal(ObservableByteArray src, int srcIndex, int length) {
        growCapacity(length);
        src.copyTo(srcIndex, array, size, length);
        size += length;
        fireChange(length != 0, size - length, size);
    }

    private void addAllInternal(byte[] src, int srcIndex, int length) {
        growCapacity(length);
        System.arraycopy(src, srcIndex, array, size, length);
        size += length;
        fireChange(length != 0, size - length, size);
    }

    @Override
    public void addAll(ObservableByteArray src) {
        addAllInternal(src, 0, src.size());
    }

    @Override
    public void addAll(byte... elements) {
        addAllInternal(elements, 0, elements.length);
    }

    @Override
    public void addAll(ObservableByteArray src, int srcIndex, int length) {
        rangeCheck(src, srcIndex, length);
        addAllInternal(src, srcIndex, length);
    }

    @Override
    public void addAll(byte[] src, int srcIndex, int length) {
        rangeCheck(src, srcIndex, length);
        addAllInternal(src, srcIndex, length);
    }

    private void setAllInternal(ObservableByteArray src, int srcIndex, int length) {
        boolean sizeChanged = size() != length;
        if (src == this) {
            if (srcIndex == 0) {
                resize(length);
            } else {
                System.arraycopy(array, srcIndex, array, 0, length);
                size = length;
                fireChange(sizeChanged, 0, size);
            }
        } else {
            size = 0;
            ensureCapacity(length);
            src.copyTo(srcIndex, array, 0, length);
            size = length;
            fireChange(sizeChanged, 0, size);
        }
    }

    private void setAllInternal(byte[] src, int srcIndex, int length) {
        boolean sizeChanged = size() != length;
        size = 0;
        ensureCapacity(length);
        System.arraycopy(src, srcIndex, array, 0, length);
        size = length;
        fireChange(sizeChanged, 0, size);
    }

    @Override
    public void setAll(ObservableByteArray src) {
        setAllInternal(src, 0, src.size());
    }

    @Override
    public void setAll(ObservableByteArray src, int srcIndex, int length) {
        rangeCheck(src, srcIndex, length);
        setAllInternal(src, srcIndex, length);
    }

    @Override
    public void setAll(byte[] src, int srcIndex, int length) {
        rangeCheck(src, srcIndex, length);
        setAllInternal(src, srcIndex, length);
    }

    @Override
    public void setAll(byte[] src) {
        setAllInternal(src, 0, src.length);
    }

    @Override
    public void set(int destIndex, byte[] src, int srcIndex, int length) {
        rangeCheck(destIndex + length);
        System.arraycopy(src, srcIndex, array, destIndex, length);
        fireChange(false, destIndex, destIndex + length);
    }

    @Override
    public void set(int destIndex, ObservableByteArray src, int srcIndex, int length) {
        rangeCheck(destIndex + length);
        src.copyTo(srcIndex, array, destIndex, length);
        fireChange(false, destIndex, destIndex + length);
    }

    @Override
    public byte[] toArray(byte[] dest) {
        if ((dest == null) || (size() > dest.length)) {
            dest = new byte[size()];
        }
        System.arraycopy(array, 0, dest, 0, size());
        return dest;
    }

    @Override
    public byte get(int index) {
        rangeCheck(index + 1);
        return array[index];
    }

    @Override
    public void set(int index, byte value) {
        rangeCheck(index + 1);
        array[index] = value;
        fireChange(false, index, index + 1);
    }

    @Override
    public byte[] toArray(int index, byte[] dest, int length) {
        rangeCheck(index + length);
        if ((dest == null) || (length > dest.length)) {
            dest = new byte[length];
        }
        System.arraycopy(array, index, dest, 0, length);
        return dest;
    }

    @Override
    public void copyTo(int srcIndex, byte[] dest, int destIndex, int length) {
        rangeCheck(srcIndex + length);
        System.arraycopy(array, srcIndex, dest, destIndex, length);
    }

    @Override
    public void copyTo(int srcIndex, ObservableByteArray dest, int destIndex, int length) {
        rangeCheck(srcIndex + length);
        dest.set(destIndex, array, srcIndex, length);
    }

    @Override
    public void resize(int newSize) {
        if (newSize < 0) {
            throw new NegativeArraySizeException("Can't resize to negative value: " + newSize);
        }
        ensureCapacity(newSize);
        int minSize = Math.min(size, newSize);
        boolean sizeChanged = size != newSize;
        size = newSize;
        Arrays.fill(array, minSize, size, (byte) 0);
        fireChange(sizeChanged, minSize, newSize);
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private void growCapacity(int length) {
        int minCapacity = size + length;
        int oldCapacity = array.length;
        if (minCapacity > array.length) {
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity < minCapacity) newCapacity = minCapacity;
            if (newCapacity > MAX_ARRAY_SIZE) newCapacity = hugeCapacity(minCapacity);
            ensureCapacity(newCapacity);
        } else if (length > 0 && minCapacity < 0) {
            throw new OutOfMemoryError(); // overflow
        }
    }

    @Override
    public void ensureCapacity(int capacity) {
        if (array.length < capacity) {
            array = Arrays.copyOf(array, capacity);
        }
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    @Override
    public void trimToSize() {
        if (array.length != size) {
            byte[] newArray = new byte[size];
            System.arraycopy(array, 0, newArray, 0, size);
            array = newArray;
        }
    }

    private void rangeCheck(int size) {
        if (size > this.size) throw new ArrayIndexOutOfBoundsException(this.size);
    }

    private void rangeCheck(ObservableByteArray src, int srcIndex, int length) {
        if (src == null) throw new NullPointerException();
        if (srcIndex < 0 || srcIndex + length > src.size()) {
            throw new ArrayIndexOutOfBoundsException(src.size());
        }
        if (length < 0) throw new ArrayIndexOutOfBoundsException(-1);
    }

    private void rangeCheck(byte[] src, int srcIndex, int length) {
        if (src == null) throw new NullPointerException();
        if (srcIndex < 0 || srcIndex + length > src.length) {
            throw new ArrayIndexOutOfBoundsException(src.length);
        }
        if (length < 0) throw new ArrayIndexOutOfBoundsException(-1);
    }

    @Override
    public String toString() {
        if (array == null)
            return "null";

        int iMax = size() - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(array[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
}
