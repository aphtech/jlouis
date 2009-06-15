package org.liblouis;
import org.liblouis.library.Liblouis;
import org.liblouis.TranslationException;
import org.liblouis.TranslationResult;
import com.sun.jna.ptr.IntByReference;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;

public class Louis {
    private Liblouis louisLib;
    private String encoding;
    private Properties propConfig;
    private int outRatio = 2;
    public Louis() {
        louisLib = Liblouis.INSTANCE;
        propConfig = new Properties();
        try {
            loadConfig();
        } catch (IOException e) {
        }
        encoding = propConfig.getProperty("jlouis.encoding", "utf-16le");
    }
    protected void loadConfig() throws IOException {
        InputStream defaultConfig = this.getClass().getResourceAsStream("/jlouis.properties");
        if (defaultConfig != null) {
            propConfig.load(defaultConfig);
        }
    }
    protected byte[] createArrayFromString(String inbuf) throws TranslationException {
        byte[] inbufArray;
        try {
            inbufArray = inbuf.getBytes(encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Encoding not supported by JVM");
        }
        return inbufArray;
    }
    protected String createStringFromArray(byte[] outbufArray, int inlen) throws TranslationException {
        String outbuf;
        try {
            outbuf = new String(outbufArray, 0, inlen, encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Encoding not supported by JVM");
        }
        return outbuf;
    }
    public String getVersion() {
        return louisLib.lou_version();
    }
    public String translateString(String trantab, String inbuf, byte[] typeforms, int mode) throws TranslationException {
        byte[] spacing = null;
        int inlen = inbuf.length();
        byte[] inbufArray = createArrayFromString(inbuf);
        int encodingSize = inbufArray.length/inlen;
        int outlen = outRatio * inlen;
        byte[] typeformsCopy = null;
        if (typeforms != null) {
            typeformsCopy = Arrays.copyOf(typeforms, outlen);
        }
        byte[] outbufArray = new byte[outRatio * inbufArray.length];
        IntByReference poutlen = new IntByReference(outlen);
        if (louisLib.lou_translateString(trantab, inbufArray, new IntByReference(inlen),
              outbufArray, poutlen, typeformsCopy, spacing, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        int numOfBytes = poutlen.getValue() * encodingSize;
        String outbuf = createStringFromArray(outbufArray, numOfBytes);
        return outbuf;
    }
    public TranslationResult translate(String trantab, String inbuf, int cursorPos, int mode) throws TranslationException {
        byte[] typeForms = null;
        return translate(trantab, inbuf, typeForms, cursorPos, mode);
    }
    public TranslationResult translate(String trantab, String inbuf, byte[] typeForms, int cursorPos, int mode) throws TranslationException {
        byte[] spacing = null;
        byte[] inbufArray = createArrayFromString(inbuf);
        int inlen = inbuf.length();
        int encodingSize = inbufArray.length/inlen;
        int outlen = inlen * outRatio;
        byte[] typeformsCopy = null;
        if (typeForms != null) {
            typeformsCopy = Arrays.copyOf(typeForms, outlen);
        }
        byte[] outbufArray = new byte[outlen*encodingSize];
        IntByReference poutlen = new IntByReference(outlen);
        IntByReference pcursorPos = new IntByReference(cursorPos);
        int[] outPos = new int[inlen];
        int[] inPos = new int[outlen];
        if (louisLib.lou_translate(trantab, inbufArray, new IntByReference(inlen), outbufArray, poutlen, typeformsCopy, spacing, outPos, inPos, pcursorPos, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        int numOfBytes = encodingSize * poutlen.getValue();
        String outbuf = createStringFromArray(outbufArray, numOfBytes);
        return new TranslationResult(outbuf, outPos, inPos, pcursorPos.getValue());
    }
    public String backTranslateString(String trantab, String inbuf, byte[] typeforms, int mode) throws TranslationException {
        byte[] spacing = null;
        byte[] inbufArray = createArrayFromString(inbuf);
        int inlen = inbuf.length();
        int encodingSize = inbufArray.length/inlen;
        byte[] outbufArray = new byte[outRatio * inbufArray.length];
        int outlen = outRatio * inlen;
        byte[] typeformsCopy = null;
        if (typeforms != null) {
            typeformsCopy = Arrays.copyOf(typeforms, outlen);
        }
        IntByReference poutlen = new IntByReference(outlen);
        if (louisLib.lou_backTranslateString(trantab, inbufArray, new IntByReference(inlen), outbufArray, poutlen, typeformsCopy, spacing, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        int numOfBytes = poutlen.getValue() * encodingSize;
        String outbuf = createStringFromArray(outbufArray, numOfBytes);
        return outbuf;
    }
    public TranslationResult backTranslate(String trantab, String inbuf, int cursorPos, int mode) throws TranslationException {
        byte[] typeForms = null;
        return backTranslate(trantab, inbuf, typeForms, cursorPos, mode);
    }
    public TranslationResult backTranslate(String trantab, String inbuf, byte[] typeForms, int cursorPos, int mode) throws TranslationException {
        byte[] spacing = null;
        byte[] inbufArray = createArrayFromString(inbuf);
        int inlen = inbuf.length();
        int encodingSize = inbufArray.length/inlen;
        int outlen = inlen * outRatio;
        byte[] typeformsCopy = null;
        if (typeForms != null) {
            typeformsCopy = Arrays.copyOf(typeForms, outlen);
        }
        byte[] outbufArray = new byte[outlen*encodingSize];
        IntByReference poutlen = new IntByReference(outlen);
        IntByReference pcursorPos = new IntByReference(cursorPos);
        int[] outPos = new int[inlen];
        int[] inPos = new int[outlen];
        if (louisLib.lou_backTranslate(trantab, inbufArray, new IntByReference(inlen), outbufArray, poutlen, typeformsCopy, spacing, outPos, inPos, pcursorPos, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        int numOfBytes = encodingSize * poutlen.getValue();
        String outbuf = createStringFromArray(outbufArray, numOfBytes);
        return new TranslationResult(outbuf, outPos, inPos, pcursorPos.getValue());
    }
    public byte[] hyphenate(String trantab, String inbuf, int mode) throws TranslationException {
        byte[] inbufArray = createArrayFromString(inbuf);
        int inlen = inbuf.length();
        byte[] hyphens = new byte[inlen * outRatio];
        if (louisLib.lou_hyphenate(trantab, inbufArray, inlen, hyphens, mode) == 0) {
            for (int i=0; i < hyphens.length; i++) {
                hyphens[i] = ' ';
            }
        }
        return Arrays.copyOf(hyphens, inlen);
    }
    public void setLogFileName(String fileName) {
        louisLib.lou_logFile(fileName);
    }
    public void logPrint(String format, Object... args) {
        louisLib.lou_logPrint(format, args);
    }
    public void close() {
        louisLib.lou_free();
    }
}
