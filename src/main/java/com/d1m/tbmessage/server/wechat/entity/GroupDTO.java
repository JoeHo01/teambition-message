package com.d1m.tbmessage.server.wechat.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GroupDTO implements Serializable{
	private static Map<String, GroupDTO> instance;

	public static Map<String, GroupDTO> getInstance() {
		if (instance == null) {
			instance = new HashMap<>();
		}
		return instance;
	}

	private String id;

	private String nickName;

	private Map<String, MemberDTO> members = new HashMap<>();

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

	public Map<String, MemberDTO> getMembers() {
		return members;
	}

	public void setMembers(String id, MemberDTO member) {
		this.members.put(id, member);
	}
}
