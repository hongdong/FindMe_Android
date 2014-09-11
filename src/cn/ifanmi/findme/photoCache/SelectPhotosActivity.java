package cn.ifanmi.findme.photoCache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.photoCache.AlbumHelper;
import cn.ifanmi.findme.photoCache.ImageBucket;
import cn.ifanmi.findme.photoCache.ImageBucketAdapter;
import cn.ifanmi.findme.string.IntentString;

public class SelectPhotosActivity extends Activity implements IfanActivity {

	private Button btn_back;
	private AlbumHelper helper;
	private List<ImageBucket> buckets;
	private GridView gridView;
	private ImageBucketAdapter adapter;
	public static Bitmap bitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_photos);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		helper = AlbumHelper.getHelper();
		helper.init(this);
		buckets = helper.getImagesBucketList(false);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_add_photos);
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_select_photos);
		gridView = (GridView)findViewById(R.id.grid_select_photos);
		
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		adapter = new ImageBucketAdapter(this, buckets);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(SelectPhotosActivity.this, PhotosActivity.class);
				intent.putExtra(IntentString.Extra.BUCKET_NAME, buckets.get(arg2).bucketName);
				intent.putExtra(IntentString.Extra.IMAGE_LIST, (Serializable)buckets.get(arg2).imageList);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {

	}

}
