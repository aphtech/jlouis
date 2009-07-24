package org.liblouis.library;
import com.sun.jna.Native;
import com.sun.jna.Library;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;
/**
 * This interface provides the code to use liblouis through JNA.
 * 
 * <p>While this interface is public developers intending to use liblouis should not use thiss interface directly
 * instead they should use classes in the main package {@link org.liblouis}.
 * This package purely maps liblouis to java and so calls of these functions may not be very natural to java programmers
 * as the calls are defined as by the C library of liblouis.</p>
 *
 * <p>Particular reasons why this interface should not be used directly include:
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
 * <li>It is possible to crash the JVM by doing an invalid call to this class,
 * so simply don't use this directly.</li>
 * </ul>
 * </p>
 * 
 * @author Michael Whapples
 * @see "The liblouis documentation"
 * @version 1.0
 */
public interface Liblouis extends Library {
    /**
     * This variable contains the instance pointing to the library of liblouis.
     *
     * <p>Please refer to the interface docs to understand why a java application 
     * should not use this instance directly {@link org.liblouis.library.Liblouis}.
 * </p>
     */
    Liblouis INSTANCE = (Liblouis) Native.loadLibrary("louis", Liblouis.class);
    /**
     * Constants for liblouis typeforms.
     *
     * <p>Liblouis takes bytes to represent type forms, use these to find out 
     * the constant values. You may also find the liblouis documentation useful.</p>
     * @see "The liblouis documentation"
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
     * <p>Use these constants to inform liblouis of the translation mode to be 
     * used. Refer to the liblouis documentation to find out the definitions.</p>
     */
    public static interface translationModes {
        public static final int noContractions = 1;
        public static final int compbrlAtCursor = 2;
        public static final int dotsIO = 4;
        public static final int comp8Dots = 8;
        public static final int pass1Only = 16;
    }
    /**
     * The lou_translateString function.
     * 
     * <p>This method is for liblouis's lou_translateString, refer to the liblouis
     * documentation for the meaning of the parameters.</p>
     *
     * <p>Particular things to note about this method which differ from the
     * expected.
     * <ul>
     * <li>This method takes a byte array for inbuf, this byte array should
     * be the bytes making up the unicode to be passed into lou_translateString
     * using the correct encoding for liblouis (remember that liblouis can be
     * compiled for 16-bit and 32-bit unicode.</li>
     * <li>inlen is the length of the original string not the byte array
     * being passed into this call.</li>
     * <li>The two above items also apply to outbuf and outlen. If you make a
     * mistake on setting outbuf and outlen sizes correctly this can lead to
     * errors which may crash the JVM. Due to this you are strongly advised
     * to avoid using these library classes directly as the main API {@link org.liblouis} protects you from setting this
     * incorrectly.</li>
     * </ul></p>
     *
     * @param trantab A string of the tables to use, with the table names
     *        separated by commas.
     * @param inbuf A byte array of the unicode to be translated.
     *        When constructing inbuf, don't forget to account for the possibility that liblouis can be compiled for
     *        either 16-bit or 32-bit unicode.
     * @param inlen The length of the string to be passed in. NOTE: The string 
     * length is not the same as the byte array used for inbuf due to unicode 
     * encoding size.
     * @param outbuf A byte array for the translation to be inserted into. 
     * Please ensure that you make the array of a large enough size to hold 
     * the translation.
     * @param outlen The length of the string expected to be returned. 
     * NOTE: Remember that the string size is different to the byte array
     * size due to the unicode encoding.
     * @param typeform A byte array to mark which characters have
     * certain attributes such as bold. 
     * {@link org.liblouis.library.Louis.typeforms}.
     * @param spacing A byte array used to represent spacing.
     * @param mode This indicates how the translation should be done. Use
     * values from {@link org.liblouis.library.Louis.translationModes}.
     */
    int lou_translateString(String trantab, byte[] inbuf, IntByReference inlen, byte[] outbuf, IntByReference outlen, byte[] typeform, byte[] spacing, int mode);
    /**
     * The lou_translate function.
     *
     * The same information about the useage of the @see lou_translateString
     * method applies to this method. This method takes the additional 
     * parameters stated in the liblouis documentation.
     */
    int lou_translate(String trantab, byte[] inbuf, IntByReference inlen, byte[] outbuf, IntByReference outlen, byte[] typeform, byte[] spacing, int[] outpos, int[] inpos, IntByReference cursorpos, int mode);
    /**
     * The lou_backTranslateString function.
     * 
     * This method uses the lou_backTranslateString function of liblouis to
     * perform back translation.
     *
     * The useage of this method is as described for lou_translateString.
     */
    int lou_backTranslateString(String trantab, byte[] inbuf, IntByReference inlen, byte[] outbuf, IntByReference outlen, byte[] typeform, byte[] spacing, int mode);
    /**
     * The lou_backTranslate function.
     *
     * This method uses the lou_backTranslate function to perform back 
     * translation. The information about @see lou_translateString applies to 
     * this method with the addition of the parameters specified in the 
     * liblouis documentation for the lou_backTranslate function.
     */
    int lou_backTranslate(String trantab, byte[] inbuf, IntByReference inlen, byte[] outbuf, IntByReference outlen, byte[] typeform, byte[] spacing, int[] outpos, int[] inpos, IntByReference cursorpos, int mode);
    /**
     * The lou_hyphenate function.
     *
     * This method is for the lou_hyphenate function of liblouis. parameters
     * are as given in the liblouis documentation.
     *
     * Things which should be noted:
     * <ul>
     * <li>inbuf is a byte array containing the bytes of the unicode which
     * should be passed to the lou_hyphenate function. Be aware that liblouis
     * can be compiled for either 16-bit unicode or 32-bit unicode so ensure the
     * correct encoding is used.</li>
     * <li>inlen is the length of the string being input not the length of the 
     * byte array.</li>
     * <li>hyphens is a byte array, it should be of length of inlen not inbuf.</li>
     * </ul>
     */
    int lou_hyphenate(String trantab, byte[] inbuf, int inlen, byte[] hyphens, int mode);
    /**
     * The lou_logFile function.
     *
     * This method will make liblouis put logging information in a log file
     * of the filename given as the parameter.
     */
    void lou_logFile(String fileName);
    /**
     * The lou_logPrint function.
     *
     * This method is as in the liblouis documentation.
     */
    void lou_logPrint(String format, Object... args);
    /**
     * The lou_getTable function.
     * 
     * There is no reason for java applications to call this method, it is
     * being included purely for completeness.
     * 
     * This method will compile the translation tables given to it, but as
     * any function which requires compiled tables calls this function it
     * is not needed to be called externally.
     */
    void lou_getTable(String tablelist);
    /**
     * The lou_version function.
     *
     * This method returns a string giving the version of liblouis.
     */
    String lou_version();
    /**
     * The lou_free function.
     * 
     * This function should be called at the end of use of liblouis to free memory.
     * According to the liblouis documentation you should not call this method 
     * after each call to lou_translateString, etc.
     */
    void lou_free();
}
