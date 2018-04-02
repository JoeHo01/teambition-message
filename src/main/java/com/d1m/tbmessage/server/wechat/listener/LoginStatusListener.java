package com.d1m.tbmessage.server.wechat.listener;

import com.d1m.tbmessage.server.wechat.core.Core;
import com.d1m.tbmessage.server.wechat.login.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.d1m.tbmessage.common.util.SleepUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * 检查微信在线状态
 * <p>
 * 如何来感知微信状态？
 * 微信会有心跳包，LoginServiceImpl.syncCheck()正常在线情况下返回的消息中retcode报文应该为"0"，心跳间隔一般在25秒，
 * 那么可以通过最后收到正常报文的时间来作为判断是否在线的依据。若报文间隔大于60秒，则认为已掉线。
 * </p>
 * 
 * @author Joe He
 * @date 创建时间：2017年5月17日 下午10:53:15
 * @version 1.0
 *
 */
@Service
public class LoginStatusListener{

	private static Logger LOG = LoggerFactory.getLogger(LoginStatusListener.class);

	private Core core = Core.getInstance();

	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Autowired
	public LoginStatusListener(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
	}

	public void start() {
		threadPoolTaskExecutor.execute(new Task());
	}

	private class Task implements Runnable {
		@Override
		public void run() {
			while (core.isAlive()) {
				long t1 = System.currentTimeMillis(); // 秒为单位
				if (t1 - core.getLastNormalRetcodeTime() > 60 * 1000) { // 超过60秒，判为离线
					core.setAlive(false);
					LOG.info("WeChat Logout");
				}
				SleepUtil.sleep(10 * 1000); // 休眠10秒
			}
		}
	}
}
