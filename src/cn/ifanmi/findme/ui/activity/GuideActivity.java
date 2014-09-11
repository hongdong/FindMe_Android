package cn.ifanmi.findme.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.CommonFPAdapter;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.ui.fragment.GuideFragment;
import cn.ifanmi.findme.util.SPUtil;

public class GuideActivity extends FragmentActivity implements IfanActivity {

	private ViewPager viewPager;
	private List<Fragment> fragments;
	private int[] imageIds = {R.drawable.img_guide_01, R.drawable.img_guide_02, 
			R.drawable.img_guide_03, R.drawable.img_guide_04, R.drawable.img_guide_05x, };
	private ViewGroup vg_circle_image;
	private ImageView[] imageViews;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		viewPager = (ViewPager)findViewById(R.id.vp_guide);
		vg_circle_image = (ViewGroup)findViewById(R.id.layout_guide_circle_img);
		
		fragments = new ArrayList<Fragment>();
		for (int i = 0; i < imageIds.length; i++) {
			GuideFragment fragment = new GuideFragment(this, imageIds, i, handler);
			fragments.add(fragment);
		}
		CommonFPAdapter adapter = new CommonFPAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				for (int i = 0; i < imageViews.length; i++) {
					if (i == arg0) {
						imageViews[i].setImageResource(R.drawable.dot_red);
					} else {
						imageViews[i].setImageResource(R.drawable.dot_gray);
					}
				}
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		vg_circle_image.removeAllViews();
		imageViews = new ImageView[fragments.size()];
		for (int i = 0; i < fragments.size(); i++) {
			View view = LayoutInflater.from(this).inflate(R.layout.circle_imageview, null);
			imageViews[i] = (ImageView) view.findViewById(R.id.img_circle);
			if (0 == i) {
				imageViews[i].setImageResource(R.drawable.dot_red);
			} else {
				imageViews[i].setImageResource(R.drawable.dot_gray);
			}
			vg_circle_image.addView(view);
		}
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NormalString.Handler.WHAT_SUCCESS:
				SPUtil.setFirstUse(GuideActivity.this);
				User defaultUser = SPUtil.getDefaultUser(GuideActivity.this, User.ID);
				if (null != defaultUser) {				
					jumpToHomePage();		
				} else {
					jumpToAuthorize();	
				}
				break;
			}
		}
	};
	
	private void jumpToHomePage() {
		Intent intent = new Intent(GuideActivity.this, HomePageActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void jumpToAuthorize() {
		Intent intent = new Intent(GuideActivity.this, AuthoActivity.class);
		startActivity(intent);
		finish();
	}

}
