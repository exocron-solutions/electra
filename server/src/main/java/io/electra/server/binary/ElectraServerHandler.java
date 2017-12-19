/*
 * MIT License
 *
 * Copyright (c) 2017 Felix Klauke, JackWhite20
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.electra.server.binary;

import io.electra.common.server.Action;
import io.electra.core.DefaultDatabaseImpl;
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
        logger.info("New binary protocol connection from {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("{} disconnected", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        int length = byteBuf.readInt();

        if (length > 0) {
            byte actionValue = byteBuf.readByte();

            Action action = Action.of(actionValue);

            switch (action) {
                case GET:
                    int callbackId = byteBuf.readInt();
                    int keyHash = byteBuf.readInt();

                    byte[] bytes = ((DefaultDatabaseImpl) ElectraBinaryServer.getInstance().getDatabase()).get(keyHash);

                    ByteBuf buffer = ctx.alloc().buffer();
                    buffer.writeByte(0);
                    buffer.writeInt(callbackId);
                    // TODO: 03.12.2017 Cleaner
                    buffer.writeBytes((bytes != null) ? bytes : new byte[] {});

                    ctx.writeAndFlush(buffer).addListener(future -> buffer.release());
                    break;
                case PUT:
                    int putKeyHash = byteBuf.readInt();
                    byte[] putBytes = new byte[length - 5];
                    byteBuf.readBytes(putBytes);

                    ((DefaultDatabaseImpl) ElectraBinaryServer.getInstance().getDatabase()).save(putKeyHash, putBytes);
                    break;
                case REMOVE:
                    int removeKeyHash = byteBuf.readInt();

                    ((DefaultDatabaseImpl) ElectraBinaryServer.getInstance().getDatabase()).remove(removeKeyHash);
                    break;
                case UPDATE:
                    int updateKeyHash = byteBuf.readInt();
                    byte[] updateBytes = new byte[length - 5];
                    byteBuf.readBytes(updateBytes);

                    ((DefaultDatabaseImpl) ElectraBinaryServer.getInstance().getDatabase()).update(updateKeyHash, updateBytes);
                    break;
                case CREATE_STORAGE:
                    int createStorageNameLength = byteBuf.readInt();
                    // TODO: 16.12.2017 Limit size
                    byte[] createStorageNameBytes = new byte[createStorageNameLength];

                    // TODO: 16.12.2017 Create the actual storage
                    break;
                case DELETE_STORAGE:
                    int deleteStorageNameLength = byteBuf.readInt();
                    // TODO: 16.12.2017 Limit size
                    byte[] deleteStorageNameBytes = new byte[deleteStorageNameLength];

                    // TODO: 16.12.2017 Delete the actual storage
                    break;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
