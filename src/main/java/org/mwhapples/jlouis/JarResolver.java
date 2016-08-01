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
		String dataPath = new Louis().getDataPath();
		String[] louisDataPaths;
		if (dataPath != null) {
			louisDataPaths = dataPath.split(",");
		} else {
			louisDataPaths = new String[0];
		}
		for (int i = 0; i < tables.length; i++) {
			// First check if the file is absolute
			File file = new File(tables[i]);
			if (file.isAbsolute() && file.isFile()) {
				files.add(file);
				continue;
			}
			// Now check if the file is relative to the base
			if (base != null) {
				File baseFile = new File(base);
				file = new File(baseFile.getParent(), tables[i]);
				log.debug("Checking if table {} is relative to base, absolute path tested is {}", tables[i], file.getAbsolutePath());
				if (file.isFile()) {
					log.debug("Table is relative");
					files.add(file);
					continue;
				}
			}
			// Now see if the file is in the data path
			file = null;
			int j = 0;
			log.debug("Checking for table on data path");
			while (j < louisDataPaths.length && file == null) {
				String louisDataPath = louisDataPaths[j];
				file = new File(louisDataPath, tables[i]);
				if (!file.isFile()) {
					file = null;
				}
				j++;
			}
			if (file != null) {
				log.debug("Table found on data path");
				files.add(file);
				continue;
			}
			// As a fallback try and load from the classpath
			log.debug("Attempting to load table {} from classpath", tables[i]);
			File tmpDir = getTempTableDir();
			File tmpFile = new File(tmpDir, tables[i]);
			String tmpFilePath = tmpFile.getAbsolutePath();
			if (EXTRACTED_FILES.contains(tmpFilePath)) {
				log.debug("Table previously extracted");
				files.add(tmpFile);
				continue;
			}
			URL resourceTable = getClass().getResource("/org/mwhapples/jlouis/tables/" + tables[i]);
			if (resourceTable != null) {
				try (OutputStream os = new FileOutputStream(tmpFile)) {
					log.debug("Extracting from classpath");
					Resources.copy(resourceTable, os);
				} catch (FileNotFoundException e) {
					log.warn("Unable to create file when extracting table", e);
					continue;
				} catch (IOException e) {
					log.warn("Unable to extract table from jar", e);
					continue;
				}
				log.debug("Extracted from classpath");
				files.add(tmpFile);
				EXTRACTED_FILES.add(tmpFilePath);
				continue;
			}
			throw new RuntimeException(String.format("Table %s not found", tables[i]));
		}
		return files.toArray(new File[0]);
	}

}
