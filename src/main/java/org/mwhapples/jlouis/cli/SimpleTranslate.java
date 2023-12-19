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
package org.mwhapples.jlouis.cli;

import org.mwhapples.jlouis.Louis;
import org.mwhapples.jlouis.TranslationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTranslate {
    private static final Logger logger = LoggerFactory.getLogger(SimpleTranslate.class);

    public static void main(String[] args) throws TranslationException {

        //   check that library will load first
        Louis translator = new Louis();
        if (args.length != 2) {
            System.out.println("LibLouis version:  " + translator.getVersion());
            System.out.println("Usage: <translation table> <text to translate>");
            System.exit(0);
        }
        logger.info("Getting liblouis version");
        logger.info("Using buffer size {}", translator.getEncodingSize());
        logger.info("Performing forward translation for string of length {} using mode {}", args[1].length(), 0);
        String brailleStr = translator.translateString(args[0], args[1], null, 0);
        logger.info(brailleStr);
        byte[] hyphens = translator.hyphenate(args[0], brailleStr, 1);
        char[] hyphenChars = new char[hyphens.length];
        for (int i = 0; i < hyphens.length; i++) {
            if (hyphens[i] == '1') {
                hyphenChars[i] = '-';
            } else {
                hyphenChars[i] = ' ';
            }
        }
        logger.info(new String(hyphenChars));
        String backTranslationStr = translator.backTranslateString(args[0], brailleStr, null, 0);
        logger.info(backTranslationStr);
        hyphens = translator.hyphenate(args[0], backTranslationStr, 0);
        hyphenChars = new char[hyphens.length];
        for (int i = 0; i < hyphens.length; i++) {
            if (hyphens[i] == '1') {
                hyphenChars[i] = '-';
            } else {
                hyphenChars[i] = ' ';
            }
        }
        logger.info(new String(hyphenChars));
        translator.close();
    }
}
