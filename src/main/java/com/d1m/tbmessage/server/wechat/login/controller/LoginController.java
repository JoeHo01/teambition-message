package com.d1m.tbmessage.server.wechat.login.controller;

import com.d1m.tbmessage.server.wechat.api.WechatTools;
import com.d1m.tbmessage.server.wechat.core.Core;
import com.d1m.tbmessage.server.wechat.listener.MessageListener;
import com.d1m.tbmessage.server.wechat.login.service.ILoginService;
import com.d1m.tbmessage.server.wechat.listener.LoginStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.d1m.tbmessage.common.SleepUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
		for (int count = 0; count < 10; count++) {
			LOG.info("1. 获取微信UUID");
			while (loginService.getUuid() == null) {
				LOG.warn("1.1. 获取微信UUID失败，两秒后重新获取");
				while (loginService.getUuid() == null) {
					SleepUtil.sleep(2000);
				}
			}
			LOG.info("2. 获取登陆二维码图片");
			String qrPath = this.getClass().getClassLoader().getResource("").getPath() + "static/login/";

			System.out.println("Path1 : " + qrPath);
			System.out.println("Path2 : " + request.getSession().getServletContext().getContextPath());

			if (loginService.getQR(qrPath)) {
				break;
			}
		}
		threadPoolTaskExecutor.execute(new LoginTask());
	}

	private class LoginTask implements Runnable {

		@Override
		public void run() {
			if (core.isAlive()) { // 已登陆
				LOG.info("itchat4j已登陆");
				return;
			}
			while (true) {
				LOG.info("3. 请扫描二维码图片，并在手机上确认");
				if (!core.isAlive()) {
					loginService.login();
					core.setAlive(true);
					LOG.info(("登陆成功"));
					break;
				}
				LOG.info("3.1 登陆超时，请重新扫描二维码图片");
			}

			LOG.info("4. 登陆成功，微信初始化");
			if (!loginService.webWxInit()) {
				LOG.info("4.1 微信初始化异常");
				System.exit(0);
			}

			LOG.info("5. 开启微信状态通知");
			loginService.wxStatusNotify();

			LOG.info("6. 获取联系人信息");
			loginService.webWxGetContact();

			LOG.info("7. 获取群好友及群好友列表");
			loginService.WebWxBatchGetContact();

			LOG.info("8. 缓存本次登陆好友相关消息");
			WechatTools.setUserInfo(); // 登陆成功后缓存本次登陆好友相关消息(NickName, UserName)

			LOG.info("9. 开始接收消息");
			messageListener.start();

			LOG.info("10.开启微信状态检测线程");
			loginStatusListener.start();
		}
	}
}