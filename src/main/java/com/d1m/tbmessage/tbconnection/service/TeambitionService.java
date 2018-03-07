package com.d1m.tbmessage.tbconnection.service;

import com.d1m.tbmessage.common.HttpUtil;
import com.d1m.tbmessage.tbconnection.constant.TeambitionURL;
import com.d1m.tbmessage.tbconnection.entity.ProjectTagDTO;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.helpers.MessageFormatter;
import sun.misc.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeambitionService {

	private static final String organizationId = "5a30c1688a4d91000158ce4f";

	private static final String projects = "5a672d36494dac156ccca5c2";

	private static final String messageType = "text";

	private static final String X_API_KEY = "e84d537d25fc3a0978bd0c76b732b366";

	private static final String CONTENT_TYPE = "application/json";

	private static final String ACCESS_TOKEN = "OAuth2 eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfdXNlcklkIjoiNTllNDgxZjExOGZkZGEwMDAxNmYyYjA3IiwiYXVkIjoib2F1dGgyIiwiYXV0aFVwZGF0ZWQiOjE1MDgxNDc2OTc4ODYsImNsaWVudEtleSI6ImU1MjMyYzgwLTBjYmQtMTFlOC1iNTk3LWY5NzljOTgzNjE0MCIsImV4cCI6MTU1MTQxMDIyMSwiaWF0IjoxNTE5ODc0MjIxLCJpc3MiOiJhY2NvdW50cyJ9.aY1TRq3KYCH0Y07W7HFuEamBrqlZn0nwSRWoa231T_4";

	public static void sendMessage(String groupName, String memberName, String message){

		if (groupName == null){
			groupName = "私信";
		}

		String text = "来自: " + groupName + "#" + memberName + " :" + message;

		Map<String, String> headers = new HashMap<>();
		headers.put("x-api-key", X_API_KEY);
		headers.put("Content-Type", CONTENT_TYPE);

		String data = "{\"_organizationId\":\"" + organizationId + "\"," +"\"projects\":[\""+ projects +"\"]," +"\"messageType\":\""+ messageType +"\"," +	"\"text\":\"" + text + "\"}";

		HttpEntity entity = new StringEntity(data, ContentType.create("text/json", "UTF-8"));

		HttpResponse post = HttpUtil.post(headers, TeambitionURL.MESSAGE_SEND.getUrl(), entity, null);
	}

	public static List<ProjectTagDTO> getProjects(){
		String url = MessageFormatter.format(TeambitionURL.PROJECT_TAGS_GET.getUrl(), organizationId).getMessage();
		HttpResponse httpResponse = HttpUtil.get(null, url, null, ACCESS_TOKEN);
		try {
			byte[] bytes = IOUtils.readFully(httpResponse.getEntity().getContent(), 1024, true);
			System.out.println(new String(bytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
