package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.logic.FanApplication;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.StringUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class FindFDActivity extends Activity implements IfanActivity {

	private Person person;
	private Button btn_back, btn_pass, btn_like;
	private View view_normal, view_content;
	private ImageView img_photo, img_auth, img_sex;
	private TextView txt_nickname, txt_cl, txt_college, txt_dept, txt_grage, txt_signature;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_fd);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		FanApplication application = (FanApplication) getApplication();
		person = application.getPerson();
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_find_fd);
		btn_pass = (Button)findViewById(R.id.btn_find_pass);
		btn_like = (Button)findViewById(R.id.btn_find_like);
		view_normal = findViewById(R.id.img_find_fd_normal);
		view_content = findViewById(R.id.layout_find_fd_content);
		img_photo = (ImageView)findViewById(R.id.img_find_center_photo);
		img_auth = (ImageView)findViewById(R.id.img_find_center_auth);
		img_sex = (ImageView)findViewById(R.id.img_find_center_sex);
		txt_nickname = (TextView)findViewById(R.id.txt_find_center_nickname);
		txt_cl = (TextView)findViewById(R.id.txt_find_center_cl);
		txt_college = (TextView)findViewById(R.id.txt_find_center_college);
		txt_dept = (TextView)findViewById(R.id.txt_find_center_dept);
		txt_grage = (TextView)findViewById(R.id.txt_find_center_grade);
		txt_signature = (TextView)findViewById(R.id.txt_find_center_signature);
		progressDialog = new ProgressDialog(this);
		
		String smallUrl = StringUtil.getSmallPhoto(person.getPhoto(), NormalString.SmallPhoto.PERSON_FIND);
		ImageViewUtil.displayCircleImageView(this, img_photo, smallUrl);
		
		String isAuth = person.getIsAuth();
		if (isAuth.equals(Person.ISAUTH_YES)) {
			img_auth.setVisibility(View.VISIBLE);
		} else if (isAuth.equals(Person.ISAUTH_NO)) {
			img_auth.setVisibility(View.GONE);
		}
		
		String sex = person.getSex();
		if (sex.equals(Person.MALE)) {
			img_sex.setImageResource(R.drawable.sex_male);
		} else if (sex.equals(Person.FEMALE)) {
			img_sex.setImageResource(R.drawable.sex_female);
		}
		
		txt_nickname.setText(person.getNickname());
		txt_cl.setText(person.getCl());
		txt_college.setText(person.getCollegeName());
		txt_dept.setText(person.getDeptName());
		txt_grage.setText(person.getGrade());
		txt_signature.setText(person.getSignature());
		
		view_normal.setVisibility(View.GONE);
		view_content.setVisibility(View.VISIBLE);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		btn_pass.setOnClickListener(listener);
		btn_like.setOnClickListener(listener);
		img_photo.setOnClickListener(listener);
	}
	
	private void showProgressDialog() {
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage("稍等片刻...");
		progressDialog.show();
	}
	
	private void dismissProgressDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	public void doPassTask(String personId) {
		showProgressDialog();
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, FindFDActivity.class.getSimpleName());
		taskParams.put(Task.FAN_GETMATCH_TYPE, Task.FAN_GETMATCH_TYPE_FANS);
		taskParams.put(Person.ID, personId);
		Task task = new Task(Task.FAN_PASS, taskParams);
		IfanService.addTask(task);
	}
	
	private void doLikeTask(String personId) {
		showProgressDialog();
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, FindFDActivity.class.getSimpleName());
		taskParams.put(Task.FAN_GETMATCH_TYPE, Task.FAN_GETMATCH_TYPE_FANS);
		taskParams.put(Person.ID, personId);
		Task task = new Task(Task.FAN_LIKE, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		switch (taskId) {
		case Task.FAN_PASS:
			String pass = (String) map.get(JsonString.Return.RESULT);
			refreshPass(pass);
			break;
		
		case Task.FAN_LIKE:
			String like = (String) map.get(JsonString.Return.RESULT);
			refreshLike(like);
			break;
		}
	}
	
	private void refreshPass(String pass) {
		if (pass != null) {
			try {
				JSONObject jsonObject = new JSONObject(pass);
				String state = jsonObject.getString(JsonString.Match.MATCH);
				if (state.equals("true")) {	
					ToastUtil.prompt(this, "成功Pass掉了对方！");
					finish();
				} else {
					ToastUtil.prompt(this, "Pass操作失败！");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	private void refreshLike(String like) {
		if (like != null) {
			try {
				JSONObject jsonObject = new JSONObject(like);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20002")) {	//对方已经Like过我
					ToastUtil.prompt(this, "双方相互Like，你们已经是好友了");
					Intent intent = new Intent(IntentString.Receiver.NEW_FRIEND);
					sendBroadcast(intent);
					
					EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
					TextMessageBody txtBody = new TextMessageBody("我们已经是好友了，可以开始聊天了");
					message.addBody(txtBody);
					message.setReceipt(person.getId());
					EMConversation conversation = EMChatManager.getInstance().getConversation(person.getId());
					conversation.addMessage(message);
					
					finish();
				} else if (state.equals("10004")) {
					ToastUtil.longPrompt(this, "检测到您的头像不是真人头像，Like操作无效");
				} else {
					ToastUtil.prompt(this, "Like操作失败！");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
			case R.id.btn_back_find_fd:
				finish();
				break;
				
			case R.id.btn_find_pass:
				doPassTask(person.getId());
				break;
				
			case R.id.btn_find_like:
				doLikeTask(person.getId());
				break;
				
			case R.id.img_find_center_photo:
				Intent pdIntent = new Intent(FindFDActivity.this, PersonDataActivity.class);
				//FanApplication application = (FanApplication) getApplication();
				//application.setPerson(person);
				//这里不要设置全局变量了，因为进来这个页面的之前已经设置过了，是一样的
				startActivity(pdIntent);
				break;
			}
		}
	}

}
