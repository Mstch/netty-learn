package com.tiddar.nettylearn.simplechat;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangweichen
 * @date 2019-08-11 16:37
 */
public class ChatData {
    public static AttributeKey<Boolean> ONLINE = AttributeKey.valueOf("online");
    public static AttributeKey<String> NAME = AttributeKey.valueOf("name");

    private static final ConcurrentHashMap<String, Channel> users = new ConcurrentHashMap<>();
    /**
     * key: sender name_receiver name, value:[sender name,content] sort by time
     */
    private static final ConcurrentHashMap<String, List<String[]>> chatHistory = new ConcurrentHashMap<>();

    /**
     * @param name
     * @param channel
     * @return 是否可以使用此用户名登录，判据为此用户名目前无client正在登录
     */
    public static boolean login(String name, Channel channel) {
        if (StringUtils.isEmpty(name) || name.contains("_")) {
            return false;
        }
        synchronized (users){
            if(!(users.containsKey(name)) || !(users.get(name).hasAttr(ONLINE))){
                channel.attr(ONLINE).set(true);
                users.put(name,channel);
                return true;
            }
        }
        return false;
    }

    public static void save(String sender, String receiver, String content) {
        String key = sender + "_" + sender;
        if (sender.compareTo(receiver) > 0) {
            key = receiver + "_" + sender;
        }
        chatHistory.merge(key, Collections.singletonList(new String[]{sender, content}), (oldValue, newValue) -> {
            oldValue.add(newValue.get(0));
            return oldValue;
        });
    }

    public static Set<String> listUser() {
        return users.keySet();
    }

    public static List<String[]> listChatHistory(String loginUser, String anotherUser) {
        if (loginUser.compareTo(anotherUser) < 0) {
            return chatHistory.get(loginUser + "_" + anotherUser);
        }
        return chatHistory.get(anotherUser + "_" + loginUser);
    }
}
