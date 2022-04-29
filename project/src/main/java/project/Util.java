package project;

import java.util.Date;

/**
 * Helper class for printing debug statements.
 */
public class Util {

	/**
	 * Set to true to print debug messages.
	 */
	public static boolean DEBUG = false;

	public static boolean LOG = true;

	public static boolean ERROR = false;

	/**
	 * Get the name of the current thread.
	 * 
	 * @return the current thread name
	 */
	public static String getThreadName() {
		return Thread.currentThread().getName();
	}

	/**
	 * Method to prepend to every print to the console. Displays what thread is
	 * currently printing.
	 * 
	 * @return the string for the thread name
	 */
	public static String getThread() {
		return "[THREAD=" + Util.getThreadName() + "] ";
	}

	/**
	 * Returns a string object used for logging.
	 * 
	 * @param date a date object used for getting information
	 * @return the string of the hours,minutes, and seconds
	 */
	@SuppressWarnings("deprecation")
	public static String dateToString(Date date) {
		return "" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
	}

	/**
	 * Print log message. These have their thread pre-pended and are always printed.
	 * 
	 * @param msg the message to log
	 */
	public static void log(String msg) {
		if (Util.LOG) {
			System.out.println(Util.getTime() + "[LOG]" + Util.getThread() + msg);
		}
	}

	/**
	 * Print debug message. These have their thread pre-pended and are only printed
	 * if Util.DEBUG is set to true.
	 * 
	 * @param msg the message to add to debug
	 */
	public static void debug(String msg) {
		if (Util.DEBUG) {
			System.out.println(Util.getTime() + "[DEBUG]" + Util.getThread() + msg);
		}
	}

	/**
	 * Method to return the current time when the log was printed. Used for tracking
	 * code execution.
	 * 
	 * @return the current time
	 */
	public static String getTime() {
		return "[" + new Date().toString() + "]";
	}

	/**
	 * Method to print INFO level messages. Logs of this level will always print to
	 * the console.
	 * 
	 * @param msg the message to add to info
	 */
	public static void info(String msg) {
		System.out.println(Util.getTime() + "[INFO]" + Util.getThread() + msg);
	}

	/**
	 * Method to print ERROR level messages. Logs of this level will only be printed
	 * if the ERROR boolean is set to True.
	 * 
	 * @param msg the message to add to error
	 */
	public static void error(String msg) {
		if (Util.ERROR) {
			System.out.println(Util.getTime() + "[ERROR]" + Util.getThread() + msg);
		}
	}
}
