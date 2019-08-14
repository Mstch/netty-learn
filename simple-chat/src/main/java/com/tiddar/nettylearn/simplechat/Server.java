package com.tiddar.nettylearn.simplechat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.log4j.Log4j2;

/**
 * @author zhangweichen
 * @date 2019-08-06 10:56
 */
@Log4j2
public class Server {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
            new ServerBootstrap()
                    .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler())
                .childHandler(
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new ChatMessageDecoder()).addLast(new ChatHandler());
                            }
                        })
                .bind(1234)
                .addListener((ChannelFutureListener) future -> log.info("服务启动成功!"));
    }
}
