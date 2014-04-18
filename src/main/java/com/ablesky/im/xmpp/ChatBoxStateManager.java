package com.ablesky.im.xmpp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地存储用户聊天框状态
 * 
 * @author kbian
 */
public class ChatBoxStateManager {
	public final static ChatBoxStateManager cbsm = new ChatBoxStateManager();

	private final Map<String, ChatBoxState> stateCenter = new ConcurrentHashMap<String, ChatBoxState>();

	public static ChatBoxStateManager getInstance() {
		return cbsm;
	}

	public void saveOrUpdateChatBoxState(String uname, ChatBoxState state) {
		stateCenter.put(uname, state);
	}

	public ChatBoxState getChatBoxState(String uname) {
		return stateCenter.get(uname);
	}

	public void removeChatBoxState(String uname) {
		stateCenter.remove(uname);
	}
}
