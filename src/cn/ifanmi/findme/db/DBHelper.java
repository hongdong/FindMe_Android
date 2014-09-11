package cn.ifanmi.findme.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.ifanmi.findme.R;

public class DBHelper extends SQLiteOpenHelper {

	private Context context;
	
	public DBHelper(Context context) {
		super(context, DBInfo.DB.DB_NAME, null, DBInfo.DB.DB_VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] db_create_sqls = context.getResources().getStringArray(R.array.db_create_sql);
		for (int i = 0; i < db_create_sqls.length; i++) {
			db.execSQL(db_create_sqls[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String[] db_create_sqls = context.getResources().getStringArray(R.array.db_create_sql);
		String[] db_drop_sqls = context.getResources().getStringArray(R.array.db_drop_sql);
		if (newVersion > oldVersion) {
			for (int i = 0; i < db_drop_sqls.length; i++) {
				db.execSQL(db_drop_sqls[i]);	
				db.execSQL(db_create_sqls[i]);	
			}
		}
	}

}
