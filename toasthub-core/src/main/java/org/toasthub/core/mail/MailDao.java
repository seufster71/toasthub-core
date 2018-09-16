/*
 * Copyright (C) 2016 The ToastHub Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.toasthub.core.mail;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Repository;
import org.toasthub.core.general.model.EmailInvite;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.utils.TenantContext;

@Repository("MailDao")
public class MailDao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final String adminName = "";
	private static final String password = "";
	private static final Properties props = new Properties();
	
	public MailDao(){
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
	}
	
	public void sendEmailInvite(RestRequest request, RestResponse response) throws Exception{
		//SendMailIntf invite
		EmailInvite invite = (EmailInvite) request.getParam("emailInvite");
		invite.setStatus(EmailInvite.PEND);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			
			Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(adminName, password);
					}
			});

			Message message = new MimeMessage(session);
			try {
				message.setFrom(new InternetAddress(adminName));
				message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(invite.getReceiverEmail()));
				message.setSubject(invite.getSubject());
				message.setText(invite.getMessage());
				Transport.send(message);
			} catch (Exception e) {
				// need to track this !!
				e.printStackTrace();
			}
			System.out.println("Email sent to " + invite.getReceiverEmail());
					
		});
		executor.shutdown();
	}
	
	public void sendMailTLS(String email, String urlParams) throws Exception{
		//boolean status = false;
		String serverName = TenantContext.getURLDomain();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(adminName, password);
			}
		  });
	
			Message message = new MimeMessage(session);
			try {
				message.setFrom(new InternetAddress(adminName));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
				message.setSubject("Testing Registration process");
				message.setText("Welcome new user click link to confirm registration http://"+serverName+":8090/login/login.html?"+urlParams );
				Transport.send(message);
			} catch (Exception e) {
				// need to track this !!
				e.printStackTrace();
			}
			System.out.println("Registration email sent");
		});
		executor.shutdown();
	}
	
	public void sendEmailPasswordReset(String username, String email, String userPass) throws Exception{
		//boolean status = false;
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(adminName, password);
					}
			});
			Message message = new MimeMessage(session);
			try {
				message.setFrom(new InternetAddress(adminName));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
				message.setSubject("Password Reset");
				message.setText("Welcome "+username+" your password has been reset. "+userPass );
				Transport.send(message);
			} catch (Exception e) {
				// need to track this !!
				e.printStackTrace();
			}
			System.out.println("Password Reset sent to " + username);
		});	
		executor.shutdown();
	}
	
	public void sendEmailNotification(String username, String email, String msg) throws Exception{
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(adminName, password);
				}
			});
			Message message = new MimeMessage(session);
			try {
				message.setFrom(new InternetAddress(adminName));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
				message.setSubject("Notification of change");
				message.setText(msg);
				Transport.send(message);
			} catch (Exception e) {
				// need to track this !!
				e.printStackTrace();
			}
			System.out.println("Email sent to " + username);
		});
		executor.shutdown();
	}
	/*
	public boolean sendMailSSL() throws Exception{
		boolean status = false;
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "465");
		
		Session session = Session.getDefaultInstance(properties,
				new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication(){
						return new PasswordAuthentication(adminName,password);
					}
			});
		try {
			 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("guru@cborgtech.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("guru@cborgtech.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler," +
					"\n\n No spam to my email, please!");
 
			Transport.send(message);
 
			System.out.println("Done");
			status = true;
		} catch (MessagingException e) {
			e.printStackTrace();
			//throw new RuntimeException(e);
		}
		
		return status;
	}*/
}
