package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Match;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.PersonService;
import cn.ifanmi.findme.logic.FanApplication;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.StringUtil;
import cn.ifanmi.findme.util.ToastUtil;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

public class FindActivity extends Activity implements IfanActivity {

	private String mySex;
	private Button btn_info, btn_pass, btn_like;
	private View view_info, view_normal, view_content, view_countdown, view_match;
	private ImageView img_photo, img_auth, img_sex;
	private TextView txt_new_info, txt_nickname, txt_cl;
	private TextView txt_college, txt_dept, txt_grage, txt_signature;
	private TextView txt_countdown;
	private CountDownTimer countDownTimer;
	private ProgressDialog progressDialog;
	private Match match;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		btn_info = (Button)findViewById(R.id.btn_find_info);
		btn_pass = (Button)findViewById(R.id.btn_find_pass);
		btn_like = (Button)findViewById(R.id.btn_find_like);
		view_info = findViewById(R.id.layout_find_info);
		view_normal = findViewById(R.id.layout_find_normal);
		view_content = findViewById(R.id.layout_find_content);
		view_countdown = findViewById(R.id.layout_find_countdown);
		view_match = findViewById(R.id.layout_find_match);
		img_photo = (ImageView)findViewById(R.id.img_find_center_photo);
		img_auth = (ImageView)findViewById(R.id.img_find_center_auth);
		img_sex = (ImageView)findViewById(R.id.img_find_center_sex);
		txt_new_info = (TextView)findViewById(R.id.txt_find_new_info);
		txt_nickname = (TextView)findViewById(R.id.txt_find_center_nickname);
		txt_cl = (TextView)findViewById(R.id.txt_find_center_cl);
		txt_college = (TextView)findViewById(R.id.txt_find_center_college);
		txt_dept = (TextView)findViewById(R.id.txt_find_center_dept);
		txt_grage = (TextView)findViewById(R.id.txt_find_center_grade);
		txt_signature = (TextView)findViewById(R.id.txt_find_center_signature);
		txt_countdown = (TextView)findViewById(R.id.txt_find_countdown);
		progressDialog = new ProgressDialog(this);
		
		view_normal.setVisibility(View.VISIBLE);
		doGetMatchTask();
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_info.setOnClickListener(listener);
		btn_pass.setOnClickListener(listener);
		btn_like.setOnClickListener(listener);
		img_photo.setOnClickListener(listener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (null == mySex) {
			PersonService service = new PersonService(this);
			String personId = SPUtil.getDefaultUser(this, null).getId();
			Person person = service.getPersonById(personId);
			if (person != null) {
				mySex = person.getSex();
				if (mySex.equals(Person.FEMALE)) {
					view_info.setVisibility(View.VISIBLE);
				} else if (mySex.equals(Person.MALE)) {
					view_info.setVisibility(View.GONE);
				}
			}
			service.closeDBHelper();
		}
		refreshNewInfo();
	}
	
	public void refreshNewInfo() {
		int push_ff = SPUtil.getPush(this, SPUtil.PUSH_FF);
		if (push_ff > 0) {
			txt_new_info.setText(String.valueOf(push_ff));
			txt_new_info.setVisibility(View.VISIBLE);
		} else {
			txt_new_info.setVisibility(View.GONE);
		}
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
	
	public void doGetMatchTask() {
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, FindActivity.class.getSimpleName());
		taskParams.put(Task.FAN_GETMATCH_TYPE, Task.FAN_GETMATCH_TYPE_MATCH);
		Task task = new Task(Task.FAN_GETMATCH, taskParams);
		IfanService.addTask(task);
	}
	
	public void doPassTask(String personId) {
		showProgressDialog();
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, FindActivity.class.getSimpleName());
		taskParams.put(Task.FAN_GETMATCH_TYPE, Task.FAN_GETMATCH_TYPE_MATCH);
		taskParams.put(Person.ID, personId);
		Task task = new Task(Task.FAN_PASS, taskParams);
		IfanService.addTask(task);
	}
	
	private void doLikeTask(String personId) {
		showProgressDialog();
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, FindActivity.class.getSimpleName());
		taskParams.put(Task.FAN_GETMATCH_TYPE, Task.FAN_GETMATCH_TYPE_MATCH);
		taskParams.put(Person.ID, personId);
		Task task = new Task(Task.FAN_LIKE, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		switch (taskId) {
		case Task.FAN_GETMATCH:
			match = (Match) map.get(JsonString.Return.RESULT);
			refreshMatch();
			break;
			
		case Task.FAN_PASS:
			match = (Match) map.get(JsonString.Return.RESULT);
			refreshPass();
			break;
		
		case Task.FAN_LIKE:
			String like = (String) map.get(JsonString.Return.RESULT);
			refreshLike(like);
			break;
		}
	}
	
	private void refreshMatch() {
		if (match != null) {
			String hasLike = match.getHasLike();
			if (hasLike.equals("0")) {
				Person person = match.getPerson();
				if (person != null) {
					displayMatch();
				} else {	//今天还没开始匹配或者名额用完了
					displayCountdown();
				}
			} else if (hasLike.equals("1")) {	//今天已经Like过了
				displayCountdown();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	private void refreshPass() {
		if (match != null) {	//可以调pass接口，说明还没有Like过
			Person person = match.getPerson();
			if (person != null) {
				displayMatch();
			} else {	//名额用完了，pass接口可以调，说明肯定匹配过了
				ToastUtil.longPrompt(this, "今天的名额已经用完了");
				displayCountdown();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	private void displayMatch() {
		Person person = match.getPerson();
		
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
	}
	
	private void refreshLike(String like) {
		if (like != null) {
			try {
				JSONObject jsonObject = new JSONObject(like);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {	//首次Like对方
					ToastUtil.prompt(this, "Like对方成功");
					displayCountdown();
					if (mySex != null) {
						if (mySex.equals(Person.MALE)) {
							doGetMatchTask();
						}
					}
				} else if (state.equals("20002")) {	//对方已经Like过我
					ToastUtil.prompt(this, "双方相互Like，你们已经是好友了");
					displayCountdown();
					if (mySex != null) {
						if (mySex.equals(Person.MALE)) {
							doGetMatchTask();
						}
					}
					
					Intent intent = new Intent(IntentString.Receiver.NEW_FRIEND);
					sendBroadcast(intent);
					EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
					TextMessageBody txtBody = new TextMessageBody("我们已经是好友了，可以开始聊天了");
					message.addBody(txtBody);
					message.setReceipt(match.getPerson().getId());
					EMConversation conversation = 
							EMChatManager.getInstance().getConversation(match.getPerson().getId());
					conversation.addMessage(message);
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
	
	private void displayCountdown() {
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
		countDownTimer = new CountDownTimer(Long.parseLong(match.getCountdown()), 1000) {
			public void onTick(long millisUntilFinished) {
				int totalSecond = (int) (millisUntilFinished / 1000);
				int hour = totalSecond / 3600; 
				int minute = totalSecond % 3600 / 60;
				int second = totalSecond % 60;
				String h, m, s;
				if (hour < 10) {
					h = "0" + String.valueOf(hour);
				} else {
					h = String.valueOf(hour);
				}
				if (minute < 10) {
					m = "0" + String.valueOf(minute);
				} else {
					m = String.valueOf(minute);
				}
				if (second < 10) {
					s = "0" + String.valueOf(second);
				} else {
					s = String.valueOf(second);
				}
				txt_countdown.setText(h + ":" + m + ":" + s);
			}
			public void onFinish() {
				view_countdown.setVisibility(View.GONE);
				view_match.setVisibility(View.VISIBLE);
			}
		};
		countDownTimer.start();
		view_match.setVisibility(View.GONE);
		view_countdown.setVisibility(View.VISIBLE);
		view_normal.setVisibility(View.VISIBLE);
		view_content.setVisibility(View.GONE);
	}
	
	@Override
	public void onBackPressed() {
		getParent().onBackPressed();
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_find_info:
				Intent infoIntent = new Intent(FindActivity.this, FindFansActivity.class);
				startActivity(infoIntent);
				break;
				
			case R.id.btn_find_pass:
				doPassTask(match.getPerson().getId());
				break;
				
			case R.id.btn_find_like:
				doLikeTask(match.getPerson().getId());
				break;
				
			case R.id.img_find_center_photo:
				Intent pdIntent = new Intent(FindActivity.this, PersonDataActivity.class);
				FanApplication application = (FanApplication) getApplication();
				application.setPerson(match.getPerson());
				startActivity(pdIntent);
				break;
			}
		}
	}

}
