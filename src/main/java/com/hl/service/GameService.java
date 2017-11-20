package com.hl.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.socket.WebSocketSession;

import com.hl.domain.Game;
import com.hl.domain.GameRequest;

public interface GameService {

	Game beginGame(String wait_id, String id);

	void doGame(GameRequest request,Game game);

	void killGame(String id, HashMap<String, Game> games);


}
