package org.liblouis;
import org.liblouis.library.Liblouis;
import org.liblouis.TranslationException;
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
        propConfig.load(defaultConfig);
    }
    public String getVersion() {
        return louisLib.lou_version();
    }
    public String translateString(String trantab, String inbuf, byte[] typeforms, int mode) throws TranslationException {
        byte[] spacing = null;
        return translateString(trantab, inbuf, typeforms, spacing, mode);
    }
    public String translateString(String trantab, String inbuf, byte[] typeforms, byte[] spacing, int mode) throws TranslationException {
        int inlen = inbuf.length();
        byte[] inbufArray;
        String outbuf;
        try {
            inbufArray = inbuf.getBytes(encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Config encoding not supported by JVM");
        }
        int encodingSize = inbufArray.length/inlen;
        int outlen = outRatio * inlen;
        byte[] outbufArray = new byte[outRatio * inbufArray.length];
        IntByReference poutlen = new IntByReference(outlen);
        if (louisLib.lou_translateString(trantab, inbufArray, new IntByReference(inlen),
              outbufArray, poutlen, typeforms, spacing, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        int numOfBytes = poutlen.getValue() * encodingSize;
        try {
            outbuf = new String(outbufArray, 0, numOfBytes, encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Config encoding not supported by JVM");
        }
        return outbuf;
    }
    public String backTranslateString(String trantab, String inbuf, byte[] typeforms, int mode) throws TranslationException {
        byte[] spacing = null;
        return backTranslateString(trantab, inbuf, typeforms, spacing, mode);
    }
    public String backTranslateString(String trantab, String inbuf, byte[] typeforms, byte[] spacing, int mode) throws TranslationException {
        byte[] inbufArray;
        try {
            inbufArray = inbuf.getBytes(encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Config encoding not supported by JVM");
        }
        int inlen = inbuf.length();
        int encodingSize = inbufArray.length/inlen;
        byte[] outbufArray = new byte[outRatio * inbufArray.length];
        String outbuf;
        int outlen = outRatio * inlen;
        IntByReference poutlen = new IntByReference(outlen);
        if (louisLib.lou_backTranslateString(trantab, inbufArray, new IntByReference(inlen), outbufArray, poutlen, typeforms, spacing, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        int numOfBytes = poutlen.getValue() * encodingSize;
        try {
            outbuf = new String(outbufArray, 0, numOfBytes, encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Config encoding not supported by JVM");
        }
        return outbuf;
    }
    public byte[] hyphenate(String trantab, String inbuf, int mode) throws TranslationException {
        byte[] inbufArray;
        try {
            inbufArray = inbuf.getBytes(encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Config encoding not supported by JVM");
        }
        int inlen = inbuf.length();
        byte[] hyphens = new byte[inlen * 2];
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
    public void close() {
        louisLib.lou_free();
    }
}
