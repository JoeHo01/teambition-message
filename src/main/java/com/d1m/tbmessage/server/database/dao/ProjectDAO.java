package com.d1m.tbmessage.server.database.dao;

import com.d1m.tbmessage.server.database.entity.ProjectDO;
import com.d1m.tbmessage.server.database.util.SqlUtil;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
public interface ProjectDAO {
	@InsertProvider(type = ProjectProvider.class, method = "addProjects")
	void addProjects(List<ProjectDO> projects);

	@Delete("DELETE FROM project WHERE organization_id = #{organizationId}")
	void deleteProjects(String organizationId);

	@Select("SELECT id,name,description,logo,archived,creator_id,organization_id FROM project")
	List<ProjectDO> getAll();

	@Select("SELECT id FROM project WHERE name = #{name}")
	String getIdByName(String name);

	class ProjectProvider{
		public String addProjects(Map<String, ArrayList> map) {
			StringBuilder sql = new StringBuilder("INSERT INTO project ");
			sql.append("(id, name, description, logo, archived, creator_id, organization_id)").append(" VALUES ");
			Iterator projects = map.get("list").iterator();
			while (projects.hasNext()){
				Object project = projects.next();
				sql.append(SqlUtil.insertValue(project, new String[]{"id", "name", "description", "logo", "archived", "creatorId", "organizationId"}));
				if (projects.hasNext()) sql.append(',');
			}
			return sql.toString();
		}
	}
}
