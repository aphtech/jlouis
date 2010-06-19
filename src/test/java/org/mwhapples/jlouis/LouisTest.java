package org.mwhapples.jlouis;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import org.mwhapples.jlouis.Louis;

public class LouisTest {
    Louis translator;
    @Before
    public void setupTranslator() {
        translator = new Louis();
    }
    @After
    public void cleanUpTranslator() {
        translator.close();
    }
    @Test
    public void translateStringValidString() throws Exception {
        String expected = ",hello _w";
        String actual = null;
        byte[] typeforms = null;
        actual = translator.translateString("en-us-g2.ctb", "Hello world", typeforms, 0);
        assertEquals(expected, actual);
    }
    @Test
    public void backTranslateStringValidString() throws Exception {
        String expected = "Hello world";
        byte[] typeforms = null;
        String actual = translator.backTranslateString("en-us-g2.ctb", ",hello world", typeforms, 0);
        assertEquals(expected, actual);
    }
}
