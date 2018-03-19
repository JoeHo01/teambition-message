package com.d1m.tbmessage.server.wechat.login.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;

import com.d1m.tbmessage.server.wechat.login.service.WechatHttpService;
import com.d1m.tbmessage.server.teambition.config.AppSecretInfo;
import com.d1m.tbmessage.server.teambition.config.Constant;
import com.d1m.tbmessage.server.teambition.config.SendingInfo;
import com.d1m.tbmessage.server.teambition.entity.SendMessageDTO;
import com.d1m.tbmessage.server.teambition.service.TeambitionService;
import com.d1m.tbmessage.server.wechat.entity.GroupDTO;
import com.d1m.tbmessage.server.wechat.entity.MemberDTO;
import com.d1m.tbmessage.server.wechat.core.Core;
import com.d1m.tbmessage.server.wechat.core.MessageCenter;
import com.d1m.tbmessage.server.wechat.entity.MessageDTO;
import com.d1m.tbmessage.server.wechat.login.service.ILoginService;
import com.d1m.tbmessage.server.wechat.constant.enums.ResultEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.RetCodeEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.StorageLoginInfoEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.URLEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.parameters.BaseParaEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.parameters.LoginParaEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.parameters.StatusNotifyParaEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.parameters.UUIDParaEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.d1m.tbmessage.common.SleepUtil;
import com.d1m.tbmessage.server.wechat.core.MessageTool;

/**
 * 登陆服务实现类
 * 
 * @author Joe He
 * @date 创建时间：2017年5月13日 上午12:09:35
 * @version 1.0
 *
 */
@Service
public class LoginServiceImpl implements ILoginService {
	private static Logger LOG = LoggerFactory.getLogger(LoginServiceImpl.class);

	private Core core = Core.getInstance();

	private Map<String, GroupDTO> groups = GroupDTO.getInstance();

	private Map<String, MemberDTO> recommends = MemberDTO.getInstance();

	private final WechatHttpService wechatHttpService;

	private SendingInfo sendingInfo = SendingInfo.getInstance();

	private AppSecretInfo appSecretInfo = AppSecretInfo.getInstance();

	private final MessageCenter messageCenter;

	private final TeambitionService teambitionService;

	@Autowired
	public LoginServiceImpl(MessageCenter messageCenter, TeambitionService teambitionService, WechatHttpService wechatHttpService) {
		this.messageCenter = messageCenter;
		this.teambitionService = teambitionService;
		this.wechatHttpService = wechatHttpService;
	}

	@Override
	public boolean login() {

		boolean isLogin = false;
		// 组装参数和URL
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(LoginParaEnum.LOGIN_ICON.para(), LoginParaEnum.LOGIN_ICON.value()));
		params.add(new BasicNameValuePair(LoginParaEnum.UUID.para(), core.getUuid()));
		params.add(new BasicNameValuePair(LoginParaEnum.TIP.para(), LoginParaEnum.TIP.value()));

		// long time = 4000;
		while (!isLogin) {
			// SleepUtil.sleep(time += 1000);
			long millis = System.currentTimeMillis();
			params.add(new BasicNameValuePair(LoginParaEnum.R.para(), String.valueOf(millis / 1579L)));
			params.add(new BasicNameValuePair(LoginParaEnum._.para(), String.valueOf(millis)));
			HttpEntity entity = wechatHttpService.doGet(URLEnum.LOGIN_URL.getUrl(), params, true, null);

			try {
				String result = EntityUtils.toString(entity);
				String status = checklogin(result);

				if (ResultEnum.SUCCESS.getCode().equals(status)) {
					processLoginInfo(result); // 处理结果
					isLogin = true;
					core.setAlive(isLogin);
					break;
				}
				if (ResultEnum.WAIT_CONFIRM.getCode().equals(status)) {
					LOG.info("请点击微信确认按钮，进行登陆");
				}

			} catch (Exception e) {
				LOG.error("微信登陆异常！", e);
			}
		}
		return isLogin;
	}

	@Override
	public String getUuid() {
		// 组装参数和URL
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(UUIDParaEnum.APP_ID.para(), UUIDParaEnum.APP_ID.value()));
		params.add(new BasicNameValuePair(UUIDParaEnum.FUN.para(), UUIDParaEnum.FUN.value()));
		params.add(new BasicNameValuePair(UUIDParaEnum.LANG.para(), UUIDParaEnum.LANG.value()));
		params.add(new BasicNameValuePair(UUIDParaEnum._.para(), String.valueOf(System.currentTimeMillis())));

		HttpEntity entity = wechatHttpService.doGet(URLEnum.UUID_URL.getUrl(), params, true, null);

		try {
			String result = EntityUtils.toString(entity);
			String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
			Matcher matcher = MessageTool.getMatcher(regEx, result);
			if (matcher.find()) {
				if ((ResultEnum.SUCCESS.getCode().equals(matcher.group(1)))) {
					core.setUuid(matcher.group(2));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return core.getUuid();
	}

	@Override
	public boolean getQR(String qrPath) {
		qrPath = qrPath + File.separator + "QR.jpg";
		String qrUrl = URLEnum.QRCODE_URL.getUrl() + core.getUuid();
		HttpEntity entity = wechatHttpService.doGet(qrUrl, null, true, null);
		try {
			OutputStream out = new FileOutputStream(qrPath);
			byte[] bytes = EntityUtils.toByteArray(entity);
			out.write(bytes);
			out.flush();
			out.close();

		} catch (Exception e) {
			LOG.info(e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public boolean webWxInit() {
		core.setAlive(true);
		core.setLastNormalRetcodeTime(System.currentTimeMillis());
		// 组装请求URL和参数
		String url = String.format(URLEnum.INIT_URL.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()),
				String.valueOf(System.currentTimeMillis() / 3158L),
				core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));

		Map<String, Object> paramMap = core.getParamMap();

		// 请求初始化接口
		HttpEntity entity = wechatHttpService.doPost(url, JSON.toJSONString(paramMap));
		try {
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			JSONObject obj = JSON.parseObject(result);

			JSONObject user = obj.getJSONObject(StorageLoginInfoEnum.User.getKey());
			JSONObject syncKey = obj.getJSONObject(StorageLoginInfoEnum.SyncKey.getKey());

			core.getLoginInfo().put(StorageLoginInfoEnum.InviteStartCount.getKey(),
					obj.getInteger(StorageLoginInfoEnum.InviteStartCount.getKey()));
			core.getLoginInfo().put(StorageLoginInfoEnum.SyncKey.getKey(), syncKey);

			JSONArray syncArray = syncKey.getJSONArray("List");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < syncArray.size(); i++) {
				sb.append(syncArray.getJSONObject(i).getString("Key")).append("_").append(syncArray.getJSONObject(i).getString("Val")).append("|");
			}
			String synckey = sb.toString();

			core.getLoginInfo().put(StorageLoginInfoEnum.synckey.getKey(), synckey.substring(0, synckey.length() - 1));
			core.setUserName(user.getString("UserName"));
			core.setNickName(user.getString("NickName"));
			core.setUserSelf(obj.getJSONObject("User"));

			String chatSet = obj.getString("ChatSet");
			String[] chatSetArray = chatSet.split(",");
			for (String aChatSetArray : chatSetArray) {
				if (aChatSetArray.contains("@@")) {
					// 更新GroupIdList
					core.getGroupIdList().add(aChatSetArray); //
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void wxStatusNotify() {
		// 组装请求URL和参数
		String url = String.format(URLEnum.STATUS_NOTIFY_URL.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));

		Map<String, Object> paramMap = core.getParamMap();
		paramMap.put(StatusNotifyParaEnum.CODE.para(), StatusNotifyParaEnum.CODE.value());
		paramMap.put(StatusNotifyParaEnum.FROM_USERNAME.para(), core.getUserName());
		paramMap.put(StatusNotifyParaEnum.TO_USERNAME.para(), core.getUserName());
		paramMap.put(StatusNotifyParaEnum.CLIENT_MSG_ID.para(), System.currentTimeMillis());
		String paramStr = JSON.toJSONString(paramMap);

		try {
			HttpEntity entity = wechatHttpService.doPost(url, paramStr);
			EntityUtils.toString(entity, Consts.UTF_8);
		} catch (Exception e) {
			LOG.error("微信状态通知接口失败！", e);
		}

	}

	@Override
	public void startReceiving() {
		core.setAlive(true);
		new Thread(new Runnable() {
			int retryCount = 0;

			@Override
			public void run() {
				while (core.isAlive()) {
					try {
						Map<String, String> resultMap = syncCheck();
						LOG.info(JSONObject.toJSONString(resultMap));
						String retcode = resultMap.get("retcode");
						String selector = resultMap.get("selector");
						if (retcode.equals(RetCodeEnum.UNKOWN.getCode())) {
							LOG.info(RetCodeEnum.UNKOWN.getType());
						} else if (retcode.equals(RetCodeEnum.LOGIN_OUT.getCode())) { // 退出
							LOG.info(RetCodeEnum.LOGIN_OUT.getType());
							break;
						} else if (retcode.equals(RetCodeEnum.LOGIN_OTHERWHERE.getCode())) { // 其它地方登陆
							LOG.info(RetCodeEnum.LOGIN_OTHERWHERE.getType());
							break;
						} else if (retcode.equals(RetCodeEnum.MOBILE_LOGIN_OUT.getCode())) { // 移动端退出
							LOG.info(RetCodeEnum.MOBILE_LOGIN_OUT.getType());
							break;
						} else if (retcode.equals(RetCodeEnum.NORMAL.getCode())) {
							core.setLastNormalRetcodeTime(System.currentTimeMillis()); // 最后收到正常报文时间
							JSONObject msgObj = webWxSync();
							switch (selector) {
								case "2":
									if (msgObj != null) {
										try {
											List<MessageDTO> messages = messageCenter.produceMsg(msgObj.getJSONArray("AddMsgList"));
											for (MessageDTO message : messages){
												if (message.isGroupMsg())sendMessage(message);
											}
										} catch (Exception e) {
											LOG.info(e.getMessage());
										}
									}
									break;
								case "7":
									webWxSync();
									break;
								case "4":
									continue;
								case "3":
									continue;
								case "6":
									if (msgObj != null) {
										try {
											JSONArray msgList = msgObj.getJSONArray("AddMsgList");
											JSONArray modContactList = msgObj.getJSONArray("ModContactList"); // 存在删除或者新增的好友信息
											messageCenter.produceMsg(msgList);
											for (int j = 0; j < msgList.size(); j++) {
												// 存在主动加好友之后的同步联系人到本地
												MemberDTO memberDTO = modContactList.getObject(j, MemberDTO.class);
												recommends.put(memberDTO.getUserName(), memberDTO);
											}
										} catch (Exception e) {
											LOG.info(e.getMessage());
										}
									}
									break;
							}
						}
					} catch (Exception e) {
						LOG.info(e.getMessage());
						retryCount += 1;
						if (core.getReceivingRetryCount() < retryCount) {
							core.setAlive(false);
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								LOG.info(e.getMessage());
							}
						}
					}
				}
			}
		}).start();
	}

	private void sendMessage(MessageDTO message) {
		String projectId = sendingInfo.getProjectId(message.getFromUserName());
		if (StringUtils.isEmpty(projectId))	return;
		SendMessageDTO sendMessageDTO = new SendMessageDTO();
		sendMessageDTO.setOrganizationId(appSecretInfo.getOrganizationId());
		sendMessageDTO.addProject(projectId);
		sendMessageDTO.setMessageType(Constant.MESSAGE_TYPE_TEXT);
		sendMessageDTO.setText(message.getFromNickName() + ": " + message.getText());
		teambitionService.sendMessage(sendMessageDTO);
	}

	@Override
	public void webWxGetContact() {
		String url = String.format(URLEnum.WEB_WX_GET_CONTACT.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()));
		Map<String, Object> paramMap = core.getParamMap();
		HttpEntity entity = wechatHttpService.doPost(url, JSON.toJSONString(paramMap));

		try {
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			JSONObject fullFriendsJsonList = JSON.parseObject(result);
			// 查看seq是否为0，0表示好友列表已全部获取完毕，若大于0，则表示好友列表未获取完毕，当前的字节数（断点续传）
			long seq = 0;
			long currentTime = 0L;
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			if (fullFriendsJsonList.get("Seq") != null) {
				seq = fullFriendsJsonList.getLong("Seq");
				currentTime = new Date().getTime();
			}
			core.setMemberCount(fullFriendsJsonList.getInteger(StorageLoginInfoEnum.MemberCount.getKey()));
			JSONArray member = fullFriendsJsonList.getJSONArray(StorageLoginInfoEnum.MemberList.getKey());
			// 循环获取seq直到为0，即获取全部好友列表 ==0：好友获取完毕 >0：好友未获取完毕，此时seq为已获取的字节数
			while (seq > 0) {
				// 设置seq传参
				params.add(new BasicNameValuePair("r", String.valueOf(currentTime)));
				params.add(new BasicNameValuePair("seq", String.valueOf(seq)));
				entity = wechatHttpService.doGet(url, params, false, null);

				params.remove(new BasicNameValuePair("r", String.valueOf(currentTime)));
				params.remove(new BasicNameValuePair("seq", String.valueOf(seq)));

				result = EntityUtils.toString(entity, Consts.UTF_8);
				fullFriendsJsonList = JSON.parseObject(result);

				if (fullFriendsJsonList.get("Seq") != null) {
					seq = fullFriendsJsonList.getLong("Seq");
					currentTime = new Date().getTime();
				}

				// 累加好友列表
				member.addAll(fullFriendsJsonList.getJSONArray(StorageLoginInfoEnum.MemberList.getKey()));
			}
			core.setMemberCount(member.size());
			for (Object aMember : member) {
				JSONObject o = (JSONObject) aMember;
				// o.getInteger("VerifyFlag") & 8) != 0 公众号/服务号
				// Config.API_SPECIAL_USER.contains(o.getString("UserName")) 特殊账号

				if (o.getString("UserName").contains("@") && !o.getString("UserName").contains("@@")) { //群聊
					groups.put(o.getString("UserName"), new GroupDTO());
				}else if (o.getString("UserName").contains("@")) { // 普通联系人
					recommends.put(o.getString("UserName"), JSON.toJavaObject(o, MemberDTO.class));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void WebWxBatchGetContact() {
		String url = String.format(URLEnum.WEB_WX_BATCH_GET_CONTACT.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()), new Date().getTime(),
				core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));
		Map<String, Object> paramMap = core.getParamMap();
		paramMap.put("Count", core.getGroupIdList().size());
		List<Map<String, String>> list = new ArrayList<>();
		for (int i = 0; i < core.getGroupIdList().size(); i++) {
			HashMap<String, String> map = new HashMap<>();
			map.put("UserName", core.getGroupIdList().get(i));
			map.put("EncryChatRoomId", "");
			list.add(map);
		}
		paramMap.put("List", list);
		HttpEntity entity = wechatHttpService.doPost(url, JSON.toJSONString(paramMap));
		try {
			String text = EntityUtils.toString(entity, Consts.UTF_8);
			JSONObject obj = JSON.parseObject(text);
			JSONArray contactList = obj.getJSONArray("ContactList");
			for (int i = 0; i < contactList.size(); i++) { // 群好友
				if (contactList.getJSONObject(i).getString("UserName").contains("@@")) { // 群
					GroupDTO group = new GroupDTO();
					JSONArray memberList = contactList.getJSONObject(i).getJSONArray("MemberList");
					for (int j = 0; j < memberList.size(); j++) {
						MemberDTO memberDTO = memberList.getObject(j, MemberDTO.class);
						group.getMembers().put(memberDTO.getUserName(), memberDTO);
						if (memberDTO.getUserName().equals(core.getUserName())) {
							sendingInfo.setId(contactList.getJSONObject(i).getString("UserName"), getProjectName(memberDTO.getDisplayName()));
						}
					}
					group.setId(contactList.getJSONObject(i).getString("UserName"));
					group.setNickName(contactList.getJSONObject(i).getString("NickName"));
					groups.put(group.getId(), group);
				}
			}
		} catch (Exception e) {
			LOG.info(e.getMessage());
		}
	}

	private String getProjectName(String displayName){
		return displayName.substring(displayName.indexOf("#") + 1, displayName.length());
	}

	/**
	 * 检查登陆状态
	 *
	 * @param result
	 * @return
	 */
	private String checklogin(String result) {
		String regEx = "window.code=(\\d+)";
		Matcher matcher = MessageTool.getMatcher(regEx, result);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * 处理登陆信息
	 *
	 * @author Joe He
	 * @date 2017年4月9日 下午12:16:26
	 */
	private void processLoginInfo(String loginContent) {
		String regEx = "window.redirect_uri=\"(\\S+)\";";
		Matcher matcher = MessageTool.getMatcher(regEx, loginContent);
		if (matcher.find()) {
			String originalUrl = matcher.group(1);
			String url = originalUrl.substring(0, originalUrl.lastIndexOf('/')); // https://wx2.qq.com/cgi-bin/mmwebwx-bin
			core.getLoginInfo().put("url", url);
			Map<String, List<String>> possibleUrlMap = this.getPossibleUrlMap();
			Iterator<Entry<String, List<String>>> iterator = possibleUrlMap.entrySet().iterator();
			Map.Entry<String, List<String>> entry;
			String fileUrl;
			String syncUrl;
			while (iterator.hasNext()) {
				entry = iterator.next();
				String indexUrl = entry.getKey();
				fileUrl = "https://" + entry.getValue().get(0) + "/cgi-bin/mmwebwx-bin";
				syncUrl = "https://" + entry.getValue().get(1) + "/cgi-bin/mmwebwx-bin";
				if (core.getLoginInfo().get("url").toString().contains(indexUrl)) {
					core.setIndexUrl(indexUrl);
					core.getLoginInfo().put("fileUrl", fileUrl);
					core.getLoginInfo().put("syncUrl", syncUrl);
					break;
				}
			}
			if (core.getLoginInfo().get("fileUrl") == null && core.getLoginInfo().get("syncUrl") == null) {
				core.getLoginInfo().put("fileUrl", url);
				core.getLoginInfo().put("syncUrl", url);
			}
			core.getLoginInfo().put("deviceid", "e" + String.valueOf(new Random().nextLong()).substring(1, 16)); // 生成15位随机数
			core.getLoginInfo().put("BaseRequest", new ArrayList<String>());
			String text = "";

			try {
				HttpEntity entity = wechatHttpService.doGet(originalUrl, null, false, null);
				text = EntityUtils.toString(entity);
			} catch (Exception e) {
				LOG.info(e.getMessage());
				return;
			}
			//add by 默非默 2017-08-01 22:28:09
			//如果登录被禁止时，则登录返回的message内容不为空，下面代码则判断登录内容是否为空，不为空则退出程序
			String msg = getLoginMessage(text);
			if (!"".equals(msg)){
				LOG.info(msg);
				System.exit(0);
			}
			Document doc = MessageTool.xmlParser(text);
			if (doc != null) {
				core.getLoginInfo().put(StorageLoginInfoEnum.skey.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.skey.getKey()).item(0).getFirstChild()
								.getNodeValue());
				core.getLoginInfo().put(StorageLoginInfoEnum.wxsid.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.wxsid.getKey()).item(0).getFirstChild()
								.getNodeValue());
				core.getLoginInfo().put(StorageLoginInfoEnum.wxuin.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.wxuin.getKey()).item(0).getFirstChild()
								.getNodeValue());
				core.getLoginInfo().put(StorageLoginInfoEnum.pass_ticket.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.pass_ticket.getKey()).item(0).getFirstChild()
								.getNodeValue());
			}

		}
	}

	private Map<String, List<String>> getPossibleUrlMap() {
		Map<String, List<String>> possibleUrlMap = new HashMap<String, List<String>>();
		possibleUrlMap.put("wx.qq.com", new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.wx.qq.com");
				add("webpush.wx.qq.com");
			}
		});

		possibleUrlMap.put("wx2.qq.com", new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.wx2.qq.com");
				add("webpush.wx2.qq.com");
			}
		});
		possibleUrlMap.put("wx8.qq.com", new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.wx8.qq.com");
				add("webpush.wx8.qq.com");
			}
		});

		possibleUrlMap.put("web2.wechat.com", new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.web2.wechat.com");
				add("webpush.web2.wechat.com");
			}
		});
		possibleUrlMap.put("wechat.com", new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.web.wechat.com");
				add("webpush.web.wechat.com");
			}
		});
		return possibleUrlMap;
	}

	/**
	 * 同步消息 sync the messages
	 * 
	 * @author Joe He
	 * @date 2017年5月12日 上午12:24:55
	 * @return
	 */
	private JSONObject webWxSync() {
		JSONObject result = null;
		String url = String.format(URLEnum.WEB_WX_SYNC_URL.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()),
				core.getLoginInfo().get(StorageLoginInfoEnum.wxsid.getKey()),
				core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey()),
				core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));
		Map<String, Object> paramMap = core.getParamMap();
		paramMap.put(StorageLoginInfoEnum.SyncKey.getKey(),
				core.getLoginInfo().get(StorageLoginInfoEnum.SyncKey.getKey()));
		paramMap.put("rr", -new Date().getTime() / 1000);
		String paramStr = JSON.toJSONString(paramMap);
		try {
			HttpEntity entity = wechatHttpService.doPost(url, paramStr);
			String text = EntityUtils.toString(entity, Consts.UTF_8);
			JSONObject obj = JSON.parseObject(text);
			if (obj.getJSONObject("BaseResponse").getInteger("Ret") != 0) {
				result = null;
			} else {
				result = obj;
				core.getLoginInfo().put(StorageLoginInfoEnum.SyncKey.getKey(), obj.getJSONObject("SyncCheckKey"));
				JSONArray syncArray = obj.getJSONObject(StorageLoginInfoEnum.SyncKey.getKey()).getJSONArray("List");
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < syncArray.size(); i++) {
					sb.append(syncArray.getJSONObject(i).getString("Key")).append("_").append(syncArray.getJSONObject(i).getString("Val")).append("|");
				}
				String synckey = sb.toString();
				core.getLoginInfo().put(StorageLoginInfoEnum.synckey.getKey(), synckey.substring(0, synckey.length() - 1));
			}
		} catch (Exception e) {
			LOG.info(e.getMessage());
		}
		return result;

	}

	/**
	 * 检查是否有新消息 check whether there's a message
	 * 
	 * @author Joe He
	 * @date 2017年4月16日 上午11:11:34
	 * @return
	 * 
	 */
	private Map<String, String> syncCheck() {
		Map<String, String> resultMap = new HashMap<String, String>();
		// 组装请求URL和参数
		String url = core.getLoginInfo().get(StorageLoginInfoEnum.syncUrl.getKey()) + URLEnum.SYNC_CHECK_URL.getUrl();
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		for (BaseParaEnum baseRequest : BaseParaEnum.values()) {
			params.add(new BasicNameValuePair(baseRequest.para().toLowerCase(),
					core.getLoginInfo().get(baseRequest.value()).toString()));
		}
		params.add(new BasicNameValuePair("r", String.valueOf(new Date().getTime())));
		params.add(new BasicNameValuePair("synckey", (String) core.getLoginInfo().get("synckey")));
		params.add(new BasicNameValuePair("_", String.valueOf(new Date().getTime())));
		SleepUtil.sleep(7);
		try {
			HttpEntity entity = wechatHttpService.doGet(url, params, true, null);
			if (entity == null) {
				resultMap.put("retcode", "9999");
				resultMap.put("selector", "9999");
				return resultMap;
			}
			String text = EntityUtils.toString(entity);
			String regEx = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}";
			Matcher matcher = MessageTool.getMatcher(regEx, text);
			if (!matcher.find() || matcher.group(1).equals("2")) {
				LOG.info(String.format("Unexpected sync check result: %s", text));
			} else {
				resultMap.put("retcode", matcher.group(1));
				resultMap.put("selector", matcher.group(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 解析登录返回的消息，如果成功登录，则message为空
	 * @param result
	 * @return
	 */
	private String getLoginMessage(String result){
		String[] strArr = result.split("<message>");
		String[] rs = strArr[1].split("</message>");
		if (rs.length > 1) {
			return rs[0];
		}
		return "";
	}
}
