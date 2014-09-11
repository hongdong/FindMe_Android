package cn.ifanmi.findme.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.SPUtil;

public class SelectCDGActivity extends Activity implements IfanActivity {

	private User user;
	private ListView listView;
	private Button btn_back, btn_ok;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_cdg);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		user = SPUtil.getDefaultUser(this, null);
	}

	@Override
	public void initView() {
		listView = (ListView)findViewById(R.id.list_login);
		btn_back = (Button)findViewById(R.id.btn_login_back);
		btn_ok = (Button)findViewById(R.id.btn_login_ok);
		
		String[] baseLeft = new String[] {"学校  :", "院系  :", "年级  :", };
		String[] baseRight = new String[] {user.getCollegeName(), user.getDeptName(), user.getGrade()};
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < baseLeft.length; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(NormalString.List.LEFT, baseLeft[i]);
			item.put(NormalString.List.RIGHT, baseRight[i]);
			list.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.list_select_cdg_item, 
				new String[] {NormalString.List.LEFT, NormalString.List.RIGHT}, 
				new int[] {R.id.txt_login_list_item_left, R.id.txt_login_list_item_right});
		listView.setAdapter(adapter);	
		
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SelectCDGActivity.this, SelectCollegeActivity.class);
				startActivity(intent);
				finish();
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showPromptDialog();
			}
		});
	}
	
	private void showPromptDialog() {  
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
        builder.setTitle("提示"); 
        builder.setMessage("学校信息选择后将不可修改，要进入进一步吗？");
        builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss(); 
                jumpToCompleteUD();
            }  
        });  
        builder.setNegativeButton("取消", new AlertDialog.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
            }  
        });  
        builder.create().show();  
	}
	
	private void jumpToCompleteUD() {
		Intent intent = new Intent(this, CompleteUDActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}

}
