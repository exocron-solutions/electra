package io.electra.server.cache;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
public interface Cache<Key, Value> {

    void put(Key key, Value value);

    Value get(Key key);

    void invalidate(Key key);

    int size();

    void clear();
}
