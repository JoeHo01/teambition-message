package com.d1m.tbmessage.server.teambition.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.d1m.tbmessage.common.HttpService;
import com.d1m.tbmessage.server.database.dao.ProjectDAO;
import com.d1m.tbmessage.server.teambition.constant.TeambitionURL;
import com.d1m.tbmessage.server.teambition.entity.ProjectTagDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class TeambitionService {

	@Autowired
	private ProjectDAO projectDAO;

	private static final String organizationId = "5a30c1688a4d91000158ce4f";

	private static final String projects = "5a672d36494dac156ccca5c2";

	private static final String messageType = "text";

	private static final String X_API_KEY = "e84d537d25fc3a0978bd0c76b732b366";

	private static final String CONTENT_TYPE = "application/json";

	private static final String ACCESS_TOKEN = "OAuth2 eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfdXNlcklkIjoiNTllNDgxZjExOGZkZGEwMDAxNmYyYjA3IiwiYXVkIjoib2F1dGgyIiwiYXV0aFVwZGF0ZWQiOjE1MDgxNDc2OTc4ODYsImNsaWVudEtleSI6ImU1MjMyYzgwLTBjYmQtMTFlOC1iNTk3LWY5NzljOTgzNjE0MCIsImV4cCI6MTU1MTQxMDIyMSwiaWF0IjoxNTE5ODc0MjIxLCJpc3MiOiJhY2NvdW50cyJ9.aY1TRq3KYCH0Y07W7HFuEamBrqlZn0nwSRWoa231T_4";

	public void sendMessage(String groupName, String memberName, String message){

		if (groupName == null){
			groupName = "私信";
		}

		String text = "来自: " + groupName + "#" + memberName + " :" + message;

		Map<String, String> headers = new HashMap<>();
		headers.put("x-api-key", X_API_KEY);
		headers.put("Content-Type", CONTENT_TYPE);

		String data = "{\"_organizationId\":\"" + organizationId + "\"," +"\"projects\":[\""+ projects +"\"]," +"\"messageType\":\""+ messageType +"\"," +	"\"text\":\"" + text + "\"}";

		HttpEntity entity = new StringEntity(data, ContentType.create("text/json", "UTF-8"));

		HttpResponse post = HttpService.post(headers, TeambitionURL.MESSAGE_SEND.url(), entity, null);
	}

	public List<ProjectTagDTO> getProjectTags(String organizationId){
		String url = MessageFormatter.format(TeambitionURL.PROJECT_TAGS_GET.url(), organizationId).getMessage();
		HttpResponse httpResponse = HttpService.get(null, url, null, ACCESS_TOKEN);
		try {
			byte[] bytes = IOUtils.readFully(httpResponse.getEntity().getContent(), -1, true);
			JSONArray projectTags = JSONArray.parseArray(new String(bytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void reloadProjects(String organizationId){
		projectDAO.deleteProjects(organizationId);
		String getProjectTagsURL = MessageFormatter.format(TeambitionURL.PROJECT_TAGS_GET.url(), organizationId).getMessage();
		List<Map<String, String>> projectTags = new ArrayList<>();
		HttpResponse projectTagsResponse = HttpService.get(null, getProjectTagsURL, null, ACCESS_TOKEN);
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Map<String, String>> projects = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(projectTags)){
			for (Map<String, String> projectTag : projectTags){
				String getProjectsURL = MessageFormatter.format(TeambitionURL.PROJECTS_GET.url(), organizationId, projectTag.get("id")).getMessage();
				HttpResponse getProjectsResponse = HttpService.get(null, getProjectsURL, null, ACCESS_TOKEN);
				try {
					InputStream content = getProjectsResponse.getEntity().getContent();
					JSONArray projectDTOs = JSONArray.parseArray(new String(IOUtils.readFully(content, -1, true)));
					content.close();
					for (int i = 0; i < projectDTOs.size(); i++) {
						JSONObject projectDTO = projectDTOs.getJSONObject(i);
						Map<String, String> project = new HashMap<>();
						project.put("id", projectDTO.getString("_id"));
						project.put("name", projectDTO.getString("name"));
						project.put("description", projectDTO.getString("description"));
						project.put("projectTag", projectTag.get("name"));
						project.put("organizationId", organizationId);
						projects.add(project);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (CollectionUtils.isNotEmpty(projects)) projectDAO.addProjects(projects);
	}
}
