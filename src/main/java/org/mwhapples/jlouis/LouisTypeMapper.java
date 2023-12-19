package org.mwhapples.jlouis;

import java.io.File;
import java.io.IOException;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.StringArray;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;

public class LouisTypeMapper extends DefaultTypeMapper {

    public LouisTypeMapper() {
        ToNativeConverter fileArrayConverter = new ToNativeConverter() {
            @Override
            public Object toNative(Object value, ToNativeContext context) {
                if (value == null) {
                    return null;
                }
                if (value instanceof File[]) {
                    File[] files = (File[]) value;
                    String[] strings = new String[files.length];
                    for (int i = 0; i < files.length; i++) {
                        try {
                            strings[i] = files[i].getCanonicalPath();
                        } catch (IOException e) {
                            strings[i] = files[i].getAbsolutePath();
                        }
                    }
                    return new StringArray(strings);
                } else if (value instanceof String[]) {
                    return new StringArray((String[]) value);
                }
                return null;
            }

            @Override
            public Class<?> nativeType() {
                return StringArray.class;
            }
        };
        addToNativeConverter(File[].class, fileArrayConverter);
        addToNativeConverter(String[].class, fileArrayConverter);
    }

    public static final LouisTypeMapper INSTANCE = new LouisTypeMapper();
}
