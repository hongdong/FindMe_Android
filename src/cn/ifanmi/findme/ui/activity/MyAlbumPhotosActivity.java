package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.Map;
import net.tsz.afinal.FinalBitmap;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import cn.ifanmi.findme.adapter.MAPhotosPagerAdapter;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.PersonService;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.SdCardUtil;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class MyAlbumPhotosActivity extends Activity implements IfanActivity {

	private String[] photos;
	private int clickPosition, index;
	private ViewPager viewPager;
	private MAPhotosPagerAdapter adapter;
	private Button btn_del, btn_save;
	private TextView txt_index;
	private String personId;
	private PersonService service;
	private ProgressDialog progressDialog;
	private boolean flag_bottom = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_album_photos);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		photos = getIntent().getStringArrayExtra(IntentString.Extra.PHOTOS);
		clickPosition = getIntent().getIntExtra(IntentString.Extra.CLICK_POSITION, 0);
		personId = SPUtil.getDefaultUser(this, null).getId();
		service = new PersonService(this);
	}

	@Override
	public void initView() {
		viewPager = (ViewPager)findViewById(R.id.vp_my_album_photos);
		btn_del = (Button)findViewById(R.id.btn_my_album_photos_del);
		btn_save = (Button)findViewById(R.id.btn_my_album_photos_save);
		txt_index = (TextView)findViewById(R.id.txt_my_album_photos_index);
		progressDialog = new ProgressDialog(this);
		
		txt_index.setText(clickPosition + 1 + "/" + photos.length);
		adapter = new MAPhotosPagerAdapter(this, photos, handler);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				txt_index.setText(arg0 + 1 + "/" + photos.length);
				index = arg0;
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		viewPager.setCurrentItem(clickPosition);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_del.setOnClickListener(listener);
		btn_save.setOnClickListener(listener);
	}
	
	private void showProgressDialog() {
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage("正在删除...");
		progressDialog.show();
	}
	
	private void dismissProgressDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	private void doDeleteAPTask() {
		showProgressDialog();
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, MyAlbumPhotosActivity.class.getSimpleName());
		taskParams.put(Person.ALBUM, photos[index]);
		Task task = new Task(Task.FAN_DELETEAP, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		String result = (String) map.get(JsonString.Return.RESULT);
		if (result != null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {	
					ToastUtil.prompt(this, "删除成功！");
					//这里要进行index，内存数据，本地数据库的更新
					if (1 == photos.length) {
						service.updataPersonInfo(personId, Person.ALBUM, "");
						finish();
					} else {
						int curIndex = 0;
						if (index == photos.length - 1) {
							curIndex = index;
							txt_index.setText(curIndex + "/" + String.valueOf(photos.length - 1));
						} else {
							curIndex = index + 1;
							txt_index.setText(String.valueOf(curIndex) + "/" + String.valueOf(photos.length - 1));
						}
						
						String[] temp = new String[photos.length - 1]; 
						System.arraycopy(photos, 0, temp, 0, index);
						System.arraycopy(photos, index + 1, temp, index, temp.length - index);
						photos = temp;
						adapter = new MAPhotosPagerAdapter(this, photos, handler);
						viewPager.setAdapter(adapter);
						/**
						 * 为什么要调用下面这一步？
						 * 因为当只有一个页面的时候，setCurrentItem不会触发onPageSelected，
						 * 这个时候index还是旧的，是1，而photos只有一张照片
						 * photos[1]就会数组越界
						 */
						index = curIndex - 1;
						viewPager.setCurrentItem(curIndex - 1);
						
						String album = "";
						for (int i = 0; i < photos.length; i++) {
							album += photos[i] + ",";
						}
						service.updataPersonInfo(personId, Person.ALBUM, album);
					}
				} else {
					ToastUtil.prompt(this, "删除失败！");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
		service.closeDBHelper();
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
			case R.id.btn_my_album_photos_del:
				showPromptDialog();
				break;
			
			case R.id.btn_my_album_photos_save:
				FinalBitmap finalBitmap = FinalBitmap.create(MyAlbumPhotosActivity.this);
				Bitmap bitmap = finalBitmap.getBitmapFromCache(photos[index]);
				if (bitmap != null) {
					SdCardUtil.savePhoto(MyAlbumPhotosActivity.this, bitmap);
				} else {
					ToastUtil.prompt(MyAlbumPhotosActivity.this, "还在下载大图");
				}
				break;
			}
		}
	}
	
	private void showPromptDialog() {  
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
        builder.setTitle("删除照片"); 
        builder.setMessage("照片删除后将不可恢复，真的要删除吗？");
        builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss(); 
                doDeleteAPTask();
            }  
        });  
        builder.setNegativeButton("取消", new AlertDialog.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
            }  
        });  
        builder.create().show();  
	}

}
