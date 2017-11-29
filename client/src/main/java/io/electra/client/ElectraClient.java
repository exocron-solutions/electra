package io.electra.client;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public interface ElectraClient {

    void get(String key);

    void get(byte[] keyBytes);

    void put(String key, String value);

    void put(byte[] key, byte[] value);

    void disconnect();
}
