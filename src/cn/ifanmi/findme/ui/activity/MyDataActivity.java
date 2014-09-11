package cn.ifanmi.findme.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.PersonService;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.logic.IqiniuService;
import cn.ifanmi.findme.logic.IqiniuService.QiniuCallback;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.ui.view.HeadPopupWindow;
import cn.ifanmi.findme.ui.view.MyListView;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.StringUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class MyDataActivity extends Activity implements IfanActivity {

	private String personId;
	private PersonService service;
	private Person person;
	private Button btn_back;
	private ImageView img_photo, img_auth, img_sex;
	private TextView txt_name, txt_cl;
	private MyListView mlist_info, mlist_college;
	private ProgressDialog progressDialog;
	private Uri photoUri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_data);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		personId = SPUtil.getDefaultUser(this, null).getId();
		service = new PersonService(this);
		person = service.getPersonById(personId);
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_my_data);
		img_photo = (ImageView)findViewById(R.id.img_my_data_photo);
		img_auth = (ImageView)findViewById(R.id.img_my_data_auth);
		img_sex = (ImageView)findViewById(R.id.img_my_data_sex);
		txt_name = (TextView)findViewById(R.id.txt_my_data_name);
		txt_cl = (TextView)findViewById(R.id.txt_my_data_cl);
		mlist_info = (MyListView)findViewById(R.id.list_my_data_info);
		mlist_college = (MyListView)findViewById(R.id.list_my_data_college);
		progressDialog = new ProgressDialog(this);
		
		String smallUrl = StringUtil.getSmallPhoto(person.getPhoto(), NormalString.SmallPhoto.PERSON_DATA);
		ImageViewUtil.displayCircleImageView(this, img_photo, smallUrl);
		
		String sex = person.getSex();
		if (sex.equals(Person.MALE)) {
			img_sex.setImageResource(R.drawable.sex_male);
		} else if (sex.equals(Person.FEMALE)) {
			img_sex.setImageResource(R.drawable.sex_female);
		}
		
		String auth = person.getIsAuth();
		if (auth.equals(Person.ISAUTH_YES)) {
			img_auth.setVisibility(View.VISIBLE);
		} else if (auth.equals(Person.ISAUTH_NO)) {
			img_auth.setVisibility(View.GONE);
		}
		
		txt_name.setText(person.getName());
		txt_cl.setText(person.getCl());
		
		displayCollege();
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		img_photo.setOnClickListener(listener);
	}
	
	private void displayInfo() {
		String[] infoLeft = new String[] {"昵称:", "个性签名:", };
		String[] infoRight = new String[] {person.getNickname(), person.getSignature(), };
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < infoLeft.length; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(NormalString.List.LEFT, infoLeft[i]);
			item.put(NormalString.List.RIGHT, infoRight[i]);
			list.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.my_data_info, 
				new String[] {NormalString.List.LEFT, NormalString.List.RIGHT, }, 
				new int[] {R.id.txt_my_data_info_left, R.id.txt_my_data_info_right});
		mlist_info.setAdapter(adapter);
		mlist_info.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (arg2) {
				case 0:
					Intent nickname = new Intent(MyDataActivity.this, UpdateNicknameActivity.class);
					startActivity(nickname);
					break;
					
				case 1:
					Intent signature = new Intent(MyDataActivity.this, UpdateSignatureActivity.class);
					startActivity(signature);
					break;
				}
			}
		});
	}
	
	private void displayCollege() {
		String[] infoLeft = new String[] {"学校:", "院系:", "年级:", };
		String[] infoRight = new String[] {person.getCollegeName(), person.getDeptName(), person.getGrade(), };
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < infoLeft.length; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(NormalString.List.LEFT, infoLeft[i]);
			item.put(NormalString.List.RIGHT, infoRight[i]);
			list.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.my_data_college, 
				new String[] {NormalString.List.LEFT, NormalString.List.RIGHT, }, 
				new int[] {R.id.txt_my_data_college_left, R.id.txt_my_data_college_right});
		mlist_college.setAdapter(adapter);
		mlist_college.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		/**
		 * displayInfo里面显示的信息可能在下一级页面中改变，
		 * 所以要onResume里面重复刷新。这样做比较方便
		 */
		person = service.getPersonById(personId);
		displayInfo();
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
		taskParams.put(Task.FAN_ACTIVITY, MyDataActivity.class.getSimpleName());
		taskParams.put(Task.FAN_GETTOKEN_TYPE, Task.FAN_GETTOKEN_TYPE_USER);
		Task task = new Task(Task.FAN_GETTOKEN, taskParams);
		IfanService.addTask(task);
	}
	
	private void doUploadPhotoTask(String token) {
		showProgressDialog("正在上传...");
		IqiniuService iqiniuService = new IqiniuService(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Task.FAN_QINIU_TOKEN, token);
		params.put(Task.FAN_QINIU_URI, photoUri);
		Task task = new Task(Task.FAN_QINIU_MYURI, params);
		iqiniuService.upload(task, new QiniuCallback() {
			public void refresh(String result) {
				if (result != null) {
					dismissProgressDialog();
					doUpdataUPTask(result);
				} else {
					dismissProgressDialog();
					ToastUtil.prompt(MyDataActivity.this, "上传照片失败");
				}
			}
		});
	}
	
	private void doUpdataUPTask(String fileName) {
		showProgressDialog("正在上传...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, MyDataActivity.class.getSimpleName());
		taskParams.put(Person.PHOTO, fileName);
		Task task = new Task(Task.FAN_UPDATEUP, taskParams);
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
		case Task.FAN_UPDATEUP:
			String photo = (String) map.get(JsonString.Return.RESULT);
			refreshPhoto(photo);
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
	
	private void refreshPhoto(String photo) {
		if (photo != null) {
			try {
				JSONObject jsonObject = new JSONObject(photo);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {
					ToastUtil.prompt(this, "修改头像成功");
					String result = jsonObject.getString(JsonString.Person.PHOTO);
					String smallUrl = StringUtil.getSmallPhoto(result, NormalString.SmallPhoto.PERSON_DATA);
					ImageViewUtil.displayCircleImageView(this, img_photo, smallUrl);
					service.updataPersonInfo(personId, Person.PHOTO, result);
				} else {
					ToastUtil.prompt(this, "修改头像失败");
				}
			}  catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
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
					doGetTokenTask();
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
		service.closeDBHelper();
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_my_data:
				finish();
				break;
				
			case R.id.img_my_data_photo:
				new HeadPopupWindow(MyDataActivity.this, img_photo);
				break;
			}
		}
	}

}
