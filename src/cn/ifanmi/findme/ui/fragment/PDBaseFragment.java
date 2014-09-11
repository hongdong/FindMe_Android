package cn.ifanmi.findme.ui.fragment;

import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.listener.NetPhotosListener;
import cn.ifanmi.findme.logic.IfanFragment;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.StringUtil;

@SuppressLint("ValidFragment")
public class PDBaseFragment extends Fragment implements IfanFragment {

	private Context context;
	private Person person;
	private View view;
	private ImageView img_photo, img_auth, img_sex;
	private TextView txt_name, txt_cl;
	
	public PDBaseFragment() {
		
	}

	public PDBaseFragment(Context context, Person person) {
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
		view = LayoutInflater.from(context).inflate(R.layout.fm_pdbase, null);
		img_photo = (ImageView)view.findViewById(R.id.img_pdbase_photo);
		img_auth = (ImageView)view.findViewById(R.id.img_pdbase_auth);
		img_sex = (ImageView)view.findViewById(R.id.img_pdbase_sex);
		txt_name = (TextView)view.findViewById(R.id.txt_pdbase_name);
		txt_cl = (TextView)view.findViewById(R.id.txt_pdbase_cl);
		
		String smallUrl = StringUtil.getSmallPhoto(person.getPhoto(), NormalString.SmallPhoto.PERSON_DATA);
		ImageViewUtil.displayCircleImageView(context, img_photo, smallUrl);
		
		String[] photos = {person.getPhoto()};
		img_photo.setOnClickListener(new NetPhotosListener(context, photos));
		
		String auth = person.getIsAuth();
		if (auth.equals(Person.ISAUTH_YES)) {
			img_auth.setVisibility(View.VISIBLE);
		} else if (auth.equals(Person.ISAUTH_NO)) {
			img_auth.setVisibility(View.GONE);
		}
		
		String sex = person.getSex();
		if (sex.equals(Person.MALE)) {
			img_sex.setImageResource(R.drawable.sex_male);
		} else if (sex.equals(Person.FEMALE)) {
			img_sex.setImageResource(R.drawable.sex_female);
		}
		
		txt_name.setText(person.getName());
		txt_cl.setText(person.getCl());
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}

}
