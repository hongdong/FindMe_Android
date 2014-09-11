package cn.ifanmi.findme.adapter;

import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Person;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MeListAdapter extends BaseAdapter {

	private Context context;
	private String isAuth;
	private int[] imageIds = {R.drawable.img_auth, R.drawable.img_share, };
	private String[] texts = {"实名认证", "推荐给好友", };
	
	public MeListAdapter(Context context, String isAuth) {
		this.context = context;
		this.isAuth = isAuth;
	}

	@Override
	public int getCount() {
		return imageIds.length;
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
		convertView = LayoutInflater.from(context).inflate(R.layout.me_list, null);
		ImageView imageView = (ImageView)convertView.findViewById(R.id.img_me_list);
		TextView textView = (TextView)convertView.findViewById(R.id.txt_me_list);
		imageView.setImageResource(imageIds[position]);
		textView.setText(texts[position]);
		if (0 == position) {
			ImageView img_auth = (ImageView)convertView.findViewById(R.id.img_me_list_auth);
			if (isAuth.equals(Person.ISAUTH_YES)) {
				img_auth.setImageResource(R.drawable.img_auth_yes);
			} else if (isAuth.equals(Person.ISAUTH_NO)) {
				img_auth.setImageResource(R.drawable.img_auth_no);
			}
		}
		return convertView;
	}

}
