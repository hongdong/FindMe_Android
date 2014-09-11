package cn.ifanmi.findme.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.string.JsonString;

public class IfanService extends Service implements Runnable {

	//主服务是否运行
	private boolean isRun;
	
	//任务队列
	private static Queue<Task> tasks = new LinkedList<Task>();
	
	//Activity链表
	private static ArrayList<Activity> activities = new ArrayList<Activity>();
	
	//Fragment链表
	private static ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	
	//执行各种任务的方法
	private DoTaskService doTaskService = new DoTaskService(IfanService.this);
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			String activity = msg.getData().getString(Task.FAN_ACTIVITY);
			if (activity != null) {
				IfanActivity ifanActivity = (IfanActivity) getActivityByName(activity);
				if (ifanActivity != null) {
					ifanActivity.refresh(msg.what, (Map<String, Object>)msg.obj);
				}
			} else {
				String fragment = msg.getData().getString(Task.FAN_FRAGMENT);
				IfanFragment ifanFragment = (IfanFragment) getFragmentByName(fragment);
				if (ifanFragment != null) {
					ifanFragment.refresh(msg.what, (Map<String, Object>)msg.obj);
				}
			}
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		isRun = true;
		Thread thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * 添加任务到任务队列
	 * @param task	任务
	 */
	public static void addTask(Task task) {
		tasks.add(task);
	}
		
	@Override
	public void run() {
		while (isRun) {
			if (!tasks.isEmpty()) {
				Task task = tasks.poll();	//ִ执行完任务后将它从队列中删除
				if (task != null) {
					doTask(task);
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 执行网络任务
	 * @param task	任务
	 */
	private void doTask(Task task) {
		Message msg = new Message();
		msg.what = task.getTaskId();
		Map<String, Object> taskParams = task.getTaskParams();
		
		String activity = (String) taskParams.get(Task.FAN_ACTIVITY);
		if (activity != null) {
			msg.getData().putString(Task.FAN_ACTIVITY, activity);
		}
		
		String fragment = (String) taskParams.get(Task.FAN_FRAGMENT);
		if (fragment != null) {
			msg.getData().putString(Task.FAN_FRAGMENT, fragment);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(JsonString.Return.RESULT, doTaskService.doTask(task));
		
		String mode = (String) taskParams.get(Task.FAN_MODE);
		if (mode != null) {
			map.put(Task.FAN_MODE, mode);
		}
		
		msg.obj = map;
		handler.sendMessage(msg);
	}
	
	/**
	 * 将Activity添加到Activity链表中去
	 * @param activity
	 */
	public static void addActivity(Activity activity) {
		activities.add(activity);
	}
	
	/**
	 * 将Activity从Activity链表中移除
	 * @param activity
	 */
	public static void removeActivity(Activity activity) {
		activities.remove(activity);
	}
	
	/**
	 * 根据Activity的name从Activity链表中找到它
	 * @param name
	 * @return
	 */
	private Activity getActivityByName(String name) {
		if (!activities.isEmpty()) {
			for (Activity activity : activities) {
				if (activity != null) {
					if (activity.getClass().getSimpleName().equals(name)) {
						return activity;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 将Fragment添加到Fragment链表中去
	 * @param fragment
	 */
	public static void addFragment(Fragment fragment) {
		fragments.add(fragment);
	}
	
	/**
	 * 将Fragment从Fragment链表中移除
	 * @param fragment
	 */
	public static void removeFragment(Fragment fragment) {
		fragments.remove(fragment);
	}
	
	/**
	 * 根据Fragment的name从Fragment链表中找到它
	 * @param name
	 * @return
	 */
	private Fragment getFragmentByName(String name) {
		if (!fragments.isEmpty()) {
			for (Fragment fragment : fragments) {
				if (fragment != null) {
					if (fragment.getClass().getSimpleName().equals(name)) {
						return fragment;
					}
				}
			}
		}
		return null;
	}	
	
	public static void emptyList() {
		activities.clear();
		fragments.clear();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
