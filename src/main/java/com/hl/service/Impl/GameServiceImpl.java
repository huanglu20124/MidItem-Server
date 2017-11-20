package com.hl.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.hl.dao.PersonDao;
import com.hl.dao.RedisDao;
import com.hl.domain.Game;
import com.hl.domain.GameRequest;
import com.hl.domain.GameResponse;
import com.hl.domain.Person;
import com.hl.service.GameService;
import com.hl.websocket.SystemWebSocketHandler;
import com.sun.research.ws.wadl.Response;

@Service("gameService")
public class GameServiceImpl implements GameService {
	
	@Resource(name="systemWebSocketHandler")
	private SystemWebSocketHandler webSocketHandler;

	@Resource(name="redisDao")
	private RedisDao redisDao;
	
	@Resource(name="personDao")
	private PersonDao personDao;

	@Override
	public Game beginGame(String user_id1, String user_id2) {
		Game game = new Game();
		String game_id = UUID.randomUUID().toString();
		game.setGame_id(game_id);
		System.out.println("生成的的game_id=" + game_id);
		//分配武将
		Map<Integer, Person>map = new HashMap<>();
		//得到5个武力值队列
		List<Integer>person_id_list1 = getIntegerList(redisDao.getRangeId("ability1"));
		List<Integer>person_id_list2 = getIntegerList(redisDao.getRangeId("ability2"));
		List<Integer>person_id_list3 = getIntegerList(redisDao.getRangeId("ability3"));
		List<Integer>person_id_list4 = getIntegerList(redisDao.getRangeId("ability4"));
		List<Integer>person_id_list5 = getIntegerList(redisDao.getRangeId("ability5"));
		Person person1 = getListRandomPerson(person_id_list1);
		Person person2 = getListRandomPerson(person_id_list2);
		Person person3 = getListRandomPerson(person_id_list3);
		Person person4 = getListRandomPerson(person_id_list4);
		Person person5 = getListRandomPerson(person_id_list5);
		map.put(person1.getPerson_id(), person1);
		map.put(person2.getPerson_id(), person2);
	    map.put(person3.getPerson_id(), person3);
		map.put(person4.getPerson_id(), person4);
	    map.put(person5.getPerson_id(), person5);
		game.setPerson_map(map);
		//设计先走的人
		Integer first = new Random().nextInt(2)+1;
		game.setFirst(first);
		System.out.println(first+"先手");
		//发送给对应用户
		GameResponse response = new GameResponse();
		response.setCode(0);
		response.setGame(game);
		
		game.setMy_id(user_id1);
		game.setOpposite_id(user_id2);
		webSocketHandler.sendToUserById(user_id1, JSON.toJSONString(response));
		game.setMy_id(user_id2);
		game.setOpposite_id(user_id1);
		webSocketHandler.sendToUserById(user_id2, JSON.toJSONString(response));
		return game;
	}
	
	private List<Integer> getIntegerList(List<Object>list){
		List<Integer> list_temp = new ArrayList<>();
		for(int i = 0; i < list.size(); i++){
			Object object = list.get(i);
			list_temp.add(new Integer((String)object));
		}
		return list_temp;
	}
	
	private Person getListRandomPerson(List<Integer>person_id_list){
		Integer ran_num = new Random().nextInt(person_id_list.size());
		Person person = personDao.getPersonById(person_id_list.get(ran_num));
		return person;
	}
	
	@Override
	public void doGame(GameRequest request,Game game) {
		//游戏过程的执行，收到用户发来的请求
		Integer code = request.getCode();
		Integer person_id = request.getPerson_id();
		GameResponse opposite_response = new GameResponse();
		opposite_response.setPerson_id(person_id);
		GameResponse my_response = new GameResponse();
		
		if(code == 0){
			Person prePerson = game.getPrePerson();
			if(prePerson != null){
				//开始pk
				Person myPerson = personDao.getPersonById(person_id);
				System.out.println("开始pk，两个武将分别是：prePerson"+prePerson.getPerson_id()
				+ "  myPerson" + myPerson.getPerson_id());
				Integer isWin = 0; //0,1,2 分别代表平局，对方赢，我赢
				//判断相克关系
				if(prePerson.getPerson_field() == (myPerson.getPerson_field() + 1) 
						|| (prePerson.getPerson_field()==5 && myPerson.getPerson_field() ==1)){
					//那对方赢了
					isWin = 1;
				}else if(myPerson.getPerson_field() == (prePerson.getPerson_field() + 1)
						|| (myPerson.getPerson_field()==5 && prePerson.getPerson_field() ==1)){
					isWin = 2;
				}else {
					//进行武力值判断
					if(prePerson.getAbility() > myPerson.getAbility()){
						isWin = 1;
					}else if(prePerson.getAbility() == myPerson.getAbility()){
						isWin = 0;
					}else {
						isWin = 2;
					}
				}
				
				//发送结果
				if(isWin == 1){
					//我输了
					opposite_response.setCode(2);
					my_response.setCode(1);
					System.out.println(request.getMy_id()+"输了");
				}else if(isWin == 2){
					//我赢了
					opposite_response.setCode(1);
					my_response.setCode(2);
					System.out.println(request.getMy_id()+"赢了");
				}else {
					//平局
					opposite_response.setCode(5);
					my_response.setCode(5);
					System.out.println(request.getMy_id()+"平局");
				}
				
				webSocketHandler.sendToUserById(request.getOpposite_id().toString(), 
						JSON.toJSONString(opposite_response));
				webSocketHandler.sendToUserById(request.getMy_id().toString(), 
						JSON.toJSONString(my_response));
				//最后清空preperson
				game.setPrePerson(null);
			}else {
				//否则该武将等待
				prePerson = personDao.getPersonById(person_id);
				System.out.println("prePerson为" + prePerson.getPerson_id());
				game.setPrePerson(prePerson);
				System.out.println("武将"+person_id+"等待");
			}
		}		
	}

	@Override
	public void killGame(String id, HashMap<String, Game> games) {
		//谁先结束连接，谁就要通知另外一方
		GameResponse response = new GameResponse();
		for(String key : games.keySet()){
			Game game = games.get(key);
			//则要通知另外一方
			if(game.getMy_id().equals(id)){
				response.setCode(3);
				webSocketHandler.sendToUserById(game.getOpposite_id(), JSON.toJSONString(response));
				System.out.println("发送结束游戏通知");
				games.remove(game.getGame_id());
				break;
			}
			else if(game.getOpposite_id().equals(id)){
				response.setCode(3);
				webSocketHandler.sendToUserById(game.getMy_id(), JSON.toJSONString(response));
				System.out.println("发送结束游戏通知");
				games.remove(game.getGame_id());
				break;
			}
		}
		
	}


	 
}
