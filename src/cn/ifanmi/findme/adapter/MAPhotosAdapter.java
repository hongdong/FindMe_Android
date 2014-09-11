package cn.ifanmi.findme.adapter;

import cn.ifanmi.findme.R;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.StringUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MAPhotosAdapter extends BaseAdapter {

	private Context context;
	private String[] photos;
	
	public MAPhotosAdapter(Context context, String[] photos) {
		this.context = context;
		this.photos = photos;
	}

	@Override
	public int getCount() {
		return photos.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_ma_photos_item, null);
			holder = new Holder();
			holder.imageView = (ImageView)convertView.findViewById(R.id.img_ma_photos);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		String smallUrl = StringUtil.getSmallPhoto(photos[position], NormalString.SmallPhoto.ALBUM);
		ImageViewUtil.displayNormalImageView(context, holder.imageView, smallUrl);
		return convertView;
	}
	
	class Holder {
		ImageView imageView;
	}

}
