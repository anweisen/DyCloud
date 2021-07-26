package org.slf4j.impl;

import net.anweisen.utilities.common.logging.ILogger;
import net.anweisen.utilities.common.logging.internal.Slf4jILoggerWrapper;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class OurLoggerFactory implements ILoggerFactory {

	@Override
	public Logger getLogger(String name) {
		return new Slf4jILoggerWrapper(ILogger.forName(name));
	}

}
