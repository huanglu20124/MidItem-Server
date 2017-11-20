package com.hl.domain;

public class GameResponse {
	//服务器返回的响应类
	private Integer code; //0:游戏开始      1：你的武将输了       2：你的武将赢了        3:对方掉线了或者结束了游戏    4:游戏未开始，请等待       5：（补充）武将平局
	private Integer person_id;//对方所出的武将id
	private Game game; //只有code=0的时候，才会接收到game；
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Integer getPerson_id() {
		return person_id;
	}

	public void setPerson_id(Integer person_id) {
		this.person_id = person_id;
	}
	
	
	
}
