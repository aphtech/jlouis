package org.mwhapples.jlouis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLogCallback implements LogCallback {
	private static Logger logger = LoggerFactory.getLogger(DefaultLogCallback.class);

	public void logMessage(int level, String msg) {
		if (level <= LogLevels.DEBUG) {
			logger.debug(msg);
		} else if (level <= LogLevels.INFO) {
			logger.info(msg);
		} else if (level <= LogLevels.WARNING) {
			logger.warn(msg);
		} else {
			logger.error(msg);
		}
		System.out.println("Logging message: " + msg);
	}

}
