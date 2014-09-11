package cn.ifanmi.findme.ui.fragment;

import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.logic.IfanFragment;

@SuppressLint("ValidFragment")
public class PDMoreFragment extends Fragment implements IfanFragment {

	private Context context;
	private Person person;
	private View view;
	private TextView txt_nickname, txt_college, txt_dept, txt_grade;
	
	public PDMoreFragment() {
		
	}

	public PDMoreFragment(Context context, Person person) {
		this.context = context;
		this.person = person;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		initView();
		return view;
	}

	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		view = LayoutInflater.from(context).inflate(R.layout.fm_pdmore, null);
		txt_nickname = (TextView)view.findViewById(R.id.txt_pdmore_nickname);
		txt_college = (TextView)view.findViewById(R.id.txt_pdmore_college);
		txt_dept = (TextView)view.findViewById(R.id.txt_pdmore_dept);
		txt_grade = (TextView)view.findViewById(R.id.txt_pdmore_grade);
		
		txt_nickname.setText(person.getNickname());
		txt_college.setText(person.getCollegeName());
		txt_dept.setText(person.getDeptName());
		txt_grade.setText(person.getGrade());
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}

}
