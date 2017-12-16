package io.electra.client;

import io.electra.common.server.Action;
import io.electra.common.server.ElectraChannelInitializer;
import io.electra.common.server.ElectraThreadFactory;
import io.electra.common.server.PipelineUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.apache.commons.codec.Charsets;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class DefaultElectraClient implements ElectraClient {

    private String host;

    private int port;

    private Channel channel;

    private ElectraBinaryHandler electraBinaryHandler;

    public DefaultElectraClient(String host, int port) {
        this.host = host;
        this.port = port;

        connect();
    }

    private void connect() {
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(PipelineUtils.newEventLoopGroup(2, new ElectraThreadFactory("Electra Client Thread")))
                    .channel(PipelineUtils.getChannel())
                    .handler(new ElectraChannelInitializer(electraBinaryHandler = new ElectraBinaryHandler()));

            channel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void get(String key, Consumer<String> consumer) {
        int keyHash = Arrays.hashCode(key.getBytes(Charsets.UTF_8));

        int callbackId = ElectraBinaryHandler.callbackId.incrementAndGet();

        ByteBuf byteBuf = Unpooled.buffer().writeByte(Action.GET.getValue()).writeInt(callbackId).writeInt(keyHash);

        electraBinaryHandler.send(byteBuf, bytes -> consumer.accept(new String(bytes, Charsets.UTF_8)), callbackId);
    }

    @Override
    public void get(byte[] keyBytes, Consumer<byte[]> consumer) {
        int keyHash = Arrays.hashCode(keyBytes);

        int callbackId = ElectraBinaryHandler.callbackId.incrementAndGet();

        ByteBuf byteBuf = Unpooled.buffer().writeByte(Action.GET.getValue()).writeInt(callbackId).writeInt(keyHash);

        electraBinaryHandler.send(byteBuf, consumer, callbackId);
    }

    @Override
    public void put(String key, String value) {
        put(key.getBytes(Charsets.UTF_8), value.getBytes(Charsets.UTF_8));
    }

    @Override
    public void put(byte[] key, byte[] value) {
        int keyHash = Arrays.hashCode(key);

        ByteBuf byteBuf = Unpooled.buffer().writeByte(Action.PUT.getValue()).writeInt(keyHash).writeBytes(value);

        electraBinaryHandler.send(byteBuf, null, -1);
    }

    @Override
    public void remove(String key) {
        remove(key.getBytes(Charsets.UTF_8));
    }

    @Override
    public void remove(byte[] key) {
        remove(Arrays.hashCode(key));
    }

    @Override
    public void remove(int keyHash) {
        ByteBuf byteBuf = Unpooled.buffer().writeByte(Action.REMOVE.getValue()).writeInt(keyHash);
      
        electraBinaryHandler.send(byteBuf, null, -1);
    }

    @Override
    public void update(String key, byte[] newValue) {
        update(key.getBytes(Charsets.UTF_8), newValue);
    }

    @Override
    public void update(byte[] key, byte[] newValue) {
        update(Arrays.hashCode(key), newValue);
    }

    @Override
    public void update(int keyHash, byte[] newValue) {
        ByteBuf byteBuf = Unpooled.buffer().writeByte(Action.UPDATE.getValue()).writeInt(keyHash);

        electraBinaryHandler.send(byteBuf, null, -1);
    }

    @Override
    public void createStorage(String name) {
        // TODO: 16.12.2017 Limit size
        byte[] nameBytes = name.getBytes(Charsets.UTF_8);

        ByteBuf byteBuf = Unpooled.buffer().writeByte(Action.CREATE_STORAGE.getValue()).writeInt(nameBytes.length).writeBytes(nameBytes);

        electraBinaryHandler.send(byteBuf, null, -1);
    }

    @Override
    public void deleteStorage(String name) {
        // TODO: 16.12.2017 Limit size
        byte[] nameBytes = name.getBytes(Charsets.UTF_8);

        ByteBuf byteBuf = Unpooled.buffer().writeByte(Action.DELETE_STORAGE.getValue()).writeInt(nameBytes.length).writeBytes(nameBytes);

        electraBinaryHandler.send(byteBuf, null, -1);
    }

    @Override
    public void disconnect() {
        channel.close();
    }
}
