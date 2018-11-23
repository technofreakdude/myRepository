package TestProperty;

import java.util.HashMap;

public class ResourceBundle {
	private String Get_User_Data;
	
	private HashMap property = new HashMap();

	
	public String getProperty(String key) {
		return this.property.get(key).toString();
	}

	public void setProperty(String key, String value) {
		this.property.put(key, value);
	}

	/*public String getGet_User_Data() {
		return Get_User_Data;
	}

	public void setGet_User_Data(String get_User_Data) {
		Get_User_Data = get_User_Data;
	}*/
	
}
