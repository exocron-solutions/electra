package io.electra.server.binary;

import io.electra.server.DefaultDatabaseImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class ElectraServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static Logger logger = LoggerFactory.getLogger(ElectraServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New connection");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        int length = byteBuf.readInt();

        byte action = byteBuf.readByte();

        // GET
        if (action == 0) {
            int callbackId = byteBuf.readInt();
            int keyHash = byteBuf.readInt();

            byte[] bytes = ((DefaultDatabaseImpl) ElectraBinaryServer.getInstance().getDatabase()).get(keyHash);

            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeByte(0);
            buffer.writeInt(callbackId);
            buffer.writeBytes(bytes);

            ctx.writeAndFlush(buffer).addListener(future -> buffer.release());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
