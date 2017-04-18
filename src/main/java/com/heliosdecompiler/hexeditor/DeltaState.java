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

import javax.xml.bind.DatatypeConverter;

public class DeltaState {
    private int from;
    private int to;
    private byte[] before;
    private byte[] after;

    public DeltaState(int from, int to, byte[] before, byte[] after) {
        this.from = from;
        this.to = to;
        this.before = before;
        this.after = after;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public byte[] getBefore() {
        return before;
    }

    public byte[] getAfter() {
        return after;
    }
}
