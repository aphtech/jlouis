/**
 * Copyright (c) 2010-2015 Michael whapples
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
package org.mwhapples.jlouis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.glass.utils.NativeLibLoader;
import com.sun.jna.FromNativeContext;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * <p>The main class for accessing the LibLouis API.</p>
 * <p>The exposed public methods are synchronized as liblouis itself is not thread save
 * 
 * @author Michael Whapples
 *
 */
public class Louis {
	/**
	 * Constants for liblouis typeforms.
	 * 
	 * <p>
	 * Liblouis takes bytes to represent type forms, use these to find out the
	 * constant values. You may also find the liblouis documentation useful.
	 * </p>
	 * 
	 * 
	 */
	public static interface TypeForms {
		public static final short PLAIN_TEXT = 0x0000;
		public static final short ITALIC = 0x0001;
		public static final short UNDERLINE = 0x0002;
		public static final short BOLD = 0x0004;
		public static final short COMPUTER_BRAILLE = 0x0008;
		public static final short PASSAGE_BREAK = 0x0010;
		public static final short WORD_RESET = 0x0020;
		public static final short SCRIPT = 0x0040;
		public static final short TRANS_NOTE = 0x0080;
		public static final short TRANS_1 = 0X0100;
		public static final short TRANS_2 = 0X0200;
		public static final short TRANS_3 = 0X0400;
		public static final short TRANS_4 = 0X0800;
		public static final short TRANS_5 = 0X1000;
		public static final short NO_CONTRACT = 0x2000;
	}

	/**
	 * Constants for liblouis translation mode.
	 * 
	 * <p>
	 * Use these constants to inform liblouis of the translation mode to be
	 * used. Refer to the liblouis documentation to find out the definitions.
	 * </p>
	 */
	public static interface TranslationModes {
		public static final int NO_CONTRACTIONS = 1;
		public static final int COMPBRL_AT_CURSOR = 2;
		public static final int DOTS_IO = 4;
		public static final int COMP8DOTS = 8;
		public static final int PASS1_ONLY = 16;
		public static final int COMPBRL_LEFT_CURSOR = 32;
		public static final int OTHER_TRANS = 64;
		public static final int UC_BRL = 128;
		public static final int NO_TERMINATOR = 256;
	}

	private int outRatio;
	private static LogCallback callback;
	private static int encodingSize;
	private static Properties libConfig;
	private static Logger logger = LoggerFactory.getLogger(Louis.class);
	/**
	 * Note: All calls to the native lou_ methods must synchronize on this
	 * as liblouis is not thread safe
	 */
	private static final Object THREAD_SAFE_LOCK = new Object();
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

	private static TableResolver tr = null;
	/**
	 * Create a object for accessing LibLouis.
	 */
	public Louis() {
		outRatio = 4 + (Louis.encodingSize * 2);
	}
	
	/**
	 * <p>Get the LibLouis version.</p>
	 * @return The LibLouis version.
	 */
	public String getVersion() {
		synchronized (THREAD_SAFE_LOCK) {
			return Louis.lou_version();
		}
	}

	/**
	 * <p>Get the size of widechar.</p>
	 * 
	 * @return The size of the widechar definition.
	 */
	public int getEncodingSize() {
		synchronized (THREAD_SAFE_LOCK) {
			return Louis.lou_charSize();
		}
	}

	/**
	 * Simple translation of a string into Braille.
	 * 
	 * <p>This method translates the string in inbuf to Braille using the lou_translateString function in LibLouis.</p>
	 * 
	 * @param tablesList The list of translation tables for LibLouis.
	 * @param inbuf The string to translate
	 * @param mode The mode for LibLouis translation.
	 * @return A string containing the Braille translation.
	 * @throws TranslationException When LibLouis cannot perform the translation.
	 */
	public String translateString(String tablesList, String inbuf, int mode) throws TranslationException {
		return translateString(tablesList, inbuf, null, mode);
	}
	/**
	 * Simple translation of a string into Braille.
	 * 
	 * <p>This method translates the string in inbuf to Braille using the lou_translateString function in LibLouis.</p>
	 * 
	 * @param tablesList The list of translation tables for LibLouis.
	 * @param inbuf The string to translate
	 * @param typeforms The typeforms relating to the string. Pass in null if no typeforms apply.
	 * @param mode The mode for LibLouis translation.
	 * @return A string containing the Braille translation.
	 * @throws TranslationException When LibLouis cannot perform the translation.
	 */
	public String translateString(String tablesList, String inbuf,
			short[] typeforms, int mode) throws TranslationException {
		if ((inbuf == null) || (inbuf.isEmpty())) {
			return "";
		}
		synchronized (THREAD_SAFE_LOCK) {
			byte[] spacing = null;
			int inlen = inbuf.length();
			Louis.WideChar wInbuf = new Louis.WideChar(inbuf);
			int outlen = outRatio * inlen;
			short[] typeformsCopy = null;
			if (typeforms != null) {
				typeformsCopy = Arrays.copyOf(typeforms, outlen);
			}
			Louis.WideChar wOutbuf = new Louis.WideChar(outlen);
			IntByReference poutlen = new IntByReference(outlen);
			if (Louis.lou_translateString(tablesList, wInbuf,
					new IntByReference(inlen), wOutbuf, poutlen, typeformsCopy,
					spacing, mode) == 0) {
				throw new TranslationException("Unable to complete translation");
			}
			return wOutbuf.getText(poutlen.getValue());
		}
	}

	public TranslationResult translate(String trantab, String inbuf,
			int cursorPos, int mode) throws TranslationException {
		short[] typeForms = null;
		return translate(trantab, inbuf, typeForms, cursorPos, mode);
	}

	public TranslationResult translate(String trantab, String inbuf,
			short[] typeForms, int cursorPos, int mode)
			throws TranslationException {
		synchronized (THREAD_SAFE_LOCK) {
			if ((inbuf == null) || (inbuf.isEmpty())) {
				return new TranslationResult("", new int[0], new int[0], 0);
			}
			byte[] spacing = null;
			Louis.WideChar wInbuf = new Louis.WideChar(inbuf);
			int inlen = wInbuf.length();
			int outlen = inlen * outRatio;
			short[] typeformsCopy = null;
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
	}

	public String backTranslateString(String trantab, String inbuf,
			short[] typeforms, int mode) throws TranslationException {
		synchronized (THREAD_SAFE_LOCK) {
			byte[] spacing = null;
			Louis.WideChar wInbuf = new Louis.WideChar(inbuf);
			int inlen = wInbuf.length();
			int outlen = outRatio * inlen;
			Louis.WideChar wOutbuf = new Louis.WideChar(outlen);
			short[] typeformsCopy = null;
			if (typeforms != null) {
				typeformsCopy = Arrays.copyOf(typeforms, outlen);
			}
			IntByReference poutlen = new IntByReference(outlen);
			if (Louis.lou_backTranslateString(trantab, wInbuf, new IntByReference(
					inlen), wOutbuf, poutlen, typeformsCopy, spacing, mode) == 0) {
				throw new TranslationException("Unable to complete translation");
			}
			return wOutbuf.getText(poutlen.getValue());
		}
	}

	public TranslationResult backTranslate(String trantab, String inbuf,
			int cursorPos, int mode) throws TranslationException {
		short[] typeForms = null;
		return backTranslate(trantab, inbuf, typeForms, cursorPos, mode);
	}

	public TranslationResult backTranslate(String trantab, String inbuf,
			short[] typeForms, int cursorPos, int mode)
			throws TranslationException {
		synchronized (THREAD_SAFE_LOCK) {
			byte[] spacing = null;
			Louis.WideChar wInbuf = new Louis.WideChar(inbuf);
			int inlen = wInbuf.length();
			int outlen = inlen * outRatio;
			short[] typeformsCopy = null;
			if (typeForms != null) {
				typeformsCopy = Arrays.copyOf(typeForms, outlen);
			}
			Louis.WideChar wOutbuf = new Louis.WideChar(outlen);
			IntByReference poutlen = new IntByReference(outlen);
			IntByReference pcursorPos = new IntByReference(cursorPos);
			int[] outPos = new int[inlen];
			int[] inPos = new int[outlen];
			if (Louis.lou_backTranslate(trantab, wInbuf, new IntByReference(inlen),
					wOutbuf, poutlen, typeformsCopy, spacing, outPos, inPos,
					pcursorPos, mode) == 0) {
				throw new TranslationException("Unable to complete translation");
			}
			String outbuf = wOutbuf.getText(poutlen.getValue());
			return new TranslationResult(outbuf, outPos, inPos,
					pcursorPos.getValue());
		}
	}

	public byte[] hyphenate(String trantab, String inbuf, int mode)
			throws TranslationException {
		synchronized (THREAD_SAFE_LOCK) {
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
	}

	public void close() {
		synchronized (THREAD_SAFE_LOCK) {
			Louis.lou_free();
		}
	}
	
	/**
	 * Set the log level for the LibLouis callback. This only sets the level for when the logging callback will be called by LibLouis, it does not set any other logging levels.
	 * 
	 * @param level The level at which the logging callback will be called by LibLouis.
	 */
	public void setLogLevel(int level) {
		synchronized (THREAD_SAFE_LOCK) {
			lou_setLogLevel(level);
		}
	}
	/**
	 * Set the data path used by LibLouis. Tables should be inside a directory liblouis/tables inside the path given by this function call.
	 * 
	 * @param path The data path for LibLouis.
	 */
	public void setDataPath(String path) {
		synchronized (THREAD_SAFE_LOCK) {
			lou_setDataPath(path);
		}
	}
	
	/**
	 * Get the data path of LibLouis.
	 * 
	 * @return The data path currently used by LibLouis.
	 */
	public String getDataPath() {
		synchronized (THREAD_SAFE_LOCK) {
			return lou_getDataPath();
		}
	}
	
	public static void registerTableResolver(TableResolver resolver) {
		tr = resolver;
		synchronized (THREAD_SAFE_LOCK) {
			lou_registerTableResolver(tr);
		}
	}
	/**
	 * Register a callback for logging. If you give a null value for the callback object then the default logging callback will be used.
	 * 
	 * @param cb The callback.
	 */
	public static void registerLogCallback(LogCallback cb) {
		// Remember that we should hold a reference to the callback object to ensure it remains valid for LibLouis.
		if (cb == null) {
			callback = new DefaultLogCallback();
		} else {
			callback = cb;
		}
		synchronized (THREAD_SAFE_LOCK) {
			lou_registerLogCallback(callback);
		}
	}

	public void dotsToChar(
		String trantab,
		Louis.WideChar inbuf, Louis.WideChar outbuf,
		int length, int mode)
	{
		synchronized (THREAD_SAFE_LOCK) {
			lou_dotsToChar(trantab, inbuf, outbuf, length, mode);
		}
	}

	public void charToDots(
		String trantab,
		Louis.WideChar inbuf, Louis.WideChar outbuf,
		int length, int mode)
	{
		synchronized (THREAD_SAFE_LOCK) {
			lou_charToDots(trantab, inbuf, outbuf, length, mode);
		}
	}

	// Initialise this as a native library for JNA
	static {
		int platform = Platform.getOSType();
		String libName;
		switch (platform) {
		case Platform.FREEBSD:
			libName = libConfig.getProperty("jlouis.library.name.freebsd",
					"liblouis.so");
			break;
		case Platform.LINUX:
			libName = libConfig.getProperty("jlouis.library.name.linux",
					"liblouis.so");
			break;
		case Platform.MAC:
			libName = libConfig.getProperty("jlouis.library.name.mac", "liblouis.dylib");
			break;
		case Platform.OPENBSD:
			libName = libConfig.getProperty("jlouis.library.name.openbsd",
					"liblouis.so");
			break;
		case Platform.SOLARIS:
			libName = libConfig.getProperty("jlouis.library.name.solaris",
					"liblouis.so");
			break;
		case Platform.WINDOWS:
			libName = libConfig.getProperty("jlouis.library.name.windows",
					"liblouis.dll");
			break;
		case Platform.WINDOWSCE:
			libName = libConfig.getProperty("jlouis.library.name.windowsce",
					"liblouis.dll");
			break;
		default:
			libName = "liblouis.so";
			break;
		}
		String libPath = System.getProperty("jlouis.library.path",
				libConfig.getProperty("jlouis.library.path"));
		File libFile;
		if (libPath != null && (!libPath.isEmpty())) {
			libFile = new File(libPath, libName);
		} else {
			try {
				libFile = Native.extractFromResourcePath("/" + Platform.RESOURCE_PREFIX + "/" + libName);
				logger.info("libFile extract:  " + libFile);
			} catch (IOException e) {
				String errorMsg = "There is no bundled binary for your platform: " + Platform.RESOURCE_PREFIX + "/" + libName;
				logger.error(errorMsg);
				throw new RuntimeException(errorMsg);
			}
		}
		if (!(libFile.exists() && libFile.isFile())) {
			throw new UnsatisfiedLinkError(String.format("Library \"%s\" does not exist", libFile.getAbsolutePath()));
		}
		Map<String, Object> options = new HashMap<>();
		options.put(Library.OPTION_TYPE_MAPPER, new LouisTypeMapper());
		NativeLibrary	nativeLib = NativeLibrary.getInstance(libFile.getAbsolutePath(), options);
		Native.register(nativeLib);
		Louis.encodingSize = Louis.lou_charSize();
		callback = new DefaultLogCallback();
		lou_registerLogCallback(callback);
		if (System.getProperty("jlouis.data.path") != null) {
			lou_setDataPath(System.getProperty("jlouis.data.path"));
		}
		registerTableResolver(new JarResolver());
	}

	public static class WideChar implements NativeMapped {
		private static String encoding;
		private Memory buffer;
		private int length;

		private void setEncoding() {
			if (Louis.WideChar.encoding != null)
				return;
			if (Louis.encodingSize == 2) {
				Louis.WideChar.encoding = "utf-16le";
			} else if (Louis.encodingSize == 4) {
				Louis.WideChar.encoding = "utf-32le";
			}
		}

		public WideChar(int length) {
			this.setEncoding();
			if (Louis.encodingSize == 0)
				return;
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
				this.buffer.write(0, text.getBytes(encoding), 0,
						Louis.encodingSize * text.length());
			} catch (UnsupportedEncodingException e) {

			}
		}

		public String getText(int length) {
			int bufferLength = length * Louis.encodingSize;
			try {
				return new String(buffer.getByteArray(0, bufferLength), 0,
						bufferLength, encoding);
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
			byte[] nativeBytes = (byte[]) nativeValue;
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
	private static native int lou_backTranslate(String trantab,
			Louis.WideChar inbuf, IntByReference inlen, Louis.WideChar outbuf,
			IntByReference outlen, short[] typeform, byte[] spacing,
			int[] outpos, int[] inpos, IntByReference cursorpos, int mode);

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
			IntByReference outlen, short[] typeform, byte[] spacing, int mode);

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
	private static native int lou_hyphenate(String trantab,
			Louis.WideChar inbuf, int inlen, byte[] hyphens, int mode);


	/**
	 * The lou_translate function.
	 * 
	 * The same information about the useage of the @see lou_translateString
	 * method applies to this method. This method takes the additional
	 * parameters stated in the liblouis documentation.
	 */
	private static native int lou_translate(String trantab,
			Louis.WideChar inbuf, IntByReference inlen, Louis.WideChar outbuf,
			IntByReference outlen, short[] typeform, byte[] spacing,
			int[] outpos, int[] inpos, IntByReference cursorpos, int mode);

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
	 *            {@link org.mwhapples.jlouis.Louis.TypeForms}.
	 * @param spacing
	 *            A byte array used to represent spacing.
	 * @param mode
	 *            This indicates how the translation should be done. Use values
	 *            from
	 *            {@link org.mwhapples.jlouis.Louis.TranslationModes}.
	 */
	private static native int lou_translateString(String trantab,
			Louis.WideChar inbuf, IntByReference inlen, Louis.WideChar outbuf,
			IntByReference outlen, short[] typeform, byte[] spacing, int mode);

	/**
	 * The lou_version function.
	 * 
	 * This method returns a string giving the version of liblouis.
	 */
	private static native String lou_version();
	
	/**
	 * Set the data path for LibLouis. Tables should be in a liblouis/tables directory inside this path.
	 * 
	 * @param path The data path to use.
	 */
	private static native void lou_setDataPath(String path);
	
	/**
	 * Get the current data path of LibLouis.
	 * 
	 * @return The current data path used by LibLouis.
	 */
	private static native String lou_getDataPath();
	
	/**
	 * 
	 * @param resolver
	 */
	private static native void lou_registerTableResolver(TableResolver resolver);
	/**
	 * Set a callback for LibLouis logging.
	 * 
	 * @param cb The callback object.
	 */
	private static native void lou_registerLogCallback(LogCallback cb);
	
	/**
	 * Set the log level for liblouis. This only sets the level at which the logging callback will be called, any further processing done by the callback is independent of this.
	 * 
	 * @param level The level for the logging callback to be set to.
	 */
	private static native void lou_setLogLevel(int level);

	/**
	 *
	 */
	private static native void lou_dotsToChar(
		String trantab,
		Louis.WideChar inbuf, Louis.WideChar outbuf,
		int length, int mode);

	/**
	 *
	 */
	private static native void lou_charToDots(
		String trantab,
		Louis.WideChar inbuf, Louis.WideChar outbuf,
		int length, int mode);

}
