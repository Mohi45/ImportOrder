package com.framework.selenium;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMailSSL {

	private static Multipart failureMessage;

	public static Multipart getFailureMessage() {
		return failureMessage;
	}

	public static void setFailureMessage(Multipart failureMessage) {
		SendMailSSL.failureMessage = failureMessage;
	}

	// construct
	// public SendMailSSL(Multipart multiPart){
	// this.failureMessage = multiPart;
	// }

	public static Session createConnection() throws MessagingException {
		// Create IMAPSSLStore object
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		// All sysout statements are used for testing, have to remove them
		// while implementation
		System.out.println("Connecting to gmail...");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("onlineweekend.diningedge@gmail.com", "edge2017");// change
				// accordingly
			}
		});
		return session;
	}

	public static void sendFailedOrder(String OrderTD, String status) {
		String[] to = { "ashutoshsaxena06@gmail.com", "dawn@diningedge.com", "paola@diningedge.com",
				"naomi.canning@diningedge.com", "frank@diningedge.com" };
		String user = "onlineweekend.diningedge@gmail.com";// change
															// accordingly
		try {
			// get connection
			Session session = createConnection();
			// String filepath = RandomAction.setdownloadDir();

			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(user));

			InternetAddress[] recipientAddress = new InternetAddress[to.length];
			int counter = 0;
			for (String recipient : to) {
				recipientAddress[counter] = new InternetAddress(recipient.trim());
				counter++;
			}

			message.addRecipients(Message.RecipientType.TO, recipientAddress);
			// accordingly
			// message.addRecipient(Message.RecipientType.TO, new
			// InternetAddress(to));

			// smessageBodyPart1.addRecipient(Message.RecipientType.CC, new
			// InternetAddress("teamesave@gmail.com"));

			// Subject of mails
			message.setSubject("Fwd: High Priority " + OrderTD + " - status :: " + status);
			// Body of mails
			message.setContent(failureMessage);
			
			try {
				MimeBodyPart messageBodyPart2 = new MimeBodyPart();
				String filename = "C:\\Users\\ImportOrder\\Log\\"+ OrderTD + ".png";
				DataSource source = new FileDataSource("C:\\Users\\ImportOrder\\Log"+ OrderTD + ".png");
				messageBodyPart2.setDataHandler(new DataHandler(source));
				messageBodyPart2.setFileName(filename);
				System.out.println("Attached file - " + OrderTD);
				} catch (Exception e) {
			}

			Transport.send(message);

			System.out.println("Message send success");

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendMailAction(String OrderTD, String status) {
		String[] to = { "ashutoshsaxena06@gmail.com", "dawn@diningedge.com" };

		String user = "onlineweekend.diningedge@gmail.com";// change
															// accordingly
		try {
			// get connection
			Session session = createConnection();
			// String filepath = RandomAction.setdownloadDir();

			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(user));

			InternetAddress[] recipientAddress = new InternetAddress[to.length];
			int counter = 0;
			for (String recipient : to) {
				recipientAddress[counter] = new InternetAddress(recipient.trim());
				counter++;
			}

			message.addRecipients(Message.RecipientType.TO, recipientAddress);
			
			try {
				MimeBodyPart messageBodyPart2 = new MimeBodyPart();
				String filename = "C:\\Users\\ImportOrder\\Log\\"+ OrderTD + ".png";
				DataSource source = new FileDataSource("C:\\Users\\ImportOrder\\Log\\"+ OrderTD + ".png");
				messageBodyPart2.setDataHandler(new DataHandler(source));
				messageBodyPart2.setFileName(filename);
				System.out.println("Attached file - " + OrderTD);
				} catch (Exception e) {
					e.printStackTrace();
			}
			// accordingly
			// message.addRecipient(Message.RecipientType.TO, new
			// InternetAddress(to));

			// smessageBodyPart1.addRecipient(Message.RecipientType.CC, new
			// InternetAddress("teamesave@gmail.com"));

			// Subject of mails
			message.setSubject("OrderID : " + OrderTD + " - status :: " + status);
			// Body of mails
			message.setText("<Auto generated mail notification> 'Order Information for CheneyItrade'");

			Transport.send(message);

			System.out.println("Message send success");

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}