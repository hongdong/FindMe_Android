package cn.ifanmi.findme.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardUtil {

	public static void show(Context context, View view) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);  
	    imm.showSoftInput(view, 0); 
	}
	
	public static void dismiss(Context context, View view) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}
