package com.d1m.tbmessage.server.database.dao;

import com.d1m.tbmessage.server.database.entity.AppSecretDO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCookieDAO {

	@Select("SELECT a.key,a.value FROM user_cookie a")
	List<AppSecretDO> findAll();
}
