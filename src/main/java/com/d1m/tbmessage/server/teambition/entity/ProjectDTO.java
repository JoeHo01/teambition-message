package com.d1m.tbmessage.server.teambition.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectDTO {

	@JsonProperty("_id")
	private String id;

	private String name;

	private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
