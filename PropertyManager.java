package TestProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class  PropertyManager {
	ResourceBundle resB = new ResourceBundle();
	Properties property;
	FileInputStream fs;

	private static PropertyManager propertymanager = null; 
	// static method to create instance of Singleton class 
    public static PropertyManager getInstance() 
    { 
        if (propertymanager == null) 
        	propertymanager = new PropertyManager(); 
        
        return propertymanager; 
    } 
    // 	private constructor restricted to this class itself 
    private PropertyManager() 
    { 
        load();
    } 
    	
	public void load() {
		System.out.println("load method");
		try {
			fs = new FileInputStream("c:\\ConnectedChillersConfig\\config_dev.properties");
			property = new Properties();
			property.load(fs);
			
			//resB.setGet_User_Data(property.getProperty("Get_User_Data"));
			System.out.println("From propert File system - key=sensorUnits");
			System.out.println(property.getProperty("sensorUnits"));
			resB.setProperty("sensorUnits", property.getProperty("sensorUnits"));
			System.out.println("After setting in resource bundle . Value of key = sensorUnits is :: ");
			System.out.println(resB.getProperty("sensorUnits"));
			
		} catch (IOException ioE) {
			System.out.println("In Catch  of Property manager");
		}
	}

	public String getPropertyResB(String key) {
		System.out.println("getPropertyResBundle");
		System.out.println(resB.getProperty(key));
		return resB.getProperty(key);
	}
}
