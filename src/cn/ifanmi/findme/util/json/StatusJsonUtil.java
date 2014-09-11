package cn.ifanmi.findme.util.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.ifanmi.findme.bean.InfoStatus;
import cn.ifanmi.findme.bean.Message;
import cn.ifanmi.findme.bean.Status;
import cn.ifanmi.findme.string.JsonString;

public class StatusJsonUtil {

	public static List<Status> getStatuses(String jsonString) {
		List<Status> statuses = new ArrayList<Status>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray(JsonString.Status.STATUS_LIST);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				Status status = new Status();
				status.setId(object.getString(JsonString.Status.ID));
				status.setPersonId(object.getJSONObject(JsonString.Status.PERSON)
						.getString(JsonString.Status.PERSONID));
				status.setContent(object.getString(JsonString.Status.CONTENT));
				String photos = "";
				if (!object.isNull(JsonString.Status.PHOTOS)) {
					JSONArray photoArray = object.getJSONArray(JsonString.Status.PHOTOS);
					for (int j = 0; j < photoArray.length(); j++) {
						String photo = photoArray.getString(j);
						photos += photo + ",";
					}
				}
				status.setPhotos(photos);
				status.setReleaseTime(object.getString(JsonString.Status.RELEASETIME));
				status.setReadCount(object.getString(JsonString.Status.READCOUNT));
				status.setPraiseCount(object.getString(JsonString.Status.PRAISECOUNT));
				status.setMessageCount(object.getString(JsonString.Status.MESSAGECOUNT));
				status.setIsOfficial(object.getString(JsonString.Status.ISOFFICIAL));
				status.setIsTop(object.getString(JsonString.Status.ISTOP));
				statuses.add(status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return statuses;
	}
	
	public static List<InfoStatus> getStatusInfos(String jsonString) {
		List<InfoStatus> infoStatuses = new ArrayList<InfoStatus>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray(JsonString.StatusInfo.STATUSINFO_LIST);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				Status status = new Status();
				status.setId(object.getString(JsonString.Status.ID));
				status.setPersonId(object.getJSONObject(JsonString.Status.PERSON)
						.getString(JsonString.Status.PERSONID));
				status.setContent(object.getString(JsonString.Status.CONTENT));
				String photos = "";
				if (!object.isNull(JsonString.Status.PHOTOS)) {
					JSONArray photoArray = object.getJSONArray(JsonString.Status.PHOTOS);
					for (int j = 0; j < photoArray.length(); j++) {
						String photo = photoArray.getString(j);
						photos += photo + ",";
					}
				}
				status.setPhotos(photos);
				status.setReleaseTime(object.getString(JsonString.Status.RELEASETIME));
				status.setReadCount(object.getString(JsonString.Status.READCOUNT));
				status.setPraiseCount(object.getString(JsonString.Status.PRAISECOUNT));
				status.setMessageCount(object.getString(JsonString.Status.MESSAGECOUNT));
				status.setIsOfficial(object.getString(JsonString.Status.ISOFFICIAL));
				status.setIsTop(object.getString(JsonString.Status.ISTOP));
				InfoStatus infoStatus = new InfoStatus();
				infoStatus.setStatus(status);
				infoStatus.setIsRead(object.getString(JsonString.Status.ISREAD));
				infoStatus.setUpdateTime(object.getString(JsonString.Status.UPDATETIME));
				infoStatuses.add(infoStatus);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return infoStatuses;
	}
	
	public static Map<String, Object> getMapSDM(String jsonString) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject mapObject = jsonObject.getJSONObject(JsonString.MapSDM.MAP_SDM);
			
			map.put(JsonString.MapSDM.IS_PRAISE, mapObject.getString(JsonString.MapSDM.IS_PRAISE));
			
			JSONObject indexObject = mapObject.getJSONObject(JsonString.MapSDM.INDEX);
			map.put(JsonString.MapSDM.BEGIN_INDEX, indexObject.getString(JsonString.MapSDM.BEGIN_INDEX));
			map.put(JsonString.MapSDM.END_INDEX, indexObject.getString(JsonString.MapSDM.END_INDEX));
			
			List<Message> messages = new ArrayList<Message>();
			if (!mapObject.isNull(JsonString.MapSDM.MESSAGE_LIST)) {
				JSONArray jsonArray = mapObject.getJSONArray(JsonString.MapSDM.MESSAGE_LIST);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					Message message = new Message();
					message.setId(object.getString(JsonString.MapSDM.ID));
					message.setPersonId(object.getJSONObject(JsonString.MapSDM.PERSON)
							.getString(JsonString.MapSDM.PERSONID));
					message.setContent(object.getString(JsonString.MapSDM.CONTENT));
					message.setReleaseTime(object.getString(JsonString.MapSDM.RELEASETIME));
					messages.add(message);
				}
			}
			map.put(JsonString.MapSDM.MESSAGE_LIST, messages);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
