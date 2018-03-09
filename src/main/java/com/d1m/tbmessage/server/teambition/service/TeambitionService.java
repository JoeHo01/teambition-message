package com.d1m.tbmessage.server.teambition.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.d1m.tbmessage.common.HttpService;
import com.d1m.tbmessage.common.HttpUtil;
import com.d1m.tbmessage.server.database.dao.ProjectDAO;
import com.d1m.tbmessage.server.database.entity.ProjectDO;
import com.d1m.tbmessage.server.teambition.config.TeambitionURL;
import com.d1m.tbmessage.server.teambition.entity.SendMessageDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class TeambitionService {

	private final ProjectDAO projectDAO;

	private static Logger LOG = LoggerFactory.getLogger(TeambitionService.class);

	private static final String X_API_KEY = "e84d537d25fc3a0978bd0c76b732b366";

	@Autowired
	public TeambitionService(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}

	public void sendMessage(SendMessageDTO sendMessageDTO){

//		if (groupName == null){
//			groupName = "私信";
//		}
//
//		String text = "来自: " + groupName + "#" + memberName + " :" + message;

		Map<String, String> headers = new HashMap<>();
		headers.put(HttpUtil.X_API_KEY, X_API_KEY);
		headers.put(HttpUtil.CONTENT_TYPE, HttpUtil.DEFAULT_RESPONSE_TYPE);

		HttpEntity entity = new StringEntity(sendMessageDTO.toString(), ContentType.create("text/json", "UTF-8"));
		try {
			HttpService.post(headers, TeambitionURL.MESSAGE_SEND.url(), entity, null);
		} catch (HttpException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void reloadProjects(String organizationId){
		projectDAO.deleteProjects(organizationId);
		String getProjectTagsURL = MessageFormatter.format(TeambitionURL.PROJECT_TAGS_GET.url(), organizationId).getMessage();
		List<Map<String, String>> projectTags = new ArrayList<>();
		try {
			HttpResponse projectTagsResponse = HttpService.get(null, getProjectTagsURL, null, ACCESS_TOKEN);
			JSONArray projectTagDTOs = JSONArray.parseArray(EntityUtils.toString(projectTagsResponse.getEntity(), "UTF_8"));
			if (projectTagDTOs != null) {
				for (int i = 0; i < projectTagDTOs.size(); i++) {
					JSONObject projectTagDTO = projectTagDTOs.getJSONObject(i);
					Map<String, String> projectTag = new HashMap<>();
					projectTag.put("id", projectTagDTO.getString("_id"));
					projectTag.put("name", projectTagDTO.getString("name"));
					projectTags.add(projectTag);
				}
			}
		} catch (IOException | HttpException e) {
			LOG.error(e.getMessage(), e);
		}
		List<ProjectDO> projects = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(projectTags)){
			for (Map<String, String> projectTag : projectTags){
				String getProjectsURL = MessageFormatter.format(TeambitionURL.PROJECTS_GET.url(), organizationId, projectTag.get("id")).getMessage();
				try {
					HttpResponse getProjectsResponse = HttpService.get(null, getProjectsURL, null, ACCESS_TOKEN);
					JSONArray projectDTOs = JSONArray.parseArray(EntityUtils.toString(getProjectsResponse.getEntity()));
					for (int i = 0; i < projectDTOs.size(); i++) {
						JSONObject projectDTO = projectDTOs.getJSONObject(i);
						ProjectDO project = new ProjectDO();
						project.setId(projectDTO.getString("_id"));
						project.setName(projectDTO.getString("name"));
						project.setDescription(projectDTO.getString("description"));
						project.setProjectTag(projectTag.get("name"));
						project.setOrganizationId(organizationId);
						projects.add(project);
					}
				} catch (IOException | HttpException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(projects)) projectDAO.addProjects(projects);
	}
}
