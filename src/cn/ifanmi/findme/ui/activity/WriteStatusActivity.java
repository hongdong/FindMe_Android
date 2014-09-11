package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.WSPhotosAdapter;
import cn.ifanmi.findme.bean.Status;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.logic.IqiniuService;
import cn.ifanmi.findme.logic.IqiniuService.QiniuCallback;
import cn.ifanmi.findme.photoCache.Bimp;
import cn.ifanmi.findme.photoCache.LocalPhotosActivity;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.ui.view.MyGridView;
import cn.ifanmi.findme.ui.view.PhotosPopupWindow;
import cn.ifanmi.findme.util.KeyBoardUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class WriteStatusActivity extends Activity implements IfanActivity {

	private Button btn_back, btn_ok, btn_exp;
	private EditText edit_content;
	private MyGridView mgridView;
	private WSPhotosAdapter adapter;
	private ProgressDialog progressDialog;
	private String curFileName;	//文件名的处理堪称经典
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_status);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		Bimp.create(Status.MAX_PHOTO_COUNT);
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_write_status);
		btn_ok = (Button)findViewById(R.id.btn_ok_write_status);
		btn_exp = (Button)findViewById(R.id.btn_write_status_expression);
		edit_content = (EditText)findViewById(R.id.edit_write_status_content);
		mgridView = (MyGridView)findViewById(R.id.grid_write_status_photos);
		progressDialog = new ProgressDialog(this);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		btn_ok.setOnClickListener(listener);
		btn_exp.setOnClickListener(listener);
		edit_content.setOnClickListener(listener);
		
		adapter = new WSPhotosAdapter(this);
		mgridView.setAdapter(adapter);
		mgridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == Bimp.bmp.size()) {
					KeyBoardUtil.dismiss(WriteStatusActivity.this, edit_content);
					/**
					 * adapter的长度是(Bimp.bmp.size() + 1)，
					 * 如果已经达到上传上限，最后面的加号图片被隐藏，这个时候不会跳到这里面来。
					 * 这个分支只有在加号图片显示的时候，且点了加号图片时，才可能进来
					 * 取当前时间做文件名，因为可能拍多张照片
					 */
					curFileName = System.currentTimeMillis() + ".png";
					new PhotosPopupWindow(WriteStatusActivity.this, mgridView, curFileName);
				} else {
					Intent intent = new Intent(WriteStatusActivity.this, LocalPhotosActivity.class);
					intent.putExtra(IntentString.Extra.CLICK_POSITION, arg2);
					startActivity(intent);
				}
			}
		});
	}
	
	private void showProgressDialog(String message) {
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(message);
		progressDialog.show();
	}
	
	private void dismissProgressDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	private void doGetTokenTask() {
		showProgressDialog("照片处理中...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, WriteStatusActivity.class.getSimpleName());
		taskParams.put(Task.FAN_GETTOKEN_TYPE, Task.FAN_GETTOKEN_TYPE_POST);
		Task task = new Task(Task.FAN_GETTOKEN, taskParams);
		IfanService.addTask(task);
	}
	
	private void doUploadPhotoTask(String token) {
		showProgressDialog("正在上传照片...");
		IqiniuService iqiniuService = new IqiniuService(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Task.FAN_QINIU_TOKEN, token);
		params.put(Task.FAN_QINIU_DRR, Bimp.drr);
		Task task = new Task(Task.FAN_QINIU_OTHERDRR, params);
		iqiniuService.upload(task, new QiniuCallback() {
			public void refresh(String result) {
				if (result != null) {
					dismissProgressDialog();
					doWriteStatusTask(result);
				} else {
					dismissProgressDialog();
					ToastUtil.prompt(WriteStatusActivity.this, "上传照片失败");
				}
			}
		});
	}
	
	private void doWriteStatusTask(String uploadphoto) {
		showProgressDialog("正在发布...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, WriteStatusActivity.class.getSimpleName());
		taskParams.put(Status.CONTENT, edit_content.getText().toString().trim());
		taskParams.put(Status.PHOTOS, uploadphoto);
		Task task = new Task(Task.FAN_WRITESTATUS, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		switch (taskId) {
		case Task.FAN_GETTOKEN:
			String token = (String) map.get(JsonString.Return.RESULT);
			refreshToken(token);
			break;
			
		case Task.FAN_WRITESTATUS:
			String write = (String) map.get(JsonString.Return.RESULT);
			refreshWrite(write);
			break;
		}
	}
	
	private void refreshToken(String token) {
		if (token != null) {
			try {
				JSONObject jsonObject = new JSONObject(token);
				String result = jsonObject.getString("result");
				if (result.equals("1")) {	
					String qiniuToken = jsonObject.getString("token");
					doUploadPhotoTask(qiniuToken);
				} else {
					ToastUtil.prompt(this, "上传照片失败");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	private void refreshWrite(String write) {
		if (write != null) {
			try {
				JSONObject jsonObject = new JSONObject(write);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {	
					Intent intent = new Intent();
					setResult(IntentString.ResultCode.WRITESTATUS_GROUP, intent);
					finish();
				} else {
					ToastUtil.prompt(this, "发布失败！");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.update();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_CANCELED) {
			switch (requestCode) {
			case IntentString.RequestCode.CAMERA:
				/**
				 * 拍完照片后把文件名路径加到Bimp.drr，
				 * 注意，这个时候不能调用adapter.update()
				 * 因为拍照返回后会先调onActivityResult，再调onResume，
				 * http://stackoverflow.com/questions/6468319/onactivityresult-onresume
				 * 而onResume里面也调用了adapter.update()，adapter.update()每次启动一个线程
				 * 这样就有可能产生线程同步问题
				 */
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() 
						+ NormalString.File.DIR_FANMI + curFileName;
				Bimp.drr.add(path);
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
		Bimp.destroy();
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_write_status:
				finish();
				break;
				
			case R.id.btn_ok_write_status:
				if (edit_content.getText().toString().trim().equals("")) {
					ToastUtil.prompt(WriteStatusActivity.this, "内容不能为空");
				} else {
					if (Bimp.drr.size() > 0) {
						doGetTokenTask();
					} else {
						doWriteStatusTask(null);
					}
				}
				break;
				
			case R.id.btn_write_status_expression:
				//加表情
				break;
				
			case R.id.edit_write_status_content:
				//取消表情
				break;
			}
		}
	}

}
