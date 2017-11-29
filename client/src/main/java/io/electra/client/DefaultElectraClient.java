package io.electra.client;

import io.electra.common.server.ElectraChannelInitializer;
import io.electra.common.server.ElectraThreadFactory;
import io.electra.common.server.PipelineUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

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
                    .channel(NioSocketChannel.class)
                    .handler(new ElectraChannelInitializer(electraBinaryHandler = new ElectraBinaryHandler()));

            channel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void get(String key) {

    }

    @Override
    public void get(byte[] keyBytes) {

    }

    @Override
    public void put(String key, String value) {

    }

    @Override
    public void put(byte[] key, byte[] value) {

    }

    @Override
    public void disconnect() {
        channel.close();
    }
}
