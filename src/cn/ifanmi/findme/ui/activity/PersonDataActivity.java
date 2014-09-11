package cn.ifanmi.findme.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.CommonFPAdapter;
import cn.ifanmi.findme.adapter.MAPhotosAdapter;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.PersonService;
import cn.ifanmi.findme.listener.NetPhotosGridListener;
import cn.ifanmi.findme.logic.FanApplication;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.ui.fragment.PDBaseFragment;
import cn.ifanmi.findme.ui.fragment.PDMoreFragment;
import cn.ifanmi.findme.ui.view.MyGridView;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class PersonDataActivity extends FragmentActivity implements IfanActivity {

	private String personId;
	private Button btn_back;
	private ViewPager viewPager;
	private List<Fragment> fragments;
	private ViewGroup vg_circle_image;
	private ImageView[] imageViews;
	private TextView txt_signature;
	private MyGridView mgrid_album;
	private TextView txt_no_picture;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_data);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		personId = getIntent().getStringExtra(Person.ID);
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_person_data);
		viewPager = (ViewPager)findViewById(R.id.vp_pd);
		vg_circle_image = (ViewGroup)findViewById(R.id.layout_pd_circle_img);
		txt_signature = (TextView)findViewById(R.id.txt_pd_signature);
		mgrid_album = (MyGridView)findViewById(R.id.mgrid_pd);
		txt_no_picture = (TextView)findViewById(R.id.txt_pd_no_picture);
		
		if (null == personId) {
			FanApplication application = (FanApplication) getApplication();
			Person person = application.getPerson();
			refreshPerson(person);
		} else {
			String myId = SPUtil.getDefaultUser(this, null).getId();
			PersonService service = new PersonService(this);
			Person person = service.getPersonById(myId);
			service.closeDBHelper();
			if (myId.equals(personId) && person != null) {
				refreshPerson(person);
			} else {
				doGetPDTask();
			}
		}
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
	}
	
	private void doGetPDTask() {
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, PersonDataActivity.class.getSimpleName());
		taskParams.put(Person.ID, personId);
		Task task = new Task(Task.FAN_GETPD, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		switch (taskId) {
		case Task.FAN_GETPD:
			Person person = (Person) map.get(JsonString.Return.RESULT);
			if (person != null) {
				refreshPerson(person);
			} else {
				ToastUtil.noNet(this);
			}
			break;
		}
	}
	
	@SuppressLint("HandlerLeak")
	private void refreshPerson(Person person) {
		/**
		 * 用scrollViewMsg.scrollTo(0,0);一点效果没有。
		 * 因为我的scrollView里面ListView的上面还有其他的控件，
		 * 所以我想到一个办法就是使得一开始的时候就让上面其中一个控件获得焦点，滚动条自然就到顶部去了
		 */
		btn_back.setFocusable(true);
		btn_back.setFocusableInTouchMode(true);
		btn_back.requestFocus();
		PDBaseFragment baseFragment = new PDBaseFragment(this, person);
		PDMoreFragment moreFragment = new PDMoreFragment(this, person);
		fragments = new ArrayList<Fragment>();
		fragments.add(baseFragment);
		fragments.add(moreFragment);
		CommonFPAdapter fpAdapter = new CommonFPAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(fpAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				for (int i = 0; i < imageViews.length; i++) {
					if (i == arg0) {
						imageViews[i].setImageResource(R.drawable.dot_white);
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
				imageViews[i].setImageResource(R.drawable.dot_white);
			} else {
				imageViews[i].setImageResource(R.drawable.dot_gray);
			}
			vg_circle_image.addView(view);
		}
		
		txt_signature.setText(person.getSignature());
		
		final String[] album = person.getAlbum().split(",");
		if (album[0] != "") {
			txt_no_picture.setVisibility(View.GONE);
			MAPhotosAdapter adapter = new MAPhotosAdapter(this, album);
			mgrid_album.setAdapter(adapter);
			mgrid_album.setVisibility(View.VISIBLE);
			mgrid_album.setOnItemClickListener(new NetPhotosGridListener(this, album));
		} else {
			mgrid_album.setVisibility(View.GONE);
			txt_no_picture.setVisibility(View.VISIBLE);
		}
		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				int i = viewPager.getCurrentItem();
				int index = (i + 1) % (fragments.size());
				viewPager.setCurrentItem(index);
			}
		};
		new Timer().schedule(new TimerTask() {
			public void run() {
				handler.sendEmptyMessage(0);
			}
		}, 5000, 5000);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_person_data:
				finish();
				break;
			}
		}
	}

}
