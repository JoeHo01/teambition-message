package com.d1m.tbmessage.server.wechat.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.d1m.tbmessage.common.HttpService;
import com.d1m.tbmessage.server.wechat.entity.MessageDTO;
import com.d1m.tbmessage.server.wechat.constant.enums.parameters.BaseParaEnum;

/**
 * 核心存储类，全局只保存一份，单例模式
 * 
 * @author Joe He
 * @date 创建时间：2017年4月23日 下午2:33:56
 * @version 1.0
 *
 */
public class Core {

	private static Core instance;

	private Core() {

	}

	public static Core getInstance() {
		if (instance == null) {
			synchronized (Core.class) {
				instance = new Core();
			}
		}
		return instance;
	}

	boolean alive = false;
	private int memberCount = 0;

	private String indexUrl;

	private String userName;
	private String nickName;
	private List<MessageDTO> msgList = new ArrayList<MessageDTO>();

	private JSONObject userSelf; // 登陆账号自身信息

	private Map<String, JSONObject> userInfoMap = new HashMap<String, JSONObject>();

	private List<String> groupIdList = new ArrayList<>();
	private List<JSONObject> contactList = new ArrayList<>();

	private Map<String, Object> loginInfo = new HashMap<String, Object>();
	private HttpService httpService = HttpService.getInstance();
	private String uuid = null;

	private boolean useHotReload = false;
	private String hotReloadDir = "itchat.pkl";
	private int receivingRetryCount = 5;

	private long lastNormalRetcodeTime; // 最后一次收到正常retcode的时间，秒为单位

	/**
	 * 请求参数
	 */
	public Map<String, Object> getParamMap() {
		return new HashMap<String, Object>(1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				Map<String, String> map = new HashMap<String, String>();
				for (BaseParaEnum baseRequest : BaseParaEnum.values()) {
					map.put(baseRequest.para(), getLoginInfo().get(baseRequest.value()).toString());
				}
				put("BaseRequest", map);
			}
		};
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Map<String, Object> getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(Map<String, Object> loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public boolean isUseHotReload() {
		return useHotReload;
	}

	public void setUseHotReload(boolean useHotReload) {
		this.useHotReload = useHotReload;
	}

	public String getHotReloadDir() {
		return hotReloadDir;
	}

	public void setHotReloadDir(String hotReloadDir) {
		this.hotReloadDir = hotReloadDir;
	}

	public int getReceivingRetryCount() {
		return receivingRetryCount;
	}

	public void setReceivingRetryCount(int receivingRetryCount) {
		this.receivingRetryCount = receivingRetryCount;
	}

	public HttpService getHttpService() {
		return httpService;
	}

	public List<MessageDTO> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<MessageDTO> msgList) {
		this.msgList = msgList;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public JSONObject getUserSelf() {
		return userSelf;
	}

	public void setUserSelf(JSONObject userSelf) {
		this.userSelf = userSelf;
	}

	public Map<String, JSONObject> getUserInfoMap() {
		return userInfoMap;
	}

	public void setUserInfoMap(Map<String, JSONObject> userInfoMap) {
		this.userInfoMap = userInfoMap;
	}

	public List<String> getGroupIdList() {
		return groupIdList;
	}

	public void setGroupIdList(List<String> groupIdList) {
		this.groupIdList = groupIdList;
	}

	public List<JSONObject> getContactList() {
		return contactList;
	}

	public void setContactList(List<JSONObject> contactList) {
		this.contactList = contactList;
	}

	public synchronized long getLastNormalRetcodeTime() {
		return lastNormalRetcodeTime;
	}

	public synchronized void setLastNormalRetcodeTime(long lastNormalRetcodeTime) {
		this.lastNormalRetcodeTime = lastNormalRetcodeTime;
	}

	public String getIndexUrl() {
		return indexUrl;
	}

	public void setIndexUrl(String indexUrl) {
		this.indexUrl = indexUrl;
	}

}
