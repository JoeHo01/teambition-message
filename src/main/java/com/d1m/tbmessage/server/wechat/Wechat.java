package com.d1m.tbmessage.server.wechat;

import com.d1m.tbmessage.server.Application;
import com.d1m.tbmessage.server.wechat.login.controller.LoginController;

public class Wechat {
	public static void start(){
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
		// 登陆
		Application.getBean(LoginController.class).login();
	}
}
