/**
 * Copyright (c) 2010-2015 Michael whapples
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
package org.mwhapples.jlouis.cli;

import org.mwhapples.jlouis.Louis;
import org.mwhapples.jlouis.TranslationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTranslate {
	private static Logger logger = LoggerFactory.getLogger(SimpleTranslate.class);
	public static void main(String[] args) throws TranslationException {
    if (args.length != 2) {
      System.out.println("Invalid usage: arguments should be translation table and text to translate");
      System.exit(1);
    }
    Louis translator = new Louis();
    logger.info("Getting liblouis version");
    short[] typeforms = null;
    logger.info("Using buffer size %d", translator.getEncodingSize());
    logger.info("Performing forward translation for string of length %d using mode %d", args[1].length(), 0);
    String brailleStr = translator.translateString(args[0], args[1], typeforms, 0);
    logger.info(brailleStr);
    byte[] hyphens = translator.hyphenate(args[0], brailleStr, 1);
    char[] hyphenChars = new char[hyphens.length];
    for (int i=0; i< hyphens.length; i++) {
      if (hyphens[i] == '1') {
        hyphenChars[i] = '-';
      } else {
        hyphenChars[i] = ' ';
      }
    }
    logger.info(new String(hyphenChars));
    String backTranslationStr = translator.backTranslateString(args[0], brailleStr, typeforms, 0);
    logger.info(backTranslationStr);
    hyphens = translator.hyphenate(args[0], backTranslationStr, 0);
    hyphenChars = new char[hyphens.length];
    for (int i=0; i < hyphens.length; i++) {
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
