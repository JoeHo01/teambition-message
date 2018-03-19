package com.d1m.tbmessage.server;

import com.d1m.tbmessage.server.management.controller.AppSecretController;
import com.d1m.tbmessage.server.wechat.login.controller.LoginController;

public class App {
	public static void start(){
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误

		Application.getBean(AppSecretController.class).init();
		Application.getBean(LoginController.class).login();
	}
}
