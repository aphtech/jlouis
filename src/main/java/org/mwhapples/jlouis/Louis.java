package org.mwhapples.jlouis;

/*
 * Copyright (c) 2010-2011 Michael whapples
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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class Louis {
    /**
     * Constants for liblouis typeforms.
     * 
     * <p>
     * Liblouis takes bytes to represent type forms, use these to find out the
     * constant values. You may also find the liblouis documentation useful.
     * </p>
     * 
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
     * <p>
     * Use these constants to inform liblouis of the translation mode to be
     * used. Refer to the liblouis documentation to find out the definitions.
     * </p>
     */
    public static interface translationModes {
        public static final int noContractions = 1;
        public static final int compbrlAtCursor = 2;
        public static final int dotsIO = 4;
        public static final int comp8Dots = 8;
        public static final int pass1Only = 16;
        public static final int compbrlLeftCursor = 32;
    }

    private int outRatio;
    private static int encodingSize;
    private static Properties libConfig;
    static {
        try {
            libConfig = loadConfig("jlouis.properties");
        } catch (IOException e) {

        }
    }

    private static Properties loadConfig(String resourceName)
            throws IOException {
        InputStream configStream = ClassLoader
                .getSystemResourceAsStream(resourceName);
        Properties configProperties = new Properties();
        if (configStream != null) {
            configProperties.load(configStream);
        }
        return configProperties;
    }

    public Louis() {
        outRatio = 2;
    }
    public String getVersion() {
        return Louis.lou_version();
    }

    public int getEncodingSize() {
        return Louis.lou_charSize();
    }

    public String translateString(String trantab, String inbuf,
            byte[] typeforms, int mode) throws TranslationException {
        byte[] spacing = null;
        int inlen = inbuf.length();
        Louis.WideChar wInbuf = new Louis.WideChar(inbuf);
        int outlen = outRatio * inlen;
        byte[] typeformsCopy = null;
        if (typeforms != null) {
            typeformsCopy = Arrays.copyOf(typeforms, outlen);
        }
        Louis.WideChar wOutbuf = new Louis.WideChar(outlen);
        IntByReference poutlen = new IntByReference(outlen);
        if (Louis.lou_translateString(trantab, wInbuf, new IntByReference(
                inlen), wOutbuf, poutlen, typeformsCopy, spacing, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        return wOutbuf.getText(poutlen.getValue());
    }

    public TranslationResult translate(String trantab, String inbuf,
            int cursorPos, int mode) throws TranslationException {
        byte[] typeForms = null;
        return translate(trantab, inbuf, typeForms, cursorPos, mode);
    }

    public TranslationResult translate(String trantab, String inbuf,
            byte[] typeForms, int cursorPos, int mode)
            throws TranslationException {
        byte[] spacing = null;
        Louis.WideChar wInbuf = new Louis.WideChar(inbuf);
        int inlen = wInbuf.length();
        int outlen = inlen * outRatio;
        byte[] typeformsCopy = null;
        if (typeForms != null) {
            typeformsCopy = Arrays.copyOf(typeForms, outlen);
        }
        Louis.WideChar wOutbuf = new Louis.WideChar(outlen);
        IntByReference poutlen = new IntByReference(outlen);
        IntByReference pcursorPos = new IntByReference(cursorPos);
        int[] outPos = new int[inlen];
        int[] inPos = new int[outlen];
        if (Louis.lou_translate(trantab, wInbuf, new IntByReference(inlen),
                wOutbuf, poutlen, typeformsCopy, spacing, outPos, inPos,
                pcursorPos, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        String outbuf = wOutbuf.getText(poutlen.getValue());
        return new TranslationResult(outbuf, outPos, inPos,
                pcursorPos.getValue());
    }

    public String backTranslateString(String trantab, String inbuf,
            byte[] typeforms, int mode) throws TranslationException {
        byte[] spacing = null;
        Louis.WideChar wInbuf = new Louis.WideChar(inbuf);
        int inlen = wInbuf.length();
        int outlen = outRatio * inlen;
        Louis.WideChar wOutbuf = new Louis.WideChar(outlen);
        byte[] typeformsCopy = null;
        if (typeforms != null) {
            typeformsCopy = Arrays.copyOf(typeforms, outlen);
        }
        IntByReference poutlen = new IntByReference(outlen);
        if (Louis.lou_backTranslateString(trantab, wInbuf,
                new IntByReference(inlen), wOutbuf, poutlen, typeformsCopy,
                spacing, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        return wOutbuf.getText(poutlen.getValue());
    }

    public TranslationResult backTranslate(String trantab, String inbuf,
            int cursorPos, int mode) throws TranslationException {
        byte[] typeForms = null;
        return backTranslate(trantab, inbuf, typeForms, cursorPos, mode);
    }

    public TranslationResult backTranslate(String trantab, String inbuf,
            byte[] typeForms, int cursorPos, int mode)
            throws TranslationException {
        byte[] spacing = null;
        Louis.WideChar wInbuf = new Louis.WideChar(inbuf);
        int inlen = wInbuf.length();
        int outlen = inlen * outRatio;
        byte[] typeformsCopy = null;
        if (typeForms != null) {
            typeformsCopy = Arrays.copyOf(typeForms, outlen);
        }
        Louis.WideChar wOutbuf = new Louis.WideChar(outlen);
        IntByReference poutlen = new IntByReference(outlen);
        IntByReference pcursorPos = new IntByReference(cursorPos);
        int[] outPos = new int[inlen];
        int[] inPos = new int[outlen];
        if (Louis.lou_backTranslate(trantab, wInbuf, new IntByReference(
                inlen), wOutbuf, poutlen, typeformsCopy, spacing, outPos,
                inPos, pcursorPos, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        String outbuf = wOutbuf.getText(poutlen.getValue());
        return new TranslationResult(outbuf, outPos, inPos,
                pcursorPos.getValue());
    }

    public byte[] hyphenate(String trantab, String inbuf, int mode)
            throws TranslationException {
        Louis.WideChar wInbuf = new Louis.WideChar(inbuf);
        int inlen = wInbuf.length();
        byte[] hyphens = new byte[inlen * outRatio];
        if (Louis.lou_hyphenate(trantab, wInbuf, inlen, hyphens, mode) == 0) {
            for (int i = 0; i < hyphens.length; i++) {
                hyphens[i] = ' ';
            }
        }
        return Arrays.copyOf(hyphens, inlen);
    }

    public void setLogFileName(String fileName) {
        Louis.lou_logFile(fileName);
    }

    public void logPrint(String format, Object... args) {
        Louis.lou_logPrint(String.format(format, args));
    }

    public void close() {
        Louis.lou_free();
    }

    // Initialise this as a native library for JNA
    static {
        int platform = Platform.getOSType();
        String libName;
        switch (platform) {
        case Platform.FREEBSD:
            libName = libConfig.getProperty("jlouis.library.name.freebsd",
                    "louis");
            break;
        case Platform.LINUX:
            libName = libConfig.getProperty("jlouis.library.name.linux",
                    "louis");
            break;
        case Platform.MAC:
            libName = libConfig.getProperty("jlouis.library.name.mac", "louis");
            break;
        case Platform.OPENBSD:
            libName = libConfig.getProperty("jlouis.library.name.openbsd",
                    "louis");
            break;
        case Platform.SOLARIS:
            libName = libConfig.getProperty("jlouis.library.name.solaris",
                    "louis");
            break;
        case Platform.WINDOWS:
            libName = libConfig.getProperty("jlouis.library.name.windows",
                    "louis");
            break;
        case Platform.WINDOWSCE:
            libName = libConfig.getProperty("jlouis.library.name.windowsce",
                    "louis");
        default:
            libName = "louis";
            break;
        }
        String libPath = System.getProperty("jlouis.library.path", libConfig.getProperty("jlouis.library.path"));
        if (libPath != null) {
            NativeLibrary.addSearchPath(libName, libPath);
        }
        Native.register(libName);
        Louis.encodingSize = Louis.lou_charSize();
    }

    public static class WideChar implements NativeMapped {
        private static String encoding;
        private Memory buffer;
        private int length;
        private void setEncoding() {
            if (Louis.WideChar.encoding != null) return;
            if (Louis.encodingSize == 2) {
                Louis.WideChar.encoding = "utf-16le";
            } else if (Louis.encodingSize == 4) {
                Louis.WideChar.encoding = "utf-32le";
            }
        }
        public WideChar(int length) {
            this.setEncoding();
            if (Louis.encodingSize == 0) return;
            this.length = length;
            buffer = new Memory(Louis.encodingSize * length);
        }
        public WideChar(String text) {
            this.setEncoding();
            this.setText(text);
        }
        public WideChar() {
            this(1);
        }
        private void setText(String text) {
            try {
                this.length = text.length();
                buffer = new Memory(this.length * Louis.encodingSize);
                this.buffer.write(0, text.getBytes(encoding), 0, Louis.encodingSize * text.length());
            } catch (UnsupportedEncodingException e) {

            }
        }
        public String getText(int length) {
            int bufferLength = length * Louis.encodingSize;
            try {
                return new String(buffer.getByteArray(0, bufferLength), 0, bufferLength, encoding);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        public int length() {
            return this.length;
        }
        public Class<?> nativeType() {
            return Pointer.class;
        }
        public Object toNative() {
            return this.buffer;
        }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            byte[] nativeBytes = (byte[])nativeValue;
            WideChar javaValue = new WideChar();
            try {
                javaValue.setText(new String(nativeBytes, encoding));
            } catch (UnsupportedEncodingException e) {
            }
            return javaValue;
        }
    }

    /**
     * The lou_backTranslate function.
     * 
     * This method uses the lou_backTranslate function to perform back
     * translation. The information about @see lou_translateString applies to
     * this method with the addition of the parameters specified in the liblouis
     * documentation for the lou_backTranslate function.
     */
    private static native int lou_backTranslate(String trantab, Louis.WideChar inbuf,
            IntByReference inlen, Louis.WideChar outbuf, IntByReference outlen,
            byte[] typeform, byte[] spacing, int[] outpos, int[] inpos,
            IntByReference cursorpos, int mode);

    /**
     * The lou_backTranslateString function.
     * 
     * This method uses the lou_backTranslateString function of liblouis to
     * perform back translation.
     * 
     * The useage of this method is as described for lou_translateString.
     */
    private static native int lou_backTranslateString(String trantab,
            Louis.WideChar inbuf, IntByReference inlen, Louis.WideChar outbuf,
            IntByReference outlen, byte[] typeform, byte[] spacing, int mode);

    /**
     * This method will get the size of the encoding used for widechar in
     * liblouis.
     * 
     * @return The size of widechar.
     */
    private static native int lou_charSize();

    /**
     * The lou_free function.
     * 
     * This function should be called at the end of use of liblouis to free
     * memory. According to the liblouis documentation you should not call this
     * method after each call to lou_translateString, etc.
     */
    private static native void lou_free();

    /**
     * The lou_getTable function.
     * 
     * There is no reason for java applications to call this method, it is being
     * included purely for completeness.
     * 
     * This method will compile the translation tables given to it, but as any
     * function which requires compiled tables calls this function it is not
     * needed to be called externally.
     */
    private static native void lou_getTable(String tablelist);

    /**
     * The lou_hyphenate function.
     * 
     * This method is for the lou_hyphenate function of liblouis. parameters are
     * as given in the liblouis documentation.
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
    private static native int lou_hyphenate(String trantab, Louis.WideChar inbuf,
            int inlen, byte[] hyphens, int mode);

    /**
     * The lou_logFile function.
     * 
     * This method will make liblouis put logging information in a log file of
     * the filename given as the parameter.
     */
    private static native void lou_logFile(String fileName);

    /**
     * The lou_logPrint function.
     * 
     * This method is as in the liblouis documentation.
     */
    private static native void lou_logPrint(String format);

    /**
     * The lou_translate function.
     * 
     * The same information about the useage of the @see lou_translateString
     * method applies to this method. This method takes the additional
     * parameters stated in the liblouis documentation.
     */
    private static native int lou_translate(String trantab, Louis.WideChar inbuf,
            IntByReference inlen, Louis.WideChar outbuf, IntByReference outlen,
            byte[] typeform, byte[] spacing, int[] outpos, int[] inpos,
            IntByReference cursorpos, int mode);

    /**
     * The lou_translateString function.
     * 
     * <p>
     * This method is for liblouis's lou_translateString, refer to the liblouis
     * documentation for the meaning of the parameters.
     * </p>
     * 
     * <p>
     * Particular things to note about this method which differ from the
     * expected.
     * <ul>
     * <li>This method takes a byte array for inbuf, this byte array should be
     * the bytes making up the unicode to be passed into lou_translateString
     * using the correct encoding for liblouis (remember that liblouis can be
     * compiled for 16-bit and 32-bit unicode.</li>
     * <li>inlen is the length of the original string not the byte array being
     * passed into this call.</li>
     * <li>The two above items also apply to outbuf and outlen. If you make a
     * mistake on setting outbuf and outlen sizes correctly this can lead to
     * errors which may crash the JVM. Due to this you are strongly advised to
     * avoid using these library classes directly as the main API
     * {@link org.mwhapples.jlouis} protects you from setting this incorrectly.</li>
     * </ul>
     * </p>
     * 
     * @param trantab
     *            A string of the tables to use, with the table names separated
     *            by commas.
     * @param inbuf
     *            A byte array of the unicode to be translated. When
     *            constructing inbuf, don't forget to account for the
     *            possibility that liblouis can be compiled for either 16-bit or
     *            32-bit unicode.
     * @param inlen
     *            The length of the string to be passed in. NOTE: The string
     *            length is not the same as the byte array used for inbuf due to
     *            unicode encoding size.
     * @param outbuf
     *            A byte array for the translation to be inserted into. Please
     *            ensure that you make the array of a large enough size to hold
     *            the translation.
     * @param outlen
     *            The length of the string expected to be returned. NOTE:
     *            Remember that the string size is different to the byte array
     *            size due to the unicode encoding.
     * @param typeform
     *            A byte array to mark which characters have certain attributes
     *            such as bold.
     *            {@link org.mwhapples.jlouis.library.Louis.typeforms}.
     * @param spacing
     *            A byte array used to represent spacing.
     * @param mode
     *            This indicates how the translation should be done. Use values
     *            from
     *            {@link org.mwhapples.jlouis.library.Louis.translationModes}.
     */
    private static native int lou_translateString(String trantab, Louis.WideChar inbuf,
            IntByReference inlen, Louis.WideChar outbuf, IntByReference outlen,
            byte[] typeform, byte[] spacing, int mode);

    /**
     * The lou_version function.
     * 
     * This method returns a string giving the version of liblouis.
     */
    private static native String lou_version();
}
