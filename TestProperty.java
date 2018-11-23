package TestProperty;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestProperty {
	Properties property;
	FileInputStream fs;

	public TestProperty() throws IOException {
		fs = new FileInputStream("c:\\ConnectedChillersConfig\\config_dev.properties");
		
		property = new Properties();
		property.load(fs);
		
	System.out.println(property.getProperty("Get_User_Data"));
		
	}

	public static void main(String[] args) throws IOException {
		TestProperty tst = new TestProperty();
		
	}
}