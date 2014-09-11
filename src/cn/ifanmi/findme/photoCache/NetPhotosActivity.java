package cn.ifanmi.findme.photoCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.tsz.afinal.FinalBitmap;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.CommonFPAdapter;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.SdCardUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class NetPhotosActivity extends FragmentActivity implements IfanActivity {

	private String[] photos;
	private int clickPosition;
	private ViewPager viewPager;
	private List<Fragment> fragments;
	private Button btn_exit, btn_save;
	private TextView txt_index;
	private boolean flag_bottom = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_photos);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		photos = getIntent().getStringArrayExtra(IntentString.Extra.PHOTOS);
		clickPosition = getIntent().getIntExtra(IntentString.Extra.CLICK_POSITION, 0);
	}

	@Override
	public void initView() {
		viewPager = (ViewPager)findViewById(R.id.vp_net_photos);
		btn_exit = (Button)findViewById(R.id.btn_net_photos_exit);
		btn_save = (Button)findViewById(R.id.btn_net_photos_save);
		txt_index = (TextView)findViewById(R.id.txt_net_photos_index);
		
		txt_index.setText(clickPosition + 1 + "/" + photos.length);
		
		fragments = new ArrayList<Fragment>();
		for (int i = 0; i < photos.length; i++) {
			NetPhotoFragment fragment = new NetPhotoFragment(this, photos[i], handler);
			fragments.add(fragment);
		}
		CommonFPAdapter adapter = new CommonFPAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				txt_index.setText(arg0 + 1 + "/" + photos.length);
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		viewPager.setCurrentItem(clickPosition);
		MyOnClickListener listener = new MyOnClickListener();
		btn_exit.setOnClickListener(listener);
		btn_save.setOnClickListener(listener);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NormalString.Handler.WHAT_SUCCESS:
				View view = findViewById(R.id.layout_net_photos);
				if (flag_bottom) {
					view.setVisibility(View.GONE);
				} else {
					view.setVisibility(View.VISIBLE);
				}
				flag_bottom = !flag_bottom;
				break;
			}
		}
	};
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.vp_net_photos:
				View view = findViewById(R.id.layout_net_photos);
				Animation animation = null;
				if (flag_bottom) {
					animation = AnimationUtils.loadAnimation(NetPhotosActivity.this, R.anim.photos_bottom_exit);
				} else {
					animation = AnimationUtils.loadAnimation(NetPhotosActivity.this, R.anim.photos_bottom_enter);
				}
				view.setAnimation(animation);
				flag_bottom = !flag_bottom;
				break;
			case R.id.btn_net_photos_exit:
				finish();
				break;
			case R.id.btn_net_photos_save:
				String photo = photos[viewPager.getCurrentItem()];
				FinalBitmap finalBitmap = FinalBitmap.create(NetPhotosActivity.this);
				Bitmap bitmap = finalBitmap.getBitmapFromCache(photo);
				if (bitmap != null) {
					SdCardUtil.savePhoto(NetPhotosActivity.this, bitmap);
				} else {
					ToastUtil.prompt(NetPhotosActivity.this, "还在下载大图");
				}
				break;
			}
		}
	}

}
