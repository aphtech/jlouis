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
package org.mwhapples.jlouis.testutils;

import javax.xml.bind.annotation.XmlList;

/**
 * Data for tests of the translate method.
 * 
 * @author Michael Whapples
 *
 */
public class TranslateData {
	private String tables = "en-us-g2.ctb";
	public String getTables() {
		return tables;
	}
	public void setTables(String tables) {
		this.tables = tables;
	}
	private String inputStr;
	private String brlStr;
	private short[] typeForms = null;
	private int cursor;
	private int mode;
	private int[] outputPos;
	private int[] inputPos;
	private boolean enabled = true;
	public String getInputStr() {
		return inputStr;
	}
	public void setInputStr(String inputStr) {
		this.inputStr = inputStr;
	}
	public String getBrlStr() {
		return brlStr;
	}
	public void setBrlStr(String brlStr) {
		this.brlStr = brlStr;
	}
	@XmlList
	public short[] getTypeForms() {
		return typeForms;
	}
	public void setTypeForms(short[] typeForms) {
		this.typeForms = typeForms;
	}
	public int getCursor() {
		return cursor;
	}
	public void setCursor(int cursor) {
		this.cursor = cursor;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	@XmlList
	public int[] getOutputPos() {
		return outputPos;
	}
	public void setOutputPos(int[] outputPos) {
		this.outputPos = outputPos;
	}
	@XmlList
	public int[] getInputPos() {
		return inputPos;
	}
	public void setInputPos(int[] inputPos) {
		this.inputPos = inputPos;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
