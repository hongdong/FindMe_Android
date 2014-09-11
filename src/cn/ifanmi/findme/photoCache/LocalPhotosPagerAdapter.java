package cn.ifanmi.findme.photoCache;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.string.NormalString;

public class LocalPhotosPagerAdapter extends PagerAdapter {
	
	private Context context;
	private Handler handler;
	
	public LocalPhotosPagerAdapter(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	public int getCount() {
		return Bimp.bmp.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	/**
	 * 不这样重写这个方法，notifyDataSetChanged后很可能不会刷新PagerAdapter
	 * http://blog.csdn.net/yuzhiboyi/article/details/7642502
	 */
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = LayoutInflater.from(context).inflate(R.layout.list_local_photos_item, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.img_local_photos);
		imageView.setImageBitmap(Bimp.bmp.get(position));
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handler.sendEmptyMessage(NormalString.Handler.WHAT_SUCCESS);
			}
		});
		container.addView(view);
		return view;
	}
	
	class Holder {
		ImageView imageView;
	}

}
