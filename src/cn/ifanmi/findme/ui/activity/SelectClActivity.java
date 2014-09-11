package cn.ifanmi.findme.ui.activity;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.ClAdapter;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.string.IntentString;

public class SelectClActivity extends Activity implements IfanActivity {
	
	private Button btn_back;
	private ListView list_cl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_cl);
		initData();
		initView();
	}
	
	@Override
	public void initData() {

	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_select_cl);
		list_cl = (ListView)findViewById(R.id.list_select_cl);
		
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		final ClAdapter adapter = new ClAdapter(this);
		list_cl.setAdapter(adapter);
		list_cl.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				intent.putExtra(User.CL, adapter.getClByIndex(arg2));
				setResult(IntentString.ResultCode.SELECTCL_COMPLETEUD, intent);
				finish();
			}
		});
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {

	}

}
