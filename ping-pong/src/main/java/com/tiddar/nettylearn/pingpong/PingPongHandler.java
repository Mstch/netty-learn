package com.tiddar.nettylearn.pingpong;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhangweichen
 * @date 2019-08-05 18:22
 */
@Log4j2
public class PingPongHandler extends SimpleChannelInboundHandler {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
    ByteBuf buf = ctx.alloc().buffer();
    String res =
        StringUtils.join(
            StringUtils.chomp(((ByteBuf) msg).toString(CharsetUtil.UTF_8)), "_", "pong\n\r");
    buf.writeCharSequence(res, CharsetUtil.UTF_8);
    ctx.writeAndFlush(buf)
        .addListener(
            (ChannelFutureListener)
                future -> {
                  log.info("发送消息:{}", res);
                  ctx.close();
                });
  }
}
