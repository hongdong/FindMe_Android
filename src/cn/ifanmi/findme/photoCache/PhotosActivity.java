package cn.ifanmi.findme.photoCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.photoCache.ImageGridAdapter.TextCallback;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.ToastUtil;

public class PhotosActivity extends Activity implements IfanActivity {

	private List<ImageItem> imageItems;
	private String bucketName;
	private Button btn_back, btn_ok;
	private TextView txt_bar;
	private GridView gridView;
	private ImageGridAdapter adapter;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NormalString.Handler.WHAT_SUCCESS:
				ToastUtil.prompt(PhotosActivity.this, "最多选择" + Bimp.MAX + "张图片");
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photos);
		initData();
		initView();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initData() {
		bucketName = getIntent().getStringExtra(IntentString.Extra.BUCKET_NAME);
		imageItems = (List<ImageItem>) getIntent().getSerializableExtra(IntentString.Extra.IMAGE_LIST);
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_photos);
		btn_ok = (Button)findViewById(R.id.btn_ok_photos);
		txt_bar = (TextView)findViewById(R.id.txt_photos_bar);
		gridView = (GridView)findViewById(R.id.grid_photos);
		
		txt_bar.setText(bucketName);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ArrayList<String> list = new ArrayList<String>();
				Collection<String> c = adapter.map.values();
				Iterator<String> it = c.iterator();
				for (; it.hasNext();) {
					list.add(it.next());
				}
				for (int i = 0; i < list.size(); i++) {
					if (Bimp.drr.size() < 9) {
						Bimp.drr.add(list.get(i));
					}
				}
				finish();
			}
		});
		
		adapter = new ImageGridAdapter(this, imageItems, handler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				btn_ok.setText("完成" + "(" + count + ")");
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				adapter.notifyDataSetChanged();
			}
		});
		
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}

}
