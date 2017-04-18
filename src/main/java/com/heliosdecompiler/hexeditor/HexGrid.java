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

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import org.controlsfx.control.spreadsheet.*;

import java.util.*;

public class HexGrid extends GridBase {
    private static final ByteSpreadsheetCellType BYTE = new ByteSpreadsheetCellType();

    private ObservableList<String> rowHeaders;

    /**
     * {@inheritDoc}
     */
    public HexGrid() {
        super(0, 17);

        rowHeaders = new ObservableList<String>() {
            @Override
            public void addListener(ListChangeListener<? super String> listener) {

            }

            @Override
            public void removeListener(ListChangeListener<? super String> listener) {

            }

            @Override
            public boolean addAll(String... elements) {
                return false;
            }

            @Override
            public boolean setAll(String... elements) {
                return false;
            }

            @Override
            public boolean setAll(Collection<? extends String> col) {
                return false;
            }

            @Override
            public boolean removeAll(String... elements) {
                return false;
            }

            @Override
            public boolean retainAll(String... elements) {
                return false;
            }

            @Override
            public void remove(int from, int to) {

            }

            @Override
            public int size() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    long index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < Long.MAX_VALUE;
                    }

                    @Override
                    public String next() {
                        String result = Long.toHexString(index);
                        index += 16;
                        return result;
                    }
                };
            }

            @Override
            public Object[] toArray() {
                throw new IllegalArgumentException();
            }

            @Override
            public <T> T[] toArray(T[] a) {
                throw new IllegalArgumentException();
            }

            @Override
            public boolean add(String s) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public String get(int index) {
                return String.format("%08X", index * 16);
            }

            @Override
            public String set(int index, String element) {
                return null;
            }

            @Override
            public void add(int index, String element) {

            }

            @Override
            public String remove(int index) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @Override
            public ListIterator<String> listIterator() {
                return null;
            }

            @Override
            public ListIterator<String> listIterator(int index) {
                return null;
            }

            @Override
            public List<String> subList(int fromIndex, int toIndex) {
                return null;
            }

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }
        };

        getColumnHeaders().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", "ASCII");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(int row, int column, Object value) {
        if (row < getRowCount() && column < getColumnCount() && !isLocked()) {
            SpreadsheetCell cell = getRows().get(row).get(column);
            Object previousItem = cell.getItem();
            Object convertedValue = cell.getCellType().convertValue(value);

            if (previousItem == null && (convertedValue instanceof Byte && (Byte) convertedValue == 0x00)) {
                return;
            }

            cell.setItem(convertedValue);
            if (!java.util.Objects.equals(previousItem, cell.getItem())) {
                GridChange cellChange = new GridChange(row, column, previousItem, convertedValue);
                Event.fireEvent(this, cellChange);
            }
        }
    }

    @Override
    public ObservableList<String> getRowHeaders() {
        return rowHeaders;
    }

    public void resize(int rowCount) {
        if (rowCount <= getRowCount()) {
            getRows().remove(getRowCount(), rowCount);
            return;
        }

        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList(new ArrayList<>(rowCount - getRowCount()));

        for (int row = getRowCount(); row < rowCount; ++row) {
            ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList(new ArrayList<>(17));
            for (int column = 0; column < getColumnCount() - 1; ++column) {
                list.add(new SpreadsheetCellBase(row, column, 1, 1, BYTE));
            }
            SpreadsheetCell cell = new SpreadsheetCellBase(row, 16, 1, 1, SpreadsheetCellType.STRING);
            cell.setEditable(false);
            list.add(cell);
            rows.add(list);
        }

        getRows().addAll(rows);
    }
}
