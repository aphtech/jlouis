package org.liblouis.library;
import com.sun.jna.Native;
import com.sun.jna.Library;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;
/**
 * This interface provides the code to use liblouis through JNA.
 * 
 * While this interface is public developers intending to use liblouis should not use thiss interface directly
 * instead they should use classes in the main package ( @see org.liblouis ).
 * This package purely maps liblouis to java and so calls of these functions may not be very natural to java programmers
 * as the calls are defined as by the C library of liblouis.
 *
 * Particular reasons why this interface should not be used directly include:
 * <ul>
 * <li>Certain calls require unicode strings, but as liblouis can be compiled 
 * for 16-bit or 32-bit unicode, it is impossible to use a specific type in this mapping for those parameters. To overcome
 * this this interface defines these parameters as byte arrays.</li>
 * <li>Due to unicode parameters being passed around as byte arrays it means that the byte array length does not correspond
 * to the length of the actual string (which is actually what liblouis expects).
 * This means to work out the length parameters to pass to liblouis you must
 * account for the number of bytes per character.</li>
 * <li>Liblouis returns information by altering content of variables passed to
 * it as pointers, this isn't the most natural java calling style.</li>
 * 
 * @author Michael Whapples
 * @version 1.0
 */
public interface Liblouis extends Library {
    /**
     * This variable contains the instance pointing to the library of liblouis.
     *
     * Please refer to the interface docs to understand why a java application 
     * should not use this instance directly ( @see org.liblouis.library.Liblouis ).
     */
    Liblouis INSTANCE = (Liblouis) Native.loadLibrary("louis", Liblouis.class);
    /**
     * Constants for liblouis typeforms.
     *
     * Liblouis takes bytes to represent type forms, use these to find out 
     * the constant values. You may also find the liblouis documentation useful.
     */
    public static interface typeforms {
        public static final byte plain_text = 0;
        public static final byte italic = 1;
        public static final byte underline = 2;
        public static final byte bold = 4;
        public static final byte computer_braille = 8;
    }
    /**
     * Constants for liblouis translation mode.
     *
     * Use these constants to inform liblouis of the translation mode to be 
     * used. Refer to the liblouis documentation to find out the definitions.
     */
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
