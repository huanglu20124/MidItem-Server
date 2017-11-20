package com.hl.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.websocket.Session;

import org.apache.hadoop.mapred.gethistory_jsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.hl.dao.RedisDao;
import com.hl.domain.Game;
import com.hl.domain.GameRequest;
import com.hl.domain.GameResponse;
import com.hl.domain.Person;
import com.hl.service.GameService;

@Component("systemWebSocketHandler")
public class SystemWebSocketHandler implements WebSocketHandler {
		
	@Resource(name = "redisDao")
	private RedisDao redisDao;
	
	private Logger log = LoggerFactory.getLogger(SystemWebSocketHandler.class);
	
    private static final ArrayList<WebSocketSession> users = new ArrayList<WebSocketSession>();
 
    @Resource(name="gameService")
    private GameService gameService;

    private static final HashMap<String, Game>games = new HashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	System.out.println("通过websocket与web前端建立连接,id为" + session.getId());
    	users.add(session); //把当前会话添加到用户列表里
    	//判断当前是否有用户等待，有的话直接开打
    	String wait_id =  (String) redisDao.getValue("wait_id");
    	System.out.println("wait_id=" + wait_id);
    	if(wait_id != null){
    		//删除key
    		redisDao.deleteKey("wait_id");
    		System.out.println(wait_id + "移出等待");
    		Game game = gameService.beginGame(wait_id,session.getId());
    		//加入到游戏集合中
    		games.put(game.getGame_id(), game);
    	}else {
    		//没有的话自己就是wait_id,告诉用户等待
    		redisDao.addKey("wait_id", session.getId());
    		System.out.println(session.getId() + "加入等待");
    		GameResponse response = new GameResponse();
    		response.setCode(4);
    		session.sendMessage(new TextMessage(JSON.toJSONString(response)));
		}
    }
   
    //接收消息，（可选）返回消息
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    	System.out.println("接收到消息" + message.getPayload());
    	GameRequest request = null;
    	try {
			request = JSON.parseObject(message.getPayload().toString(), GameRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if(request != null){
    		//得到game对象传入
    		String game_id = request.getGame_id();
    		System.out.println("收到的game_id=" + game_id);
    		Game game = null;
    		if(game_id != null && (game = (Game) games.get(game_id)) != null){
    			System.out.println("得到游戏对象");
    			//结束游戏的请求在外面处理
    			if(request.getCode() == 1){
    				gameService.killGame(request.getMy_id().toString(), games);
    			}else {
        			gameService.doGame(request,game);
				}
    		}
    	}
    }
 
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
            System.out.println("传输异常，websocket会话关闭");
        }
        users.remove(session);
        log.debug("handleTransportError" + exception.getMessage());
    }
 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    	System.out.println(session.getId() + "会话关闭");
        //删除wait的key
    	String wait_id = (String) redisDao.getValue("wait_id");
    	if(wait_id!=null && wait_id.equals(session.getId())){
    		redisDao.deleteKey("wait_id");
    	}
    	//有一个会话关闭后，需要检查是否需要通知另外一个用户
    	gameService.killGame(session.getId(),games);
    	users.remove(session);
    }
 
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
 
    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
    	
    }
    
    public boolean sendToUserById(String id,String msg){
    	for(WebSocketSession user : users){
    		if(user.getId().equals(id)){
    			try {
					user.sendMessage(new TextMessage(msg));
					System.out.println("给"+id+"的消息成功发送");
				} catch (IOException e) {
					System.out.println("发送消息出错！");
					e.printStackTrace();
					return false;
				}
    		}
    	}
    	return true;
    }
    
    
 
}
