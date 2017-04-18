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

import org.controlsfx.control.spreadsheet.SpreadsheetCellEditor;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class ByteSpreadsheetCellType extends SpreadsheetCellType<Byte> {

    @Override
    public SpreadsheetCellEditor createEditor(SpreadsheetView view) {
        return new ByteEditor(view);
    }

    @Override
    public String toString(Byte object) {
        return ByteHelper.toString(object);
    }

    @Override
    public boolean match(Object value) {
        return true;
    }

    @Override
    public Byte convertValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        } else {
            return value == null ? 0 : ByteHelper.fromString(value.toString());
        }
    }
}
