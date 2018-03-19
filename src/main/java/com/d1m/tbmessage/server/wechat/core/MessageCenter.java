package com.d1m.tbmessage.server.wechat.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;


import com.d1m.tbmessage.server.wechat.entity.GroupDTO;
import com.d1m.tbmessage.server.wechat.entity.MemberDTO;
import com.d1m.tbmessage.server.wechat.constant.enums.MsgTypeEnum;
import com.d1m.tbmessage.server.wechat.entity.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import static com.d1m.tbmessage.server.wechat.constant.enums.MsgCodeEnum.*;

/**
 * 消息处理中心
 * 
 * @author Joe He
 * @date 创建时间：2017年5月14日 下午12:47:50
 * @version 1.0
 *
 */
@Component
public class MessageCenter {
	private static Logger LOG = LoggerFactory.getLogger(MessageCenter.class);

	private static Map<String, MemberDTO> recommends = MemberDTO.getInstance();

	private static Map<String, GroupDTO> groups = GroupDTO.getInstance();

	/**
	 * 接收消息，放入队列
	 * 
	 * @author Joe He
	 * @date 2017年4月23日 下午2:30:48
	 * @param msgList message
	 */
	public List<MessageDTO> produceMsg(JSONArray msgList) {
		List<MessageDTO> result = new ArrayList<>();
		for (int i = 0; i < msgList.size(); i++) {
			JSONObject msg = msgList.getJSONObject(i);
			processMsg(msg);

			// develop test log
			if (msg.getBoolean("groupMsg")){

				String groupName = groups.get(msg.getString("FromUserName")).getNickName();
				String memberName = groups.get(msg.getString("FromUserName")).getMembers().get(msg.getString("FromMemberName")).getNickName();

				LOG.info("消息--" + groupName + "#" + memberName + ": " + msg.getString("Content"));
			}else {
				LOG.info("消息--" + recommends.get(msg.getString("FromUserName")).getNickName() + msg.getString("Content"));
			}
			result.add(msgList.getObject(i, MessageDTO.class));
		}
		return result;
	}

	private void processMsg(JSONObject msg){
		msg.put("groupMsg", false);// 是否是群消息
		if (msg.getString("FromUserName").contains("@@") || msg.getString("ToUserName").contains("@@")) { // 群聊消息
			// 群消息与普通消息不同的是在其消息体（Content）中会包含发送者id及":<br/>"消息，这里需要处理一下，去掉多余信息，只保留消息内容
			String content = msg.getString("Content");
			if (content.contains("<br/>")) {
				msg.put("FromMemberName", content.substring(0, content.indexOf(":<br/>")));
				msg.put("Content", content.substring(content.indexOf("<br/>") + 5));
				msg.put("groupMsg", true);
			}
			String groupName = groups.get(msg.getString("FromUserName")).getNickName();
			String memberName = groups.get(msg.getString("FromUserName")).getMembers().get(msg.getString("FromMemberName")).getNickName();
			msg.put("FromNickName", groupName + "#" + memberName);
		} else {
			MessageTool.msgFormatter(msg, "Content");
		}

		if (msg.getInteger("MsgType").equals(MSGTYPE_TEXT.getCode())) { // words
			JSONObject m = new JSONObject();
			// 文本消息
			if (msg.getString("Url").length() != 0) {
				String regEx = "(.+?\\(.+?\\))";
				Matcher matcher = MessageTool.getMatcher(regEx, msg.getString("Content"));
				String data = "Map";
				if (matcher.find()) {
					data = matcher.group(1);
				}
				m.put("Type", "Map");
				m.put("Text", data);
			} else {
				m.put("Type", MsgTypeEnum.TEXT.getType());
				m.put("Text", msg.getString("Content").replaceAll("\"", "\\\""));
			}
			msg.put("Type", m.getString("Type"));
			msg.put("Text", m.getString("Text"));
		} else if (msg.getInteger("MsgType").equals(MSGTYPE_IMAGE.getCode())
				|| msg.getInteger("MsgType").equals(MSGTYPE_EMOTICON.getCode())) { // 图片消息
			msg.put("Type", MsgTypeEnum.PIC.getType());
		} else if (msg.getInteger("MsgType").equals(MSGTYPE_VOICE.getCode())) { // 语音消息
			msg.put("Type", MsgTypeEnum.VOICE.getType());
		} else if (msg.getInteger("MsgType").equals(MSGTYPE_VERIFYMSG.getCode())) {// friends
			// 好友确认消息
			// MessageTools.addFriend(config, userName, 3, ticket); // 确认添加好友
			msg.put("Type", MsgTypeEnum.VERIFYMSG.getType());

		} else if (msg.getInteger("MsgType").equals(MSGTYPE_SHARECARD.getCode())) { // 共享名片
			msg.put("Type", MsgTypeEnum.NAMECARD.getType());

		} else if (msg.getInteger("MsgType").equals(MSGTYPE_VIDEO.getCode())
				|| msg.getInteger("MsgType").equals(MSGTYPE_MICROVIDEO.getCode())) {// viedo
			msg.put("Type", MsgTypeEnum.VIEDO.getType());
		} else if (msg.getInteger("MsgType").equals(MSGTYPE_MEDIA.getCode())) { // 多媒体消息
			msg.put("Type", MsgTypeEnum.MEDIA.getType());
		} else if (msg.getInteger("MsgType").equals(MSGTYPE_STATUSNOTIFY.getCode())) {// phone
			// init
			// 微信初始化消息

		} else if (msg.getInteger("MsgType").equals(MSGTYPE_SYS.getCode())) {// 系统消息
			msg.put("Type", MsgTypeEnum.SYS.getType());
		} else if (msg.getInteger("MsgType").equals(MSGTYPE_RECALLED.getCode())) { // 撤回消息

		} else {
			LOG.info("Useless msg");
		}
	}
}
