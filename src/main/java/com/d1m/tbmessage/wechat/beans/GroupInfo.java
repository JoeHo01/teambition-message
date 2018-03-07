package com.d1m.tbmessage.wechat.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GroupInfo implements Serializable{
	private static Map<String, GroupInfo> instance;

	public static Map<String, GroupInfo> getInstance() {
		if (instance == null) {
			instance = new HashMap<>();
		}
		return instance;
	}

	private String id;

	private String nickName;

	private Map<String, RecommendInfo> members = new HashMap<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Map<String, RecommendInfo> getMembers() {
		return members;
	}

	public void setMembers(String id, RecommendInfo member) {
		this.members.put(id, member);
	}
}
