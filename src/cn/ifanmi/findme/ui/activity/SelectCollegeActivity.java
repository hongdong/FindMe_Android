package cn.ifanmi.findme.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.util.KeyBoardUtil;
import cn.ifanmi.findme.util.SPUtil;

public class SelectCollegeActivity extends Activity implements IfanActivity {

	private String[] hotCollegeNames, allCollegeNames;
	private EditText edit;
	private ListView list;
	private ArrayAdapter<String> adapter;
	private List<String> currentCollegeNames;
	private String[] collegeIds;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_college);
		initData();
		initView();
	}

	@Override
	public void initData() {
		hotCollegeNames = getResources().getStringArray(R.array.college_name);
		allCollegeNames = getResources().getStringArray(R.array.all_college_name);
		collegeIds = getResources().getStringArray(R.array.all_college_id);
		currentCollegeNames = new ArrayList<String>();
	}

	@Override
	public void initView() {
		edit = (EditText)findViewById(R.id.edit_select_college);
		list = (ListView)findViewById(R.id.list_select_college);
		showHotCollege();
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				KeyBoardUtil.dismiss(SelectCollegeActivity.this, edit);
				String collegeName = currentCollegeNames.get(arg2);
				String collegeId = null;
				for (int i = 0; i < allCollegeNames.length; i++) {
					if (collegeName.equals(allCollegeNames[i])) {
						collegeId = collegeIds[i];
						break;
					}
				}
				SPUtil.saveDefaultUser(SelectCollegeActivity.this, User.COLLEGEID, collegeId);
				SPUtil.saveDefaultUser(SelectCollegeActivity.this, User.COLLEGENAME, collegeName);
				Intent intent = new Intent(SelectCollegeActivity.this, SelectDeptActivity.class);
				intent.putExtra(IntentString.Extra.COLLEGEID, collegeId);
				startActivity(intent);
			}
		});
		list.setTextFilterEnabled(true);
		edit.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			public void afterTextChanged(Editable s) {
				String subString = edit.getText().toString().trim();
				if (subString.equals("")) {
					showHotCollege();
				} else {
					showSearchCollege(subString);
				}
			}
		});
	}
	
	/**
	 * 显示热门学校
	 */
	private void showHotCollege() {
		currentCollegeNames.clear();
		for (int i = 0; i < hotCollegeNames.length; i++) {
				currentCollegeNames.add(hotCollegeNames[i]);
		}
		adapter = new ArrayAdapter<String>(
				SelectCollegeActivity.this, R.layout.list_common_txt_item, currentCollegeNames);
		list.setAdapter(adapter);
	}
	
	/**
	 * 显示关键字搜索后的学校
	 * @param subString	关键字
	 */
	private void showSearchCollege(String subString) {
		currentCollegeNames.clear();
		for (int i = 0; i < allCollegeNames.length; i++) {
			if (allCollegeNames[i].contains(subString)) {
				currentCollegeNames.add(allCollegeNames[i]);
			}
		}
		adapter = new ArrayAdapter<String>(
				SelectCollegeActivity.this, R.layout.list_common_txt_item, currentCollegeNames);
		list.setAdapter(adapter);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
}
