package com.sun.websocket.server;

/**
 * @Author sunjx
 * @Date 2020-12-05 15:39
 * @Description
 **/
public class MessageBody {

    private String token;
    private String totoken;
    private String msg;

    public MessageBody(String token, String totoken, String msg) {
        this.token = token;
        this.totoken = totoken;
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTotoken() {
        return totoken;
    }

    public void setTotoken(String totoken) {
        this.totoken = totoken;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
