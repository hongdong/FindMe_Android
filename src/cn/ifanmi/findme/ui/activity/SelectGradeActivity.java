package cn.ifanmi.findme.ui.activity;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.util.SPUtil;

public class SelectGradeActivity extends Activity implements IfanActivity {

	private Button btn_back;
	private ListView list;
	private ArrayAdapter<String> adapter;
	private String[] grades = new String[] {"2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_grade);
		initData();
		initView();
	}
	
	@Override
	public void initData() {

	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_select_grade);
		list = (ListView)findViewById(R.id.list_select_grade);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		adapter = new ArrayAdapter<String>(this, R.layout.list_common_txt_item, grades);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String grade = grades[arg2];
				SPUtil.saveDefaultUser(SelectGradeActivity.this, User.GRADE, grade);
				Intent intent = new Intent(SelectGradeActivity.this, SelectCDGActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {

	}

}
