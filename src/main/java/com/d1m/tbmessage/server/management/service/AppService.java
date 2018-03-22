package com.d1m.tbmessage.server.management.service;

import com.d1m.tbmessage.common.util.WordsUtil;
import com.d1m.tbmessage.common.annotation.AnnotationUtil;
import com.d1m.tbmessage.server.database.dao.AppSecretDAO;
import com.d1m.tbmessage.server.database.dao.OrganizationDAO;
import com.d1m.tbmessage.server.database.dao.ProjectDAO;
import com.d1m.tbmessage.server.database.entity.AppSecretDO;
import com.d1m.tbmessage.server.database.entity.ProjectDO;
import com.d1m.tbmessage.server.teambition.config.AppSecretInfo;
import com.d1m.tbmessage.server.teambition.config.SendingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@Service
public class AppService {

	private static Logger LOG = LoggerFactory.getLogger(AppService.class);

	private final AppSecretDAO appSecretDAO;

	private final OrganizationDAO organizationDAO;

	private final ProjectDAO projectDAO;

	private SendingInfo sendingInfo = SendingInfo.getInstance();

	private AppSecretInfo appSecretInfo = AppSecretInfo.getInstance();

	@Autowired
	public AppService(AppSecretDAO appSecretDAO, OrganizationDAO organizationDAO, ProjectDAO projectDAO) {
		this.appSecretDAO = appSecretDAO;
		this.organizationDAO = organizationDAO;
		this.projectDAO = projectDAO;
	}

	public void getAppSecretInfo(){
		// get app secret information from DB
		List<AppSecretDO> appSecrets = appSecretDAO.getAppSecret();
		Map<String, String> appSecretsName = AnnotationUtil.getNamedField(AppSecretInfo.class);

		// foreach app secrets
		for (AppSecretDO appSecret : appSecrets) {
			try {
				// set value
				AppSecretInfo.class.getMethod("set" + WordsUtil.upperFirstCase(appSecretsName.get(appSecret.getKey())), String.class).invoke(appSecretInfo, appSecret.getValue());
			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	public String getOrganizationId(String name) {
		return organizationDAO.getIdByName(name);
	}

	public void getProjects() {
		List<ProjectDO> projects = projectDAO.getAll();
		for (ProjectDO project : projects) {
			sendingInfo.setProject(project.getName(), project.getId());
		}
	}

	public String getProjectIdByName(String name) {
		return projectDAO.getIdByName(name);
	}
}
