package cn.ifanmi.findme.ui.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.InfoAdapter;
import cn.ifanmi.findme.bean.Friend;
import cn.ifanmi.findme.dbService.FriendService;
import cn.ifanmi.findme.logic.IfanActivity;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

public class InfoActivity extends BaseActivity implements IfanActivity {

	private Button btn_info;
	private FriendService service;
	private List<Friend> friends, contactFriends;
	private ListView listView;
	private InfoAdapter adapter;
	private TextView txt_no_info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		service = new FriendService(this);
	}

	@Override
	public void initView() {
		btn_info = (Button)findViewById(R.id.btn_info_setting);
		listView = (ListView)findViewById(R.id.list_info);
		txt_no_info = (TextView)findViewById(R.id.txt_info_no_info);
		
		btn_info.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(InfoActivity.this, InfoSettingActivity.class);
				startActivity(intent);
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(InfoActivity.this, ChatActivity.class);
				intent.putExtra(Friend.ID, contactFriends.get(arg2).getId());
				intent.putExtra(Friend.PHOTO, contactFriends.get(arg2).getPhoto());
				intent.putExtra(Friend.NICKNAME, contactFriends.get(arg2).getNickname());
				startActivity(intent);
			}
		});
		registerForContextMenu(listView);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.delete_message, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_message:
			Friend friend = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			EMChatManager.getInstance().deleteConversation(friend.getId());	// 删除此会话
			onResume();		//这里要刷新消息列表
			((HomePageActivity) getParent()).updateUrmCount();	// 更新消息未读数
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshList();
	}
	
	@Override
	public void onBackPressed() {
		getParent().onBackPressed();
	}
	
	public void refreshList() {
		friends = service.getFriends();
		if (friends != null) {
			contactFriends = loadUsersWithRecentChat();
			adapter = new InfoAdapter(this, contactFriends);
			listView.setAdapter(adapter);
			if (contactFriends.size() > 0) {
				txt_no_info.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
			} else {
				txt_no_info.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			}
		}
	}
	
	private List<Friend> loadUsersWithRecentChat() {
		List<Friend> contactFriends = new ArrayList<Friend>();
		for (int i = 0; i < friends.size(); i++) {
			EMConversation conversation = EMChatManager.getInstance().getConversation(friends.get(i).getId());
			if (conversation.getMsgCount() > 0) {
				contactFriends.add(friends.get(i));
			}
		}
		sortUserByLastChatTime(contactFriends);
		return contactFriends;
	}
	
	private void sortUserByLastChatTime(List<Friend> friends) {
		Collections.sort(friends, new Comparator<Friend>() {
			public int compare(final Friend friend1, final Friend friend2) {
				EMConversation conversation1 = EMChatManager.getInstance().getConversation(friend1.getId());
				EMConversation conversation2 = EMChatManager.getInstance().getConversation(friend2.getId());
				EMMessage emMessage1 = conversation1.getLastMessage();
				EMMessage emMessage2 = conversation2.getLastMessage();
				if (emMessage2.getMsgTime() == emMessage1.getMsgTime()) {
					return 0;
				} else if (emMessage2.getMsgTime() > emMessage1.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

}
