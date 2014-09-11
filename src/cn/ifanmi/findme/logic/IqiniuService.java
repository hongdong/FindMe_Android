package cn.ifanmi.findme.logic;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.qiniu.auth.JSONObjectRet;
import cn.ifanmi.findme.qiniu.io.IO;
import cn.ifanmi.findme.qiniu.io.PutExtra;
import cn.ifanmi.findme.qiniu.utils.QiniuException;
import cn.ifanmi.findme.util.SPUtil;

public class IqiniuService {

	private Context context;
	private int uploadCount;
	private String fileNames;
	private boolean uploadError;
	
	public IqiniuService(Context context) {
		this.context = context;
	}
	
	@SuppressLint("HandlerLeak")
	public void upload(final Task task, final QiniuCallback callback) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				callback.refresh((String)msg.obj);
			}
		};
		new Thread() {
			public void run() {
				int taskId = task.getTaskId();
				switch (taskId) {
				case Task.FAN_QINIU_MYURI:
					doMyUriTask(task, handler);
					break;
				case Task.FAN_QINIU_MYDRR:
					doMyDrrTask(task, handler);
					break;	
				case Task.FAN_QINIU_OTHERURI:
					doOtherUriTask(task, handler);
					break;
				case Task.FAN_QINIU_OTHERDRR:
					doOtherDrrTask(task, handler);
					break;
				}
			};
		}.start();
	}
	
	public void doMyUriTask(Task task, final Handler handler) {
		Map<String, Object> params = task.getTaskParams();
		String token = (String) params.get(Task.FAN_QINIU_TOKEN);
		Uri uri = (Uri) params.get(Task.FAN_QINIU_URI);
		String myId = SPUtil.getDefaultUser(context, null).getId();
		String random = String.valueOf((int) (Math.random() * 100000000));
		final String fileName = myId + "/" + System.currentTimeMillis() + random + ".png";
		PutExtra putExtra = new PutExtra();
		IO.putFile(context, token, fileName, uri, putExtra, new JSONObjectRet() {
			public void onFailure(QiniuException ex) {
				Message msg = new Message();
				msg.obj = null;
				handler.sendMessage(msg);
			}
			public void onSuccess(JSONObject obj) {
				Message msg = new Message();
				msg.obj = fileName;
				handler.sendMessage(msg);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public void doMyDrrTask(Task task, final Handler handler) {
		Map<String, Object> params = task.getTaskParams();
		String token = (String) params.get(Task.FAN_QINIU_TOKEN);
		final List<String> drr = (List<String>) params.get(Task.FAN_QINIU_DRR);
		String myId = SPUtil.getDefaultUser(context, null).getId();
		uploadCount = 0;
		fileNames = "";
		uploadError = false;
		for (int i = 0; i < drr.size(); i++) {
			String random = String.valueOf((int) (Math.random() * 100000000));
			final String fileName = myId + "/" + System.currentTimeMillis() + random + ".png";
			File file = new File(drr.get(i));
			PutExtra putExtra = new PutExtra();
			IO.putFile(token, fileName, file, putExtra, new JSONObjectRet() {
				public void onFailure(QiniuException ex) {
					if (!uploadError) {
						uploadError = true;
						Message msg = new Message();
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
				public void onSuccess(JSONObject obj) {
					uploadCount++;
					fileNames += fileName + ",";
					if (uploadCount == drr.size()) {
						Message msg = new Message();
						msg.obj = fileNames;
						handler.sendMessage(msg);
					}
				}
			});
		}
	}
	
	public void doOtherUriTask(Task task, final Handler handler) {
		//暂时没有这个
	}
	
	@SuppressWarnings("unchecked")
	public void doOtherDrrTask(Task task, final Handler handler) {
		Map<String, Object> params = task.getTaskParams();
		String token = (String) params.get(Task.FAN_QINIU_TOKEN);
		final List<String> drr = (List<String>) params.get(Task.FAN_QINIU_DRR);
		uploadCount = 0;
		fileNames = "";
		uploadError = false;
		for (int i = 0; i < drr.size(); i++) {
			String random = String.valueOf((int) (Math.random() * 100000000));
			final String fileName = System.currentTimeMillis() + random + ".png";
			File file = new File(drr.get(i));
			PutExtra putExtra = new PutExtra();
			IO.putFile(token, fileName, file, putExtra, new JSONObjectRet() {
				public void onFailure(QiniuException ex) {
					if (!uploadError) {
						uploadError = true;
						Message msg = new Message();
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
				public void onSuccess(JSONObject obj) {
					uploadCount++;
					fileNames += fileName + ",";
					if (uploadCount == drr.size()) {
						Message msg = new Message();
						msg.obj = fileNames;
						handler.sendMessage(msg);
					}
				}
			});
		}
	}

	public interface QiniuCallback {
		public void refresh(String result);
	}
	
}
