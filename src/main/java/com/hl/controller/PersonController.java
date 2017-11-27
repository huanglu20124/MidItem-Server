package com.hl.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSON;
import com.hl.domain.Person;
import com.hl.domain.SimpleResponse;
import com.hl.service.PersonService;

@Controller
public class PersonController {
	
	@Resource(name="personService")
	private PersonService personService;
	
	@RequestMapping(value = "/getTwentyPerson.action", method = RequestMethod.POST)
	public void getTwentyPerson(Integer page, String keyword, HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("utf-8");
		List<Person>list = personService.getTenPerson(page,keyword);
		if(list == null){
			list = new ArrayList<>();
		}
		response.getWriter().write(JSON.toJSONString(list));
	}
	
	@RequestMapping(value = "/addUpdatePerson.action", method = RequestMethod.POST)
	public void addUpdatePerson(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("utf-8");
		SimpleResponse simpleResponse = new SimpleResponse();
		//处理文字信息
		Person person = JSON.parseObject(request.getParameter("person"),Person.class);
		System.out.println(person.getPerson_id() + "  " + person.getName());
		if(person.getPerson_id() == null){
			System.out.println("准备添加");
			Integer person_id = null;
			person_id = personService.addPerson(person, request);
			if(person_id == null){
				simpleResponse.setErr("添加失败");
			}else {
				simpleResponse.setSuccess("添加成功");
				simpleResponse.setPerson_id(person_id);
			}
		}else {
			System.out.println("准备修改");
			Boolean flag = personService.updatePerson(person, request);
			if(flag) simpleResponse.setSuccess("更新成功");
			else {
				simpleResponse.setErr("更新失败");
			}
		}
		response.getWriter().write(JSON.toJSONString(simpleResponse));
	}	
	
	@RequestMapping(value = "/deletePerson.action", method = RequestMethod.POST)
	public void deletePerson(Integer person_id, HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("utf-8");
		SimpleResponse simpleResponse = new SimpleResponse();
		if(personService.deletePerson(person_id) == true){
			simpleResponse.setSuccess("删除成功");
		}else {
			simpleResponse.setErr("删除失败");
		}
		response.getWriter().write(JSON.toJSONString(simpleResponse));
	}
	
	@RequestMapping(value = "/addGood.action", method = RequestMethod.POST)
	public void addGood(Integer person_id,HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("utf-8");
		//点赞功能
		SimpleResponse simpleResponse = new SimpleResponse();
		if(personService.addGood(person_id) == true){
			simpleResponse.setSuccess("点赞成功");
		}else {
			simpleResponse.setErr("点赞失败");
		}
		response.getWriter().write(JSON.toJSONString(simpleResponse));
	}
	
	@RequestMapping(value = "/getGoodRank.action", method = RequestMethod.POST)
	public void getGoodRank(Integer page,HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("utf-8");
		//获取点赞排行榜
		List<Person>list = personService.getTenRankList(page);
	    response.getWriter().write(JSON.toJSONString(list));
	}	
}
