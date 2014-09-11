package cn.ifanmi.findme.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Friend;
import cn.ifanmi.findme.listener.PersonDataListener;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.StringUtil;

public class FriendAdapter extends BaseAdapter implements SectionIndexer {

	private Context context;
	private List<Friend> friends;
	private SparseIntArray positionOfSection;
	private SparseIntArray sectionOfPosition;
	
	public FriendAdapter(Context context, List<Friend> friends) {
		this.context = context;
		this.friends = friends;
	}

	@Override
	public int getCount() {
		return friends.size();
	}

	@Override
	public Friend getItem(int position) {
		return friends.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_friend_item, null);
			holder = new Holder();
			holder.txt_header = (TextView)convertView.findViewById(R.id.txt_friend_header);
			holder.img_photo = (ImageView)convertView.findViewById(R.id.img_friend_photo);
			holder.txt_nickname = (TextView)convertView.findViewById(R.id.txt_friend_nickname);
			holder.txt_signature = (TextView)convertView.findViewById(R.id.txt_friend_signature);
			holder.txt_urm_count = (TextView)convertView.findViewById(R.id.txt_friend_urm_count);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		Friend friend = friends.get(position);
		String header = friend.getHeader();
		if (0 == position || !header.equals(getItem(position - 1).getHeader())) {
			if (header.isEmpty()) {
				holder.txt_header.setVisibility(View.GONE);
			} else {
				holder.txt_header.setText(header);
				holder.txt_header.setVisibility(View.VISIBLE);
			}
		} else {
			holder.txt_header.setVisibility(View.GONE);
		}
		String smallUrl = StringUtil.getSmallPhoto(friend.getPhoto(), NormalString.SmallPhoto.PERSON_FRIEND);
		ImageViewUtil.displayCircleImageView(context, holder.img_photo, smallUrl);
		holder.img_photo.setOnClickListener(new PersonDataListener(context, friend.getId()));
		holder.txt_nickname.setText(friend.getNickname());
		holder.txt_signature.setText(friend.getSignature());
		/**
		 * 官方demo里，自定义的未读消息数其实只是未读新好友消息数。
		 * 可能是服务器没有对这个进行统计和累加，所以放在本地数据库。
		 * 不过这也是一种不好的做法，因为换手机就会丢数据。
		 * 每个好友的未读消息数是在环信系统api里面，而不是这里的getUrmCount
		 */
		holder.txt_urm_count.setText(friend.getUrmCount());	
		
		return convertView;
	}

	@Override
	public int getPositionForSection(int section) {
		return positionOfSection.get(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		return sectionOfPosition.get(position);
	}

	@Override
	public Object[] getSections() {
		positionOfSection = new SparseIntArray();
		sectionOfPosition = new SparseIntArray();
		int count = getCount();
		List<String> list = new ArrayList<String>();
		list.add(context.getString(R.string.search_header));
		positionOfSection.put(0, 0);
		sectionOfPosition.put(0, 0);
		for (int i = 0; i < count; i++) {
			String letter = getItem(i).getHeader();
			int section = list.size() - 1;
			if (list.get(section) != null && !list.get(section).equals(letter)) {
				list.add(letter);
				section++;
				positionOfSection.put(section, i);
			}
			sectionOfPosition.put(i, section);
		}
		return list.toArray(new String[list.size()]);
	}
	
	private class Holder {
		TextView txt_header;
		ImageView img_photo;
		TextView txt_nickname;
		TextView txt_signature;
		TextView txt_urm_count;
	}

}
