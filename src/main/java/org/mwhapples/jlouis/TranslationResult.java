/**
 * Copyright (c) 2010-2015 Michael whapples
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * For attribution notices please see the file NOTICES.TXT which should be
 * included in the distribution.
 */
package org.mwhapples.jlouis;

import java.util.Arrays;
/*
 * Copyright (c) 2010-2011 Michael whapples
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
 *
 * For attribution notices please see the file NOTICES.TXT which should be
 * included in the distribution.
 */

public class TranslationResult {
    private final String translation;
    private final int[] inputPos;
    private final int[] outputPos;
    private final int cursorPos;

    TranslationResult(String translation, int[] outputPos, int[] inputPos, int cursorPos) {
        if (translation.length() > inputPos.length) {
            throw new IllegalArgumentException("The inputPos is too short, it should be at least as long as the translation string");
        }
        this.translation = translation;
        this.inputPos = Arrays.copyOf(inputPos, translation.length());
        this.outputPos = outputPos;
        this.cursorPos = cursorPos;
    }

    public String getTranslation() {
        return translation;
    }

    public int[] getInputPos() {
        return inputPos;
    }

    public int[] getOutputPos() {
        return outputPos;
    }

    public int getCursorPos() {
        return cursorPos;
    }
}
