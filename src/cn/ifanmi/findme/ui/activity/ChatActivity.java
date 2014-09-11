package cn.ifanmi.findme.ui.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.ChatAdapter;
import cn.ifanmi.findme.bean.Friend;
import cn.ifanmi.findme.em.AlertDialogActivity;
import cn.ifanmi.findme.em.BaiduMapActivity;
import cn.ifanmi.findme.em.ImageGridActivity;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.KeyBoardUtil;
import cn.ifanmi.findme.util.SdCardUtil;
import cn.ifanmi.findme.util.ToastUtil;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;

public class ChatActivity extends Activity implements IfanActivity {

	private UtilHolder uh;
	private TopHolder th;
	private CenterHolder ch;
	private BottomHolder bh;
	public static int resendPos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ch.img_recording.setImageDrawable(ch.drawables[msg.what]);
		}
	};
	
	private class UtilHolder {
		String friendId;
		String friendPhoto;
		String nickname;
		String curFileName;
		ClipboardManager clipboard;
		EMConversation conversation;
		VoiceRecorder voiceRecorder;
		NewMessageBroadcastReceiver nmReceiver;
		AckMessageBroadcastReceiver amReceiver;
	}
	
	private class TopHolder {
		Button btn_back;
		TextView txt_bar;
		Button btn_delete;
	}
	
	private class CenterHolder {
		ProgressBar loadmorePB;
		boolean isloading;
		boolean haveMoreData = true;
		View view_recording;
		ImageView img_recording;
		TextView txt_recording;
		Drawable[] drawables;
		ListView listView;
		ChatAdapter adapter;
	}
	
	private class BottomHolder {
		Button btn_more;
		EditText edit_message;
		View view_ptos;
		View view_edit;
		ImageView img_exp;
		Button btn_voice; 
		Button btn_keyboard; 
		Button btn_send;
		View view_more;
		View view_exp;
		GridView grid_exp;
		int[] imageIds = new int[105];
		ImageView img_delexp;
		View view_container;
		ImageView img_photo;
		ImageView img_picture;
		ImageView img_location;
		ImageView img_video;
		ImageView img_file;
	}
	
	@Override
	public void initData() {
		uh = new UtilHolder();
		uh.friendId = getIntent().getStringExtra(Friend.ID);
		uh.friendPhoto = getIntent().getStringExtra(Friend.PHOTO);
		uh.nickname = getIntent().getStringExtra(Friend.NICKNAME);
		uh.clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		uh.conversation = EMChatManager.getInstance().getConversation(uh.friendId);
		uh.conversation.resetUnsetMsgCount();
		uh.voiceRecorder = new VoiceRecorder(handler);
	}

	@Override
	public void initView() {
		th = new TopHolder();
		ch = new CenterHolder();
		bh = new BottomHolder();
		
		th.btn_back = (Button)findViewById(R.id.btn_back_chat);
		th.txt_bar = (TextView)findViewById(R.id.txt_bar_chat);
		th.btn_delete = (Button)findViewById(R.id.btn_delete_chat);
		
		ch.loadmorePB = (ProgressBar)findViewById(R.id.pb_chat_center);
		ch.view_recording = findViewById(R.id.layout_chat_center_recording);
		ch.img_recording = (ImageView)findViewById(R.id.img_chat_center_recording);
		ch.txt_recording = (TextView)findViewById(R.id.txt_chat_center_recording);
		// 动画资源文件,用于录制语音时显示
		ch.drawables = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02), 
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04), 
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06), 
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08), 
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10), 
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12), 
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };
		ch.listView = (ListView)findViewById(R.id.list_chat_center);
		
		bh.btn_more = (Button)findViewById(R.id.btn_cbpm_more);
		bh.edit_message = (EditText)findViewById(R.id.edit_cbpm);
		bh.view_ptos = findViewById(R.id.layout_cbpm_ptos);
		bh.view_edit = findViewById(R.id.layout_cbpm_edit);
		bh.img_exp = (ImageView)findViewById(R.id.img_cbpm_exp);
		bh.btn_voice = (Button)findViewById(R.id.btn_cbpm_voice);
		bh.btn_keyboard = (Button)findViewById(R.id.btn_cbpm_keyboard);
		bh.btn_send = (Button)findViewById(R.id.btn_cbpm_send);
		bh.view_more = findViewById(R.id.layout_cbm);
		bh.view_exp = findViewById(R.id.layout_cbm_exp);
		bh.grid_exp = (GridView)findViewById(R.id.grid_cbm_exp);
		bh.img_delexp = (ImageView)findViewById(R.id.img_cbm_exp_delete);
		bh.view_container = findViewById(R.id.layout_cbm_container);
		bh.img_photo = (ImageView)findViewById(R.id.img_cbm_photo);
		bh.img_picture = (ImageView)findViewById(R.id.img_cbm_picture);
		bh.img_location = (ImageView)findViewById(R.id.img_cbm_location);
		bh.img_video = (ImageView)findViewById(R.id.img_cbm_video);
		bh.img_file = (ImageView)findViewById(R.id.img_cbm_file);
		
		th.txt_bar.setText(uh.nickname);
		
		ch.adapter = new ChatAdapter(this, uh.friendId, uh.friendPhoto);
		ch.listView.setAdapter(ch.adapter);
		ch.listView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					if (view.getFirstVisiblePosition() == 0 && !ch.isloading && ch.haveMoreData) {
						ch.loadmorePB.setVisibility(View.VISIBLE);
						List<EMMessage> messages;
						try {
							messages = uh.conversation.loadMoreMsgFromDB(
									ch.adapter.getItem(0).getMsgId(), NormalString.Other.EM_PAGESIZE);
						} catch (Exception e1) {
							ch.loadmorePB.setVisibility(View.GONE);
							return;
						}
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
						}
						if (messages.size() != 0) {
							ch.adapter.notifyDataSetChanged();
							ch.listView.setSelection(messages.size() - 1);
							if (messages.size() != NormalString.Other.EM_PAGESIZE)
								ch.haveMoreData = false;
						} else {
							ch.haveMoreData = false;
						}
						ch.loadmorePB.setVisibility(View.GONE);
						ch.isloading = false;
					}
					break;
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		if (ch.listView.getCount() > 0) {
			ch.listView.setSelection(ch.listView.getCount() - 1);
		}
		
		bh.edit_message.requestFocus();
		bh.edit_message.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			public void afterTextChanged(Editable s) {
				String string = bh.edit_message.getText().toString();
				if (string.isEmpty()) {
					if (!bh.btn_keyboard.isShown()) {
						bh.btn_voice.setVisibility(View.VISIBLE);
						bh.btn_send.setVisibility(View.GONE);
					}
				} else {
					bh.btn_voice.setVisibility(View.GONE);
					bh.btn_send.setVisibility(View.VISIBLE);
				}
			}
		});
		createExpressionGridView();
		bh.grid_exp.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bh.imageIds[arg2]);
				ImageSpan imageSpan = new ImageSpan(ChatActivity.this, bitmap);
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
				int selectionStart = bh.edit_message.getSelectionStart();
				bh.edit_message.getText().insert(selectionStart, spannableString);
			}
		});
		
		MyOnClickListener clickListener = new MyOnClickListener();
		th.btn_back.setOnClickListener(clickListener);
		th.btn_delete.setOnClickListener(clickListener);
		bh.btn_more.setOnClickListener(clickListener);
		bh.edit_message.setOnClickListener(clickListener);
		bh.img_exp.setOnClickListener(clickListener);
		bh.btn_voice.setOnClickListener(clickListener);
		bh.btn_keyboard.setOnClickListener(clickListener);
		bh.btn_send.setOnClickListener(clickListener);
		bh.img_delexp.setOnClickListener(clickListener);
		bh.img_photo.setOnClickListener(clickListener);
		bh.img_picture.setOnClickListener(clickListener);
		bh.img_location.setOnClickListener(clickListener);
		bh.img_video.setOnClickListener(clickListener);
		bh.img_video.setOnClickListener(clickListener);
		bh.img_file.setOnClickListener(clickListener);
		
		MyOnTouchListener touchListener = new MyOnTouchListener();
		ch.listView.setOnTouchListener(touchListener);
		bh.view_ptos.setOnTouchListener(touchListener);
		
		uh.nmReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = 
				new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		/**
		 * 设置广播的优先级别大于HomePageAcitivity,
		 * 这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		 */
		intentFilter.setPriority(5);
		registerReceiver(uh.nmReceiver, intentFilter);

		uh.amReceiver = new AckMessageBroadcastReceiver();
		IntentFilter ackMessageIntentFilter = 
				new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(5);
		registerReceiver(uh.amReceiver, ackMessageIntentFilter);
	}
	
	private void createExpressionGridView() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 105; i++) {
			try {
				Field field = R.drawable.class.getDeclaredField("smiley_" + i);
				int resourceId = Integer.parseInt(field.get(null).toString());
				bh.imageIds[i] = resourceId;
			} catch (Exception e) {
				e.printStackTrace();
			}
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", bh.imageIds[i]);
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.expression_cell, new String[] { "image" },
				new int[] { R.id.img_expression_cell });
		bh.grid_exp.setAdapter(simpleAdapter);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == IntentString.RequestCode.CHAT_CONTEXTMENU) {
			switch (resultCode) {
			case IntentString.ResultCode.CONTEXTMENU_CHAT_COPY:
				EMMessage copyMsg = ((EMMessage) ch.adapter.getItem(
						data.getIntExtra(IntentString.Extra.ALERT_POSITION, -1)));
				if (copyMsg.getType() == EMMessage.Type.TXT) {
					uh.clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
				} else {
					//先不做其它类型的复制
				}
				break;
			
			case IntentString.ResultCode.CONTEXTMENU_CHAT_DELETE: // 删除消息
				EMMessage deleteMsg = (EMMessage) ch.adapter.getItem(data.getIntExtra("position", -1));
				uh.conversation.removeMessage(deleteMsg.getMsgId());
				ch.adapter.notifyDataSetChanged();
				ch.listView.setSelection(data.getIntExtra("position", ch.adapter.getCount()) - 1);
				break;
				
			case IntentString.ResultCode.CONTEXTMENU_CHAT_FORWARD:
				//转发先不做
				break;
			}
		}
		if (resultCode == RESULT_OK) { 
			switch (requestCode) {
			case IntentString.RequestCode.CHAT_ALERT_EMPTY:
				EMChatManager.getInstance().clearConversation(uh.friendId);
				ch.adapter.notifyDataSetChanged();
				break;
				
			case IntentString.RequestCode.IMAGE:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null)
						sendPicByUri(selectedImage);
				}
				break;
				
			case IntentString.RequestCode.CAMERA:
				File tempFile = new File(
						Environment.getExternalStorageDirectory().getAbsolutePath() + NormalString.File.DIR_FANMI, 
						uh.curFileName);
				sendPicture(tempFile.getAbsolutePath());
				break;
				
			case IntentString.RequestCode.CHAT_BAIDUMAP:
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				String locationAddress = data.getStringExtra("address");
				if (locationAddress != null && !locationAddress.equals("")) {
					//moreClick();
					sendLocationMsg(latitude, longitude, "", locationAddress);
				} else {
					ToastUtil.prompt(this, "无法获取到您的位置信息！");
				}
				break;
				
			case IntentString.RequestCode.CHAT_VIDEO:
				int duration = data.getIntExtra("dur", 0);
				String videoPath = data.getStringExtra("path");
				sendVideo(videoPath, duration / 1000);
				break;
				
			case IntentString.RequestCode.CHAT_FILE:
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFile(uri);
					}
				}
				break;
				
			case IntentString.RequestCode.CHAT_ALERT_TEXT:
			case IntentString.RequestCode.CHAT_ALERT_VOICE:
			case IntentString.RequestCode.CHAT_ALERT_PICTURE:
			case IntentString.RequestCode.CHAT_ALERT_LOCATION:
			case IntentString.RequestCode.CHAT_ALERT_VIDEO:
			case IntentString.RequestCode.CHAT_ALERT_FILE:
				resendMessage();
				break;
				
			default:
				ch.adapter.notifyDataSetChanged();
				setResult(RESULT_OK);
				break;
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		ch.adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		if (bh.view_more.isShown()) {
			bh.view_more.setVisibility(View.GONE);
			bh.img_exp.setImageResource(R.drawable.img_cbpm_exp_normal);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
		unregisterReceiver(uh.nmReceiver);
		unregisterReceiver(uh.amReceiver);
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_chat:
				finish();
				break;
				
			case R.id.btn_delete_chat:
				emptyHistory();
				break;
				
			case R.id.btn_cbpm_more:
				moreClick();
				break;
				
			case R.id.edit_cbpm:
				editClick();
				break;
				
			case R.id.img_cbpm_exp:
				setModeExp();
				break;
				
			case R.id.btn_cbpm_voice:
				setModeVoice();
				break;
				
			case R.id.btn_cbpm_keyboard:
				setModeKeyboard();
				break;
				
			case R.id.btn_cbpm_send:
				sendText();
				break;
				
			case R.id.img_cbm_exp_delete:
				deleteExp();
				break;
				
			case R.id.img_cbm_photo:
				getPicFromCamera();
				break;
				
			case R.id.img_cbm_picture:
				getPicFromLocal();
				break;
				
			case R.id.img_cbm_location:
				Intent locationIntent = new Intent(ChatActivity.this, BaiduMapActivity.class);
				startActivityForResult(locationIntent, IntentString.RequestCode.CHAT_BAIDUMAP);
				break;
				
			case R.id.img_cbm_video:
				Intent videoIntent = new Intent(ChatActivity.this, ImageGridActivity.class);
				startActivityForResult(videoIntent, IntentString.RequestCode.CHAT_VIDEO);
				break;
				
			case R.id.img_cbm_file:
				getFileFromLocal();
				break;
			}
		}
	}
	
	class MyOnTouchListener implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.list_chat_center:
				KeyBoardUtil.dismiss(ChatActivity.this, ch.listView);
				bh.view_more.setVisibility(View.GONE);
				bh.img_exp.setImageResource(R.drawable.img_cbpm_exp_normal);
				bh.view_exp.setVisibility(View.GONE);
				bh.view_container.setVisibility(View.GONE);
				return false;
			case R.id.layout_cbpm_ptos:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (!SdCardUtil.hasSdcard()) {
						ToastUtil.prompt(ChatActivity.this, "发送语音需要sdcard支持！");
						return false;
					}
					try {
						v.setPressed(true);
						ch.view_recording.setVisibility(View.VISIBLE);
						ch.txt_recording.setText(getString(R.string.move_up_to_cancel));
						ch.txt_recording.setBackgroundColor(Color.TRANSPARENT);
						uh.voiceRecorder.startRecording(null, uh.friendId, getApplicationContext());
					} catch (Exception e) {
						v.setPressed(false);
						ch.view_recording.setVisibility(View.INVISIBLE);
						ToastUtil.prompt(ChatActivity.this, "录音失败，请重试！");
						return false;
					}
					return true;
					
				case MotionEvent.ACTION_MOVE: 
					if (event.getY() < 0) {
						ch.txt_recording.setText(getString(R.string.release_to_cancel));
						ch.txt_recording.setBackgroundResource(R.drawable.txt_recording_bg);
					} else {
						ch.txt_recording.setText(getString(R.string.move_up_to_cancel));
						ch.txt_recording.setBackgroundColor(Color.TRANSPARENT);
					}
					return true;
					
				case MotionEvent.ACTION_UP:
					v.setPressed(false);
					ch.view_recording.setVisibility(View.INVISIBLE);
					if (event.getY() < 0) {
						uh.voiceRecorder.discardRecording();
					} else {
						try {
							int length = uh.voiceRecorder.stopRecoding();
							if (length > 0) {
								sendVoice(uh.voiceRecorder.getVoiceFilePath(), 
										uh.voiceRecorder.getVoiceFileName(uh.friendId),
										Integer.toString(length), false);
							}
						} catch (Exception e) {
							ToastUtil.prompt(ChatActivity.this, "发送失败，请检测服务器是否连接");
						}
					}
					return true;
					
				default:
					return false;
				}

			default:
				return false;
			}
		}
	}
	
	private void emptyHistory() {
		Intent intent = new Intent(this, AlertDialogActivity.class);
		intent.putExtra(IntentString.Extra.ALERT_TITLEISCANCLE, true);
		intent.putExtra(IntentString.Extra.ALERT_MESSAGE, "是否清空所有聊天记录");
		intent.putExtra(IntentString.Extra.ALERT_CANCLE, true);
		startActivityForResult(intent, IntentString.RequestCode.CHAT_ALERT_EMPTY);
	}
	
	private void moreClick() {
		if (!bh.view_more.isShown()) {
			KeyBoardUtil.dismiss(this, bh.edit_message);
			bh.view_more.setVisibility(View.VISIBLE);
			bh.view_container.setVisibility(View.VISIBLE);
			bh.view_exp.setVisibility(View.GONE);
		} else {
			if (bh.view_exp.isShown()) {
				bh.view_exp.setVisibility(View.GONE);
				bh.view_container.setVisibility(View.VISIBLE);
				bh.img_exp.setImageResource(R.drawable.img_cbpm_exp_normal);
			} else {
				bh.view_more.setVisibility(View.GONE);
			}
		}

	}
	
	private void editClick() {
		ch.listView.setSelection(ch.listView.getCount() - 1);
		if (bh.view_more.isShown()) {
			bh.view_more.setVisibility(View.GONE);
			bh.img_exp.setImageResource(R.drawable.img_cbpm_exp_normal);
		}

	}
	
	private void setModeExp() {
		bh.img_exp.setImageResource(R.drawable.img_cbpm_exp_enable);
		bh.view_container.setVisibility(View.GONE);
		bh.view_more.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Animation animation = AnimationUtils.loadAnimation(
						ChatActivity.this,
						R.anim.expression_enter);
				bh.view_exp.setAnimation(animation);
				bh.view_exp.setVisibility(View.VISIBLE);
			}
		}, 200);
		KeyBoardUtil.dismiss(ChatActivity.this, bh.edit_message);
	}
	
	private void setModeVoice() {
		bh.view_edit.setVisibility(View.GONE);
		KeyBoardUtil.dismiss(this, bh.edit_message);
		bh.view_more.setVisibility(View.GONE);
		bh.btn_voice.setVisibility(View.GONE);
		bh.btn_keyboard.setVisibility(View.VISIBLE);
		bh.view_ptos.setVisibility(View.VISIBLE);
		bh.img_exp.setImageResource(R.drawable.img_cbpm_exp_normal);
		bh.view_container.setVisibility(View.VISIBLE);
		bh.view_exp.setVisibility(View.GONE);
	}

	private void setModeKeyboard() {
		bh.view_edit.setVisibility(View.VISIBLE);
		bh.view_exp.setVisibility(View.GONE);
		bh.view_more.setVisibility(View.GONE);
		bh.btn_keyboard.setVisibility(View.GONE);
		bh.btn_voice.setVisibility(View.VISIBLE);
		bh.edit_message.requestFocus();
		bh.view_ptos.setVisibility(View.GONE);
	}
	
	public void getPicFromCamera() {
		if (!SdCardUtil.hasSdcard()) {  
			ToastUtil.prompt(this, "未找到存储卡，无法存储照片！");
        } else { 
        	File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
        			+ NormalString.File.DIR_FANMI);  
            if(!file.exists()){  
                file.mkdir();  
            }  
            uh.curFileName = System.currentTimeMillis() + ".png";
            File fileName = new File(
            		Environment.getExternalStorageDirectory().getAbsolutePath() + NormalString.File.DIR_FANMI, 
            		uh.curFileName);  
            Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileName));
			startActivityForResult(intentFromCapture, IntentString.RequestCode.CAMERA);
		}
	}
	
	public void getPicFromLocal() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*"); // 设置文件类型
		startActivityForResult(intent, IntentString.RequestCode.IMAGE);
	}
	
	private void getFileFromLocal() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, IntentString.RequestCode.CHAT_FILE);
	}
	
	private void sendText() {
		String content = bh.edit_message.getText().toString();
		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			TextMessageBody txtBody = new TextMessageBody(content);
			message.addBody(txtBody);
			message.setReceipt(uh.friendId);
			uh.conversation.addMessage(message);
			ch.adapter.notifyDataSetChanged();
			ch.listView.setSelection(ch.listView.getCount() - 1);
			bh.edit_message.setText("");
			setResult(RESULT_OK);
		}
	}
	
	private void deleteExp() {
		int selectionStart = bh.edit_message.getSelectionStart();// 获取光标的位置
		if (selectionStart > 0) {
			String body = bh.edit_message.getText().toString();
			if (!TextUtils.isEmpty(body)) {
				String tempStr = body.substring(0, selectionStart);
				int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
				if (i != -1) {
					CharSequence cs = tempStr.subSequence(i, selectionStart - 3);
					if (cs.equals("[fac")) {// 判断是否是一个表情
						bh.edit_message.getEditableText().delete(i, selectionStart);
						return;
					}
				}
				bh.edit_message.getEditableText().delete(selectionStart - 1, selectionStart);
			}
		}
	}
	
	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			String to = uh.friendId;
			message.setReceipt(to);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
			message.addBody(body);

			uh.conversation.addMessage(message);
			ch.adapter.notifyDataSetChanged();
			ch.listView.setSelection(ch.listView.getCount() - 1);
			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendPicture(final String filePath) {
		String to = uh.friendId;
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		message.addBody(body);
		uh.conversation.addMessage(message);
		ch.adapter.notifyDataSetChanged();
		ch.listView.setSelection(ch.listView.getCount() - 1);
		setResult(RESULT_OK);
	}
	
	private void sendPicByUri(Uri selectedImage) {
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex("_data");
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		if (picturePath == null || picturePath.equals("null")) {
			ToastUtil.prompt(this, "找不到图片");
			return;
		}
		sendPicture(picturePath);
	}
	
	private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
		LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
		message.addBody(locBody);
		message.setReceipt(uh.friendId);
		uh.conversation.addMessage(message);
		ch.adapter.notifyDataSetChanged();
		ch.listView.setSelection(ch.listView.getCount()-1);
		setResult(RESULT_OK);
	}
	
	private void sendVideo(final String filePath, final int length) {
		File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
		Bitmap bitmap = null;
		FileOutputStream fos = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			bitmap = ThumbnailUtils.createVideoThumbnail(filePath, 3);
			if (bitmap == null) {
				EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
			}
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
		}
		
		final File videoFile = new File(filePath);
		if (!videoFile.exists()) {
			return;
		}
		try {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
			String to = uh.friendId;
			message.setReceipt(to);
			VideoMessageBody body = new VideoMessageBody(videoFile, file.getAbsolutePath(), length, videoFile.length());
			message.addBody(body);
			uh.conversation.addMessage(message);
			ch.adapter.notifyDataSetChanged();
			ch.listView.setSelection(ch.listView.getCount() - 1);
			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void sendFile(Uri uri) {
		String filePath = null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;
			try {
				cursor = getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			ToastUtil.prompt(this, "文件不存在");
			return;
		}
		if (file.length() > 10 * 1024 * 1024) {
			ToastUtil.prompt(this, "文件不能大于10M");
			return;
		}

		// 创建一个文件消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
		message.setReceipt(uh.friendId);
		// add message body
		NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
		message.addBody(body);

		uh.conversation.addMessage(message);
		ch.adapter.notifyDataSetChanged();
		ch.listView.setSelection(ch.listView.getCount() - 1);
		setResult(RESULT_OK);
	}
	
	private void resendMessage() {
		EMMessage msg = null;
		msg = uh.conversation.getMessage(resendPos);
		msg.status = EMMessage.Status.CREATE;
		ch.adapter.notifyDataSetChanged();
		ch.listView.setSelection(resendPos);
	}
	
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String username = intent.getStringExtra("from");
			if (!username.equals(uh.friendId)) {
				return;	// 消息不是发给当前会话，return
			}
			ch.adapter.notifyDataSetChanged();
			ch.listView.setSelection(ch.listView.getCount() - 1);
			//中断这个广播，不要让HomePage再接收它
			abortBroadcast();
		}
	}

	private class AckMessageBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;	// 把message设为已读
				}
			}
			ch.adapter.notifyDataSetChanged();
			abortBroadcast();
		}
	};

}
