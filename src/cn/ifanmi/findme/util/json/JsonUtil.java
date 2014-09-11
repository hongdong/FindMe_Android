package cn.ifanmi.findme.util.json;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.string.JsonString;

public class JsonUtil {
	
	public static List<String> getDepts(String jsonString) {
		List<String> depts = new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray(JsonString.Dept.DEPT_LIST);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				String dept = object.getString(JsonString.Dept.DEPT_NAME);
				depts.add(dept);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return depts;
	}
	
	public static Person getPerson(String jsonString) {
		Person person = new Person();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject object = jsonObject.getJSONObject(JsonString.Person.PERSON);
			person = getPersonByObject(object);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return person;
	}
	
	public static Person getPersonByObject(JSONObject object) {
		Person person = new Person();
		try {
			person.setId(object.getString(JsonString.Person.ID));
			person.setPhoto(object.getString(JsonString.Person.PHOTO));
			person.setNickname(object.getString(JsonString.Person.NICKNAME));
			person.setSignature(object.getString(JsonString.Person.SIGNATURE));
			JSONObject collegeObject = object.getJSONObject(JsonString.Person.COLLEGE);
			person.setCollegeName(collegeObject.getString(JsonString.Person.COLLEGENAME));
			JSONObject deptObject = object.getJSONObject(JsonString.Person.DEPT);
			person.setDeptName(deptObject.getString(JsonString.Person.DEPTNAME));
			person.setGrade(object.getString(JsonString.Person.GRADE));
			person.setSex(object.getString(JsonString.Person.SEX));
			person.setName(object.getString(JsonString.Person.NAME));
			person.setCl(object.getString(JsonString.Person.CL));
			String album = "";
			if (!object.isNull(JsonString.Person.ALBUM)) {
				JSONArray albumArray = object.getJSONArray(JsonString.Person.ALBUM);
				for (int i = 0; i < albumArray.length(); i++) {
					String photo = albumArray.getString(i);
					album += photo + ",";
				}
			}
			person.setAlbum(album);
			person.setIsAuth(object.getString(JsonString.Person.ISAUTH));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return person;
	}
	
}
