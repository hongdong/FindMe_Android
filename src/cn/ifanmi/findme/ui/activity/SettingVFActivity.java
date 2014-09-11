package cn.ifanmi.findme.ui.activity;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.logic.IfanActivity;

public class SettingVFActivity extends Activity implements IfanActivity {

	private Button btn_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_vf);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_setting_vf);
		
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}

}
