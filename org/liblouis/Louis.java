package org.liblouis;
import org.liblouis.library.Liblouis;
import org.liblouis.TranslationException;
import com.sun.jna.ptr.IntByReference;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

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
        int inlen = inbuf.length();
        byte[] inbufArray;
        String outbuf;
        try {
            inbufArray = inbuf.getBytes(encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Config encoding not supported by JVM");
        }
        int outlen = outRatio * inlen;
        byte[] outbufArray = new byte[outRatio * inbufArray.length];
        byte[] spacing = null;
        if (louisLib.lou_translateString(trantab, inbufArray, new IntByReference(inlen),
              outbufArray, new IntByReference(outlen), typeforms, spacing, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        try {
            outbuf = new String(outbufArray, encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Config encoding not supported by JVM");
        }
        return outbuf;
    }
    public String backTranslateString(String trantab, String inbuf, byte[] typeforms, int mode) throws TranslationException {
        byte[] inbufArray;
        try {
            inbufArray = inbuf.getBytes(encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Config encoding not supported by JVM");
        }
        byte[] outbufArray = new byte[outRatio * inbufArray.length];
        byte[] spacing = null;
        int inlen = inbuf.length();
        String outbuf;
        int outlen = outRatio * inlen;
        if (louisLib.lou_backTranslateString(trantab, inbufArray, new IntByReference(inlen), outbufArray, new IntByReference(outlen), typeforms, spacing, mode) == 0) {
            throw new TranslationException("Unable to complete translation");
        }
        try {
            outbuf = new String(outbufArray, encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new TranslationException("Config encoding not supported by JVM");
        }
        return outbuf;
    }
    public void close() {
        louisLib.lou_free();
    }
}
