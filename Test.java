import org.liblouis.Louis;
import org.liblouis.TranslationException;

public class Test {
  public static void main(String[] args) throws TranslationException {
    Louis translator = new Louis();
    byte[] typeforms = null;
    System.out.println(translator.translateString(args[0], args[1], typeforms, 0));
    translator.close();
  }
}
