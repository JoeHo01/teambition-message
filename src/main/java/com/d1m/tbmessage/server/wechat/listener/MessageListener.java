package com.d1m.tbmessage.server.wechat.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.d1m.tbmessage.common.util.SleepUtil;
import com.d1m.tbmessage.server.management.service.MessageService;
import com.d1m.tbmessage.server.wechat.constant.enums.RetCodeEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.StorageLoginInfoEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.URLEnum;
import com.d1m.tbmessage.server.wechat.constant.enums.parameters.BaseParaEnum;
import com.d1m.tbmessage.server.wechat.core.Core;
import com.d1m.tbmessage.server.wechat.core.MessageCenter;
import com.d1m.tbmessage.server.wechat.core.MessageTool;
import com.d1m.tbmessage.server.wechat.entity.MemberDTO;
import com.d1m.tbmessage.server.wechat.entity.MessageDTO;
import com.d1m.tbmessage.server.wechat.login.service.WechatHttpService;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;

@Service
public class MessageListener {

	private static Logger LOG = LoggerFactory.getLogger(LoginStatusListener.class);

	private Core core = Core.getInstance();

	private Map<String, MemberDTO> members = MemberDTO.getInstance();

	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private final WechatHttpService wechatHttpService;

	private final MessageCenter messageCenter;

	private final MessageService messageService;

	@Autowired
	public MessageListener(ThreadPoolTaskExecutor threadPoolTaskExecutor, WechatHttpService wechatHttpService, MessageCenter messageCenter, MessageService messageService) {
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
		this.wechatHttpService = wechatHttpService;
		this.messageCenter = messageCenter;
		this.messageService = messageService;
	}

	/**
	 * receive messages
	 *
	 * @author Joe He
	 */
	public void start() {
		threadPoolTaskExecutor.execute(new Task());
	}


	/**
	 * message listen task
	 */
	private class Task implements Runnable {

		@Override
		public void run() {
			core.setAlive(true);
			while (core.isAlive()) {
				try {
					Map<String, String> resultMap = syncCheck();
//						LOG.info(JSONObject.toJSONString(resultMap));
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
											if (message.isGroupMsg()) messageService.sendMessage(message);
											SleepUtil.sleep(5);
										}
									} catch (NullPointerException e) {

									} catch (Exception e) {
										LOG.info(e.getMessage(), e);
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
											members.put(memberDTO.getUserName(), memberDTO);
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
				}
			}
		}

		/**
		 * sync the messages
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
		 * check whether there's a new message
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
	}
}
