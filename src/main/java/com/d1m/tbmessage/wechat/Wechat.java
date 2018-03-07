package com.d1m.tbmessage.wechat;

import com.d1m.tbmessage.wechat.controller.LoginController;

public class Wechat {

	public static void start(){
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误

		// 登陆
		new LoginController().login();
	}
}
