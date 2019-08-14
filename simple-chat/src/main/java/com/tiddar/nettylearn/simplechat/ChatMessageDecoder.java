package com.tiddar.nettylearn.simplechat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangweichen
 * @date 2019-08-11 13:45
 */
public class ChatMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

  private static final Map<String, Command> COMMAND_MAP = new HashMap<>();

  static {
    COMMAND_MAP.put("LIST", Command.LIST);
    COMMAND_MAP.put("LOGIN", Command.LOGIN);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    String msg = StringUtils.chomp((in).toString(CharsetUtil.UTF_8)).toUpperCase();
    String[] msgSrc = msg.split(" ");
    if (msgSrc.length > 0 && COMMAND_MAP.containsKey(msgSrc[0])) {
      Command command = COMMAND_MAP.get(msgSrc[0]);
      if (command == Command.LIST) {
        out.add(new ChatMessage(command, null));
      } else {
        out.add(new ChatMessage(command, ArrayUtils.subarray(msgSrc, 1, msgSrc.length)));
      }
    } else {
      ByteBuf buf = ctx.alloc().buffer();
      buf.writeCharSequence("消息解析失败!\n\r", CharsetUtil.UTF_8);
      ctx.writeAndFlush(buf);
    }
  }
}
