package org.mwhapples.jlouis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.common.io.Resources;

public class JarResolver implements TableResolver {
	private static final Logger log = LoggerFactory.getLogger(JarResolver.class);
	private static volatile File TABLE_TEMP_DIR = null;
	public static File getTempTableDir() {
		File result = TABLE_TEMP_DIR;
		if (result == null) {
			synchronized (JarResolver.class) {
				result = TABLE_TEMP_DIR;
				if (result == null) {
					result = Files.createTempDir();
					TABLE_TEMP_DIR = result;
				}
			}
		}
		return result;
	}
	private final static List<String> EXTRACTED_FILES = new ArrayList<>();
	
	@Override
	public File[] resolveTable(String tableList, String base) {
		// Tables should be comma separated like in LibLouis
		String[] tables = tableList.split(",");
		List<File> files = new ArrayList<>();
		// NOTE: getDataPath() is getting from the DLL and is not instance specific, really should be static
		String louisDataPath = new Louis().getDataPath();
		for (int i = 0; i < tables.length; i++) {
			// First check if the file is absolute
			File file = new File(tables[i]);
			if (file.isAbsolute() && file.isFile()) {
				files.add(file);
				continue;
			}
			// Now see if the file is in the data path
			file = new File(louisDataPath, tables[i]);
			if (file.isFile()) {
				files.add(file);
				continue;
			}
			// As a fallback try and load from the classpath
			File tmpDir = getTempTableDir();
			File tmpFile = new File(tmpDir, tables[i]);
			String tmpFilePath = tmpFile.getAbsolutePath();
			if (EXTRACTED_FILES.contains(tmpFilePath)) {
				files.add(tmpFile);
				continue;
			}
			URL resourceTable = getClass().getResource("/org/mwhapples/jlouis/tables/" + tables[i]);
			try (OutputStream os = new FileOutputStream(tmpFile)) {
				Resources.copy(resourceTable, os);
			} catch (FileNotFoundException e) {
				log.warn("Unable to create file when extracting table", e);
				continue;
			} catch (IOException e) {
				log.warn("Unable to extract table from jar", e);
				continue;
			}
			files.add(tmpFile);
			EXTRACTED_FILES.add(tmpFilePath);
		}
		return files.toArray(new File[0]);
	}

}
