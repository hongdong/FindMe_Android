package cn.ifanmi.findme.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.CommonFPAdapter;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.ui.fragment.SettingSIFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SettingSIActivity extends FragmentActivity implements IfanActivity {

	private ViewPager viewPager;
	private List<Fragment> fragments;
	private int[] imageIds = {R.drawable.img_guide_01, R.drawable.img_guide_02, 
			R.drawable.img_guide_03, R.drawable.img_guide_04, R.drawable.img_guide_05, };
	private ViewGroup vg_circle_image;
	private ImageView[] imageViews;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_si);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		viewPager = (ViewPager)findViewById(R.id.vp_setting_si);
		vg_circle_image = (ViewGroup)findViewById(R.id.layout_setting_si_circle_img);
		
		fragments = new ArrayList<Fragment>();
		for (int i = 0; i < imageIds.length; i++) {
			SettingSIFragment fragment = new SettingSIFragment(this, imageIds[i]);
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

}
