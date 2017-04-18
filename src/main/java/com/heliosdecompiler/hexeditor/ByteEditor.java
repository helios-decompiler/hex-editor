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


import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.spreadsheet.SpreadsheetCellEditor;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class ByteEditor extends SpreadsheetCellEditor {

    /***************************************************************************
     * * Private Fields * *
     **************************************************************************/
    private final TextField tf;

    /***************************************************************************
     * * Constructor * *
     **************************************************************************/
    /**
     * Constructor for the IntegerEditor.
     *
     * @param view the SpreadsheetView
     */
    public ByteEditor(SpreadsheetView view) {
        super(view);
        tf = new TextField();
    }

    /***************************************************************************
     * * Public Methods * *
     **************************************************************************/
    /**
     * {@inheritDoc}
     */
    @Override
    public void startEdit(Object value) {
        if (value != null) {
            if (value instanceof Number) {
                tf.setText(ByteHelper.toString(((Number) value).byteValue()));
            } else {
                tf.setText(ByteHelper.toString(ByteHelper.fromString(value.toString())));
            }
        } else {
            tf.setText("");
        }

        tf.getStyleClass().removeAll("error"); //$NON-NLS-1$
        attachEnterEscapeEventHandler();

        tf.requestFocus();
        tf.selectAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
        tf.setOnKeyPressed(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextField getEditor() {
        return tf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getControlValue() {
        return tf.getText();
    }

    /***************************************************************************
     * * Private Methods * *
     **************************************************************************/

    private void attachEnterEscapeEventHandler() {
        tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ENTER) {
                    try {
                        if (tf.getText().equals("")) { //$NON-NLS-1$
                            endEdit(true);
                        } else {
                            Integer.parseInt(tf.getText());
                            endEdit(true);
                        }
                    } catch (Exception e) {
                    }

                } else if (t.getCode() == KeyCode.ESCAPE) {
                    endEdit(false);
                }
            }
        });
        tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                try {
                    if (tf.getText().equals("")) { //$NON-NLS-1$
                        tf.getStyleClass().removeAll("error"); //$NON-NLS-1$
                    } else {
                        Integer.parseInt(tf.getText());
                        tf.getStyleClass().removeAll("error"); //$NON-NLS-1$
                    }
                } catch (Exception e) {
                    tf.getStyleClass().add("error"); //$NON-NLS-1$
                }
            }
        });
    }
}
