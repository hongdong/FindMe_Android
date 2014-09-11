package cn.ifanmi.findme.ui.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.tsz.afinal.FinalBitmap;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.SDMAdapter;
import cn.ifanmi.findme.adapter.SGPhotosAdapter;
import cn.ifanmi.findme.bean.Message;
import cn.ifanmi.findme.bean.Status;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.listener.NetPhotosGridListener;
import cn.ifanmi.findme.listener.NetPhotosListener;
import cn.ifanmi.findme.logic.FanApplication;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.ui.view.MyGridView;
import cn.ifanmi.findme.ui.view.MyListView;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshBase;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshBase.OnRefreshListener;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshScrollView;
import cn.ifanmi.findme.util.KeyBoardUtil;
import cn.ifanmi.findme.util.StringUtil;
import cn.ifanmi.findme.util.TimeUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class StatusDetailActivity extends Activity implements IfanActivity {

	private Status status;
	private PullToRefreshScrollView pscroll;
	private ScrollView scrollView;
	private Holder holder;
	private SDMAdapter adapter;
	private List<Message> messages;
	private String oldMessageId;
	private Button btn_back, btn_exp, btn_post;
	private TextView txt_praise, txt_message;
	private View view_content, view_praise, view_message, view_pm, view_post, view_exp;
	private ImageView img_praise, img_delete;
	private boolean flag_praise;
	private EditText edit_message;
	private GridView grid_exp;
	private int[] imageIds = new int[105];
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_detail);
		IfanService.addActivity(this);
		initData();
		/**
		 * 因为有米广告下载安装后有可能app数据会被清掉。
		 * 这个时候先做成返回，不然进来会报错，空指针异常
		 */
		if (null == status) {	
			finish();
		} else {
			initView();
		}
	}
	
	@Override
	public void initData() {
		FanApplication application = (FanApplication) getApplication();
		status = application.getStatus();
	}
	
	private class Holder {
		ImageView img_photo;
		TextView txt_nickname;
		TextView txt_release_time;
		TextView txt_content;
		View view_op;
		View view_mp;
		ImageView img_photos;
		MyGridView mgrid_photos;
		MyListView mlist_message;
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_status_detail);
		txt_praise = (TextView)findViewById(R.id.txt_sd_praise);
		txt_message = (TextView)findViewById(R.id.txt_sd_message);
		view_praise = findViewById(R.id.layout_sd_praise);
		view_message = findViewById(R.id.layout_sd_message);
		view_pm = findViewById(R.id.layout_sd_praise_message);
		view_post = findViewById(R.id.layout_post_message);
		img_praise = (ImageView)findViewById(R.id.img_sd_praise);
		btn_exp = (Button)findViewById(R.id.btn_post_message_expression);
		edit_message = (EditText)findViewById(R.id.edit_post_message_message);
		btn_post = (Button)findViewById(R.id.btn_post_message_post);
		view_exp = findViewById(R.id.layout_expression);
		grid_exp = (GridView)findViewById(R.id.grid_expression);
		img_delete = (ImageView)findViewById(R.id.img_expression_delete);
		progressDialog = new ProgressDialog(this);
		
		pscroll = (PullToRefreshScrollView)findViewById(R.id.pscroll_status_detail);
		scrollView = pscroll.getRefreshableView();
		view_content = LayoutInflater.from(this).inflate(R.layout.status_detail_content, null);
		scrollView.addView(view_content);
		holder = new Holder();
		holder.img_photo = (ImageView)view_content.findViewById(R.id.img_status_photo);
		holder.txt_nickname = (TextView)view_content.findViewById(R.id.txt_status_nickname);
		holder.txt_release_time = (TextView)view_content.findViewById(R.id.txt_status_releasetime);
		holder.txt_content = (TextView)view_content.findViewById(R.id.txt_status_content);
		holder.view_op = view_content.findViewById(R.id.layout_sdc_op);
		holder.view_mp = view_content.findViewById(R.id.layout_sdc_mp);
		holder.img_photos = (ImageView)view_content.findViewById(R.id.img_status_photos);
		holder.mgrid_photos = (MyGridView)view_content.findViewById(R.id.grid_status_photos);
		holder.mlist_message = (MyListView)view_content.findViewById(R.id.mlist_status_detail);
		
		String isOfficail = status.getIsOfficial();
		if (isOfficail.equals(Status.ISOFFICIAL_YES)) {
			holder.img_photo.setImageResource(R.drawable.img_official);
			holder.txt_nickname.setText(Status.OFFICIAL_NICKNAME);
		} else if (isOfficail.equals(Status.ISOFFICIAL_NO)) {
			holder.img_photo.setImageResource(R.drawable.img_anony);
			holder.txt_nickname.setText(Status.ANONY_NICKNAME);
		}
		String isTop = status.getIsTop();
		if (isTop.equals(Status.ISTOP_YES)) {
			
		} else if (isTop.equals(Status.ISTOP_NO)) {
			
		}
		String releaseTime = TimeUtil.getDisplayTime(TimeUtil.getNow(), status.getReleaseTime());
		holder.txt_release_time.setText(releaseTime);
		holder.txt_content.setText(status.getContent());
		String[] photos = status.getPhotos().split(",");
		if (1 == photos.length) {
			if ("".equals(photos[0])) {
				holder.view_op.setVisibility(View.GONE);
				holder.view_mp.setVisibility(View.GONE);
			} else {
				String smallUrl = StringUtil.getSmallPhoto(photos[0], NormalString.SmallPhoto.STATUS_ONE);
				FinalBitmap finalBitmap = FinalBitmap.create(this);
				finalBitmap.display(holder.img_photos, smallUrl);
				holder.img_photos.setOnClickListener(new NetPhotosListener(this, photos));
				holder.view_mp.setVisibility(View.GONE);
			}
		} else if (photos.length > 1) {
			SGPhotosAdapter adapter = new SGPhotosAdapter(this, photos);
			holder.mgrid_photos.setAdapter(adapter);
			holder.mgrid_photos.setOnItemClickListener(new NetPhotosGridListener(this, photos));
			holder.view_op.setVisibility(View.GONE);
		}
		
		pscroll.setPullLoadEnabled(true);
		pscroll.setScrollLoadEnabled(false);
		pscroll.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				doGetSDMessageTask(Task.FAN_PTOR, null);
			}
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				doGetSDMessageTask(Task.FAN_LM, oldMessageId);
			}
		});
		pscroll.doPullRefreshing(true, 500);
		
		txt_praise.setText(status.getPraiseCount());
		txt_message.setText(status.getMessageCount());
		createExpressionGridView();
		grid_exp.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageIds[arg2]);
				ImageSpan imageSpan = new ImageSpan(StatusDetailActivity.this, bitmap);
				String str;
				if (arg2 < 10) {
					str = "[fac00" + arg2;
				} else if (arg2 < 100) {
					str = "[fac0" + arg2;
				} else {
					str = "[fac" + arg2;
				}
				SpannableString spannableString = new SpannableString(str);
				spannableString.setSpan(imageSpan, 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				int selectionStart = edit_message.getSelectionStart();
				edit_message.getText().insert(selectionStart, spannableString);
			}
		});
	}
	
	private void createExpressionGridView() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 105; i++) {
			try {
				Field field = R.drawable.class.getDeclaredField("smiley_" + i);
				int resourceId = Integer.parseInt(field.get(null).toString());
				imageIds[i] = resourceId;
			} catch (Exception e) {
				e.printStackTrace();
			}
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", imageIds[i]);
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.expression_cell, new String[] { "image" },
				new int[] { R.id.img_expression_cell });
		grid_exp.setAdapter(simpleAdapter);
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
	
	private void doGetSDMessageTask(String mode, String id) {
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, StatusDetailActivity.class.getSimpleName());
		taskParams.put(Task.FAN_MODE, mode);
		taskParams.put(Status.class.getSimpleName() + Status.ID, status.getId());
		taskParams.put(Message.class.getSimpleName() + Message.ID, id);
		taskParams.put(Task.FAN_GETSDM_ISINFO, Task.FAN_GETSDM_ISINFO_NO);
		Task task = new Task(Task.FAN_GETSDM, taskParams);
		IfanService.addTask(task);
	}
	
	private void doPraiseStatusTask() {
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, StatusDetailActivity.class.getSimpleName());
		taskParams.put(Status.ID, status.getId());
		if (flag_praise) {
			taskParams.put(Task.FAN_MODE, Task.FAN_PS_CANCEL);
		} else {
			taskParams.put(Task.FAN_MODE, Task.FAN_PS_PRAISE);
		}
		Task task = new Task(Task.FAN_PRAISESTATUS, taskParams);
		IfanService.addTask(task);
	}
	
	public void doSDMessageTask() {
		showProgressDialog();
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, StatusDetailActivity.class.getSimpleName());
		taskParams.put(Status.ID, status.getId());
		taskParams.put(Message.CONTENT, edit_message.getText().toString().trim());
		Task task = new Task(Task.FAN_SDMESSAGE, taskParams);
		IfanService.addTask(task);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		switch (taskId) {
		case Task.FAN_GETSDM:
			String mode = (String) map.get(Task.FAN_MODE);
			Map<String, Object> sdm = (HashMap<String, Object>) map.get(JsonString.Return.RESULT);
			refreshList(mode, sdm);
			break;
			
		case Task.FAN_PRAISESTATUS:
			String praise = (String) map.get(JsonString.Return.RESULT);
			refreshPraise(praise);
			break;
			
		case Task.FAN_SDMESSAGE:
			String message = (String) map.get(JsonString.Return.RESULT);
			refreshMessage(message);
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void refreshList(String mode, Map<String, Object> sdm) {
		if (sdm != null) {
			String isPraise = (String) sdm.get(JsonString.MapSDM.IS_PRAISE);
			if (isPraise.equals("true")) {
				img_praise.setImageResource(R.drawable.img_ispraise_yes);
				flag_praise = true;
			} else {
				flag_praise = false;
			}
			
			List<Message> newMessages = (List<Message>) sdm.get(JsonString.MapSDM.MESSAGE_LIST);
			if (newMessages.size() > 0) {
				if (mode.equals(Task.FAN_PTOR)) {
					messages = newMessages;
					adapter = new SDMAdapter(this, messages, status.getPersonId());
					holder.mlist_message.setAdapter(adapter);
				} else if (mode.equals(Task.FAN_LM)) {
					messages.addAll(newMessages);
					adapter.notifyDataSetChanged();
				}
				//因为服务器数据库原因，最旧留言的id就这样做了
				oldMessageId = (String) sdm.get(JsonString.MapSDM.END_INDEX);
			} else {
				if (mode.equals(Task.FAN_PTOR)) {
					//没有留言
				} else if (mode.equals(Task.FAN_LM)) {
					ToastUtil.prompt(this, "已经加载全部内容");
				}
			}
			registerListener();
		} else {
			ToastUtil.noNet(this);
		}
		pscroll.onPullDownRefreshComplete();
		pscroll.onPullUpRefreshComplete();
		pscroll.setLastUpdatedLabel(TimeUtil.setLastestUpdata());
	}
	
	private void refreshPraise(String praise) {
		if (praise != null) {
			try {
				JSONObject jsonObject = new JSONObject(praise);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {
					if (flag_praise) {
						ToastUtil.prompt(this, "取消赞成功！");
						img_praise.setImageResource(R.drawable.img_ispraise_no);
						int curPraise = Integer.parseInt(txt_praise.getText().toString()) - 1;
						txt_praise.setText(String.valueOf(curPraise));
					} else {
						ToastUtil.prompt(this, "赞成功！");
						img_praise.setImageResource(R.drawable.img_ispraise_yes);
						int curPraise = Integer.parseInt(txt_praise.getText().toString()) + 1;
						txt_praise.setText(String.valueOf(curPraise));
					}
					flag_praise = !flag_praise;
				} else {
					ToastUtil.prompt(this, "赞操作失败！");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	private void refreshMessage(String message) {
		if (message != null) {
			try {
				JSONObject jsonObject = new JSONObject(message);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {
					ToastUtil.prompt(this, "留言成功！");
					/**
					 * 因为新的回复在下面，所以自己回复后只能去加载更多。
					 * 如果调用刷新，而前面的都不会变，没有意义
					 */
					if (oldMessageId != null) {
						doGetSDMessageTask(Task.FAN_LM, oldMessageId);
					} else {
						doGetSDMessageTask(Task.FAN_PTOR, null);
					}
				} else {
					ToastUtil.prompt(this, "留言失败！");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (view_exp.isShown()) { // 如果显示着表情的时候，先把表情布局取消掉
			Animation animation = AnimationUtils.loadAnimation(
					StatusDetailActivity.this,
					R.anim.expression_exit);
			view_exp.setAnimation(animation);
			view_exp.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
	}
	
	private void registerListener() {
		MyOnClickListener clickListener = new MyOnClickListener();
		btn_back.setOnClickListener(clickListener);
		view_content.setOnClickListener(clickListener);
		view_praise.setOnClickListener(clickListener);
		view_message.setOnClickListener(clickListener);
		btn_exp.setOnClickListener(clickListener);
		edit_message.setOnClickListener(clickListener);
		btn_post.setOnClickListener(clickListener);
		img_delete.setOnClickListener(clickListener);
		
		MyOnTouchListener touchListener = new MyOnTouchListener(); 
		holder.mlist_message.setOnTouchListener(touchListener);
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_status_detail:
				finish();
				break;
				
			case R.id.layout_sd_content:
				if (view_post.isShown()) {
					KeyBoardUtil.dismiss(StatusDetailActivity.this, view_post);	
			       	view_post.setVisibility(View.GONE);
			       	view_exp.setVisibility(View.GONE);
			       	view_pm.setVisibility(View.VISIBLE);
				}
				break;
				
			case R.id.layout_sd_praise:
				doPraiseStatusTask();
				break;
			
			case R.id.layout_sd_message:
				view_pm.setVisibility(View.GONE);
				view_post.setVisibility(View.VISIBLE);
				edit_message.requestFocus();
				KeyBoardUtil.show(StatusDetailActivity.this, edit_message);
				break;
				
			case R.id.btn_post_message_expression:
				new Handler().postDelayed(new Runnable() {
					public void run() {
						Animation animation = AnimationUtils.loadAnimation(
								StatusDetailActivity.this, R.anim.expression_enter);
						view_exp.setAnimation(animation);
						view_exp.setVisibility(View.VISIBLE);
					}
				}, 200);
				KeyBoardUtil.dismiss(StatusDetailActivity.this, edit_message);
				break;
				
			case R.id.edit_post_message_message:
				view_exp.setVisibility(View.GONE);
				break;
				
			case R.id.btn_post_message_post:
				if (!edit_message.getText().toString().trim().isEmpty()) {
					doSDMessageTask();
					edit_message.setText("");
					KeyBoardUtil.dismiss(StatusDetailActivity.this, edit_message);
					view_exp.setVisibility(View.GONE);
				}
				break;
				
			case R.id.img_expression_delete:
				int selectionStart = edit_message.getSelectionStart();// 获取光标的位置
				if (selectionStart > 0) {
					String body = edit_message.getText().toString();
					if (!TextUtils.isEmpty(body)) {
						String tempStr = body.substring(0, selectionStart);
						int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
						if (i != -1) {
							CharSequence cs = tempStr.subSequence(i, selectionStart - 3);
							if (cs.equals("[fac")) {// 判断是否是一个表情
								edit_message.getEditableText().delete(i, selectionStart);
								return;
							}
						}
						edit_message.getEditableText().delete(selectionStart - 1, selectionStart);
					}
				}
				break;
			}
		}
	}
	
	class MyOnTouchListener implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.mlist_status_detail:
				if (view_post.isShown()) {
					KeyBoardUtil.dismiss(StatusDetailActivity.this, view_post);	
			       	view_post.setVisibility(View.GONE);
			       	view_exp.setVisibility(View.GONE);
			       	view_pm.setVisibility(View.VISIBLE);
			       	return true;
				}
				break;
			}
			return false;
		}
	}

}