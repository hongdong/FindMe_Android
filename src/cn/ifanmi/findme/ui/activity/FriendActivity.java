package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.FriendAdapter;
import cn.ifanmi.findme.bean.Friend;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.FriendService;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.ui.view.Sidebar;

public class FriendActivity extends Activity implements IfanActivity {

	private FriendService service;
	private List<Friend> friends;
	private ListView listView;
	private FriendAdapter adapter;
	private Sidebar sidebar;
	private NewFriendReceiver nfReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		service = new FriendService(this);
		friends = service.getFriends();
	}

	@Override
	public void initView() {
		listView = (ListView)findViewById(R.id.list_friend);
		sidebar = (Sidebar)findViewById(R.id.sidebar_friend);
		
		if (friends != null) {
			refreshList();
		}
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(FriendActivity.this, ChatActivity.class);
				intent.putExtra(Friend.ID, friends.get(arg2).getId());
				intent.putExtra(Friend.PHOTO, friends.get(arg2).getPhoto());
				intent.putExtra(Friend.NICKNAME, friends.get(arg2).getNickname());
				startActivity(intent);
			}
		});
		doGetFriendTask();
		
		nfReceiver = new NewFriendReceiver();
		IntentFilter ackMessageIntentFilter = new IntentFilter(IntentString.Receiver.NEW_FRIEND);
		registerReceiver(nfReceiver, ackMessageIntentFilter);
	}
	
	public void doGetFriendTask() {
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, FriendActivity.class.getSimpleName());
		Task task = new Task(Task.FAN_GETFRIEND, taskParams);
		IfanService.addTask(task);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		switch (taskId) {
		case Task.FAN_GETFRIEND:
			friends = (List<Friend>) map.get(JsonString.Return.RESULT);
			if (friends != null) {
				refreshList();
				service.emptyFriendDB();
				service.insertFriends(friends);
			}
			break;
		}
	}
	
	private void refreshList() {
/*		String photo = "http://114.215.115.33/upload/userphoto/2014072112004726025829476/20140721120047260.png";
		String[] name = new String[] {"1", "2", "啊", "病", "边", "草草", "的", "E", "F", "G", 
				"H", "I", "J", "K", "L", "M", "N", 
				"O", "P", "Q", "R", "S", "T", 
				"U", "V", "W", "X", "Y", "Z"};
		String[] sig = {"你的牌打得太好了", };
		List<Friend> hehe = new ArrayList<Friend>();
		for (int i = 0; i < name.length; i++) {
			Friend friend = new Friend();
			friend.setPhoto(photo);
			friend.setNickname(name[i]);
			friend.setHeader(FriendJsonUtil.getHeaderByNickname(name[i]));
			friend.setSignature(sig[0]);
			hehe.add(friend);
		}
		adapter = new FriendAdapter(this, hehe);*/
		adapter = new FriendAdapter(this, friends);
		listView.setAdapter(adapter);
		sidebar.setListView(listView);
		sidebar.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onBackPressed() {
		getParent().onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(nfReceiver);
	}
	
	private class NewFriendReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			doGetFriendTask();
		}
	}

}
