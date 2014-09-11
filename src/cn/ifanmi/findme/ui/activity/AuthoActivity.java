package cn.ifanmi.findme.ui.activity;

import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;

public class AuthoActivity extends Activity implements IfanActivity {

	private Button btn_login, btn_signup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.autho);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {

	}

	@Override
	public void initView() {
		btn_login = (Button)findViewById(R.id.btn_login);
		btn_signup = (Button)findViewById(R.id.btn_signup);
		
		btn_login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AuthoActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});
		
		btn_signup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AuthoActivity.this, SignupActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
	}

}
