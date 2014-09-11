package cn.ifanmi.findme.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.ui.activity.PersonDataActivity;

public class PersonDataListener implements OnClickListener {

	private Context context;
	private String personId;
	
	public PersonDataListener(Context context, String personId) {
		this.context = context;
		this.personId = personId;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(context, PersonDataActivity.class);
		intent.putExtra(Person.ID, personId);
		context.startActivity(intent);
	}

}
