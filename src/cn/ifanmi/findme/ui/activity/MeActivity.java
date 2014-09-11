package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.MeListAdapter;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.PersonService;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.ui.view.MyListView;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.StringUtil;
import cn.ifanmi.findme.util.ToastUtil;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MeActivity extends Activity implements IfanActivity {

	private String personId;
	private Person person;
	private PersonService service;
	private Holder holder = new Holder();
	MyOnClickListener listener = new MyOnClickListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		personId = SPUtil.getDefaultUser(this, null).getId();
		service = new PersonService(this);
	}
	
	private class Holder {
		View view_header;
		ImageView img_photo;
		TextView txt_nickname;
		TextView txt_signature;
		View view_album;
		MyListView mlist;
		View view_setting;
	}

	@Override
	public void initView() {
		holder.view_header = findViewById(R.id.layout_me_header);
		holder.img_photo = (ImageView)findViewById(R.id.img_me_photo);
		holder.txt_nickname = (TextView)findViewById(R.id.txt_me_nickname);
		holder.txt_signature = (TextView)findViewById(R.id.txt_me_signature);
		holder.view_album = findViewById(R.id.layout_me_album);
		holder.mlist = (MyListView)findViewById(R.id.list_me);
		holder.view_setting = findViewById(R.id.layout_me_setting);
		
		holder.mlist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (arg2) {
				case 0:
					String auth = person.getIsAuth();
					if (auth.equals(Person.ISAUTH_YES)) {
						//已经认证过了
					} else if (auth.equals(Person.ISAUTH_NO)) {
						Intent intent = new Intent(MeActivity.this, AuthActivity.class);
						startActivity(intent);
					}
					break;
					
				case 1:
					OnekeyShare oks = new OnekeyShare();
					oks.setText(getString(R.string.share) + " " + NormalString.Fanmi.URL);
					/**
					 * 以下两个字段竟然没有作用。单独一个平台一个平台做又麻烦，
					 * 直接在setText里面加链接了
					 */
					oks.setUrl(NormalString.Fanmi.URL);
					Resources  r = getResources();
					Uri uri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
					   + r.getResourcePackageName(R.drawable.img_app) + "/"
					   + r.getResourceTypeName(R.drawable.img_app) + "/"
					   + r.getResourceEntryName(R.drawable.img_app));
					oks.setImagePath(uri.getPath());
					oks.setTitle(getString(R.string.share_content));
					oks.setTitleUrl(NormalString.Fanmi.URL);
					oks.setSite(getString(R.string.app_name));
					oks.setSiteUrl(NormalString.Fanmi.URL);
					oks.setCallback(new PlatformActionListener() {
						public void onError(Platform arg0, int arg1, Throwable arg2) {
							runOnUiThread(new Runnable() {
								public void run() {
									ToastUtil.prompt(MeActivity.this, "分享失败");
								}
							});
						}
						public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
							runOnUiThread(new Runnable() {
								public void run() {
									ToastUtil.prompt(MeActivity.this, "分享成功");
								}
							});
						}
						public void onCancel(Platform arg0, int arg1) {
							
						}
					});
					oks.show(MeActivity.this);
					break;
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		person = service.getPersonById(personId);
		if (person != null) {
			displayPerson();
		} else {
			doGetPDTask();
		}
	}
	
	private void doGetPDTask() {
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, MeActivity.class.getSimpleName());
		taskParams.put(Person.ID, personId);
		Task task = new Task(Task.FAN_GETPD, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		switch (taskId) {
		case Task.FAN_GETPD:
			person = (Person) map.get(JsonString.Return.RESULT);
			if (person != null) {
				displayPerson();
				service.insertPerson(person);
			}
			break;
		}
	}
	
	private void displayPerson() {
		String smallUrl = StringUtil.getSmallPhoto(person.getPhoto(), NormalString.SmallPhoto.PERSON_ME);
		ImageViewUtil.displayCircleImageView(this, holder.img_photo, smallUrl);
		holder.txt_nickname.setText(person.getNickname());
		holder.txt_signature.setText(person.getSignature());
		MeListAdapter adapter = new MeListAdapter(this, person.getIsAuth());
		holder.mlist.setAdapter(adapter);
		holder.view_header.setOnClickListener(listener);
		holder.view_album.setOnClickListener(listener);
		holder.view_setting.setOnClickListener(listener);
	}
	
	@Override
	public void onBackPressed() {
		getParent().onBackPressed();
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layout_me_header:
				Intent myData = new Intent(MeActivity.this, MyDataActivity.class);
				startActivity(myData);
				break;
				
			case R.id.layout_me_album:
				Intent myAlbum = new Intent(MeActivity.this, MyAlbumActivity.class);
				startActivity(myAlbum);
				break;
				
			case R.id.layout_me_setting:
				Intent setting = new Intent(MeActivity.this, SettingActivity.class);
				startActivity(setting);
				break;
			}
		}
	}

}
