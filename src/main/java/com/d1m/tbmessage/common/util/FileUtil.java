package com.d1m.tbmessage.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

	public static boolean writeFile(String filePath, byte[] content, boolean append) {//append是否文件末尾追加
		try {
			File file = new File(filePath);
			// if file not exists create new file
			if (!file.exists()) if (!file.createNewFile()) return false;
			FileOutputStream out = new FileOutputStream(file, append);
			out.write(content);
			out.close();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean createDirectory(String path) {
		File file = new File(path);
		return file.exists() || file.mkdirs();
	}

}
