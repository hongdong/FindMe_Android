package cn.ifanmi.findme.logic;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import net.tsz.afinal.bitmap.display.Displayer;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Status;
import cn.ifanmi.findme.dbService.FriendService;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.ui.activity.HomePageActivity;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.SPUtil;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;

public class FanApplication extends Application {
	
	private Status status;
	private Person person;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Intent intent = new Intent(this, IfanService.class);
		startService(intent);
		
		/**
		 * FinalBitmap默认使用fadeIn的动画显示图片。
		 * fadeIn类型动画使用的是TransitionDrawable。
		 * 而这种方式在CircleImageView上显示的时候会有问题，经常会显示不出来。
		 * 所以现在对于CircleImageView用自定义动画的方式，即userDefined，
		 * 显示的时候其实不使用动画，直接显示，这样就不会再有问题了
		 */
		FinalBitmap.create(this).configDisplayer(new Displayer() {
			public void loadFailDisplay(View imageView, Bitmap bitmap) {
				
			}
			public void loadCompletedisplay(View imageView, Bitmap bitmap, BitmapDisplayConfig config) {
				switch (config.getAnimationType()) {
				case BitmapDisplayConfig.AnimationType.fadeIn:
					ImageViewUtil.fadeInDisplay(imageView, bitmap);
					break;
				case BitmapDisplayConfig.AnimationType.userDefined:
					((ImageView)imageView).setImageBitmap(bitmap);
					break;
				}
			}
		});
		
		ShareSDK.initSDK(this);
		JPushInterface.init(this);
		JPushInterface.setLatestNotifactionNumber(this, 5);
		EMChat.getInstance().init(this);	
		/**
		 * init会重置新消息提醒设置，所以要把之前的设置读出来再设置一下
		 * 下面几个选项默认都设为true，也就是说都是打开的
		 */
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		options.setNotificationEnable(SPUtil.getEMChatOption(this, SPUtil.EMCHATOPTION_NOTIFICATION));
		options.setNoticeBySound(SPUtil.getEMChatOption(this, SPUtil.EMCHATOPTION_SOUND));
		options.setNoticedByVibrate(SPUtil.getEMChatOption(this, SPUtil.EMCHATOPTION_VIBRATE));
		options.setUseSpeaker(SPUtil.getEMChatOption(this, SPUtil.EMCHATOPTION_SPEAKER));
		options.setNotifyText(new OnMessageNotifyListener() {
			public String onNewMessageNotify(EMMessage message) {
				String friendId = message.getFrom();
				FriendService service = new FriendService(getApplicationContext());
				String nickname = service.getNicknameById(friendId);
				service.closeDBHelper();
				return nickname + " 发来了一条消息哦";
			}
			public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
				return fromUsersNum + "个好友发来了" + messageNum + "条消息";
			}
		});
		options.setOnNotificationClickListener(new OnNotificationClickListener() {
			public Intent onNotificationClick(EMMessage arg0) {
				Intent intent = new Intent(IntentString.EM.ACTION_CLICK);
				intent.setClass(getApplicationContext(), HomePageActivity.class);
				intent.putExtra(IntentString.Push.TYPE, IntentString.EM.TYPE_CLICK);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				return intent;
			}
		});
	}

	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	
}
