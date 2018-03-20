package test;

import com.d1m.tbmessage.common.WordsUtil;
import com.d1m.tbmessage.server.database.entity.ProjectDO;
import com.d1m.tbmessage.server.database.util.SqlUtil;
import com.d1m.tbmessage.server.teambition.config.SendingInfo;
import com.d1m.tbmessage.server.teambition.entity.SendMessageDTO;
import com.d1m.tbmessage.server.wechat.login.service.impl.LoginServiceImpl;

public class Test {
	public static void main(String[] args) {
		ProjectDO project = new ProjectDO();
		project.setOrganizationId("orgId");
		project.setName("name");
		project.setProjectTag("tag");
		String sqlValue = SqlUtil.insertValue(project, new String[]{"id", "name", "description", "projectTag", "organizationId"});
		System.out.println(sqlValue);
	}
}
