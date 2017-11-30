package io.electra.common.server;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class ElectraChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ChannelHandler channelHandler;

    public ElectraChannelInitializer(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    protected void initChannel(SocketChannel ch) throws Exception {
        try {
            ch.config().setOption(ChannelOption.IP_TOS, 0x18);
        } catch (ChannelException e) {
            // IP_TOS is not supported (Windows XP / Windows Server 2003)
        }
        ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
        ch.config().setOption(ChannelOption.TCP_NODELAY, true);

        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Short.MAX_VALUE, 0, 4));

        ch.pipeline().addLast(new LengthFieldPrepender(4));

        ch.pipeline().addLast(channelHandler);
    }
}
