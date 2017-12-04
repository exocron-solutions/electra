package io.electra.client;

import java.util.function.Consumer;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public interface ElectraClient {

    void get(String key, Consumer<String> consumer);

    void get(byte[] keyBytes, Consumer<byte[]> consumer);

    void put(String key, String value);

    void put(byte[] key, byte[] value);

    void remove(String key);

    void remove(byte[] key);

    void remove(int keyHash);

    void disconnect();
}
