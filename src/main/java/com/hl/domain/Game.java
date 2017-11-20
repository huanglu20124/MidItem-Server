package com.hl.domain;

import java.util.Map;

public class Game {
	
	private String my_id;
	private String opposite_id;//本次会话分配的两个user_id
	private Map<Integer,Person>person_map;//通过person_id取出里面的person对象
	private String game_id;
	private Integer first; //如果是1的话，则user1先出牌；否则是2的话就2出牌
	
	//服务端用到的属性
	private Person prePerson;//如果prePerson不为null的话，则要进行对比
	
	public String getGame_id() {
		return game_id;
	}
	public void setGame_id(String game_id) {
		this.game_id = game_id;
	}
	public Integer getFirst() {
		return first;
	}
	public void setFirst(Integer first) {
		this.first = first;
	}
	public Person getPrePerson() {
		return prePerson;
	}
	public void setPrePerson(Person prePerson) {
		this.prePerson = prePerson;
	}
	public Map<Integer, Person> getPerson_map() {
		return person_map;
	}
	public void setPerson_map(Map<Integer, Person> person_map) {
		this.person_map = person_map;
	}
	public String getMy_id() {
		return my_id;
	}
	public void setMy_id(String my_id) {
		this.my_id = my_id;
	}
	public String getOpposite_id() {
		return opposite_id;
	}
	public void setOpposite_id(String opposite_id) {
		this.opposite_id = opposite_id;
	}

	
	
	
}
