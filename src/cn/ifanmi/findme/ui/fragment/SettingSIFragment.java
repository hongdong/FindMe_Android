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
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.logic.IfanFragment;

@SuppressLint("ValidFragment")
public class SettingSIFragment extends Fragment implements IfanFragment {

	private Context context;
	private int imageId;
	private View view;
	private ImageView imageView;
	
	public SettingSIFragment() {
		
	}

	public SettingSIFragment(Context context, int imageId) {
		super();
		this.context = context;
		this.imageId = imageId;
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
		view = LayoutInflater.from(context).inflate(R.layout.list_guide_item, null);
		imageView = (ImageView)view.findViewById(R.id.img_list_guide_item);
		imageView.setImageResource(imageId);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}

}
