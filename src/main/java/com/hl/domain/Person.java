package com.hl.domain;

public class Person {
	private Integer person_id;       //用户主键
	private String name;             //姓名
	private String sex;              //性别
	private String second_name;      //人物的字
	private String country;          //国家势力
	private String person_date;        //生卒年份
	private String hometown;         //籍贯
	private String description;      //简介
	private Integer ability;         //能力：1到5级
 	private Integer good;            //点赞数
 	private String head_url;         //头像url
 	private String audio_url;        //音频url
 	
 	private String uuid;             //数据库检索用的 
 	
 	private Integer person_field;    //五行属性，1到5，分别为土水火金木,后一个被前一个相克，木克土
 	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getHometown() {
		return hometown;
	}
	public void setHometown(String hometown) {
		this.hometown = hometown;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getAbility() {
		return ability;
	}
	public void setAbility(Integer ability) {
		this.ability = ability;
	}
	public Integer getGood() {
		return good;
	}
	public void setGood(Integer good) {
		this.good = good;
	}
	public String getHead_url() {
		return head_url;
	}
	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}
	public String getAudio_url() {
		return audio_url;
	}
	public void setAudio_url(String audio_url) {
		this.audio_url = audio_url;
	}
	public Integer getPerson_id() {
		return person_id;
	}
	public void setPerson_id(Integer person_id) {
		this.person_id = person_id;
	}
	public String getPerson_date() {
		return person_date;
	}
	public void setPerson_date(String person_date) {
		this.person_date = person_date;
	}
	public String getSecond_name() {
		return second_name;
	}
	public void setSecond_name(String second_name) {
		this.second_name = second_name;
	}
	public Integer getPerson_field() {
		return person_field;
	}
	public void setPerson_field(Integer person_field) {
		this.person_field = person_field;
	}

	@Override
	public boolean equals(Object obj) {
		Person person = (Person) obj;
		if(this.getPerson_id() == person.getPerson_id()){
			return true;
		}else {
			return false;
		}
	}

 	
}
