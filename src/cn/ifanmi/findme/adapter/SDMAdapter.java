package cn.ifanmi.findme.adapter;

import java.util.List;
import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Message;
import cn.ifanmi.findme.util.StringUtil;
import cn.ifanmi.findme.util.TimeUtil;

public class SDMAdapter extends BaseAdapter {

	private Context context;
	private List<Message> messages;
	private String statusPersonId;
	private SpannableString spannableString;
	
	public SDMAdapter(Context context, List<Message> messages, String statusPersonId) {
		this.context = context;
		this.messages = messages;
		this.statusPersonId = statusPersonId;
	}

	@Override
	public int getCount() {
		return messages.size();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.list_sdm_item, null);
			holder = new Holder();
			holder.img_photo = (ImageView)convertView.findViewById(R.id.img_sdm_photo);
			holder.txt_index = (TextView)convertView.findViewById(R.id.txt_sdm_index);
			holder.txt_publisher = (TextView)convertView.findViewById(R.id.txt_sdm_publisher);
			holder.txt_release_time = (TextView)convertView.findViewById(R.id.txt_sdm_release_time);
			holder.txt_content = (TextView)convertView.findViewById(R.id.txt_sdm_content);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		Message message = messages.get(position);
		String messagePersonId = message.getPersonId();
		if (messagePersonId.equals(statusPersonId)) {
			holder.img_photo.setImageResource(R.drawable.img_sdm_publisher);
			holder.txt_publisher.setVisibility(View.VISIBLE);
		} else {
			holder.img_photo.setImageResource(R.drawable.img_sdm_reply);
			holder.txt_publisher.setVisibility(View.GONE);
		}
		holder.txt_index.setText(position + 1 + "楼");
		String releaseTime = TimeUtil.getDisplayTime(TimeUtil.getNow(), message.getReleaseTime());
		holder.txt_release_time.setText(releaseTime);
		spannableString = new SpannableString(message.getContent());
		spannableString = StringUtil.spanExp(context, spannableString, message.getContent());
		holder.txt_content.setText(spannableString);
		return convertView;
	}
	
	private class Holder {
		ImageView img_photo;
		TextView txt_index;
		TextView txt_publisher;
		TextView txt_release_time;
		TextView txt_content;
	}

}
