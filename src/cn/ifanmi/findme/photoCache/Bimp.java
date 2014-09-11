package cn.ifanmi.findme.photoCache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bimp {
	public static int cur = 0;
	public static List<Bitmap> bmp = new ArrayList<Bitmap>();	
	//图片sd地址  上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
	public static List<String> drr = new ArrayList<String>();
	
	public static int MAX;
	
	public static void create(int max) {
		cur = 0;
		bmp.clear();
		drr.clear();
		MAX = max;
	}
	
	public static void destroy() {
		cur = 0;
		bmp.clear();
		drr.clear();
		MAX = 0;
	}
	
	public static Bitmap revitionImageSize(String path) {
		Bitmap bitmap = null;
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
			/**
			 * 如果我们把它设为true，
			 * 那么BitmapFactory.decodeFile(String path, Options opt)并不会真的返回一个Bitmap给你，
			 * 它仅仅会把它的宽，高取回来给你，这样就不会占用太多的内存，也就不会那么频繁的发生OOM了。
			 * http://blog.sina.com.cn/s/blog_7035ce0901017ljl.html
			 */
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, options);
			in.close();
			int i = 0;
			while (true) {
				if ((options.outWidth >> i <= 1000)
						&& (options.outHeight >> i <= 1000)) {
					in = new BufferedInputStream(
							new FileInputStream(new File(path)));
					options.inSampleSize = (int) Math.pow(2.0D, i);
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(in, null, options);
					break;
				}
				i += 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * 不压缩图片，原图
	 * @param path
	 * @return
	 */
	public static Bitmap getOriginalImage(String path) {
		Bitmap bitmap = null;
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
}
