package cn.ifanmi.findme.photoCache;

import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.logic.IfanFragment;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.ImageViewUtil;

@SuppressLint("ValidFragment")
public class NetPhotoFragment extends Fragment implements IfanFragment {

	private Context context;
	private String photo;
	private Handler handler;
	private View view;
	private ImageView imageView;
	
	public NetPhotoFragment() {
		super();
	}
	
	public NetPhotoFragment(Context context, String photo, Handler handler) {
		super();
		this.context = context;
		this.photo = photo;
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
		view = LayoutInflater.from(context).inflate(R.layout.list_net_photos_item, null);
		imageView = (ImageView)view.findViewById(R.id.img_list_net_photos_item);
		
		ImageViewUtil.displayNormalImageView(context, imageView, photo);
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handler.sendEmptyMessage(NormalString.Handler.WHAT_SUCCESS);
			}
		});
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
	
}
