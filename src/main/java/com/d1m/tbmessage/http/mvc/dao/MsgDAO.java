package com.d1m.tbmessage.http.mvc.dao;

import com.d1m.tbmessage.http.mvc.entity.MsgDO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MsgDAO {
	@Select("select * from msg")
	List<MsgDO> findAll();
}
