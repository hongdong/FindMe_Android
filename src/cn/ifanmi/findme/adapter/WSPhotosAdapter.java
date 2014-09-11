package cn.ifanmi.findme.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.photoCache.Bimp;
import cn.ifanmi.findme.string.NormalString;

public class WSPhotosAdapter extends BaseAdapter {

	private Context context;
	
	public WSPhotosAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return (Bimp.bmp.size() + 1);	//这里的+1处理非常巧妙
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
		Holder holder;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_ws_photos_item, null);
			holder = new Holder();
			holder.imageView = (ImageView)convertView.findViewById(R.id.img_ws_photos);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		if (position == Bimp.bmp.size()) {
			if (Bimp.MAX == position) {
				holder.imageView.setVisibility(View.GONE);
			} else {
				holder.imageView.setImageResource(R.drawable.img_add_photos);
			}
		} else {
			holder.imageView.setImageBitmap(Bimp.bmp.get(position));
		}
		return convertView;
	}
	
	class Holder {
		ImageView imageView;
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NormalString.Handler.WHAT_SUCCESS:
				WSPhotosAdapter.this.notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public void update() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (Bimp.cur == Bimp.drr.size()) {
						handler.sendEmptyMessage(NormalString.Handler.WHAT_SUCCESS);
						break;
					} else {
						String path = Bimp.drr.get(Bimp.cur);
						Bitmap bm = Bimp.revitionImageSize(path);
						Bimp.bmp.add(bm);
						Bimp.cur += 1;
						handler.sendEmptyMessage(NormalString.Handler.WHAT_SUCCESS);
					}
				}
			}
		}).start();
	}

}
