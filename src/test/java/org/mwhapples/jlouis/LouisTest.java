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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

import org.mwhapples.jlouis.Louis;
import org.mwhapples.jlouis.testutils.TranslateData;
import org.mwhapples.jlouis.testutils.TranslateTests;
import org.mwhapples.jlouis.Louis.TypeForms;
import java.util.Arrays;


public class LouisTest {
	Louis translator;

	@BeforeClass
	public void setupTranslator() {
		translator = new Louis();
		String testDataPath = System.getProperty("jlouis.test.data.path", null);
		if (testDataPath != null) {
			translator.setDataPath(testDataPath);
		}
	}

	@AfterClass
	public void cleanUpTranslator() {
		translator.close();
	}

	@Test
	public void translateStringValidString() throws Exception {
		String expected = ",hello _w";
		String actual = null;
		short[] typeforms = null;
		actual = translator.translateString("en-ueb-g2.ctb", "Hello world",
				typeforms, 0);
		assertEquals(expected, actual);
	}

	@Test
	public void backTranslateStringValidString() throws Exception {
		String expected = "Hello world";
		short[] typeforms = null;
		String actual = translator.backTranslateString("en-ueb-g2.ctb",
				",hello world", typeforms, 0);
		assertEquals(expected, actual);
	}
	@DataProvider(name="translateProvider")
	public Iterator<Object[]> translateProvider() throws JAXBException {
		List<Object[]> dataList = new ArrayList<Object[]>();
		InputStream is = this.getClass().getResourceAsStream("/org/mwhapples/jlouis/translateTests.xml");
		JAXBContext jc = JAXBContext.newInstance(TranslateTests.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		TranslateTests tests = (TranslateTests)unmarshaller.unmarshal(is);
		for (TranslateData data: tests.getData()) {
			if (data.isEnabled()) {
				dataList.add(new Object[] {data.getTables(), data.getInputStr(), data.getTypeForms(), data.getCursor(), data.getMode(), data.getBrlStr(), data.getOutputPos(), data.getInputPos()});
			}
		}
		return dataList.iterator();
	}
	@Test(dataProvider="translateProvider")
	public void translate(String tableList, String inputStr, short[] typeForms, int cursor, int mode, String expectedBrl, int[] expectedOutpos, int[] expectedInpos) throws TranslationException {
		TranslationResult result = translator.translate(tableList, inputStr, typeForms, cursor, mode);
		
		assertThat(result.getTranslation()).isEqualTo(expectedBrl);
		if (expectedOutpos != null) {
			assertThat(result.getOutputPos()).isEqualTo(expectedOutpos);
		}
		if (expectedInpos != null) {
			 assertThat(result.getInputPos()).isEqualTo(expectedInpos);
		}
	}
    
    

	@Test
	public void translateStringBoldParan() throws Exception {
		String expected = "\"<~7abc abc defg~'\"> defg";
		String actual = null;
        
        // make the phrase bold
		short[] typeForms = new short[19];
        typeForms[0]=TypeForms.PLAIN_TEXT ;
        Arrays.fill(typeForms, 1, 13, TypeForms.BOLD);
        Arrays.fill(typeForms, 13, 19, TypeForms.PLAIN_TEXT );
		actual = translator.translateString("en-ueb-g2.ctb", "(abc abc defg) defg",
				typeForms, 0);
		assertEquals(expected, actual);
	}

    	@Test
	public void translateStringBoldBrackets() throws Exception {
		String expected = ".<~7abc abc defg~'.> defg";
		String actual = null;
		short[] typeForms = new short[19];
        typeForms[0]=TypeForms.PLAIN_TEXT ;
        Arrays.fill(typeForms, 1, 13, TypeForms.BOLD);
        Arrays.fill(typeForms, 13, 19, TypeForms.PLAIN_TEXT );
		actual = translator.translateString("en-ueb-g2.ctb", "[abc abc defg] defg",
				typeForms, 0);
		assertEquals(expected, actual);
	}

	@Test
	public void translateStringBoldBraces() throws Exception {
		String expected = "_<~7abc abc defg~'_> defg";
		String actual = null;
		short[] typeForms = new short[19];
        typeForms[0]=TypeForms.PLAIN_TEXT ;
        Arrays.fill(typeForms, 1, 13, TypeForms.BOLD);
        Arrays.fill(typeForms, 13, 19, TypeForms.PLAIN_TEXT );
		actual = translator.translateString("en-ueb-g2.ctb", "{abc abc defg} defg",
				typeForms, 0);
		assertEquals(expected, actual);
	}

    	@Test
	public void translateStringBoldAngle() throws Exception {
		String expected = "`<~7abc abc defg~'`> defg";
		String actual = null;
		short[] typeForms = new short[19];
        typeForms[0]=TypeForms.PLAIN_TEXT ;
        Arrays.fill(typeForms, 1, 13, TypeForms.BOLD);
        Arrays.fill(typeForms, 13, 19, TypeForms.PLAIN_TEXT );

		actual = translator.translateString("en-ueb-g2.ctb", "<abc abc defg> defg",
				typeForms, 0);
		assertEquals(expected, actual);
	}
    
    
    
    
    
}
