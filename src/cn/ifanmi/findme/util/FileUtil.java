package cn.ifanmi.findme.util;

import java.io.File;
import android.text.TextUtils;

public class FileUtil {
	
	public static void deletePhoto(String pathName) {
		File dir = new File(pathName);
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {  
            files[i].delete();
        }
	}

	public static void deleteFolderFile(String filePath) {
		if (!TextUtils.isEmpty(filePath)) {  
            File file = new File(filePath);  
            if (file.isDirectory()) {// 处理目录  
                File files[] = file.listFiles();  
                for (int i = 0; i < files.length; i++) {  
                    deleteFolderFile(files[i].getAbsolutePath());  
                }
            }   
            if (!file.isDirectory()) {// 如果是文件，删除  
                file.delete();  
            } else {// 目录  
                if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除  
                    file.delete();  
                }  
            }  
        }  
	}
	
}
