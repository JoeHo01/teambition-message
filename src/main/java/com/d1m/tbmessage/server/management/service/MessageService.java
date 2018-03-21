package com.d1m.tbmessage.server.management.service;

import com.d1m.tbmessage.server.teambition.config.AppSecretInfo;
import com.d1m.tbmessage.server.teambition.config.Constant;
import com.d1m.tbmessage.server.teambition.config.SendingInfo;
import com.d1m.tbmessage.server.teambition.entity.SendMessageDTO;
import com.d1m.tbmessage.server.teambition.service.TeambitionService;
import com.d1m.tbmessage.server.wechat.entity.MessageDTO;
import com.d1m.tbmessage.server.wechat.login.service.impl.LoginServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
	private static Logger LOG = LoggerFactory.getLogger(LoginServiceImpl.class);

	private AppSecretInfo appSecretInfo = AppSecretInfo.getInstance();

	private SendingInfo sendingInfo = SendingInfo.getInstance();

	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private final TeambitionService teambitionService;

	@Autowired
	public MessageService(ThreadPoolTaskExecutor threadPoolTaskExecutor, TeambitionService teambitionService) {
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
		this.teambitionService = teambitionService;
	}

	public void sendMessage(MessageDTO message) {
		threadPoolTaskExecutor.execute(new SendingTask(message));
	}

	private class SendingTask implements Runnable {

		private MessageDTO message;

		public SendingTask(MessageDTO message) {
			this.message = message;
		}

		@Override
		public void run() {
			String projectId = sendingInfo.getProjectId(message.getFromUserName());
			if (StringUtils.isEmpty(projectId)) return;
			SendMessageDTO sendMessageDTO = new SendMessageDTO();
			sendMessageDTO.setOrganizationId(appSecretInfo.getOrganizationId());
			sendMessageDTO.addProject(projectId);
			sendMessageDTO.setMessageType(Constant.MESSAGE_TYPE_TEXT);
			sendMessageDTO.setText(message.getFromNickName() + ": " + message.getText());
			teambitionService.sendMessage(sendMessageDTO);
		}
	}
}
