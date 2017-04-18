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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.ArrayDeque;

public class HexArea extends SpreadsheetView {
    private ObservableByteArrayImpl data = new ObservableByteArrayImpl();

    private IntegerProperty historyLimit = new SimpleIntegerProperty(100);

    private volatile boolean gridChangeFired = false;
    private volatile boolean arrayChangeFired = false;
    private volatile boolean isUndoingOrRedoing = false;

    private ArrayDeque<DeltaState> undoHistory = new ArrayDeque<>();
    private ArrayDeque<DeltaState> redoHistory = new ArrayDeque<>();

    public HexArea() {
        HexGrid grid = new HexGrid();
        setGrid(grid);

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);
        setFixingColumnsAllowed(false);
        setFixingRowsAllowed(false);
        setStyle("-fx-font-family: monospace;");

        grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, e -> {
            if (arrayChangeFired)
                return;

            gridChangeFired = true;

            int index = e.getRow() * 16 + e.getColumn();
            try {
                byte b = (byte) e.getNewValue();

                if (!isUndoingOrRedoing) {
                    if (historyLimit.get() > 0) {
                        while (undoHistory.size() >= historyLimit.get()) {
                            undoHistory.poll();
                        }
                    }
                    undoHistory.push(new DeltaState(index, index, new byte[]{data.get(index)}, new byte[]{b}));
                    redoHistory.clear();
                }

                if (data.size() <= index) {
                    data.setAll(data.toArray(new byte[index + 1]));
                }
                data.set(index, b);

                StringBuilder str = new StringBuilder();
                for (int i = 0; i < 16; i++) {
                    Byte bte = (Byte) grid.getRows().get(e.getRow()).get(i).getItem();
                    if (bte == null)
                        bte = 0;

                    char chr = (char) (bte & 0xff);
                    str.append((chr == 0 || Character.isISOControl(chr)) ? '.' : chr);
                }

                grid.getRows().get(e.getRow()).get(16).itemProperty().set(str.toString());
            } catch (NumberFormatException ex) {
                // Literally should never happen
                throw new RuntimeException(ex);
            } finally {
                gridChangeFired = false;
            }
        });

        data.addListener((observableArray, sizeChanged, from, to) -> {
            if (gridChangeFired)
                return;

            arrayChangeFired = true;
            try {
                int maxRow = to / 16;
                int minRow = from / 16;
                boolean resized = false;

                if (grid.getRowCount() < maxRow + 1) {
                    resize(maxRow + 1);
                    resized = true;
                }

                for (int i = to - 1; i >= from; i--) {
                    grid.getRows().get(i / 16).get(i % 16).itemProperty().set(observableArray.get(i));
                }

                StringBuilder str = new StringBuilder();
                for (int row = maxRow; row >= minRow; row--) {
                    ObservableList<SpreadsheetCell> thisRow = grid.getRows().get(row);
                    str.setLength(0);
                    for (int i = 0; i < 16; i++) {
                        Byte b = (Byte) thisRow.get(i).getItem();
                        if (b == null)
                            b = 0;

                        char chr = (char) (b & 0xff);
                        str.append((chr == 0 || Character.isISOControl(chr)) ? '.' : chr);
                    }

                    thisRow.get(16).itemProperty().set(str.toString());
                }

                if (resized) {
                    for (int i = to; i < grid.getRows().size() * 16; i++) {
                        grid.getRows().get(i / 16).get(i % 16).setEditable(false);
                    }
                }
            } finally {
                arrayChangeFired = false;
            }
        });

        addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.isShortcutDown()) {
                if (e.getCode() == KeyCode.Z) {
                    DeltaState delta = undoHistory.poll();
                    if (delta != null) {
                        isUndoingOrRedoing = true;
                        try {
                            redoHistory.push(delta);
                            data.set(delta.getFrom(), delta.getBefore(), 0, delta.getBefore().length);
                        } finally {
                            isUndoingOrRedoing = false;
                        }
                    }
                } else if (e.getCode() == KeyCode.Y) {
                    DeltaState delta = redoHistory.poll();
                    if (delta != null) {
                        isUndoingOrRedoing = true;
                        try {
                            undoHistory.push(delta);
                            data.set(delta.getFrom(), delta.getAfter(), 0, delta.getBefore().length);
                        } finally {
                            isUndoingOrRedoing = false;
                        }
                    }
                }
            }
        });
    }

    public void setContent(byte[] newData) {
        data.setAll(newData);
    }

    public byte[] toByteArray() {
        return data.toArray(new byte[data.size()]);
    }

    public void resize(int rows) {
        ((HexGrid) getGrid()).resize(rows);
        setGrid(getGrid());
        setRowHeaderWidth(70);
        getColumns().forEach(col -> {
            col.setMinWidth(30);
            col.setMaxWidth(30);
        });
        getColumns().get(16).setMinWidth(140);
        getColumns().get(16).setMaxWidth(140);
    }

    public void clearHistory() {
        undoHistory.clear();
        redoHistory.clear();
    }

    public int getHistoryLimit() {
        return historyLimit.get();
    }

    public void setHistoryLimit(int value) {
        historyLimit.setValue(value);
    }

    public IntegerProperty historyLimitProperty() {
        return historyLimit;
    }
}
