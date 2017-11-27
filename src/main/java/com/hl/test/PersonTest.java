package com.hl.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hl.dao.PersonDao;
import com.hl.domain.Person;
import com.hl.util.Const;

/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-*.xml")*/
public class PersonTest {
	@Test
	public void getUUid() throws Exception {
		String uuid = UUID.randomUUID().toString();
		System.out.println(uuid);
	}
	
	@Test
	public void test2() throws Exception {
		List<Person>list = new ArrayList<>();
		Person person = new Person();
		person.setPerson_id(1);
		list.add(person);
		String string = JSON.toJSONString(list);
		JSONArray array = JSON.parseArray(string);
		List<Person>list2 = array.parseArray(string, Person.class);
		System.out.println(list2.get(0).getPerson_id());
	}
	
	@Test
	public void test3() throws Exception {
		int num = new Random().nextInt(5)+1;
		System.out.println(num);
	}
}
