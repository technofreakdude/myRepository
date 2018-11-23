package com.cloud.tcup.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

public class SendMailUtility {

	static String host = null;
	static String user = null;
	static String password =null;
	static int port = 0;
	
	
	public static void sendAlertMail(String alert) throws FileNotFoundException, IOException{
		CloudDataPull.logger.fatal("CloudToTcupAdapter:SendMaiUtility class. SendAlertMail");
		CloudDataPull.logger.debug("Enter to Send Email...!!!");
	  //Get the session object
		Properties props = new Properties();
		props.load(new FileInputStream("C:\\ConnectedChillersConfig\\CloudToTCUPAdapter.properties"));
		
		host = props.getProperty("emailHost");
		user = props.getProperty("emailUser");
		password = props.getProperty("emailPassword");
		port = Integer.parseInt(props.getProperty("emailPort"));
		
	   props.put("mail.smtp.host",host);
	   props.put("mail.smtp.host",host);
	   props.put("mail.smtp.auth", "true");
	   props.put("mail.smtp.starttls.enable", "true");
	   props.put("mail.smtp.port", port);  
	   props.put("mail.debug", "true");  
	   props.put("mail.smtp.socketFactory.port", port);  
	   props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  
	   props.put("mail.smtp.socketFactory.fallback", "false");
	   
	   Session session = Session.getDefaultInstance(props,
	    new javax.mail.Authenticator() {
	      protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user,password);
	      }
	    });
	   if(alert.contains("STOPPED")){
		   applicationStop(session);
	   }else{
		   createSendMessage(session);
	   }
	   
	}

	   private static void applicationStop(Session session) {
		// TODO Auto-generated method stub
		   MimeMessage message = new MimeMessage(session);
		     try {
		    	 CloudDataPull.logger.debug("Enter into applicationStop method");
				Properties prop = new Properties();
				prop.load(new FileInputStream("C:\\ConnectedChillersConfig\\CloudToTCUPAdapter.properties"));
				String email = prop.getProperty("emailID");
				message.setFrom(new InternetAddress(user));
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
			    message.setSubject("Alert-CloudToTCUPAdapter Application Stopped ");
			    // message.setHeader("Content-Type", "text/html");
			    Calendar cal = Calendar.getInstance();
			    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
			    String stopTime=dateFormat.format(cal.getTime());
			    
			    message.setContent("<html><body><h4 style =\"color:black;\">Hi Team, <br/> CloudToTCUPAdapter Application Stopped Aburptly: <br/><br/><b>"+stopTime+" IST</b>. Please check. <br/><br/>Regards,<br/>Mars Sales Director<h4></body></html>", "text/html");
			    //send the message
			    // Transport.send(message);
			    CloudDataPull.logger.debug("Hi Team, Application Stopped Aburptly: "+stopTime+" Please check. Regards,Mars Sales Director");
			     
			    SMTPTransport transport=(SMTPTransport)session.getTransport("smtp");
			    transport.connect(host, port, user, password);
			    transport.sendMessage(message, message.getAllRecipients());
			     int code =transport.getLastReturnCode();
			     String resp=transport.getLastServerResponse();
			     CloudDataPull.logger.info("Email response code:"+code +"Email response message:"+resp);
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		
	}

	//Compose the message
	private static void createSendMessage(Session session) {
	     MimeMessage message = new MimeMessage(session);
	     try {
			message.setFrom(new InternetAddress(user));
			List<EmailAlert> emailMsg = getDatabaseEmailAlerts();
			for(EmailAlert msg:emailMsg){
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(msg.getEmailID()));
			     message.setSubject("Alert- "+msg.getAlertType());
			     //message.setHeader("Content-Type", "text/html");
			     message.setContent("<html><body><h4 style =\"color:black;\">Hi"+msg.getUserID()+",<br/><br/>"+ msg.getAssetID()+" :"+msg.getAlertDesc()+" for location ->"+msg.getLocation()+"<br/><br/>Regards,<br/>Mars Sales Director<h1></body></html>","text/html");
			    //send the message
			    // Transport.send(message);
			     CloudDataPull.logger.debug("Hi"+msg.getUserID()+","+ msg.getAssetID()+" :"+msg.getAlertDesc()+" for location ->"+msg.getLocation()+" Regards,Mars Sales Director");
			     
			     SMTPTransport transport=(SMTPTransport)session.getTransport("smtp");
			     transport.connect(host, port, user, password);
			     transport.sendMessage(message, message.getAllRecipients());
			     int code =transport.getLastReturnCode();
			     String resp=transport.getLastServerResponse();
			     CloudDataPull.logger.info("Email response code:"+code +"Email response message:"+resp);
			     insertEmailDetail(code,resp);
			}
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

	private static void insertEmailDetail(int respCode,String respMsg) {
		// TODO Auto-generated method stub
		CloudDataPull.logger.debug("Start Inserting alert email details in the database");
		Connection c = null;
		PreparedStatement stm=null;
	    	EmailAlert alert=new EmailAlert();
	      try {
	    	  c = ConnectionUtility.getConnection();
	    	  Properties prop = new Properties();
	     		
	   			prop.load(new FileInputStream("C:\\ConnectedChillersConfig\\CloudToTCUPAdapter.properties"));
	   			String emailAlertInsert = prop.getProperty("emailAlertInsertQuery");
	   		   stm = c.prepareStatement(emailAlertInsert);
				
			      stm.setString(1,user);
			      stm.setString(2, alert.getEmailID());
			      stm.setString(3,alert.getEmailTime());
			      stm.setString(4, alert.getAlertDesc()+" for location ->"+alert.getLocation());
			      stm.setString(5,"Email response code:"+respCode +"Email response message:"+respMsg);
					stm.executeUpdate();
			CloudDataPull.logger.debug("Email Alert inserted in database Ends:");
	          
		   } catch (Exception e) {
			    e.printStackTrace();
			    CloudDataPull.logger.error(e.getClass().getName()+": "+e.getMessage());
			      }
	}

	private static List<EmailAlert> getDatabaseEmailAlerts() {
		// TODO Auto-generated method stub
		List<EmailAlert> alerts=new ArrayList<EmailAlert>();
		Connection c = null;
		 Statement stm=null;
	    	
	      try {
	    	  c = ConnectionUtility.getConnection();
	    	  Properties prop = new Properties();
	     		
	   			prop.load(new FileInputStream("C:\\ConnectedChillersConfig\\CloudToTCUPAdapter.properties"));
	   			stm = c.createStatement();
	            String emailsent = prop.getProperty("emailAlertQuery");
	            ResultSet rs = stm.executeQuery(emailsent);
	            CloudDataPull.logger.debug("Get Alerts detail from database");
	    
	            while (rs.next()) {
	            	//String sensor=rs.getString("SensorID");
	            	EmailAlert alert=new EmailAlert();
	            	alert.setAssetID(rs.getString("asset_id"));
	            	alert.setAlertType(rs.getString("alert_type"));
	            	alert.setAlertDesc(rs.getString("alert_description"));
	            	//alert.setAlertEmail(rs.getString("AlertEmail"));
	            	alert.setEmailTime(rs.getString("timestamp"));
	            	alert.setUserID(rs.getString("user_id"));
	            	alert.setEmailID(rs.getString("email"));
	            	alert.setLocation("address");
	            	alerts.add(alert);
	            }
	                
	      } catch (Exception e) {
		         e.printStackTrace();
		         CloudDataPull.logger.error(e.getClass().getName()+": "+e.getMessage());
		      }
	      return alerts;
	}
}
