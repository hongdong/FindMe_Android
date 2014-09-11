package cn.ifanmi.findme.adapter;

import java.util.Date;
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
import cn.ifanmi.findme.bean.Friend;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.ImageViewUtil;
import cn.ifanmi.findme.util.StringUtil;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.DateUtils;

public class InfoAdapter extends BaseAdapter {

	private Context context;
	private List<Friend> friends;
	
	public InfoAdapter(Context context, List<Friend> friends) {
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_info_item, null);
			holder = new Holder();
			holder.txt_nickname = (TextView) convertView.findViewById(R.id.txt_info_nickname);
			holder.txt_urm_count = (TextView) convertView.findViewById(R.id.txt_info_urm_count);
			holder.txt_last_message = (TextView) convertView.findViewById(R.id.txt_info_last_message);
			holder.txt_time = (TextView) convertView.findViewById(R.id.txt_info_time);
			holder.img_photo = (ImageView) convertView.findViewById(R.id.img_info_photo);
			holder.img_state = (ImageView) convertView.findViewById(R.id.img_info_state);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
/*		if(position % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.mm_listitem);
		} else {
			convertView.setBackgroundResource(R.drawable.mm_listitem_grey);
		}
*/		
		Friend friend = friends.get(position);
		
		String smallUrl = StringUtil.getSmallPhoto(friend.getPhoto(), NormalString.SmallPhoto.PERSON_INFO);
		ImageViewUtil.displayCircleImageView(context, holder.img_photo, smallUrl);
		holder.txt_nickname.setText(friend.getNickname());
		
		EMConversation conversation = EMChatManager.getInstance().getConversation(friend.getId());
		if (conversation.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			holder.txt_urm_count.setText(String.valueOf(conversation.getUnreadMsgCount()));
			holder.txt_urm_count.setVisibility(View.VISIBLE);
		} else {
			holder.txt_urm_count.setVisibility(View.GONE);
		}
		if (conversation.getMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();
			String lastString = getMessageDigest(lastMessage, context);
			SpannableString spannableString = new SpannableString(lastString);
			spannableString = StringUtil.spanExp(context, spannableString, lastString);
			holder.txt_last_message.setText(spannableString);

			holder.txt_time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
				holder.img_state.setVisibility(View.VISIBLE);
			} else {
				holder.img_state.setVisibility(View.GONE);
			}
		}
		
		return convertView;
	}
	
	private class Holder {
		/** 和谁的聊天记录 */
		TextView txt_nickname;
		/** 消息未读数 */
		TextView txt_urm_count;
		/** 最后一条消息的内容 */
		TextView txt_last_message;
		/** 最后一条消息的时间 */
		TextView txt_time;
		/** 用户头像 */
		ImageView img_photo;
		/** 最后一条消息的发送状态 */
		ImageView img_state;
	}
	
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
			if (message.direct == EMMessage.Direct.RECEIVE) {
				digest = getStrng(context, R.string.info_location_recv);
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
				digest = getStrng(context, R.string.info_location_prefix);
			}
			break;
		case IMAGE: // 图片消息
			ImageMessageBody imageMessageBody = (ImageMessageBody) message.getBody();
			digest = getStrng(context, R.string.info_picture) + imageMessageBody.getFileName();
			break;
		case VOICE:// 语音消息
			VoiceMessageBody voiceMessageBody = (VoiceMessageBody) message.getBody();
			digest = getStrng(context, R.string.info_voice) + voiceMessageBody.getFileName();
			break;
		case VIDEO: // 视频消息
			VideoMessageBody videoMessageBody = (VideoMessageBody) message.getBody();
			digest = getStrng(context, R.string.info_video) + videoMessageBody.getFileName();
			break;
		case FILE: //普通文件消息
			FileMessageBody fileMessageBody = (FileMessageBody) message.getBody();
			digest = getStrng(context, R.string.info_file) + fileMessageBody.getFileName();
			break;
		case TXT: // 文本消息
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			digest = txtBody.getMessage();
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}
		return digest;
	}
	
	String getStrng(Context context, int resId){
		return context.getResources().getString(resId);
	}

}
