package louis;
import com.sun.jna.Native;
import com.sun.jna.Library;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;
public interface Liblouis extends Library {
    Liblouis INSTANCE = (Liblouis) Native.loadLibrary("louis", Liblouis.class);
    public static interface typeforms {
        public static final byte plain_text = 0;
        public static final byte italic = 1;
        public static final byte underline = 2;
        public static final byte bold = 4;
        public static final byte computer_braille = 8;
    }
    public static interface translationModes {
        public static final int noContractions = 1;
        public static final int compbrlAtCursor = 2;
        public static final int dotsIO = 4;
        public static final int comp8Dots = 8;
        public static final int pass1Only = 16;
    }
    int lou_translateString(String trantab, byte[] inbuf, IntByReference inlen, byte[] outbuf, IntByReference outlen, byte[] typeform, byte[] spacing, int mode);
    int lou_translate(String trantab, byte[] inbuf, IntByReference inlen, byte[] outbuf, IntByReference outlen, byte[] typeform, byte[] spacing, int[] outpos, int[] inpos, IntByReference cursorpos, int mode);
    int lou_backTranslateString(String trantab, byte[] inbuf, IntByReference inlen, byte[] outbuf, IntByReference outlen, byte[] typeform, byte[] spacing, int mode);
    int lou_backTranslate(String trantab, byte[] inbuf, IntByReference inlen, byte[] outbuf, IntByReference outlen, byte[] typeform, byte[] spacing, int[] outpos, int[] inpos, IntByReference cursorpos, int mode);
    int lou_hyphenate(String trantab, byte[] inbuf, int inlen, byte[] hyphens, int mode);
    void lou_logFileName(String fileName);
    void lou_logPrint(String format);
    void lou_getTable(String tablelist);
    String lou_version();
    void lou_free();
}
