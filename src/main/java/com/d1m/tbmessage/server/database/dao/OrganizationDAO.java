package com.d1m.tbmessage.server.database.dao;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationDAO {
	@Select("SELECT id FROM organization WHERE name = #{name}")
	String getIdByName(String name);
}
