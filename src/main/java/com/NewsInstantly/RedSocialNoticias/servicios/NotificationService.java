package com.NewsInstantly.RedSocialNoticias.servicios;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

	@Autowired
	JavaMailSender mailSender;
	
	/**
	 * se encarga de enviar los email a los usuarios.
	 * se marca como async para que se haga en otro hilo de ejecucion de forma paralela
	 * y no hacer esperar al usuario a que se complete.
	 * @param body
	 * @param title
	 * @param mail
	 */
	@Async
	public void sendMail(String body,String title,String mail) {
		SimpleMailMessage message=new SimpleMailMessage();
		
		message.setTo(mail);
		message.setFrom("newsinstantly7@gmail.com");
		message.setSubject(title);
		message.setText(body);
		
		mailSender.send(message);
	}
}
