package cn.ifanmi.findme.ui.activity;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.MAPhotosAdapter;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.dbService.PersonService;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.ui.view.MyGridView;
import cn.ifanmi.findme.util.SPUtil;

public class MyAlbumActivity extends Activity implements IfanActivity {

	private String personId;
	private PersonService service;
	private Person person;
	private Button btn_back, btn_add;
	private MyGridView mgrid;
	private String[] album;
	private int max_select;
	private TextView txt_no_picture;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_album);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		personId = SPUtil.getDefaultUser(this, null).getId();
		service = new PersonService(this);
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_my_album);
		btn_add = (Button)findViewById(R.id.btn_add_my_album);
		mgrid = (MyGridView)findViewById(R.id.mgrid_my_album);
		txt_no_picture = (TextView)findViewById(R.id.txt_my_album_no_picture);
		
		btn_back.setFocusable(true);
		btn_back.setFocusableInTouchMode(true);
		btn_back.requestFocus();
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		btn_add.setOnClickListener(listener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		person = service.getPersonById(personId);
		album = person.getAlbum().split(",");
		if (album.length < Person.ALBUM_MAX_COUNT) {
			btn_add.setVisibility(View.VISIBLE);
		}
		if (album[0] != "") {
			txt_no_picture.setVisibility(View.GONE);
			max_select = Person.ALBUM_MAX_COUNT - album.length;
			MAPhotosAdapter adapter = new MAPhotosAdapter(this, album);
			mgrid.setAdapter(adapter);
			mgrid.setVisibility(View.VISIBLE);
			mgrid.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent intent = new Intent(MyAlbumActivity.this, MyAlbumPhotosActivity.class);
					intent.putExtra(IntentString.Extra.PHOTOS, album);
					intent.putExtra(IntentString.Extra.CLICK_POSITION, arg2);
					startActivity(intent);
				}
			});
		} else {
			max_select = Person.ALBUM_MAX_COUNT;
			mgrid.setVisibility(View.GONE);
			txt_no_picture.setVisibility(View.VISIBLE);
		}
		/**
		 * 一次最多只能选择Person.ALBUM_MAX_SELECT张照片
		 */
		if (max_select > Person.ALBUM_MAX_SELECT) {
			max_select = Person.ALBUM_MAX_SELECT;
		}
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
		service.closeDBHelper();
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_my_album:
				finish();
				break;
				
			case R.id.btn_add_my_album:
				Intent intent = new Intent(MyAlbumActivity.this, MyAlbumAddActivity.class);
				intent.putExtra(Person.ALBUM, max_select);
				startActivity(intent);
				break;
			}
		}
	}

}
