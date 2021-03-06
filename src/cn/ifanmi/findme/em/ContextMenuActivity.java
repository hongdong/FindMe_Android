package cn.ifanmi.findme.em;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.string.IntentString;
import com.easemob.chat.EMMessage;

public class ContextMenuActivity extends Activity {

	private int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int type = getIntent().getIntExtra("type", -1);
		if (type == EMMessage.Type.TXT.ordinal()) {
		    setContentView(R.layout.context_menu_for_text);
		} else if (type == EMMessage.Type.LOCATION.ordinal()) {
		    setContentView(R.layout.context_menu_for_location);
		} else if (type == EMMessage.Type.IMAGE.ordinal()) {
		    setContentView(R.layout.context_menu_for_image);
		} else if (type == EMMessage.Type.VOICE.ordinal()) {
		    setContentView(R.layout.context_menu_for_voice);
		}
		    
		/*    
		switch (getIntent().getIntExtra("type", -1)) {
		case txtValue:
			setContentView(R.layout.context_menu_for_text);
			break;
		case EMMessage.Type.LOCATION.ordinal():
			setContentView(R.layout.context_menu_for_location);
			break;
		case EMMessage.Type.IMAGE.ordinal():
			setContentView(R.layout.context_menu_for_image);
			break;
		case EMMessage.Type.VOICE.ordinal():
			setContentView(R.layout.context_menu_for_voice);
			break;
			//need to support netdisk and send netsdk?
		case Message.TYPE_NETDISK:
		    setContentView(R.layout.context_menu_for_netdisk);
		    break;
		case Message.TYPE_SENT_NETDISK:
		    setContentView(R.layout.context_menu_for_sent_netdisk);
		    break;
		default:
			break;
		}
		*/
		position = getIntent().getIntExtra("position", -1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void copy(View view){
		setResult(IntentString.ResultCode.CONTEXTMENU_CHAT_COPY, new Intent().putExtra("position", position));
		finish();
	}
	public void delete(View view){
		setResult(IntentString.ResultCode.CONTEXTMENU_CHAT_DELETE, new Intent().putExtra("position", position));
		finish();
	}
	public void forward(View view){
		setResult(IntentString.ResultCode.CONTEXTMENU_CHAT_FORWARD, new Intent().putExtra("position", position));
		finish();
	}
	
	public void open(View v){
	    setResult(IntentString.ResultCode.CONTEXTMENU_CHAT_OPEN, new Intent().putExtra("position", position));
        finish();
	}
	public void download(View v){
	    setResult(IntentString.ResultCode.CONTEXTMENU_CHAT_DOWNLOAD, new Intent().putExtra("position", position));
        finish();
	}
	public void toCloud(View v){
	    setResult(IntentString.ResultCode.CONTEXTMENU_CHAT_TO_CLOUD, new Intent().putExtra("position", position));
        finish();
	}
}
