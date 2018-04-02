package com.d1m.tbmessage.server.teambition.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.d1m.tbmessage.common.util.HttpUtil;
import com.d1m.tbmessage.server.database.dao.ProjectDAO;
import com.d1m.tbmessage.server.database.entity.ProjectDO;
import com.d1m.tbmessage.server.teambition.config.AppSecretInfo;
import com.d1m.tbmessage.server.teambition.config.TeambitionURL;
import com.d1m.tbmessage.server.teambition.entity.ProjectTagDTO;
import com.d1m.tbmessage.server.teambition.entity.SendMessageDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.util.*;

@Service
public class TeambitionService {

	private final ProjectDAO projectDAO;

	private final TeambitionHttpService teambitionHttpService;

	private static Logger LOG = LoggerFactory.getLogger(TeambitionService.class);

	private static AppSecretInfo appSecretInfo = AppSecretInfo.getInstance();

	@Autowired
	public TeambitionService(ProjectDAO projectDAO, TeambitionHttpService teambitionHttpService) {
		this.projectDAO = projectDAO;
		this.teambitionHttpService = teambitionHttpService;
	}

	public void sendMessage(SendMessageDTO sendMessageDTO){

		Map<String, String> headers = new HashMap<>();
		headers.put(HttpUtil.X_API_KEY, appSecretInfo.getApiKey());
		headers.put(HttpUtil.CONTENT_TYPE, HttpUtil.DEFAULT_RESPONSE_TYPE);

		HttpEntity entity = new StringEntity(sendMessageDTO.toString(), ContentType.create(HttpUtil.POST_BODY_TYPE_JSON, HttpUtil.UTF_8));
		try {
			teambitionHttpService.post(headers, TeambitionURL.MESSAGE_SEND.url(), entity, null);
		} catch (HttpException e) {
			LOG.error(e.getMessage(), e);
			e.getMessage();
		}
	}

	public void reloadProjects(String organizationId){
		projectDAO.deleteProjects(organizationId);
		String getProjectsURL = MessageFormatter.format(TeambitionURL.PROJECTS_GET.url(), organizationId).getMessage();
		List<ProjectDO> projects = null;
		try {
			HttpResponse response = teambitionHttpService.get(null, getProjectsURL, null, appSecretInfo.getAccessToken());
			projects = JSONArray.parseArray(EntityUtils.toString(response.getEntity()), ProjectDO.class);
		} catch (IOException | HttpException e) {
			LOG.error(e.getMessage(), e);
			e.getMessage();
		}
		if (CollectionUtils.isNotEmpty(projects)) projectDAO.addProjects(projects);
	}

	public void resetAccessToken() {
		String payload = "{" +
				"\"client_id\":\"" + appSecretInfo.getClientId() + "\"," +
				"\"client_secret\":\"" + appSecretInfo.getClientSecret() + "\"," +
				"\"code\":\"" + appSecretInfo.getCode() + "\"" +
				"}";
		HttpEntity entity = new StringEntity(payload, ContentType.create(HttpUtil.POST_BODY_TYPE_JSON, HttpUtil.UTF_8));
		try {
			HttpResponse response = teambitionHttpService.post(null, TeambitionURL.ACCESS_TOKEN_GET.url(), entity, null);
		} catch (HttpException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
