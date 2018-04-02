package com.d1m.tbmessage.server;

import com.d1m.tbmessage.server.management.controller.AppSecretController;

class App {
	static void start(){
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
		Application.getBean(AppSecretController.class).init();
	}
}
