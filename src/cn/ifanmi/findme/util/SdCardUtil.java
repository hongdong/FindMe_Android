package cn.ifanmi.findme.util;

import java.io.File;
import java.io.FileOutputStream;
import cn.ifanmi.findme.string.NormalString;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Environment;
/**
 * 
 * @author XuZhiwei (xuzw13@gmail.com)
 * Create at 2012-8-17 上午10:14:40
 */
public class SdCardUtil {
	/**
	 * 检查是否存在SDCard
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		} else {
			return false;
		}
	}
	
	public static void savePhoto(Context context, Bitmap bitmap) {
		if (!SdCardUtil.hasSdcard()) {  
            //如果没有SD卡  
        	AlertDialog.Builder builder = new AlertDialog.Builder(context);  
            builder.setTitle("提示");  
            builder.setMessage("当前设备无SD卡，数据无法保存");  
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {   
				public void onClick(DialogInterface dialog, int which) {  
                    dialog.dismiss();  
                }
            });
            builder.create().show();  
        } else {  
        	File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
        			+ NormalString.File.DIR_FANMI);  
            if(!file.exists()){  
                file.mkdir();  
            }  
            String filename = Environment.getExternalStorageDirectory().getAbsolutePath() 
            		+ NormalString.File.DIR_FANMI + System.currentTimeMillis() + ".png";
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(filename);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
                try {
                	out.flush();
                } catch (Exception e) {
                	e.printStackTrace();
                }
                try {
                	out.close();
                } catch (Exception e) {
                	e.printStackTrace();
                }
                out=null;
            }
            ToastUtil.prompt(context, "已保存至" + filename);
            
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
	}
	
}
