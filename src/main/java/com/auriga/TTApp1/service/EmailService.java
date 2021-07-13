package com.auriga.TTApp1.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	public void sendMessage(String to, String subject, String message) throws MessagingException {
		MimeMessage msg = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, true);
        
//		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//		simpleMailMessage.setFrom("contact@ttapp.com");
//		simpleMailMessage.setTo(to);
//		simpleMailMessage.setSubject(subject);
//		simpleMailMessage.setText(message);

		// Uncomment to send mail
		javaMailSender.send(msg);
	}
}

