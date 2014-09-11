package cn.ifanmi.findme.dbService;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.ifanmi.findme.bean.Status;
import cn.ifanmi.findme.db.DBHelper;
import cn.ifanmi.findme.db.DBInfo;

public class StatusService {

	private DBHelper dbHelper;
	
	public StatusService(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public List<Status> getStatuses() {
		List<Status> statuses = new ArrayList<Status>();
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + DBInfo.Table.STATUS, null);
		if (null == cursor || cursor.getCount() <= 0) {
			if (cursor != null) {
				cursor.close();
			}
			return null;
		}
		while (cursor.moveToNext()) {
			Status status = new Status();
			status.setId(cursor.getString(cursor.getColumnIndex(Status.ID)));
			status.setPersonId(cursor.getString(cursor.getColumnIndex(Status.PERSONID)));
			status.setContent(cursor.getString(cursor.getColumnIndex(Status.CONTENT)));
			status.setPhotos(cursor.getString(cursor.getColumnIndex(Status.PHOTOS)));
			status.setReleaseTime(cursor.getString(cursor.getColumnIndex(Status.RELEASETIME)));
			status.setReadCount(cursor.getString(cursor.getColumnIndex(Status.READCOUNT)));
			status.setPraiseCount(cursor.getString(cursor.getColumnIndex(Status.PRAISECOUNT)));
			status.setMessageCount(cursor.getString(cursor.getColumnIndex(Status.MESSAGECOUNT)));
			status.setIsOfficial(cursor.getString(cursor.getColumnIndex(Status.ISOFFICIAL)));
			status.setIsTop(cursor.getString(cursor.getColumnIndex(Status.ISTOP)));
			statuses.add(status);
		}
		cursor.close();
		return statuses;
	}
	
	public void insertStatuses(List<Status> statuses) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		for (int i = 0; i < statuses.size(); i++) {
			Status status = statuses.get(i);
			db.execSQL("insert into " + DBInfo.Table.STATUS + 
					" values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
					new String[] {status.getId(), status.getPersonId(), status.getContent(), status.getPhotos(), 
					status.getReleaseTime(), status.getReadCount(), status.getPraiseCount(), 
					status.getMessageCount(), status.getIsOfficial(), status.getIsTop(), });
		}
	}
	
	public void emptyStatusDB() {
		dbHelper.getReadableDatabase().execSQL("delete from " + DBInfo.Table.STATUS);
	}
	
	public void closeDBHelper() {
		dbHelper.close();
	}
	
}
