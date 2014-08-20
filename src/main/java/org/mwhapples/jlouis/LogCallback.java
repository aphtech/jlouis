package org.mwhapples.jlouis;

import com.sun.jna.Callback;

public interface LogCallback extends Callback {
	public void logMessage(int level, String msg);
}
