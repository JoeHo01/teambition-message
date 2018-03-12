package com.d1m.tbmessage.server.database.dao;

import com.d1m.tbmessage.server.database.entity.AppSecretDO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppSecretDAO {
	@Select("SELECT a.key,a.value FROM app_secret a")
	List<AppSecretDO> getAppSecret();
}
