package ru.artroman.videotest.utils;

public class Log {

	private final static String TAG = "TestVideo";

	public static void v(String message, Object... args) {
		if (args != null && args.length > 0) message = String.format(message, args);
		android.util.Log.v(TAG, message);
	}

	public static void d(String message, Object... args) {
		if (args != null && args.length > 0) message = String.format(message, args);
		android.util.Log.d(TAG, message);
	}

	public static void i(String message, Object... args) {
		if (args != null && args.length > 0) message = String.format(message, args);
		android.util.Log.i(TAG, message);
	}

	public static void w(String message, Object... args) {
		if (args != null && args.length > 0) message = String.format(message, args);
		android.util.Log.w(TAG, message);
	}

	public static void e(String message, Object... args) {
		if (args != null && args.length > 0) message = String.format(message, args);
		android.util.Log.e(TAG, message);
	}
}
