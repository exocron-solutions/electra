package io.electra.client;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class ElectraClientFactory {

    public static ElectraClient create(String host, int port) {
        return new DefaultElectraClient(host, port);
    }
}
