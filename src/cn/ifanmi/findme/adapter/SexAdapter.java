package cn.ifanmi.findme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.ifanmi.findme.R;

public class SexAdapter extends BaseAdapter {

	private Context context;
	
	public SexAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 2;
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
		View view = LayoutInflater.from(context).inflate(R.layout.list_sex_item, null);
		TextView textView = (TextView) view.findViewById(R.id.txt_sex_item);
		switch (position) {
		case 0:
			textView.setText("酷男");
			textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sex_male, 0, 0, 0);
			break;
			
		case 1:
			textView.setText("美女");
			textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sex_female, 0, 0, 0);
			break;
		}
		return view;
	}

}
