package org.mwhapples.jlouis;

import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class JarResolver implements TableResolver {
    private static final Logger log = LoggerFactory.getLogger(JarResolver.class);
    private static volatile File TABLE_TEMP_DIR = null;

    public static @Nonnull File getTempTableDir() {
        if (TABLE_TEMP_DIR == null) {
            synchronized (JarResolver.class) {
                if (TABLE_TEMP_DIR == null) {
                    try {
                        TABLE_TEMP_DIR = Files.createTempDirectory(null).toFile();
                    } catch (IOException e) {
                        throw new IllegalStateException("Unable to create temp directory", e);
                    }
                }
            }
        }
        return TABLE_TEMP_DIR;
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
        for (String table : tables) {
            // First check if the file is absolute
            File file = new File(table);
            if (file.isAbsolute() && file.isFile()) {
                files.add(file);
                continue;
            }
            // Now check if the file is relative to the base
            if (base != null) {
                File baseFile = new File(base);
                file = new File(baseFile.getParent(), table);
                log.debug("Checking if table {} is relative to base, absolute path tested is {}", table, file.getAbsolutePath());
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
                File louisDataPath = new File(louisDataPaths[j]);
                file = new File(louisDataPath, table);
                if (!file.isFile()) {
                    // May be it uses the old inflexible LibLouis liblouis/tables/ subdirectory
                    file = new File(new File(louisDataPath, "liblouis/tables"), table);
                    if (!file.isFile()) {
                        file = null;
                    }
                }
                j++;
            }
            if (file != null) {
                log.debug("Table found on data path");
                files.add(file);
                continue;
            }
            // As a fallback try and load from the classpath
            log.debug("Attempting to load table {} from classpath", table);
            File tmpDir = getTempTableDir();
            File tmpFile = new File(tmpDir, table);
            String tmpFilePath = tmpFile.getAbsolutePath();
            if (EXTRACTED_FILES.contains(tmpFilePath)) {
                log.debug("Table previously extracted");
                files.add(tmpFile);
                continue;
            }
            URL resourceTable = getClass().getResource("/org/mwhapples/jlouis/tables/" + table);
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
                tmpFile.deleteOnExit();
                EXTRACTED_FILES.add(tmpFilePath);
                continue;
            }
            throw new RuntimeException(String.format("Table %s not found", table));
        }
        return files.toArray(new File[0]);
    }

}
