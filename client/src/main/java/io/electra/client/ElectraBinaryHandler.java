package io.electra.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.openhft.koloboke.collect.map.hash.HashIntObjMap;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class ElectraBinaryHandler extends SimpleChannelInboundHandler<ByteBuf> {

    static AtomicInteger callbackId = new AtomicInteger(0);

    private HashIntObjMap<Consumer<byte[]>> consumers = HashIntObjMaps.newMutableMap();

    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        int length = byteBuf.readInt();

        if (length > 0) {
            byte action = byteBuf.readByte();

            if (action == 0) {
                int id = byteBuf.readInt();

                Consumer<byte[]> consumer = consumers.get(id);
                if (consumer != null) {
                    byte[] bytes = new byte[length - 5];
                    byteBuf.readBytes(bytes);
                    consumer.accept(bytes);
                }
            }
        }
    }

    public void send(ByteBuf byteBuf, Consumer<byte[]> consumer, int callbackId) {
        if (consumer != null) {
            consumers.put(callbackId, consumer);
        }

        channel.writeAndFlush(byteBuf);
    }
}
