package cn.ifanmi.findme.util;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.string.NormalString;

public class StringUtil {

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			str = str.replace(" ","+");
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	/**
	 * 
	 * @param photo
	 * @param maxLenght
	 * @return
	 */
	public static String getSmallPhoto(String photo, String maxLength) {
		return photo + "?imageView2/0/w/" + maxLength;
	}
	
	public static String getMD5String(String content) {
        try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(content.getBytes());
                return getHashString(digest);  
        } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
        }
        return null;
    }
        
    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }
    
    public static SpannableString spanExp(Context context, SpannableString spannableString, String content) {
		Pattern pattern = Pattern.compile(NormalString.Pattern.EXPRESSION);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			String tempString = content.substring(start + NormalString.Pattern.EXP_PREFIX_LEN, end);
			int number = Integer.parseInt(tempString);
			if (number >= NormalString.Pattern.EXP_START && number < NormalString.Pattern.EXP_END) {
				try {
					Field field = R.drawable.class.getDeclaredField(NormalString.Pattern.EXP_FILE_PREFIX + number);
					int resourceId = Integer.parseInt(field.get(null).toString());
					Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
					ImageSpan imageSpan = new ImageSpan(context, bitmap);
					spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return spannableString;
	}

}
