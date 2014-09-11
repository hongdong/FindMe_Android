package cn.ifanmi.findme.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.SdCardUtil;

public class IfanUpdata {

	//上下文
	private Context context;
	
	//服务器获取的json字符串
	private String jsonString;
	//服务器端版本号
	private int versionCode;
	//显示新版本特性
	private String displayMessage;
	//下载地址
	private String downloadUrl;
	//apk名字
	private String apkName;
	
	//下载进度条
	private ProgressBar progressBar;
    // 是否终止下载  
    private boolean isInterceptDownload = false;  
    //进度条显示数值  
    private int progress = 0;  

	public IfanUpdata(Context context, String jsonString) {
		this.context = context;
		this.jsonString = jsonString;
	}
	
	/**
	 * HomePageActivity调用这个函数来检测
	 */
	public void checkUpdate() {  
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject object = jsonObject.getJSONObject(JsonString.Updata.VERSION_INFO);
			versionCode = object.getInt(JsonString.Updata.VERSION_CODE);
			displayMessage = object.getString(JsonString.Updata.DISPLAY_MESSAGE);
			downloadUrl = object.getString(JsonString.Updata.DOWNLOAD_URL);
			apkName = object.getString(JsonString.Updata.APK_NAME);
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 
					PackageManager.GET_CONFIGURATIONS); 
			if (versionCode > pi.versionCode) {	
				showUpdateDialog();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 
     * 提示更新对话框          
     */  
    private void showUpdateDialog() {  
        AlertDialog.Builder builder = new AlertDialog.Builder(context);   
        builder.setTitle("发现新版本"); 
        builder.setMessage(displayMessage);
        builder.setPositiveButton("下载", new OnClickListener() {  
            @Override
			public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
                showDownloadDialog();  // 弹出下载框  
            }  
        });  
        builder.setNegativeButton("以后再说", new OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
            }  
        });  
        builder.create().show();  
    }  
    
    /** 
     * 弹出下载框 
     */  
    private void showDownloadDialog() {  
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);  
        builder.setTitle("版本更新中...");  
        builder.setMessage("换过装扮后的番迷君正向你走来");
        View view = LayoutInflater.from(context).inflate(R.layout.update_prgress, null);  
        progressBar = (ProgressBar) view.findViewById(R.id.pb_update_progress);  
        builder.setView(view);  
        builder.setNegativeButton("取消", new OnClickListener() {  
            @Override
			public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
                //终止下载  
                isInterceptDownload = true;  
            }  
        });  
        builder.create().show();  
        //下载apk  
        downloadApk();  
    }  
    
    /** 
     * 下载apk 
     */  
    private void downloadApk(){  
        //开启另一线程下载  
        Thread downLoadThread = new Thread(downApkRunnable);  
        downLoadThread.start();  
    } 
    
    /** 
     * 从服务器下载新版apk的线程 
     */  
    private Runnable downApkRunnable = new Runnable(){  
        @Override  
        public void run() {  
            if (!SdCardUtil.hasSdcard()) {  
                //如果没有SD卡  
            	AlertDialog.Builder builder = new AlertDialog.Builder(context);  
                builder.setTitle("提示");  
                builder.setMessage("当前设备无SD卡，数据无法下载");  
                builder.setPositiveButton("确定", new OnClickListener() {   
					public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                    }  
                });  
                builder.create().show();  
                return;  
            } else {  
                try {  
                    //服务器上新版apk地址  
                    URL url = new URL(downloadUrl);  
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                    conn.connect();  
                    int length = conn.getContentLength();  
                    InputStream is = conn.getInputStream();  
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
                    		+ NormalString.File.DIR_FANMI);  
                    if(!file.exists()){  
                        //如果文件夹不存在,则创建  
                        file.mkdir();  
                    }  
                    //下载服务器中新版本软件（写文件）  
                    String apkFile = Environment.getExternalStorageDirectory().getAbsolutePath() 
                    		+ NormalString.File.DIR_FANMI + apkName;  
                    File ApkFile = new File(apkFile);  
                    FileOutputStream fos = new FileOutputStream(ApkFile);  
                    int count = 0;  
                    byte buf[] = new byte[1024];  
                    do{  
                        int numRead = is.read(buf);  
                        count += numRead;  
                        //更新进度条  
                        progress = (int) (((float) count / length) * 100);  
                        handler.sendEmptyMessage(1);  
                        if(numRead <= 0){  
                            //下载完成通知安装  
                            handler.sendEmptyMessage(0);  
                            break;  
                        }  
                        fos.write(buf,0,numRead);  
                        //当点击取消时，则停止下载  
                    }while(!isInterceptDownload);  
                    fos.close();
                } catch (MalformedURLException e) {  
                    e.printStackTrace();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    };  
    
    /** 
     * 声明一个handler来跟进进度条 
     */  
    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {  
        @Override
		public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case 1:  
                // 更新进度情况  
                progressBar.setProgress(progress);  
                break;  
            case 0:  
                progressBar.setVisibility(View.INVISIBLE);  
                // 安装apk文件  
                installApk();  
                break;  
            default:  
                break;  
            }  
        };  
    };  
    
    /** 
     * 安装apk 
     */  
    private void installApk() {  
        // 获取当前sdcard存储路径  
        File apkfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
        		+ NormalString.File.DIR_FANMI + apkName);  
        if (!apkfile.exists()) {  
            return;  
        }  
        Intent intent = new Intent(Intent.ACTION_VIEW);  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        // 安装，如果签名不一致，可能出现程序未安装提示  
        intent.setDataAndType(Uri.fromFile(new File(apkfile.getAbsolutePath())), 
        		"application/vnd.android.package-archive");   
        context.startActivity(intent);  
        android.os.Process.killProcess(android.os.Process.myPid());
    }  
    
}  
