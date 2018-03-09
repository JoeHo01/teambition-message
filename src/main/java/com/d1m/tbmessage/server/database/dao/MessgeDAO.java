package com.d1m.tbmessage.server.database.dao;

import com.d1m.tbmessage.server.database.entity.MessageDO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessgeDAO {
	@Select("select * from msg")
	List<MessageDO> findAll();
}
