package cn.ifanmi.findme.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cn.ifanmi.findme.photoCache.NetPhotosActivity;
import cn.ifanmi.findme.string.IntentString;

public class NetPhotosListener implements OnClickListener {

	private Context context;
	private String[] photos;
	
	public NetPhotosListener(Context context, String[] photos) {
		this.context = context;
		this.photos = photos;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(context, NetPhotosActivity.class);
		intent.putExtra(IntentString.Extra.PHOTOS, photos);
		intent.putExtra(IntentString.Extra.CLICK_POSITION, 0);
		context.startActivity(intent);
	}

}
