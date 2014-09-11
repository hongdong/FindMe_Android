package cn.ifanmi.findme.photoCache;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.photoCache.BitmapCache.ImageCallback;

public class ImageBucketAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();

	Activity act;
	/**
	 * 图片集列表
	 */
	List<ImageBucket> dataList;
	BitmapCache cache;
	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};

	public ImageBucketAdapter(Activity act, List<ImageBucket> list) {
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	class Holder {
		private ImageView iv;
		private TextView name;
		private TextView count;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Holder holder;
		if (arg1 == null) {
			holder = new Holder();
			arg1 = View.inflate(act, R.layout.list_image_bucket_item, null);
			holder.iv = (ImageView) arg1.findViewById(R.id.img_image_bucket);
			holder.name = (TextView) arg1.findViewById(R.id.txt_image_bucket_name);
			holder.count = (TextView) arg1.findViewById(R.id.txt_image_bucket_count);
			arg1.setTag(holder);
		} else {
			holder = (Holder) arg1.getTag();
		}
		ImageBucket item = dataList.get(arg0);
		holder.count.setText(item.count + "张");
		holder.name.setText(item.bucketName);
		if (item.imageList != null && item.imageList.size() > 0) {
			String thumbPath = item.imageList.get(0).thumbnailPath;
			String sourcePath = item.imageList.get(0).imagePath;
			holder.iv.setTag(sourcePath);
			cache.displayBmp(holder.iv, thumbPath, sourcePath, callback);
		} else {
			holder.iv.setImageBitmap(null);
			Log.e(TAG, "no images in bucket " + item.bucketName);
		}
		return arg1;
	}

}
