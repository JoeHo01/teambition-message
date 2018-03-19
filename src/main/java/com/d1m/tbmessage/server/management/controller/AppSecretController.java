package com.d1m.tbmessage.server.management.controller;

import com.d1m.tbmessage.server.management.service.AppService;
import com.d1m.tbmessage.server.teambition.config.AppSecretInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AppSecretController {

	private final AppService appService;

	private static AppSecretInfo appSecretInfo = AppSecretInfo.getInstance();


	@Autowired
	public AppSecretController(AppService appService) {
		this.appService = appService;
	}

	public void init(){
		appService.getProjects();
		appService.getAppSecretInfo();
		appSecretInfo.setOrganizationId(appService.getOrganizationId("第一秒电商"));
	}
}
