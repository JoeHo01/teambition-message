package com.d1m.tbmessage.server.management.controller;

import com.d1m.tbmessage.server.database.dao.MessgeDAO;
import com.d1m.tbmessage.server.database.entity.MsgDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("hello")
public class Hello {

	@Autowired
	MessgeDAO messgeDAO;

	@RequestMapping("test")
	public List<MsgDO> test(){
		return messgeDAO.findAll();
	}
}
