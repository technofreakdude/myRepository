package TestProperty;

public class TestResourceBundle {
	public static void main(String[] args) {
		
		PropertyManager propertymanager = PropertyManager.getInstance();
		propertymanager.load();
		System.out.println("TestResourceBundle: property value = ");
		System.out.println(propertymanager.getPropertyResB("sensorUnits"));
			
	}
}
