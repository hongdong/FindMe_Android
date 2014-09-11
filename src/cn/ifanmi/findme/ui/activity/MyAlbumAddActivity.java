package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
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
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.WSPhotosAdapter;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.PersonService;
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
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class MyAlbumAddActivity extends Activity implements IfanActivity {

	private Button btn_back, btn_ok;
	private MyGridView mgridView;
	private WSPhotosAdapter adapter;
	private ProgressDialog progressDialog;
	private String curFileName;	//文件名的处理堪称经
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_album);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		Bimp.create(getIntent().getIntExtra(Person.ALBUM, 0));
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_add_album);
		btn_ok = (Button)findViewById(R.id.btn_ok_add_album);
		mgridView = (MyGridView)findViewById(R.id.grid_write_status_photos);
		progressDialog = new ProgressDialog(this);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		btn_ok.setOnClickListener(listener);
		
		adapter = new WSPhotosAdapter(this);
		mgridView.setAdapter(adapter);
		mgridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == Bimp.bmp.size()) {
					curFileName = System.currentTimeMillis() + ".png";
					new PhotosPopupWindow(MyAlbumAddActivity.this, mgridView, curFileName);
				} else {
					Intent intent = new Intent(MyAlbumAddActivity.this, LocalPhotosActivity.class);
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
		taskParams.put(Task.FAN_ACTIVITY, MyAlbumAddActivity.class.getSimpleName());
		taskParams.put(Task.FAN_GETTOKEN_TYPE, Task.FAN_GETTOKEN_TYPE_USER);
		Task task = new Task(Task.FAN_GETTOKEN, taskParams);
		IfanService.addTask(task);
	}
	
	private void doUploadPhotoTask(String token) {
		showProgressDialog("正在上传照片...");
		IqiniuService iqiniuService = new IqiniuService(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Task.FAN_QINIU_TOKEN, token);
		params.put(Task.FAN_QINIU_DRR, Bimp.drr);
		Task task = new Task(Task.FAN_QINIU_MYDRR, params);
		iqiniuService.upload(task, new QiniuCallback() {
			public void refresh(String result) {
				if (result != null) {
					dismissProgressDialog();
					doAddAlbumTask(result);
				} else {
					dismissProgressDialog();
					ToastUtil.prompt(MyAlbumAddActivity.this, "上传照片失败");
				}
			}
		});
	}
	
	private void doAddAlbumTask(String uploadAlbum) {
		showProgressDialog("正在提交到服务器...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, MyAlbumAddActivity.class.getSimpleName());
		taskParams.put(Person.ALBUM, uploadAlbum);
		Task task = new Task(Task.FAN_ADDALBUM, taskParams);
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
			
		case Task.FAN_ADDALBUM:
			String add = (String) map.get(JsonString.Return.RESULT);
			refreshAdd(add);
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
	
	private void refreshAdd(String add) {
		if (add != null) {
			try {
				JSONObject jsonObject = new JSONObject(add);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {	
					ToastUtil.prompt(this, "添加照片成功！");
					String personId = SPUtil.getDefaultUser(this, null).getId();
					PersonService service = new PersonService(this);
					String album = service.getPersonById(personId).getAlbum();
					JSONArray albumArray = jsonObject.getJSONArray(JsonString.Person.ALBUM);
					for (int i = 0; i < albumArray.length(); i++) {
						String photo = albumArray.getString(i);
						album += photo + ",";
					}
					service.updataPersonInfo(personId, Person.ALBUM, album);
					service.closeDBHelper();
					finish();
				} else {
					ToastUtil.prompt(this, "添加照片失败！");
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
			case R.id.btn_back_add_album:
				finish();
				break;
				
			case R.id.btn_ok_add_album:
				if (Bimp.drr.size() > 0) {
					doGetTokenTask();
				} else {
					ToastUtil.prompt(MyAlbumAddActivity.this, "还没有要上传的照片！");
				}
				break;
			}
		}
	}

}
