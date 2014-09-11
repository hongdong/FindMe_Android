package cn.ifanmi.findme.util.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.ifanmi.findme.bean.Match;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.string.JsonString;

public class FindJsonUtil {

	public static List<Person> getFans(String jsonString) {
		List<Person> persons = new ArrayList<Person>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray(JsonString.Fans.FANS_LIST);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				Person person = JsonUtil.getPersonByObject(object);
				persons.add(person);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return persons;
	}
	
	public static Match getMatch(String jsonString) {
		Match match = new Match();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject object = jsonObject.getJSONObject(JsonString.Match.MATCH);
			match.setHasLike(object.getString(JsonString.Match.HAS_LIKE));
			if (!object.isNull(JsonString.Match.PERSON)) {
				JSONObject personObject = object.getJSONObject(JsonString.Match.PERSON);
				match.setPerson(JsonUtil.getPersonByObject(personObject));
			}
			match.setCountdown(object.getString(JsonString.Match.COUNTDOWN));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return match;
	}
}
