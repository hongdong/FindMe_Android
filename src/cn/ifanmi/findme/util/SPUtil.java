package cn.ifanmi.findme.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.string.NormalString;

public class SPUtil {

	private static final String FIRST_USE = "first_use";
	private static final String DEFAULT_USER = "default_user";
	
	private static final String EMCHATOPTION = "emchatoption";
	public static final String EMCHATOPTION_NOTIFICATION = "notification";
	public static final String EMCHATOPTION_SOUND = "sound";
	public static final String EMCHATOPTION_VIBRATE = "vibrate";
	public static final String EMCHATOPTION_SPEAKER = "speaker";
	
	private static final String PUSH = "push";
	public static final String PUSH_FF = "push_ff";
	public static final String PUSH_SI = "push_si";
	
	/**
	 * 是否第一次使用软件。第一次使用软件要进入引导页面
	 * @param context
	 * @return
	 */
	public static boolean isFirstUse(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FIRST_USE, Context.MODE_PRIVATE);
		String first_use = sp.getString(FIRST_USE, null);
		if (NormalString.Fanmi.VERSION_CODE.equals(first_use)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 第一次引导页面展示后，第二次开始将不展示。
	 * 软件升级后NormalString.Fanmi.VERSION_CODE要做相应的改变，才能展示新版本引导页面
	 * @param context
	 */
	public static void setFirstUse(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FIRST_USE, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(FIRST_USE, NormalString.Fanmi.VERSION_CODE);
		editor.commit();
	}
	
	/**
	 * 从存储器里面找出默认账户（用于登录）
	 * @param context
	 * @param mode	如果mode为null，则返回user对象。
	 * 如果mode不为空，那么看userName是否为空，如果userName为空，那么返回空，如果不为空，那么返回user对象
	 * @return
	 */
	public static User getDefaultUser(Context context, String mode) {
		SharedPreferences sp = context.getSharedPreferences(DEFAULT_USER, Context.MODE_PRIVATE);
		String userName = sp.getString(User.NAME, null);
		/**
		 * 在logo界面调这个方法时要传不为空的进来。看看要不要自动登录到主页面。
		 * 而在login界面时传空进来，直接取出数据出用，即使userId是空的。
		 * 因为那个时候userId还没获取到，但又要用user的其它字段数据
		 */
		if (mode != null) {			
			if (null == userName) {
				return null;		//如果先前没有存储默认账户的话，就返回null
			}
		}
		String id = sp.getString(User.ID, null);
		String phoneNumber = sp.getString(User.PHONENUMBER, null);
		String password = sp.getString(User.PASSWORD, null);
		String collegeId = sp.getString(User.COLLEGEID, null);
		String collegeName = sp.getString(User.COLLEGENAME, null);
		String deptId = sp.getString(User.DEPTID, null);
		String deptName = sp.getString(User.DEPTNAME, null);
		String grade = sp.getString(User.GRADE, null);
		String photo = sp.getString(User.PHOTO, null);
		String name = sp.getString(User.NAME, null);
		String sex = sp.getString(User.SEX, null);
		String cl = sp.getString(User.CL, null);
		String nickname = sp.getString(User.NICKNAME, null);
		return new User(id, phoneNumber, password, collegeId, collegeName, 
				deptId, deptName, grade, photo, name, sex, cl, nickname);
	}
	
	public static String getUser(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(DEFAULT_USER, Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}
	
	/**
	 * 存入默认账户
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveDefaultUser(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(DEFAULT_USER, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void emptyDefaultUser(Context context) {
		SharedPreferences sp = context.getSharedPreferences(DEFAULT_USER, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(User.ID, null);
		editor.putString(User.PHONENUMBER, null);
		editor.putString(User.PASSWORD, null);
		editor.putString(User.COLLEGEID, null);
		editor.putString(User.COLLEGENAME, null);
		editor.putString(User.DEPTID, null);
		editor.putString(User.DEPTNAME, null);
		editor.putString(User.GRADE, null);
		editor.putString(User.PHOTO, null);
		editor.putString(User.NAME, null);
		editor.putString(User.SEX, null);
		editor.putString(User.CL, null);
		editor.putString(User.NICKNAME, null);
		editor.commit();
	}
	
	public static void setEMChatOption(Context context, String key, boolean value) {
		SharedPreferences sp = context.getSharedPreferences(EMCHATOPTION, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static boolean getEMChatOption(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(EMCHATOPTION, Context.MODE_PRIVATE);
		return sp.getBoolean(key, true);
	}
	
	public static void setPush(Context context, String key, int value) {
		SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static int getPush(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}
	
	public static void emptyPush(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(PUSH_FF, 0);
		editor.putInt(PUSH_SI, 0);
		editor.commit();
	}
	
}
