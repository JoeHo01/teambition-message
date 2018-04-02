package com.d1m.tbmessage.server.wechat.login.controller;

import com.d1m.tbmessage.server.wechat.api.WechatTools;
import com.d1m.tbmessage.server.wechat.core.Core;
import com.d1m.tbmessage.server.wechat.listener.MessageListener;
import com.d1m.tbmessage.server.wechat.login.service.ILoginService;
import com.d1m.tbmessage.server.wechat.listener.LoginStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.d1m.tbmessage.common.util.SleepUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * 登陆控制器
 * 
 * @author Joe He
 * @date 创建时间：2017年5月13日 下午12:56:07
 * @version 1.0
 *
 */
@RestController
@RequestMapping("wechat")
public class LoginController {
	private Logger LOG = LoggerFactory.getLogger(LoginController.class);

	private static Core core = Core.getInstance();

	private final ILoginService loginService;

	private final LoginStatusListener loginStatusListener;

	private final MessageListener messageListener;

	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Autowired
	public LoginController(ILoginService loginService, LoginStatusListener loginStatusListener, MessageListener messageListener, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
		this.loginService = loginService;
		this.loginStatusListener = loginStatusListener;
		this.messageListener = messageListener;
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
	}

	@RequestMapping(value = "login",method = RequestMethod.GET)
	public void login(HttpServletRequest request) {
		LOG.info("1. Get WeChat UUID");
		while (loginService.getUuid() == null) {
			LOG.warn("1.1. Fail to get UUID，will retry in 2 seconds");
			while (loginService.getUuid() == null) {
				SleepUtil.sleep(2000);
			}
		}
		LOG.info("2. Get QR code for login");
		String qrPath = request.getServletContext().getRealPath("") + "static" + File.separatorChar + "login";

		if (loginService.getQR(qrPath))	threadPoolTaskExecutor.execute(new LoginTask());
	}

	private class LoginTask implements Runnable {

		@Override
		public void run() {
			if (core.isAlive()) { // 已登陆
				LOG.info("Wechat is logon already");
				return;
			}

			LOG.info("3. Please check QR code and confirm");
			if (loginService.login()) {
				core.setAlive(true);
			}else return;

			LOG.info("4. Login succeed and init WeChat");
			if (!loginService.webWxInit()) {
				LOG.info("4.1 Init Error");
				return;
			}

			LOG.info("5. Start notification");
			loginService.wxStatusNotify();

			LOG.info("6. Get contacts");
			loginService.webWxGetContact();

			LOG.info("7. Get Groups");
			loginService.WebWxBatchGetContact();

			LOG.info("8. Get user information");
			WechatTools.setUserInfo(); // 登陆成功后缓存本次登陆好友相关消息(NickName, UserName)

			LOG.info("9. Start message listen");
			messageListener.start();

			LOG.info("10.Start health listen");
			loginStatusListener.start();
		}
	}
}