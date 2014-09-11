package cn.ifanmi.findme.adapter;

import cn.ifanmi.findme.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ClAdapter extends BaseAdapter {

	private Context context;
	private int[] imageIds = {R.drawable.cl00, R.drawable.cl01, R.drawable.cl02, R.drawable.cl03, 
			R.drawable.cl04, R.drawable.cl05, R.drawable.cl06, R.drawable.cl07, 
			R.drawable.cl08, R.drawable.cl09, R.drawable.cl10, R.drawable.cl11, };
	private String[] cls = {"白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", 
			"天秤座", "天蝎座", "射手座", "魔羯座", "水瓶座", "双鱼座", };
	private String[] dates = {"(3月21日-4月20日)", "(4月21日-5月21日)", "(5月22日-6月21日)", "(6月22日-7月22日)", 
			"(7月23日-8月23日)", "(8月24日-9月22日)", "(9月23日-10月23日)", "(10月24日-11月22日)", 
			"(11月23日-12月21日)", "(12月22日-1月20日)", "(1月21日-2月19日)", "(2月20日-3月20日)", };
	
	public ClAdapter(Context context) {
		this.context = context;
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
		Holder holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_cl_item, null);
			holder = new Holder();
			holder.img_cl = (ImageView)convertView.findViewById(R.id.img_list_cl_cl);
			holder.txt_cl = (TextView)convertView.findViewById(R.id.txt_list_cl_cl);
			holder.txt_date = (TextView)convertView.findViewById(R.id.txt_list_cl_date);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.img_cl.setImageResource(imageIds[position]);
		holder.txt_cl.setText(cls[position]);
		holder.txt_date.setText(dates[position]);
		return convertView;
	}
	
	private class Holder {
		ImageView img_cl;
		TextView txt_cl;
		TextView txt_date;
	}
	
	public String getClByIndex(int index) {
		return cls[index];
	}

}
