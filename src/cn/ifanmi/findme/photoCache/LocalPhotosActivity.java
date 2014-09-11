package cn.ifanmi.findme.photoCache;

import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.NormalString;

public class LocalPhotosActivity extends Activity implements IfanActivity {

	private int clickPosition, index;
	private ViewPager viewPager;
	private LocalPhotosPagerAdapter adapter;
	private Button btn_exit, btn_del;
	private TextView txt_index;
	private boolean flag_bottom = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_photos);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		clickPosition = getIntent().getIntExtra(IntentString.Extra.CLICK_POSITION, 0);
	}

	@Override
	public void initView() {
		viewPager = (ViewPager)findViewById(R.id.vp_local_photos);
		btn_exit = (Button)findViewById(R.id.btn_local_photos_exit);
		btn_del = (Button)findViewById(R.id.btn_local_photos_del);
		txt_index = (TextView)findViewById(R.id.txt_local_photos_index);
		
		txt_index.setText(clickPosition + 1 + "/" + Bimp.cur);

		adapter = new LocalPhotosPagerAdapter(this, handler);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				txt_index.setText(arg0 + 1 + "/" + Bimp.cur);
				index = arg0;
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		viewPager.setCurrentItem(clickPosition);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_exit.setOnClickListener(listener);
		btn_del.setOnClickListener(listener);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NormalString.Handler.WHAT_SUCCESS:
				View view = findViewById(R.id.layout_local_photos);
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
			case R.id.btn_local_photos_exit:
				finish();
				break;
			case R.id.btn_local_photos_del:
				if (1 == Bimp.bmp.size()) {
					Bimp.bmp.clear();
					Bimp.drr.clear();
					Bimp.cur = 0;
					finish();
				} else {
					if (index == Bimp.cur - 1) {
						txt_index.setText(index + "/" + String.valueOf(Bimp.cur - 1));
					} else {
						txt_index.setText(String.valueOf(index + 1) + "/" + String.valueOf(Bimp.cur - 1));
					}
					Bimp.bmp.remove(index);
					Bimp.drr.remove(index);
					Bimp.cur--;
					adapter.notifyDataSetChanged();
				}
				break;
			}
			
		}
	}

}
