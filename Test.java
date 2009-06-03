import org.liblouis.Louis;
import org.liblouis.TranslationException;

public class Test {
  public static void main(String[] args) throws TranslationException {
    Louis translator = new Louis();
    byte[] typeforms = null;
    String brailleStr = translator.translateString(args[0], args[1], typeforms, 0);
    System.out.println(brailleStr);
    String backTranslationStr = translator.backTranslateString(args[0], brailleStr, typeforms, 0);
    System.out.println(backTranslationStr);
    translator.close();
  }
}
