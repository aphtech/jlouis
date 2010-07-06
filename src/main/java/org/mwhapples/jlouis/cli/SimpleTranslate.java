package org.mwhapples.jlouis.cli;
/*
 * Copyright (c) 2010 Michael Whapples
 * 
 * This code is released under the QPL which can be seen in the file LICENSE distributed with this source code.
 */
import org.mwhapples.jlouis.Louis;
import org.mwhapples.jlouis.TranslationException;

public class SimpleTranslate {
  public static void main(String[] args) throws TranslationException {
    if (args.length != 2) {
      System.out.println("Invalid useage: arguments should be translation table and text to translate");
      System.exit(1);
    }
    Louis translator = new Louis();
    translator.setLogFileName("louis.error.txt");
    translator.logPrint("Getting liblouis version");
    System.out.println(translator.getVersion());
    byte[] typeforms = null;
    translator.logPrint("Using buffer size %d", translator.getEncodingSize());
    translator.logPrint("Performing forward translation for string of length %d using mode %d", args[1].length(), 0);
    String brailleStr = translator.translateString(args[0], args[1], typeforms, 0);
    System.out.println(brailleStr);
    byte[] hyphens = translator.hyphenate(args[0], brailleStr, 1);
    char[] hyphenChars = new char[hyphens.length];
    for (int i=0; i< hyphens.length; i++) {
      if (hyphens[i] == '1') {
        hyphenChars[i] = '-';
      } else {
        hyphenChars[i] = ' ';
      }
    }
    System.out.println(new String(hyphenChars));
    String backTranslationStr = translator.backTranslateString(args[0], brailleStr, typeforms, 0);
    System.out.println(backTranslationStr);
    hyphens = translator.hyphenate(args[0], backTranslationStr, 0);
    hyphenChars = new char[hyphens.length];
    for (int i=0; i < hyphens.length; i++) {
      if (hyphens[i] == '1') {
        hyphenChars[i] = '-';
      } else {
        hyphenChars[i] = ' ';
      }
    }
    System.out.println(new String(hyphenChars));
    translator.close();
  }
}
