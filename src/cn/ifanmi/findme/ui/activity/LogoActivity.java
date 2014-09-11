package cn.ifanmi.findme.ui.activity;

import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.util.SPUtil;

public class LogoActivity extends Activity implements IfanActivity {

	private ImageView img_logo;
	private Animation animation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		img_logo = (ImageView)findViewById(R.id.img_logo);
		animation = AnimationUtils.loadAnimation(LogoActivity.this, R.anim.logo);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				
			}
			public void onAnimationRepeat(Animation animation) {
				
			}
			public void onAnimationEnd(Animation animation) {
				if (SPUtil.isFirstUse(LogoActivity.this)) {
					jumpToGuide();
				} else {
					User defaultUser = SPUtil.getDefaultUser(LogoActivity.this, User.NAME);
					if (null != defaultUser) {				
						jumpToHomePage();		
					} else {
						jumpToAuthorize();	
					}
				}
			}
		});
		img_logo.setAnimation(animation);
	}
	
	private void jumpToHomePage() {
		Intent intent = new Intent(LogoActivity.this, HomePageActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void jumpToAuthorize() {
		Intent intent = new Intent(LogoActivity.this, AuthoActivity.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		finish();
	}
	
	private void jumpToGuide() {
		Intent intent = new Intent(LogoActivity.this, GuideActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
	
	@Override
	public void onBackPressed() {
		//撤销返回键
	}

}
