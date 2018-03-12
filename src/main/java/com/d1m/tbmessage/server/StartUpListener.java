package com.d1m.tbmessage.server;

import com.d1m.tbmessage.server.management.service.AppService;
import com.d1m.tbmessage.server.teambition.config.AppSecretInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
@Component
public class StartUpListener implements ServletContextListener{

	private static AppSecretInfo appSecretInfo = AppSecretInfo.getInstance();

	private final AppService appService;

	@Autowired
	public StartUpListener(AppService appService) {
		this.appService = appService;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		appService.getProjects();
		appService.getAppSecretInfo();
		appSecretInfo.setOrganizationId(appService.getOrganizationId("第一秒电商"));
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

	}
}
