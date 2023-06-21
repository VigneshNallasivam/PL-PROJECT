package com.intelizign.pl.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.intelizign.pl.model.EmployeeModel;
import com.intelizign.pl.model.ResourceModel;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
@EnableAsync
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private Configuration config;

	public final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
	
	public void sendForgetMail(String to, Map<String, Object> model) {

		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setSubject("Your new password is just a few clicks away");
			mimeMessageHelper.setFrom(env.getProperty("spring.mail.username"));

			Template t = config.getTemplate("ForgotPassword.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
			
			mimeMessageHelper.setText(html, true);
			javaMailSender.send(mimeMessage);
			
		} catch (MessagingException | IOException | TemplateException e) {

			LOGGER.error("Error while sending mail" + e.getMessage());

		}
	}

	public void sendmail(String email, EmployeeModel employee, String final_password) {
		try {
			Map<String, Object> model = new HashMap<>();
			model.put("providerName", "Intelizign");
			model.put("username", employee.getUsername());
			model.put("password", final_password);
			model.put("receiverName", employee.getUsername());
			model.put("signinlink", env.getProperty("pl.frontend.app.domain") + "signin");
			loginCredentialsMail(email, model);

		} catch (Exception e) {
			LOGGER.error("Internal Server Error while sending mail:{}.", e.getMessage());

		}
	}
	
	public void sendEmail(String email,ResourceModel resource) 
	{
		try {
			Map<String, Object> model = new HashMap<>();
			model.put("providerName", "Intelizign");
			model.put("receiverName",resource.getUsername());
			projectClosingNotificationMail(email, model);

		} catch (Exception e) {
			LOGGER.error("Internal Server Error while sending mail:{}.", e.getMessage());

		}
	}

	@Async
	public void loginCredentialsMail(String to, Map<String, Object> model) {

		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setSubject("Employee Login Credentials Mail");
			mimeMessageHelper.setFrom(env.getProperty("spring.mail.username"));

			Template t = config.getTemplate("EmployeeMail.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
			mimeMessageHelper.setText(html, true);
			javaMailSender.send(mimeMessage);
			
		} catch (MessagingException | IOException | TemplateException e) {

			LOGGER.error("Error while sending mail" + e.getMessage());

		}
	}
	
	@Async
	public void projectClosingNotificationMail(String to, Map<String, Object> model) {

		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setSubject("Project Closure Reminder Mail");
			mimeMessageHelper.setFrom(env.getProperty("spring.mail.username"));

			Template t = config.getTemplate("ResourceMail.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
			mimeMessageHelper.setText(html, true);
			javaMailSender.send(mimeMessage);
			
		} catch (MessagingException | IOException | TemplateException e) {

			LOGGER.error("Error while sending mail" + e.getMessage());

		}
	}
}
