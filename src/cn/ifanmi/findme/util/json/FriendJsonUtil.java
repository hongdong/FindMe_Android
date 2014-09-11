package cn.ifanmi.findme.util.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.easemob.util.HanziToPinyin;
import cn.ifanmi.findme.bean.Friend;
import cn.ifanmi.findme.string.JsonString;

public class FriendJsonUtil {

	public static List<Friend> getFriends(String jsonString) {
		List<Friend> friends = new ArrayList<Friend>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray(JsonString.Friend.FRIEND_LIST);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				Friend friend = new Friend();
				friend.setId(object.getString(JsonString.Friend.ID));
				friend.setPhoto(object.getString(JsonString.Friend.PHOTO));
				friend.setNickname(object.getString(JsonString.Friend.NICKNAME));
				friend.setHeader(getHeaderByNickname(friend.getNickname()));
				friend.setSignature(object.getString(JsonString.Friend.SIGNATURE));
				friends.add(friend);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Collections.sort(friends, new Comparator<Friend>() {
			public int compare(Friend lhs, Friend rhs) {
				return lhs.getHeader().compareTo(rhs.getHeader());
			}
		});
		return friends;
	}
	
	public static String getHeaderByNickname(String nickname) {
		String header = null;
		if (Character.isDigit(nickname.charAt(0))) {
			header = "#";
		} else {
			header = HanziToPinyin.getInstance()
					.get(nickname.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase();
			char c = header.toLowerCase().charAt(0);
			if (c < 'a' || c > 'z') {
				header = "#";
			}
		}
		return header;
	}
}
