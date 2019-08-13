package com.tiddar.nettylearn.simplechat;

/**
 * @author zhangweichen
 * @date 2019-08-11 13:29
 */
class ChatMessage {
    Command cmd;
    String[] args;

    ChatMessage(Command cmd, String[] args) {
        this.cmd = cmd;
        this.args = args;
    }
}
