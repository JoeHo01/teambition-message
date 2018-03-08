package com.d1m.tbmessage.server.database.dao;

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
	void addProjects(List<Map<String, String>> projects);

	class ProjectProvider{
		public String addProjects(Map<String, ArrayList> map) {
			StringBuilder sql = new StringBuilder("INSERT INTO project ");
			sql.append("(id, name, description, project_tag, organization_id)").append(" VALUES ");
			Iterator projects = map.get("list").iterator();
			while (projects.hasNext()){
				Map<String, String> project = (Map<String, String>)projects.next();
				sql.append('(').append("'").append(project.get("id")).append("'").append(COMMA).append("'").append(project.get("name")).append("'").append(COMMA).append("'").append(project.get("description")).append("'").append(COMMA).append("'").append(project.get("projectTag")).append("'").append(COMMA).append("'").append(project.get("organizationId")).append("'").append(')');
				if (projects.hasNext()) sql.append(COMMA);
			}
			return sql.toString();
		}
	}

	@Delete("DELETE FROM project WHERE organization_id = #{organizationId}")
	void deleteProjects(String organizationId);
}
