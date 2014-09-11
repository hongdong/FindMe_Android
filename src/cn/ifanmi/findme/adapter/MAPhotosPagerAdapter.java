package cn.ifanmi.findme.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.ImageViewUtil;

public class MAPhotosPagerAdapter extends PagerAdapter {
	
	private Context context;
	private String[] photos;
	private Handler handler;

	public MAPhotosPagerAdapter(Context context, String[] photos, Handler handler) {
		this.context = context;
		this.photos = photos;
		this.handler = handler;
	}

	@Override
	public int getCount() {
		return photos.length;
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
		View view = LayoutInflater.from(context).inflate(R.layout.list_ma_photo_item, null);
		ImageView imageView = (ImageView)view.findViewById(R.id.img_list_ma_photo_item);
		ImageViewUtil.displayNormalImageView(context, imageView, photos[position]);
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handler.sendEmptyMessage(NormalString.Handler.WHAT_SUCCESS);
			}
		});
		container.addView(view);
		return view;
	}

}
