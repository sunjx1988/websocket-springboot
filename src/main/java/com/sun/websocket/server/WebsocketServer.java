package com.sun.websocket.server;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author sunjx
 * @Date 2020-12-05 14:38
 * @Description
 **/
@ServerEndpoint("/ws/{token}")
@Component
public class WebsocketServer {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineNum = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token){
        sessionPools.put(token, session);
        addOnlineCount();
        System.out.println("token:" + token + ", 加入webSocket！当前人数为" + onlineNum);
        try {
            sendMessage(session, "欢迎token:" + token + ", 加入连接！");
        } catch (IOException e) {
            e.printStackTrace();
        }

        notifyOthers(token, "token:" + token + ", 上线");
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "token") String token){
        sessionPools.remove(token);
        subOnlineCount();
        System.out.println("token:" + token + ", 断开webSocket连接！当前人数为" + onlineNum);
        notifyOthers(token, "token:" + token + ", 下线");
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        if(StrUtil.isBlank(message)){
            return;

        //心跳检测
        }else if("ping".equals(message)){
            sendMessage(session, "pong");
            return;
        }

        MessageBody messageBody = JSONUtil.toBean(message, MessageBody.class);

        //没有去向用户
        if(StrUtil.isBlank(messageBody.getTotoken())){
            return;
        }

        messageBody.setToken(getToken(session));
        sendInfo(messageBody.getTotoken(), JSONUtil.toJsonStr(messageBody));
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    private static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }

    private static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

    private String getToken(Session session) {
        return sessionPools.entrySet()
                .parallelStream()
                .filter(stringSessionEntry -> stringSessionEntry.getValue().equals(session))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()).get(0);
    }

    //发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if(session != null){
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        }
    }

    //给指定用户发送信息
    public void sendInfo(String token, String message){
        Session session = sessionPools.get(token);
        try {
            sendMessage(session, message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //通知其他人
    private void notifyOthers(String token, String msg) {
        for (Map.Entry<String, Session> stringSessionEntry : sessionPools.entrySet()) {
            String tk = stringSessionEntry.getKey();
            Session ss = stringSessionEntry.getValue();

            if(token.equals(tk)){
                continue;
            }

            sendInfo(tk, msg);
        }
    }

    //全员广播
    private void notifyAll(String message) {
        for (Session session: sessionPools.values()) {
            try {
                sendMessage(session, message);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
