package com.p3.archon.utils;

import java.io.File;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class JobLogger {

	private static JobLogger JobLogger = new JobLogger();

	private static Logger logger = null;

	/**
	 * This static block will be executed the first time JobLogger is
	 * constructed/accessed in any way. This block simply calls the setupLogger
	 * helper method to perform the actual init of logging.
	 */
	static {
		setupLogger();
	}

	/**
	 * Returns a static instance of this class the application can use to access the
	 * log methods.
	 */
	public static JobLogger getLogger() {
		return JobLogger;
	}

	/**
	 * This method performs the initialization of the logging facility. A daily
	 * rolling log file scheme is used. The following are configurable in the
	 * droc.properties file: JobLogger.LogFile - Full path and file name of log
	 * file. Date and time will be appended automatically on roll-over.
	 * JobLogger.LogRollingInterval - A log4j interval pattern such as '.'yyyy-MM-dd
	 * See log4j for more details. JobLogger.LogLevel - The level of which logging
	 * should be performed at. JobLogger.LogInstanceName - A unique name used
	 * internally by log4j for this instance of the logger.
	 */
	@SuppressWarnings({ "static-access" })
	public static void setupLogger() {
		String JobLoggerErrorLogFile = null;
		String JobLoggerErrorLogRollingInterval = null;
		String JobLoggerErrorLogLevel = null;
		String JobLoggerErrorLogName = null;
		try {
			JobLoggerErrorLogLevel = "ALL";
			JobLoggerErrorLogFile = "XML2SipGenLogs" + File.separator + "log_" + UUID.randomUUID().toString() + ".txt";
			JobLoggerErrorLogRollingInterval = "'.'yyyy-MM-dd";
			JobLoggerErrorLogName = "Oracle9i2XMLJobLogger";
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		JobLogger.logger = LogManager.getLogger(JobLoggerErrorLogName);
		File f = new File(JobLoggerErrorLogFile);
		LoggerContext lc = (LoggerContext) LogManager.getContext(false);
		@SuppressWarnings("deprecation")
		RollingFileAppender rl = RollingFileAppender.newBuilder().withFileName(JobLoggerErrorLogFile)
				.withFilePattern(f.getAbsolutePath() + "%d{" + JobLoggerErrorLogRollingInterval + "}.zip")
				.withAppend(true).withName("rollingFile").withBufferedIo(true)
				.withPolicy(TimeBasedTriggeringPolicy.newBuilder().withInterval(1).withModulate(true).build())
				.withStrategy(DefaultRolloverStrategy.newBuilder().withMax("10").build())
				.withLayout(PatternLayout.newBuilder().withPattern("%d{yyyy/MM/dd:HH:mm:ss,SSS}|%-7p%m%n").build())
				.build();

		rl.start();
//		boolean addDrocAppender = true;
//		Map<String, Appender> appenderMap = ((org.apache.logging.log4j.core.Logger) JobLogger.logger).getAppenders();
//		for (Appender appender : appenderMap.values()) {
//			FileAppender fileAppender = (FileAppender) appender;
//			if ((fileAppender.getFileName() != null) && (fileAppender.getFileName().equals(JobLoggerErrorLogFile))) {
//				addDrocAppender = false;
//			}
//
//		}
//
//
//		if (addDrocAppender) {
		lc.getConfiguration().addAppender(rl);
		lc.getRootLogger().addAppender(lc.getConfiguration().getAppender(rl.getName()));
		lc.updateLoggers();
//		}

		if (JobLoggerErrorLogLevel.equalsIgnoreCase("DEBUG"))
			JobLogger.logger.atLevel(Level.DEBUG);
		else if (JobLoggerErrorLogLevel.equalsIgnoreCase("INFO"))
			JobLogger.logger.atLevel(Level.INFO);
		else if (JobLoggerErrorLogLevel.equalsIgnoreCase("WARN"))
			JobLogger.logger.atLevel(Level.WARN);
		else if (JobLoggerErrorLogLevel.equalsIgnoreCase("OFF"))
			JobLogger.logger.atLevel(Level.OFF);
		else if (JobLoggerErrorLogLevel.equalsIgnoreCase("ERROR"))
			JobLogger.logger.atLevel(Level.ERROR);
		else if (JobLoggerErrorLogLevel.equalsIgnoreCase("ALL"))
			JobLogger.logger.atLevel(Level.ALL);
		else if (JobLoggerErrorLogLevel.equalsIgnoreCase("FATAL"))
			JobLogger.logger.atLevel(Level.FATAL);
		//
		// JobLogger.logger.info(
		// "-----------------------------" + new Date().toLocaleString() +
		// "------------------------------------");
		// JobLogger.logger.info("Logging initialized...");
	}

	/**
	 * Logs a line at the DEBUG level.
	 */
	public void debug(String userId, String cls, String mtd, String msg) {
		if (logger.getLevel() != Level.ALL)
			logger.debug(formatMessage(userId, cls, mtd, msg, true, true));
		else
			formatMessage(userId, cls, mtd, msg, true, false);
	}

	/**
	 * Logs a line at the INFO level.
	 */
	public void info(String userId, String cls, String mtd, String msg) {
		if (logger.getLevel() != Level.ALL)
			logger.info(formatMessage(userId, cls, mtd, msg, true, true));
		else
			formatMessage(userId, cls, mtd, msg, true, false);
	}

	/**
	 * Logs a line at the WARN level.
	 */
	public void warn(String userId, String cls, String mtd, String msg) {
		if (logger.getLevel() != Level.ALL)
			logger.warn(formatMessage(userId, cls, mtd, msg, true, true));
		else
			formatMessage(userId, cls, mtd, msg, true, false);
	}

	/**
	 * Logs a line at the ERROR level.
	 */
	public void error(String userId, String cls, String mtd, String msg) {
		if (logger.getLevel() != Level.ALL)
			logger.error(formatMessage(userId, cls, mtd, msg, false, true));
		else
			formatMessage(userId, cls, mtd, msg, true, false);
	}

	/**
	 * Logs a line at the FATAL level.
	 */
	public void fatal(String userId, String cls, String mtd, String msg) {
		if (logger.getLevel() != Level.ALL)
			logger.fatal(formatMessage(userId, cls, mtd, msg, false, true));
		else
			formatMessage(userId, cls, mtd, msg, true, false);
	}

	/**
	 * Takes the data from the log methods into standard format.
	 */
	private String formatMessage(String userId, String cls, String mtd, String msg, boolean info,
			boolean returnForlog) {
		if (info)
			System.out.println(msg);
		else
			System.err.println(msg);

		return returnForlog ? (" | " + padding(userId, 20) + padding(cls + "." + mtd, 70) + msg) : "";
	}

	public static String padding(String mtd, int length) {
		if (mtd == null)
			return padRight("", length);
		return padRight(mtd, length);
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public static String padLeft(String s, int n) {
		return String.format("%1$" + n + "s", s);
	}

	/**
	 * Closes down log4j.
	 */
	public void close() {
		LogManager.shutdown();
	}

}
