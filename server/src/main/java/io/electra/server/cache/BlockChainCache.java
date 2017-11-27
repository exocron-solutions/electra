package io.electra.server.cache;

import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

import java.util.concurrent.TimeUnit;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
public class BlockChainCache extends AbstractCache<Integer, Integer> {

    public BlockChainCache(long expire, TimeUnit timeUnit, int expectedSize) {
        super(HashIntObjMaps.newMutableMap(expectedSize), expire, timeUnit, expectedSize);
    }

    public BlockChainCache(int expectedSize) {
        this(-1, null, expectedSize);
    }
}
