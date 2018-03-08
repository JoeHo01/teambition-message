package com.d1m.tbmessage.server.wechat.core;

import java.util.Map;
import java.util.regex.Matcher;


import com.d1m.tbmessage.server.wechat.entity.GroupDTO;
import com.d1m.tbmessage.server.wechat.entity.MemberDTO;
import com.d1m.tbmessage.server.wechat.constant.enums.MsgCodeEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.MsgTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 消息处理中心
 * 
 * @author Joe He
 * @date 创建时间：2017年5月14日 下午12:47:50
 * @version 1.0
 *
 */
public class MsgCenter {
	private static Logger LOG = LoggerFactory.getLogger(MsgCenter.class);

	private static Map<String, MemberDTO> recommends = MemberDTO.getInstance();

	private static Map<String, GroupDTO> groups = GroupDTO.getInstance();

	/**
	 * 接收消息，放入队列
	 * 
	 * @author Joe He
	 * @date 2017年4月23日 下午2:30:48
	 * @param msgList message
	 */
	public static void produceMsg(JSONArray msgList) {
		for (int i = 0; i < msgList.size(); i++) {
			JSONObject msg = msgList.getJSONObject(i);
			processMsg(msg);
			if (msg.getBoolean("groupMsg")){

				String groupName = groups.get(msg.getString("FromUserName")).getNickName();
				String memberName = groups.get(msg.getString("FromUserName")).getMembers().get(msg.getString("FromMemberName")).getNickName();

				LOG.info("收到消息一条，来自: " + groupName);
				LOG.info(memberName);

				//Test
				if ("D1M".equals(groupName) || "【IT内部】2018技术部".equals(groupName)){
//					TeambitionService.sendMessage(groupName, groups.get(msg.getString("FromUserName")).getMembers().get(msg.getString("FromMemberName")).getNickName(),msg.getString("Content"));
				}
				saveMsg(groupName, memberName, msg.getString("CreateTime"), msg.getString("Content"));
			}else {
				saveMsg(null, recommends.get(msg.getString("FromUserName")).getNickName(), msg.getString("CreateTime"), msg.getString("Content"));
				LOG.info("收到消息一条，来自: " + recommends.get(msg.getString("FromUserName")).getNickName());
			}
			LOG.info(msg.getString("Content"));
		}
	}

	private static void processMsg(JSONObject msg){
		msg.put("groupMsg", false);// 是否是群消息
		if (msg.getString("FromUserName").contains("@@") || msg.getString("ToUserName").contains("@@")) { // 群聊消息
			// 群消息与普通消息不同的是在其消息体（Content）中会包含发送者id及":<br/>"消息，这里需要处理一下，去掉多余信息，只保留消息内容
			String content = msg.getString("Content");
			if (content.contains("<br/>")) {
				msg.put("FromMemberName", content.substring(0, content.indexOf(":<br/>")));
				msg.put("Content", content.substring(content.indexOf("<br/>") + 5));
				msg.put("groupMsg", true);
			}
		} else {
			MessageTool.msgFormatter(msg, "Content");
		}
		if (msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_TEXT.getCode())) { // words
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
				m.put("Text", msg.getString("Content"));
			}
			msg.put("Type", m.getString("Type"));
			msg.put("Text", m.getString("Text"));
		} else if (msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_IMAGE.getCode())
				|| msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_EMOTICON.getCode())) { // 图片消息
			msg.put("Type", MsgTypeEnum.PIC.getType());
		} else if (msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VOICE.getCode())) { // 语音消息
			msg.put("Type", MsgTypeEnum.VOICE.getType());
		} else if (msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VERIFYMSG.getCode())) {// friends
			// 好友确认消息
			// MessageTools.addFriend(constant, userName, 3, ticket); // 确认添加好友
			msg.put("Type", MsgTypeEnum.VERIFYMSG.getType());

		} else if (msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_SHARECARD.getCode())) { // 共享名片
			msg.put("Type", MsgTypeEnum.NAMECARD.getType());

		} else if (msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VIDEO.getCode())
				|| msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_MICROVIDEO.getCode())) {// viedo
			msg.put("Type", MsgTypeEnum.VIEDO.getType());
		} else if (msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_MEDIA.getCode())) { // 多媒体消息
			msg.put("Type", MsgTypeEnum.MEDIA.getType());
		} else if (msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_STATUSNOTIFY.getCode())) {// phone
			// init
			// 微信初始化消息

		} else if (msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_SYS.getCode())) {// 系统消息
			msg.put("Type", MsgTypeEnum.SYS.getType());
		} else if (msg.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_RECALLED.getCode())) { // 撤回消息

		} else {
			LOG.info("Useless msg");
		}
	}

	private static void saveMsg(String groupName, String userName,String createTime, String massage){
//		SQL sql = new SQL();
//		String values;
//		if (groupName != null){
//			values = "'" + groupName + "','" + userName + "','" + createTime + "','" + massage + "'";
//		} else {
//			values = "null,'" + userName + "','" + createTime + "','" + massage + "'";
//		}
//		sql.update("insert into msg (group_id,sender,created_time,content) values (" + values + ")");
	}
}
