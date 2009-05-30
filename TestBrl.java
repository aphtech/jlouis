import com.sun.jna.ptr.IntByReference;
import louis.Liblouis;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class TestBrl {
  public static void main(String[] args) throws UnsupportedEncodingException, IOException {
    InputStream configStream = ClassLoader.getSystemResourceAsStream("jlouis.properties");
    Properties config = new Properties();
    config.load(configStream);
    String encoding = config.getProperty("jlouis.encoding", "utf-16le");
    Liblouis l = Liblouis.INSTANCE;
    byte[] inBuf = args[1].getBytes(encoding);
    int inlen = args[1].length();
    int encodingSize = inBuf.length/inlen;
    byte[] outBuf = new byte[inBuf.length * 2];
    int outlen = inlen * 2;
    byte[] modes = new byte[outBuf.length];
    System.out.println(l.lou_version());
    IntByReference poutlen = new IntByReference(outlen);
    l.lou_translateString(args[0], inBuf, new IntByReference(inlen), outBuf, poutlen, modes, modes, 0);
    System.out.println(new String(outBuf, "utf-32le"));
    byte[] boutbuf = new byte[2 * encodingSize * poutlen.getValue()];
    int boutlen = poutlen.getValue() * 2;
    byte[] bmodes = new byte[boutlen];
    l.lou_backTranslateString(args[0], outBuf, poutlen, boutbuf, new IntByReference(boutlen), bmodes, bmodes, 0);
    System.out.println(new String(boutbuf, "utf-32le"));
    l.lou_free();
  }
}

