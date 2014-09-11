package cn.ifanmi.findme.listener;

import cn.ifanmi.findme.photoCache.NetPhotosActivity;
import cn.ifanmi.findme.string.IntentString;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class NetPhotosGridListener implements OnItemClickListener {

	private Context context;
	private String[] photos;
	
	public NetPhotosGridListener(Context context, String[] photos) {
		this.context = context;
		this.photos = photos;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(context, NetPhotosActivity.class);
		intent.putExtra(IntentString.Extra.PHOTOS, photos);
		intent.putExtra(IntentString.Extra.CLICK_POSITION, arg2);
		context.startActivity(intent);
	}

}
