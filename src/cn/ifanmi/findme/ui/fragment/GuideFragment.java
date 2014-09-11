package cn.ifanmi.findme.ui.fragment;

import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.logic.IfanFragment;
import cn.ifanmi.findme.string.NormalString;

@SuppressLint("ValidFragment")
public class GuideFragment extends Fragment implements IfanFragment {

	private Context context;
	private int[] imageIds;
	private int index;
	private Handler handler;
	private View view;
	private ImageView imageView;
	
	public GuideFragment() {
		
	}

	public GuideFragment(Context context, int[] imageIds, int index, Handler handler) {
		this.context = context;
		this.imageIds = imageIds;
		this.index = index;
		this.handler = handler;
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
		imageView.setImageResource(imageIds[index]);
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (imageIds.length - 1 == index) {
					handler.sendEmptyMessage(NormalString.Handler.WHAT_SUCCESS);
				}
			}
		});
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}

}
