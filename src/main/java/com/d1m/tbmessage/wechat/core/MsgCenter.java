package com.d1m.tbmessage.wechat.core;

import java.util.Map;
import java.util.regex.Matcher;


import com.d1m.tbmessage.tbconnection.service.TeambitionService;
import com.d1m.tbmessage.wechat.beans.GroupInfo;
import com.d1m.tbmessage.wechat.beans.RecommendInfo;
import com.d1m.tbmessage.wechat.utils.enums.MsgCodeEnum;
import com.d1m.tbmessage.wechat.utils.enums.MsgTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.d1m.tbmessage.common.CommonTools;

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

	private static Core core = Core.getInstance();

	private static Map<String, RecommendInfo> recommends = RecommendInfo.getInstance();

	private static Map<String, GroupInfo> groups = GroupInfo.getInstance();

	/**
	 * 接收消息，放入队列
	 * 
	 * @author Joe He
	 * @date 2017年4月23日 下午2:30:48
	 * @param msgList
	 * @return
	 */
	public static void produceMsg(JSONArray msgList) {
		for (int i = 0; i < msgList.size(); i++) {
			JSONObject msg = new JSONObject();
			JSONObject m = msgList.getJSONObject(i);
			m.put("groupMsg", false);// 是否是群消息
			if (m.getString("FromUserName").contains("@@") || m.getString("ToUserName").contains("@@")) { // 群聊消息
				if (m.getString("FromUserName").contains("@@")
						&& !core.getGroupIdList().contains(m.getString("FromUserName"))) {
					core.getGroupIdList().add((m.getString("FromUserName")));
				} else if (m.getString("ToUserName").contains("@@")
						&& !core.getGroupIdList().contains(m.getString("ToUserName"))) {
					core.getGroupIdList().add((m.getString("ToUserName")));
				}
				// 群消息与普通消息不同的是在其消息体（Content）中会包含发送者id及":<br/>"消息，这里需要处理一下，去掉多余信息，只保留消息内容
				String content = m.getString("Content");
				if (content.contains("<br/>")) {
					m.put("FromMemberName", content.substring(0, content.indexOf(":<br/>")));
					m.put("Content", content.substring(content.indexOf("<br/>") + 5));
					m.put("groupMsg", true);
				}
			} else {
				CommonTools.msgFormatter(m, "Content");
			}
			if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_TEXT.getCode())) { // words
																						// 文本消息
				if (m.getString("Url").length() != 0) {
					String regEx = "(.+?\\(.+?\\))";
					Matcher matcher = CommonTools.getMatcher(regEx, m.getString("Content"));
					String data = "Map";
					if (matcher.find()) {
						data = matcher.group(1);
					}
					msg.put("Type", "Map");
					msg.put("Text", data);
				} else {
					msg.put("Type", MsgTypeEnum.TEXT.getType());
					msg.put("Text", m.getString("Content"));
				}
				m.put("Type", msg.getString("Type"));
				m.put("Text", msg.getString("Text"));
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_IMAGE.getCode())
					|| m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_EMOTICON.getCode())) { // 图片消息
				m.put("Type", MsgTypeEnum.PIC.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VOICE.getCode())) { // 语音消息
				m.put("Type", MsgTypeEnum.VOICE.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VERIFYMSG.getCode())) {// friends
				// 好友确认消息
				// MessageTools.addFriend(constant, userName, 3, ticket); // 确认添加好友
				m.put("Type", MsgTypeEnum.VERIFYMSG.getType());

			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_SHARECARD.getCode())) { // 共享名片
				m.put("Type", MsgTypeEnum.NAMECARD.getType());

			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VIDEO.getCode())
					|| m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_MICROVIDEO.getCode())) {// viedo
				m.put("Type", MsgTypeEnum.VIEDO.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_MEDIA.getCode())) { // 多媒体消息
				m.put("Type", MsgTypeEnum.MEDIA.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_STATUSNOTIFY.getCode())) {// phone
				// init
				// 微信初始化消息

			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_SYS.getCode())) {// 系统消息
				m.put("Type", MsgTypeEnum.SYS.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_RECALLED.getCode())) { // 撤回消息

			} else {
				LOG.info("Useless msg");
			}

			if (m.getBoolean("groupMsg")){

				String groupName = groups.get(m.getString("FromUserName")).getNickName();
				String memberName = groups.get(m.getString("FromUserName")).getMembers().get(m.getString("FromMemberName")).getNickName();

				LOG.info("收到消息一条，来自: " + groupName);
				LOG.info(memberName);

				//Test
				if ("D1M".equals(groupName) || "【IT内部】2018技术部".equals(groupName)){
					TeambitionService.sendMessage(groupName, groups.get(m.getString("FromUserName")).getMembers().get(m.getString("FromMemberName")).getNickName(),m.getString("Content"));
				}
				saveMsg(groupName, memberName, m.getString("CreateTime"), m.getString("Content"));
			}else {
				saveMsg(null, recommends.get(m.getString("FromUserName")).getNickName(), m.getString("CreateTime"), m.getString("Content"));
				LOG.info("收到消息一条，来自: " + recommends.get(m.getString("FromUserName")).getNickName());
			}
			LOG.info(m.getString("Content"));
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
