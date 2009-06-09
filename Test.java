import org.liblouis.Louis;
import org.liblouis.TranslationException;

public class Test {
  public static void main(String[] args) throws TranslationException {
    Louis translator = new Louis();
    translator.setLogFileName("/home/mike/louis.error.txt");
    System.out.println(translator.getVersion());
    byte[] typeforms = null;
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
