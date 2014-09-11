package cn.ifanmi.findme.adapter;

import java.util.List;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Status;
import cn.ifanmi.findme.listener.NetPhotosGridListener;
import cn.ifanmi.findme.listener.NetPhotosListener;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.ui.view.MyGridView;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.StringUtil;
import cn.ifanmi.findme.util.TimeUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusAdapter extends BaseAdapter {

	private Context context;
	private List<Status> statuses;
	
	public StatusAdapter(Context context, List<Status> statuses) {
		this.context = context;
		this.statuses = statuses;
	}

	@Override
	public int getCount() {
		return statuses.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		int type = getItemViewType(position);
		if (null == convertView) {
			holder = new Holder();
			switch (type) {
			case NormalString.StatusViewType.NO_PICTURE:
				convertView = LayoutInflater.from(context).inflate(R.layout.list_status_np_item, null);
				holder.img_photo = (ImageView)convertView.findViewById(R.id.img_status_photo);
				holder.txt_nickname = (TextView)convertView.findViewById(R.id.txt_status_nickname);
				holder.txt_release_time = (TextView)convertView.findViewById(R.id.txt_status_releasetime);
				holder.txt_content = (TextView)convertView.findViewById(R.id.txt_status_content);
				holder.txt_read = (TextView)convertView.findViewById(R.id.txt_status_read);
				holder.txt_praise = (TextView)convertView.findViewById(R.id.txt_status_praise);
				holder.txt_message = (TextView)convertView.findViewById(R.id.txt_status_message);
				break;
				
			case NormalString.StatusViewType.ONE_PICTURE:
				convertView = LayoutInflater.from(context).inflate(R.layout.list_status_op_item, null);
				holder.img_photo = (ImageView)convertView.findViewById(R.id.img_status_photo);
				holder.txt_nickname = (TextView)convertView.findViewById(R.id.txt_status_nickname);
				holder.txt_release_time = (TextView)convertView.findViewById(R.id.txt_status_releasetime);
				holder.txt_content = (TextView)convertView.findViewById(R.id.txt_status_content);
				holder.img_photos = (ImageView)convertView.findViewById(R.id.img_status_photos);
				holder.txt_read = (TextView)convertView.findViewById(R.id.txt_status_read);
				holder.txt_praise = (TextView)convertView.findViewById(R.id.txt_status_praise);
				holder.txt_message = (TextView)convertView.findViewById(R.id.txt_status_message);
				break;
				
			case NormalString.StatusViewType.MULTI_PICTURE:
				convertView = LayoutInflater.from(context).inflate(R.layout.list_status_mp_item, null);
				holder.img_photo = (ImageView)convertView.findViewById(R.id.img_status_photo);
				holder.txt_nickname = (TextView)convertView.findViewById(R.id.txt_status_nickname);
				holder.txt_release_time = (TextView)convertView.findViewById(R.id.txt_status_releasetime);
				holder.txt_content = (TextView)convertView.findViewById(R.id.txt_status_content);
				holder.mgrid_photos = (MyGridView)convertView.findViewById(R.id.grid_status_photos);
				holder.txt_read = (TextView)convertView.findViewById(R.id.txt_status_read);
				holder.txt_praise = (TextView)convertView.findViewById(R.id.txt_status_praise);
				holder.txt_message = (TextView)convertView.findViewById(R.id.txt_status_message);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		Status status = statuses.get(position);
		String isOfficail = status.getIsOfficial();
		if (isOfficail.equals(Status.ISOFFICIAL_YES)) {
			holder.img_photo.setImageResource(R.drawable.img_official);
			holder.txt_nickname.setText(Status.OFFICIAL_NICKNAME);
		} else if (isOfficail.equals(Status.ISOFFICIAL_NO)) {
			holder.img_photo.setImageResource(R.drawable.img_anony);
			holder.txt_nickname.setText(Status.ANONY_NICKNAME);
		}
		String isTop = status.getIsTop();
		if (isTop.equals(Status.ISTOP_YES)) {
			
		} else if (isTop.equals(Status.ISTOP_NO)) {
			
		}
		String releaseTime = TimeUtil.getDisplayTime(TimeUtil.getNow(), status.getReleaseTime());
		holder.txt_release_time.setText(releaseTime);
		holder.txt_content.setText(status.getContent());
		
		String[] photos = status.getPhotos().split(",");
		switch (type) {
		case NormalString.StatusViewType.NO_PICTURE:
			break;
			
		case NormalString.StatusViewType.ONE_PICTURE:
			String smallUrl = StringUtil.getSmallPhoto(photos[0], NormalString.SmallPhoto.STATUS_ONE);
			ImageViewUtil.displayNormalImageView(context, holder.img_photos, smallUrl);
			holder.img_photos.setOnClickListener(new NetPhotosListener(context, photos));
			break;
			
		case NormalString.StatusViewType.MULTI_PICTURE:
			SGPhotosAdapter adapter = new SGPhotosAdapter(context, photos);
			holder.mgrid_photos.setAdapter(adapter);
			holder.mgrid_photos.setOnItemClickListener(new NetPhotosGridListener(context, photos));
			break;
		}
		
		holder.txt_read.setText(status.getReadCount());
		holder.txt_praise.setText(status.getPraiseCount());
		holder.txt_message.setText(status.getMessageCount());
		return convertView;
	}
	
	private class Holder {
		ImageView img_photo;
		TextView txt_nickname;
		TextView txt_release_time;
		TextView txt_content;
		ImageView img_photos;
		MyGridView mgrid_photos;
		TextView txt_read;
		TextView txt_praise;
		TextView txt_message;
	}
	
	@Override
	public int getViewTypeCount() {
		return NormalString.StatusViewType.COUNT;
	}
	
	@Override
	public int getItemViewType(int position) {
		String[] photos = statuses.get(position).getPhotos().split(",");
		if (1 == photos.length) {
			if ("".equals(photos[0])) {
				return NormalString.StatusViewType.NO_PICTURE;
			} else {
				return NormalString.StatusViewType.ONE_PICTURE;
			}
		} else if (photos.length > 1) {
			return NormalString.StatusViewType.MULTI_PICTURE;
		}
		return NormalString.StatusViewType.INVALID;
	}

}
