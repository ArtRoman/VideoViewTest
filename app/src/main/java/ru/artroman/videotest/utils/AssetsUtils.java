package ru.artroman.videotest.utils;

import android.content.Context;
import android.content.res.Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetsUtils {

	private final static String VIDEO_FILE_NAME = "sample_mpeg4.mp4";
	private final static int BUFFER_SIZE = 1024 * 1024; // 1MB

	public static File getExternalVideoFile(Context context) {
		File externalFilesDir = getStorageFolder(context);
		File externalVideoFile = new File(externalFilesDir, VIDEO_FILE_NAME);
		AssetsUtils.copyFileFromAssets(context, VIDEO_FILE_NAME, externalVideoFile);
		return externalVideoFile;
	}

	private static File getStorageFolder(Context context) {
		// Get external storage folder
		File storageFolder = context.getExternalFilesDir(null);
		if (storageFolder == null) {
			// If failed, get internal storage folder
			storageFolder = context.getFilesDir();
		}
		return storageFolder;
	}

	public static void copyFileFromAssets(Context context, String fileName, File outputFile) {
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesCount;
		try {
			InputStream imageStream = context.getAssets().open(fileName);
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			while ((bytesCount = imageStream.read(buffer)) >= 0) {
				fileOutputStream.write(buffer, 0, bytesCount);
			}
			fileOutputStream.close();
			imageStream.close();
		} catch (IOException | Resources.NotFoundException e) {
			e.printStackTrace();
		}
	}
}
