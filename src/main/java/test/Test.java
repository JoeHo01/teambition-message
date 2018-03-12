package test;

import com.d1m.tbmessage.common.WordsUtil;
import com.d1m.tbmessage.server.teambition.config.SendingInfo;
import com.d1m.tbmessage.server.teambition.entity.SendMessageDTO;
import com.d1m.tbmessage.server.wechat.login.service.impl.LoginServiceImpl;

public class Test {
	public static void main(String[] args) {
		SendMessageDTO sendMessageDTO = new SendMessageDTO();
		sendMessageDTO.setMessageType("text");
		sendMessageDTO.setOrganizationId("123");
		sendMessageDTO.setText("123");
		sendMessageDTO.addProject("abc");
		System.out.println(sendMessageDTO.toString());
	}
}
