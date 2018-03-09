package com.d1m.tbmessage.server.database.dao;

import com.d1m.tbmessage.server.database.entity.ProjectDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
public interface ProjectDAO {
	char COMMA = ',';

	@InsertProvider(type = ProjectProvider.class, method = "addProjects")
	void addProjects(List<ProjectDO> projects);

	@Delete("DELETE FROM project WHERE organization_id = #{organizationId}")
	void deleteProjects(String organizationId);

	class ProjectProvider{
		public String addProjects(Map<String, ArrayList> map) {
			StringBuilder sql = new StringBuilder("INSERT INTO project ");
			sql.append("(id, name, description, project_tag, organization_id)").append(" VALUES ");
			Iterator projects = map.get("list").iterator();
			while (projects.hasNext()){
				ProjectDO project = (ProjectDO) projects.next();
				sql.append('(').append("'").append(project.getId()).append("'").append(COMMA).append("'").append(project.getName()).append("'").append(COMMA).append("'").append(project.getDescription()).append("'").append(COMMA).append("'").append(project.getProjectTag()).append("'").append(COMMA).append("'").append(project.getOrganizationId()).append("'").append(')');
				if (projects.hasNext()) sql.append(COMMA);
			}
			return sql.toString();
		}
	}
}
