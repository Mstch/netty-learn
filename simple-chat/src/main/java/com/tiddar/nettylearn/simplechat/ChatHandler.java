package com.tiddar.nettylearn.simplechat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangweichen
 * @date 2019-08-06 11:16
 */
@Log4j2
public class ChatHandler extends SimpleChannelInboundHandler<ChatMessage> {
    public static ReentrantLock loginLock = new ReentrantLock();

    private boolean check(ChatMessage message) {
        switch (message.cmd) {
            case LOGIN:
                if (message.args.length > 0) {
                    return StringUtils.isAlphanumeric(message.args[0]);
                }
                return false;
            default:
                return true;
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        if (!check(msg)) {
            ctxWrite(ctx, buf, "参数错误");
            return;
        }
        switch (msg.cmd) {
            case LIST:
                ctxWrite(ctx, buf, ChatData.listUser().toString());
                break;
            case LOGIN:
                String name = msg.args[0];
                loginLock.lock();
                try {
                    if (ChatData.login(name, ctx.channel())) {
                        ctxWrite(ctx, buf, "登录成功,您的用户名" + name);
                    } else {
                        ctxWrite(ctx, buf, "登录失败,用户名已存在");
                    }
                } catch (Exception e) {
                    ctxWrite(ctx, buf, "登录失败");
                } finally {
                    loginLock.unlock();
                }
                break;
            default:
                break;

        }
    }

    public static void ctxWrite(ChannelHandlerContext ctx, ByteBuf buf, String msg) {
        buf.writeCharSequence(msg + "\n\r", CharsetUtil.UTF_8);
        ctx.writeAndFlush(buf);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("新链接来啦!{}", ctx.channel().remoteAddress());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("与{}的链接断开", ctx.channel().remoteAddress());
    }
}
