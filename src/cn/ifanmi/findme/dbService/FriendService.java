package cn.ifanmi.findme.dbService;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.ifanmi.findme.bean.Friend;
import cn.ifanmi.findme.db.DBHelper;
import cn.ifanmi.findme.db.DBInfo;

public class FriendService {

	private DBHelper dbHelper;
	
	public FriendService(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public List<Friend> getFriends() {
		List<Friend> friends = new ArrayList<Friend>();
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + DBInfo.Table.FRIEND, null);
		if (null == cursor || cursor.getCount() <= 0) {
			if (cursor != null) {
				cursor.close();
			}
			return null;
		}
		while (cursor.moveToNext()) {
			Friend friend = new Friend();
			friend.setId(cursor.getString(cursor.getColumnIndex(Friend.ID)));
			friend.setPhoto(cursor.getString(cursor.getColumnIndex(Friend.PHOTO)));
			friend.setHeader(cursor.getString(cursor.getColumnIndex(Friend.HEADER)));
			friend.setNickname(cursor.getString(cursor.getColumnIndex(Friend.NICKNAME)));
			friend.setSignature(cursor.getString(cursor.getColumnIndex(Friend.SIGNATURE)));
			friend.setUrmCount(cursor.getString(cursor.getColumnIndex(Friend.URMCOUNT)));
			friends.add(friend);
		}
		cursor.close();
		return friends;
	}
	
	public String getNicknameById(String friendId) {
		String nickname = null;
		Cursor cursor = dbHelper.getReadableDatabase()
				.query(DBInfo.Table.FRIEND, new String[] {Friend.NICKNAME}, 
						Friend.ID + "=?", new String[] {friendId}, null, null, null);
		while (cursor.moveToNext()) {
			nickname = cursor.getString(cursor.getColumnIndex(Friend.NICKNAME));
		}
		cursor.close();
		return nickname;
	}
	
	public void insertFriends(List<Friend> friends) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		for (int i = 0; i < friends.size(); i++) {
			Friend friend = friends.get(i);
			db.execSQL("insert into " + DBInfo.Table.FRIEND + 
					" values(null, ?, ?, ?, ?, ?, ?)", 
					new String[] {friend.getId(), friend.getPhoto(), friend.getHeader(), 
					friend.getNickname(), friend.getSignature(), friend.getUrmCount(), });
		}
	}
	
	public void insertFriend(Friend friend) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.execSQL("insert into " + DBInfo.Table.FRIEND + 
				" values(null, ?, ?, ?, ?, ?, ?)", 
				new Object[] {friend.getId(), friend.getPhoto(), friend.getHeader(), 
				friend.getNickname(), friend.getSignature(), friend.getUrmCount()});
	}
	
	public void emptyFriendDB() {
		dbHelper.getReadableDatabase().execSQL("delete from " + DBInfo.Table.FRIEND);
	}
	
	public void closeDBHelper() {
		dbHelper.close();
	}
	
}
