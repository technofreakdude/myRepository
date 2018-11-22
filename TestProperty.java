package TestProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestProperty {
	Properties property;
	FileInputStream fs;

	public TestProperty() throws IOException {
		fs = new FileInputStream("c:\\configfiles\\configproperties");
		
		property = new Properties();
		property.load(fs);
		
	System.out.println(property.getProperty("firstname"));
		
	}

	public static void main(String[] args) throws IOException {
		TestProperty tst = new TestProperty();
		
	}
}
