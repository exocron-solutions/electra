package io.electra.server.cache;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
public class CacheEntry<Value> {

    private long expireBy;

    private Value value;

    CacheEntry(long expireBy, Value value) {
        this.expireBy = expireBy;
        this.value = value;
    }

    long getExpireBy() {
        return expireBy;
    }

    public Value getValue() {
        return value;
    }
}
