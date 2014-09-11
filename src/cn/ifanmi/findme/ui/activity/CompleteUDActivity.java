package cn.ifanmi.findme.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.SexAdapter;
import cn.ifanmi.findme.bean.Friend;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.dbService.FriendService;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.logic.IqiniuService;
import cn.ifanmi.findme.logic.IqiniuService.QiniuCallback;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.ui.view.HeadPopupWindow;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;
import cn.jpush.android.api.JPushInterface;

public class CompleteUDActivity extends Activity implements IfanActivity {

	private User user;
	private View view_photo, view_sex, view_cl;
	private EditText edit_name;
	private TextView txt_sex, txt_cl;
	private Button btn_ok;
	private Uri photoUri;
	private Bitmap userPhoto;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_ud);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		user = SPUtil.getDefaultUser(this, null);
	}

	@Override
	public void initView() {
		view_photo = findViewById(R.id.layout_complete_ud_photo);
		view_sex = findViewById(R.id.layout_complete_ud_sex);
		view_cl = findViewById(R.id.layout_complete_ud_cl);
		btn_ok = (Button)findViewById(R.id.btn_ok_complete_ud);
		edit_name = (EditText)findViewById(R.id.edit_complete_ud_name);
		txt_sex = (TextView)findViewById(R.id.txt_complete_ud_sex_hint);
		txt_cl = (TextView)findViewById(R.id.txt_complete_ud_cl_hint);
		progressDialog = new ProgressDialog(this);
		
		MyOnClickListener listener = new MyOnClickListener();
		view_photo.setOnClickListener(listener);
		view_sex.setOnClickListener(listener);
		view_cl.setOnClickListener(listener);
		btn_ok.setOnClickListener(listener);
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
	
	private void beforeLogin() {
		user.setName(edit_name.getText().toString().trim());
		user.setSex(txt_sex.getText().toString().trim());
		user.setCl(txt_cl.getText().toString().trim());
		user.setNickname(user.getName());
	}
	
	private void doGetTokenTask() {
		showProgressDialog("照片处理中...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, CompleteUDActivity.class.getSimpleName());
		taskParams.put(Task.FAN_GETTOKEN_TYPE, Task.FAN_GETTOKEN_TYPE_USER);
		Task task = new Task(Task.FAN_GETTOKEN, taskParams);
		IfanService.addTask(task);
	}
	
	private void doUploadPhotoTask(String token) {
		showProgressDialog("正在上传照片...");
		IqiniuService iqiniuService = new IqiniuService(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Task.FAN_QINIU_TOKEN, token);
		params.put(Task.FAN_QINIU_URI, photoUri);
		Task task = new Task(Task.FAN_QINIU_MYURI, params);
		iqiniuService.upload(task, new QiniuCallback() {
			public void refresh(String result) {
				if (result != null) {
					dismissProgressDialog();
					doLoginTask(result);
				} else {
					dismissProgressDialog();
					ToastUtil.prompt(CompleteUDActivity.this, "上传照片失败");
				}
			}
		});
	}
	
	private void doLoginTask(String userPhoto) {
		showProgressDialog("正在登录...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, CompleteUDActivity.class.getSimpleName());
		taskParams.put(User.class.getName(), user);
		taskParams.put(User.PHOTO, userPhoto);
		taskParams.put(Task.FAN_AUTHO_DEVICE_CODE, JPushInterface.getRegistrationID(this));
		taskParams.put(Task.FAN_AUTHO_SYSTEM_TYPE, Task.FAN_AUTHO_SYSTEM_TYPE_ANDROID);
		Task task = new Task(Task.FAN_LOGIN, taskParams);
		IfanService.addTask(task);
	}
	
	private void doGetFriendTask() {
		showProgressDialog("正在获取好友列表...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, CompleteUDActivity.class.getSimpleName());
		Task task = new Task(Task.FAN_GETFRIEND, taskParams);
		IfanService.addTask(task);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		switch (taskId) {
		case Task.FAN_GETTOKEN:
			String token = (String) map.get(JsonString.Return.RESULT);
			refreshToken(token);
			break;
			
		case Task.FAN_LOGIN:
			String login = (String) map.get(JsonString.Return.RESULT);
			refreshLogin(login);
			break;
			
		case Task.FAN_GETFRIEND:
			List<Friend> friends = (List<Friend>) map.get(JsonString.Return.RESULT);
			refreshFriend(friends);
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
	
	private void refreshLogin(String result) {
		if (result != null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {	
					doGetFriendTask();
				} else {
					ToastUtil.prompt(this, "登录失败");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	private void refreshFriend(List<Friend> friends) {
		if (friends != null) {
			FriendService service = new FriendService(this);
			service.insertFriends(friends);
			service.closeDBHelper();
			SPUtil.saveDefaultUser(this, User.NAME, user.getName());
			SPUtil.saveDefaultUser(this, User.SEX, user.getSex());
			SPUtil.saveDefaultUser(this, User.CL, user.getCl());
			SPUtil.saveDefaultUser(this, User.NICKNAME, user.getNickname());
			Intent intent = new Intent(this, HomePageActivity.class);
			startActivity(intent);
			finish();
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layout_complete_ud_photo:
				new HeadPopupWindow(CompleteUDActivity.this, view_photo);
				break;
				
			case R.id.layout_complete_ud_sex:
				showSexDialog();
				break;
				
			case R.id.layout_complete_ud_cl:
				Intent intent = new Intent(CompleteUDActivity.this, SelectClActivity.class);
				startActivityForResult(intent, IntentString.RequestCode.COMPLETEUD_SELECTCL);
				break;
				
			case R.id.btn_ok_complete_ud:
				if (null == userPhoto) {
					ToastUtil.prompt(CompleteUDActivity.this, "请上传头像");
				} else if (edit_name.getText().toString().trim().equals("")) {
					ToastUtil.prompt(CompleteUDActivity.this, "请填写真实姓名");
				} else if (txt_sex.getText().toString().trim().equals("")) {
					ToastUtil.prompt(CompleteUDActivity.this, "请选择性别");
				} else if (txt_cl.getText().toString().trim().equals("")) {
					ToastUtil.prompt(CompleteUDActivity.this, "请选择星座");
				} else {
					beforeLogin();
					showLoginDialog();
				}
				break;
			}
		}
	}
	
	private void showSexDialog() {
		new AlertDialog.Builder(this)
		.setAdapter(new SexAdapter(this), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					txt_sex.setText("男");
					break;
				case 1:
					txt_sex.setText("女");
					break;
				}
			}
		}).show();
	}
	
	private void showLoginDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
        builder.setTitle("提示"); 
        builder.setMessage("基本信息填写后将不可修改，确定提交吗？");
        builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss(); 
                doGetTokenTask();
            }  
        });  
        builder.setNegativeButton("取消", new AlertDialog.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
            }  
        });  
        builder.create().show();  
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_CANCELED) {
			switch (requestCode) {
			case IntentString.RequestCode.IMAGE:
				startPhotoZoom(data.getData());
				break;
			case IntentString.RequestCode.CAMERA:
				File tempFile = new File(
						Environment.getExternalStorageDirectory().getAbsolutePath() + NormalString.File.DIR_FANMI, 
						NormalString.File.IMAGE_FILE_NAME);
				startPhotoZoom(Uri.fromFile(tempFile));
				break;
			case IntentString.RequestCode.PHOTO:
				if (photoUri != null) {
					try {
						userPhoto = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));
						ImageView imageView = (ImageView)findViewById(R.id.img_complete_ud_photo);
						imageView.setImageBitmap(userPhoto);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				break;
			case IntentString.RequestCode.COMPLETEUD_SELECTCL:
				if (IntentString.ResultCode.SELECTCL_COMPLETEUD == resultCode) {
					String cl = data.getStringExtra(User.CL);
					txt_cl.setText(cl);
				}
				break;
			}		
		}
	}
	
	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 640);
		intent.putExtra("outputY", 640);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		photoUri = uri;
		startActivityForResult(intent, IntentString.RequestCode.PHOTO);
	}

}
