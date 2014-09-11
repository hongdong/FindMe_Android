package cn.ifanmi.findme.adapter;

import java.util.List;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.StringUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FansAdapter extends BaseAdapter {

	private Context context;
	private List<Person> persons;
	
	public FansAdapter(Context context, List<Person> persons) {
		this.context = context;
		this.persons = persons;
	}

	@Override
	public int getCount() {
		return persons.size();
	}

	@Override
	public Person getItem(int arg0) {
		return persons.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_fans_item, null);
			holder = new Holder();
			holder.img_photo = (ImageView)convertView.findViewById(R.id.img_fans_photo);
			holder.txt_nickname = (TextView)convertView.findViewById(R.id.txt_fans_nickname);
			holder.txt_signature = (TextView)convertView.findViewById(R.id.txt_fans_signature);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		Person person = persons.get(position);
		String smallUrl = StringUtil.getSmallPhoto(person.getPhoto(), NormalString.SmallPhoto.PERSON_FANS);
		ImageViewUtil.displayCircleImageView(context, holder.img_photo, smallUrl);
		holder.txt_nickname.setText(person.getNickname());
		holder.txt_signature.setText(person.getSignature());
		
		return convertView;
	}
	
	private class Holder {
		ImageView img_photo;
		TextView txt_nickname;
		TextView txt_signature;
	}

}
