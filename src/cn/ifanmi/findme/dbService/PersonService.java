package cn.ifanmi.findme.dbService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.db.DBHelper;
import cn.ifanmi.findme.db.DBInfo;

public class PersonService {

	private DBHelper dbHelper;
	
	public PersonService(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public Person getPersonById(String personId) {
		Person person = null;
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
				"select * from " + DBInfo.Table.PERSON + " where " + Person.ID + " = ? ", 
				new String[] {personId, });
		if (null != cursor && cursor.getCount() > 0) {
			cursor.moveToFirst();		//这一句一定要加，总之我不加就出错了
			String photo = cursor.getString(cursor.getColumnIndex(Person.PHOTO));
			String nickname = cursor.getString(cursor.getColumnIndex(Person.NICKNAME));
			String signature = cursor.getString(cursor.getColumnIndex(Person.SIGNATURE));
			String collegeName = cursor.getString(cursor.getColumnIndex(Person.COLLEGENAME));
			String deptName = cursor.getString(cursor.getColumnIndex(Person.DEPTNAME));
			String grade = cursor.getString(cursor.getColumnIndex(Person.GRADE));
			String sex = cursor.getString(cursor.getColumnIndex(Person.SEX));
			String name = cursor.getString(cursor.getColumnIndex(Person.NAME));
			String cl = cursor.getString(cursor.getColumnIndex(Person.CL));
			String album = cursor.getString(cursor.getColumnIndex(Person.ALBUM));
			String isAuth = cursor.getString(cursor.getColumnIndex(Person.ISAUTH));
			person = new Person(personId, photo, nickname, signature, 
					collegeName, deptName, grade, sex, name, cl, album, isAuth);
		}
		if (cursor != null) {
			cursor.close();
		}
		return person;
	}
	
	public void insertPerson(Person person) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.execSQL("insert into " + DBInfo.Table.PERSON + 
				" values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
				new Object[] {person.getId(), person.getPhoto(), person.getNickname(), 
				person.getSignature(), person.getCollegeName(), person.getDeptName(), 
				person.getGrade(), person.getSex(), person.getName(), 
				person.getCl(), person.getAlbum(), person.getIsAuth()});
	}
	
	public void updataPersonInfo(String personId, String column, String updata) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(column, updata);
		db.update(DBInfo.Table.PERSON, values, Person.ID + " = ? ", new String[] {personId, });
	}
	
	public void emptyPersonDB() {
		dbHelper.getReadableDatabase().execSQL("delete from " + DBInfo.Table.PERSON);
	}
	
	public void closeDBHelper() {
		dbHelper.close();
	}
	
}
