package cn.ifanmi.findme.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.drawable.Drawable;
import cn.ifanmi.findme.bean.Friend;
import cn.ifanmi.findme.bean.InfoStatus;
import cn.ifanmi.findme.bean.Match;
import cn.ifanmi.findme.bean.Message;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Status;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.util.HttpClientUtil;
import cn.ifanmi.findme.util.json.FindJsonUtil;
import cn.ifanmi.findme.util.json.FriendJsonUtil;
import cn.ifanmi.findme.util.json.JsonUtil;
import cn.ifanmi.findme.util.json.StatusJsonUtil;

public class DoTaskService {

	@SuppressWarnings("unused")
	private Context context;
	
	public DoTaskService(Context context) {
		this.context = context;
	}
	
	public Object doTask(Task task) {
		switch (task.getTaskId()) {
		case Task.FAN_AUTHO:
			return doAuthoTask(task);
		case Task.FAN_GETDEPT:
			return doGetDeptTask(task);
		case Task.FAN_LOGIN:
			return doLoginTask(task);
		case Task.FAN_CHECKUPDATE:
			return doCheckUpdateTask(task);
		case Task.FAN_EXIT:
			return doExitTask(task);
		case Task.FAN_GETSTATUS:
			return doGetStatusTask(task);
		case Task.FAN_WRITESTATUS:
			return doWriteStatusTask(task);
		case Task.FAN_GETSDM:
			return doGetMapSDMTask(task);
		case Task.FAN_PRAISESTATUS:
			return doPraiseStatusTask(task);
		case Task.FAN_SDMESSAGE:
			return doSDMessageTask(task);
		case Task.FAN_GETPD:
			return doGetPDTask(task);
		case Task.FAN_UPDATEUP:
			return doUpdateUPTask(task);
		case Task.FAN_UPDATEUD:
			return doUpdateUDTask(task);
		case Task.FAN_ADDALBUM:
			return doAddAlbumTask(task);
		case Task.FAN_DELETEAP:
			return doDeleteAPTask(task);
		case Task.FAN_GETFRIEND:
			return doGetFriendsTask(task);
		case Task.FAN_GETSTATUSINFO:
			return doGetStatusInfoTask(task);
		case Task.FAN_GETMATCH:
			return doGetMatchTask(task);
		case Task.FAN_PASS:
			return doPassTask(task);
		case Task.FAN_LIKE:
			return doLikeTask(task);
		case Task.FAN_GETFANS:
			return doGetFansTask(task);
		case Task.FAN_FEEDBACK:
			return doFeedbackTask(task);
		case Task.FAN_CHECKCAPTCHA:
			return doCheckCaptchaTask(task);
		case Task.FAN_GETPHOTO:
			return doGetPhotoTask(task);
		case Task.FAN_AUTH:
			return doAuthTask(task);
		case Task.FAN_GETTOKEN:
			return doGetTokenTask(task);
		case Task.FAN_SIGNUP:
			return doSignupTask(task);
		default:
			return null;
		}
	}
	
	private String doAuthoTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String autho_url = HttpClientUtil.BASE_URL + "data/user/login_user_phonenumber.do";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userPhoneNumber", (String) taskParams.get(User.PHONENUMBER));
		map.put("userPassword", (String) taskParams.get(User.PASSWORD));
		map.put("equitNo", (String) taskParams.get(Task.FAN_AUTHO_DEVICE_CODE));
		map.put("osType", (String) taskParams.get(Task.FAN_AUTHO_SYSTEM_TYPE));
		map.put("backLogin", (String) taskParams.get(Task.FAN_AUTHO_BACK_LOGIN));
		try {
			result = HttpClientUtil.postRequest(autho_url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private List<String> doGetDeptTask(Task task) {
		List<String> strings = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String collegeId = (String) taskParams.get(User.COLLEGEID);
		String getdept_url = HttpClientUtil.BASE_URL + "data/school/dept_list.do?schoolId=" + collegeId;
		try {
			String jsonString = HttpClientUtil.getRequest(getdept_url);
			strings = JsonUtil.getDepts(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strings;
	}
	
	private String doLoginTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		User user = (User) taskParams.get(User.class.getName());
		Map<String, String> map = new HashMap<String, String>();
		map.put("userPhoneNumber", user.getPhoneNumber());
		map.put("userPassword", user.getPassword());
		map.put("school._id", user.getCollegeId());
		map.put("school.schoolName", user.getCollegeName());
		map.put("department._id", user.getDeptId());
		map.put("department.deptName", user.getDeptName());
		map.put("userGrade", user.getGrade());
		map.put("key", (String) taskParams.get(User.PHOTO));
		map.put("userRealName", user.getName());
        map.put("userSex", user.getSex());
        map.put("userConstellation", user.getCl());
        map.put("userNickName", user.getNickname());
        map.put("userEquipment.equitNo", (String) taskParams.get(Task.FAN_AUTHO_DEVICE_CODE));
        map.put("userEquipment.osType", (String) taskParams.get(Task.FAN_AUTHO_SYSTEM_TYPE));
		try {
			String login_url = HttpClientUtil.BASE_URL + "data/user/complete_user_info.do";
			result = HttpClientUtil.postRequest(login_url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String doCheckUpdateTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String type = (String) taskParams.get(Task.FAN_AUTHO_SYSTEM_TYPE);
		String checkupdate_url = HttpClientUtil.BASE_URL + "data/version/detail.do?" + "type=" + type;
		try {
			result = HttpClientUtil.getRequest(checkupdate_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String doExitTask(Task task) {
		String result = null;
		String exit_url = HttpClientUtil.BASE_URL + "data/user/login_out.do";
		Map<String, String> map = new HashMap<String, String>();
		try {
			result = HttpClientUtil.postRequest(exit_url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<Status> doGetStatusTask(Task task) {
		List<Status> statuses = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String mode = (String) taskParams.get(Task.FAN_MODE);
		String id = (String) taskParams.get(Status.ID);
		String getstatus_url = HttpClientUtil.BASE_URL + "data/post/post_list.do?" +
				"postId=" + id + "&type=" + mode;
		try {
			String jsonString = HttpClientUtil.getRequest(getstatus_url);
			statuses = StatusJsonUtil.getStatuses(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statuses;
	}
	
	private String doWriteStatusTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		Map<String, String> map = new HashMap<String, String>();
		String key = (String) taskParams.get(Status.PHOTOS);
		if (key != null) {
			map.put("key", key);
		}
		map.put("postContent", (String) taskParams.get(Status.CONTENT));
		map.put("postOfficial", Status.ISOFFICIAL_NO);
		try {
			String addalbum_url = HttpClientUtil.BASE_URL + "data/post/release_post_qn.do";
			result = HttpClientUtil.postRequest(addalbum_url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Map<String, Object> doGetMapSDMTask(Task task) {
		Map<String, Object> result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String mode = (String) taskParams.get(Task.FAN_MODE);
		String statusId = (String) taskParams.get(Status.class.getSimpleName() + Status.ID);
		String messageId = (String) taskParams.get(Message.class.getSimpleName() + Message.ID);
		String isInfo = (String) taskParams.get(Task.FAN_GETSDM_ISINFO);
		String getmessage_url = HttpClientUtil.BASE_URL + "data/post/post_msg_list.do?" +
				"postId=" + statusId + "&type=" + mode + "&index=" + messageId + "&isNews=" + isInfo;
		try {
			String jsonString = HttpClientUtil.getRequest(getmessage_url);
			result = StatusJsonUtil.getMapSDM(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String doPraiseStatusTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String statusId = (String) taskParams.get(Status.ID);
		String mode = (String) taskParams.get(Task.FAN_MODE);
		String praisestatus_url = HttpClientUtil.BASE_URL + "data/post/post_praise.do?" + 
				"postId=" + statusId + "&type=" + mode;
		try {
			result = HttpClientUtil.getRequest(praisestatus_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String doSDMessageTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String sdmessage_url = HttpClientUtil.BASE_URL + "data/post/post_msg.do";
		Map<String, String> map = new HashMap<String, String>();
		map.put("postId", (String) taskParams.get(Status.ID));
		map.put("postMsgContent", (String) taskParams.get(Message.CONTENT));
		try {
			result = HttpClientUtil.postRequest(sdmessage_url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Person doGetPDTask(Task task) {
		Person person = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String personId = (String) taskParams.get(Person.ID);
		String getpd_url = HttpClientUtil.BASE_URL + "data/user/user_info.do?" + "userId=" + personId;
		try {
			String jsonString = HttpClientUtil.getRequest(getpd_url);
			person = JsonUtil.getPerson(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return person;
	}
	
	public String doUpdateUPTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", (String) taskParams.get(Person.PHOTO));
		try {
			String updateup_url = HttpClientUtil.BASE_URL + "data/user/user_uphoto_qn.do";
			result = HttpClientUtil.postRequest(updateup_url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String doUpdateUDTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String nickname = (String) taskParams.get(Person.NICKNAME);
		String signature = (String) taskParams.get(Person.SIGNATURE);
		String updateud_url = HttpClientUtil.BASE_URL + "data/user/update_info.do";
		Map<String, String> map = new HashMap<String, String>();
		if (nickname != null) {
			map.put("userNickName", nickname);
		}
		if (signature != null) {
			map.put("userSignature", signature);
		}
		try {
			result = HttpClientUtil.postRequest(updateud_url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String doAddAlbumTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", (String) taskParams.get(Person.ALBUM));
		try {
			String addalbum_url = HttpClientUtil.BASE_URL + "data/user/add_album_uphoto_qn.do";
			result = HttpClientUtil.postRequest(addalbum_url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String doDeleteAPTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String photo = (String) taskParams.get(Person.ALBUM);
		String deleteap_url = HttpClientUtil.BASE_URL + "data/user/del_album_uphoto.do?" + 
				"photoUrl=" + photo;
		try {
			result = HttpClientUtil.getRequest(deleteap_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<Friend> doGetFriendsTask(Task task) {
		List<Friend> friends = null;
		String getfriend_url = HttpClientUtil.BASE_URL + "data/user/user_friend.do";
		try {
			String jsonString = HttpClientUtil.getRequest(getfriend_url);
			friends = FriendJsonUtil.getFriends(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return friends;
	}
	
	public List<InfoStatus> doGetStatusInfoTask(Task task) {
		List<InfoStatus> infoStatuses = new ArrayList<InfoStatus>();
		Map<String, Object> taskParams = task.getTaskParams();
		String mode = (String) taskParams.get(Task.FAN_MODE);
		String id = (String) taskParams.get(Status.ID);
		String getstatusinfo_url = HttpClientUtil.BASE_URL + "data/news/news_list.do?" +
				"postId=" + id + "&type=" + mode;
		try {
			String jsonString = HttpClientUtil.getRequest(getstatusinfo_url);
			infoStatuses = StatusJsonUtil.getStatusInfos(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infoStatuses;
	}
	
	public Match doGetMatchTask(Task task) {
		Match match = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String type = (String) taskParams.get(Task.FAN_GETMATCH_TYPE);
		String getmatch_url = HttpClientUtil.BASE_URL + "data/user/match_info.do?" + "type=" + type;
		try {
			String jsonString = HttpClientUtil.getRequest(getmatch_url);
			match = FindJsonUtil.getMatch(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return match;
	}
	
	public Match doPassTask(Task task) {
		Match match = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String type = (String) taskParams.get(Task.FAN_GETMATCH_TYPE);
		String personId = (String) taskParams.get(Person.ID);
		String pass_url = HttpClientUtil.BASE_URL + "data/user/match_info.do?" + 
				"type=" + type + "&userMatchId=" + personId;
		try {
			String jsonString = HttpClientUtil.getRequest(pass_url);
			match = FindJsonUtil.getMatch(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return match;
	}
	
	public String doLikeTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String type = (String) taskParams.get(Task.FAN_GETMATCH_TYPE);
		String personId = (String) taskParams.get(Person.ID);
		String like_url = HttpClientUtil.BASE_URL + "data/user/like_user.do?" + 
				"type=" + type + "&likeUserId=" + personId;
		try {
			result = HttpClientUtil.getRequest(like_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<Person> doGetFansTask(Task task) {
		List<Person> persons = null;
		String getfans_url = HttpClientUtil.BASE_URL + "data/user/fans_list.do";
		try {
			String jsonString = HttpClientUtil.getRequest(getfans_url);
			persons = FindJsonUtil.getFans(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persons;
	}
	
	private String doFeedbackTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String feedback_url = HttpClientUtil.BASE_URL + "data/user/feedback.do";
		Map<String, String> map = new HashMap<String, String>();
		map.put("suggestion", (String) taskParams.get(Task.FAN_FEEDBACK_STRING));
		try {
			result = HttpClientUtil.postRequest(feedback_url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String doCheckCaptchaTask(Task task) {
		String result = null;
		String checkcaptcha_url = HttpClientUtil.BASE_URL + "data/user/auth_code.do";
		try {
			result = HttpClientUtil.getRequest(checkcaptcha_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private Drawable doGetPhotoTask(Task task) {
		Drawable drawable = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String url = (String) taskParams.get(Task.FAN_GETPHOTO_URL);
		try {
			drawable = HttpClientUtil.getDrawable(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return drawable;
	}
	
	private String doAuthTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String studentId = (String) taskParams.get(Task.FAN_AUTH_STUDENTID);
		String password = (String) taskParams.get(Task.FAN_AUTH_PASSWORD);
		String captcha = (String) taskParams.get(Task.FAN_AUTH_CAPTCHA);
		String auth_url = HttpClientUtil.BASE_URL + "data/user/user_auth.do?" + 
				"username=" + studentId + "&pwd=" + password + "&code=" + captcha;
		try {
			result = HttpClientUtil.getRequest(auth_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String doGetTokenTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String type = (String) taskParams.get(Task.FAN_GETTOKEN_TYPE);
		String gettoken_url = HttpClientUtil.BASE_URL + "data/qiniu/uploadtoken.do?" 
				+ "type=" + type;
		try {
			result = HttpClientUtil.getRequest(gettoken_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String doSignupTask(Task task) {
		String result = null;
		Map<String, Object> taskParams = task.getTaskParams();
		String signup_url = HttpClientUtil.BASE_URL + "data/user/simple_rgst.do";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userPhoneNumber", (String) taskParams.get(User.PHONENUMBER));
		map.put("userPassword", (String) taskParams.get(User.PASSWORD));
		try {
			result = HttpClientUtil.postRequest(signup_url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
