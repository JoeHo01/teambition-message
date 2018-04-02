package com.d1m.tbmessage.server.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectDO {

	private String id;

	private String name;

	private String description;

	private String logo;

	@JsonProperty("isArchived")
	private short archived;

	@JsonProperty("_creatorId")
	private String creatorId;

	@JsonProperty("_organizationId")
	private String organizationId;

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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public short getArchived() {
		return archived;
	}

	public void setArchived(short archived) {
		this.archived = archived;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
}
