package io.electra.client;

import io.netty.channel.Channel;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class DefaultElectraClient implements ElectraClient {

    private String host;

    private int port;

    private Channel channel;

    public DefaultElectraClient(String host, int port) {
        this.host = host;
        this.port = port;

        connect();
    }

    private void connect() {

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

    }
}
