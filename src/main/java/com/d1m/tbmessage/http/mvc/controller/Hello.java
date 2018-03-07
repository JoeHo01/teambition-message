package com.d1m.tbmessage.http.mvc.controller;

import com.d1m.tbmessage.http.mvc.dao.MsgDAO;
import com.d1m.tbmessage.http.mvc.entity.MsgDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("hello")
public class Hello {

	@Autowired
	MsgDAO msgDAO;

	@RequestMapping("test")
	public List<MsgDO> test(){
		return msgDAO.findAll();
	}
}
