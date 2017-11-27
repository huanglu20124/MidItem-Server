package com.hl.domain;

public class GameRequest {
	//发给服务器的请求
	private String game_id;//游戏初始化的时候，game对象里的id
	private String my_id;//游戏初始化的时候，我的用户id
	private String opposite_id;//游戏初始化的时候，对面的用户id
	private Integer person_id;//要出的武将id
	private Integer code; //0:出一张武将的请求     1：结束游戏的请求  
	
	public Integer getPerson_id() {
		return person_id;
	}
	public void setPerson_id(Integer person_id) {
		this.person_id = person_id;
	}
	public String getGame_id() {
		return game_id;
	}
	public void setGame_id(String game_id) {
		this.game_id = game_id;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
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
