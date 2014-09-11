package cn.ifanmi.findme.ui.activity;

import java.util.Map;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.util.SPUtil;

public class InfoSettingActivity extends Activity implements IfanActivity {

	private EMChatOptions options;
	private Button btn_back;
	private View view_sound, view_vibrate;
	private ImageView img_notif, img_sound, img_vibrate, img_speaker, img_d1, img_d2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_setting);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		options = EMChatManager.getInstance().getChatOptions();
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_info_setting);
		view_sound = findViewById(R.id.layout_is_switch_sound);
		view_vibrate = findViewById(R.id.layout_is_switch_vibrate);
		img_notif = (ImageView)findViewById(R.id.img_is_switch_notification);
		img_sound = (ImageView)findViewById(R.id.img_is_switch_sound);
		img_vibrate = (ImageView)findViewById(R.id.img_is_switch_vibrate);
		img_speaker = (ImageView)findViewById(R.id.img_is_switch_speaker);
		img_d1 = (ImageView)findViewById(R.id.img_is_d1);
		img_d2 = (ImageView)findViewById(R.id.img_is_d2);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		img_notif.setOnClickListener(listener);
		img_sound.setOnClickListener(listener);
		img_vibrate.setOnClickListener(listener);
		img_speaker.setOnClickListener(listener);
		
		if (options.getNotificationEnable()) {
			img_notif.setImageResource(R.drawable.icon_open);
		} else {
			img_notif.setImageResource(R.drawable.icon_close);
			img_d1.setVisibility(View.GONE);
			view_sound.setVisibility(View.GONE);
			img_d2.setVisibility(View.GONE);
			view_vibrate.setVisibility(View.GONE);
		}
		
		if (options.getNoticedBySound()) {
			img_sound.setImageResource(R.drawable.icon_open);
		} else {
			img_sound.setImageResource(R.drawable.icon_close);
		}
		
		if (options.getNoticedByVibrate()) {
			img_vibrate.setImageResource(R.drawable.icon_open);
		} else {
			img_vibrate.setImageResource(R.drawable.icon_close);
		}
		
		if (options.getUseSpeaker()) {
			img_speaker.setImageResource(R.drawable.icon_open);
		} else {
			img_speaker.setImageResource(R.drawable.icon_close);
		}
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_info_setting:
				finish();
				break;
				
			case R.id.img_is_switch_notification:
				if (options.getNotificationEnable()) {
					options.setNotificationEnable(false);
					SPUtil.setEMChatOption(InfoSettingActivity.this, SPUtil.EMCHATOPTION_NOTIFICATION, false);
					img_notif.setImageResource(R.drawable.icon_close);
					img_d1.setVisibility(View.GONE);
					view_sound.setVisibility(View.GONE);
					img_d2.setVisibility(View.GONE);
					view_vibrate.setVisibility(View.GONE);
				} else {
					options.setNotificationEnable(true);
					SPUtil.setEMChatOption(InfoSettingActivity.this, SPUtil.EMCHATOPTION_NOTIFICATION, true);
					img_notif.setImageResource(R.drawable.icon_open);
					img_d1.setVisibility(View.VISIBLE);
					view_sound.setVisibility(View.VISIBLE);
					img_d2.setVisibility(View.VISIBLE);
					view_vibrate.setVisibility(View.VISIBLE);
				}
				break;
				
			case R.id.img_is_switch_sound:
				if (options.getNoticedBySound()) {
					options.setNoticeBySound(false);
					SPUtil.setEMChatOption(InfoSettingActivity.this, SPUtil.EMCHATOPTION_SOUND, false);
					img_sound.setImageResource(R.drawable.icon_close);
				} else {
					options.setNoticeBySound(true);
					SPUtil.setEMChatOption(InfoSettingActivity.this, SPUtil.EMCHATOPTION_SOUND, true);
					img_sound.setImageResource(R.drawable.icon_open);
				}
				break;
				
			case R.id.img_is_switch_vibrate:
				if (options.getNoticedByVibrate()) {
					options.setNoticedByVibrate(false);
					SPUtil.setEMChatOption(InfoSettingActivity.this, SPUtil.EMCHATOPTION_VIBRATE, false);
					img_vibrate.setImageResource(R.drawable.icon_close);
				} else {
					options.setNoticedByVibrate(true);
					SPUtil.setEMChatOption(InfoSettingActivity.this, SPUtil.EMCHATOPTION_VIBRATE, true);
					img_vibrate.setImageResource(R.drawable.icon_open);
				}
				break;
				
			case R.id.img_is_switch_speaker:
				if (options.getUseSpeaker()) {
					options.setUseSpeaker(false);
					SPUtil.setEMChatOption(InfoSettingActivity.this, SPUtil.EMCHATOPTION_SPEAKER, false);
					img_speaker.setImageResource(R.drawable.icon_close);
				} else {
					options.setUseSpeaker(true);
					SPUtil.setEMChatOption(InfoSettingActivity.this, SPUtil.EMCHATOPTION_SPEAKER, true);
					img_speaker.setImageResource(R.drawable.icon_open);
				}
				break;
			}
		}
	}

}
