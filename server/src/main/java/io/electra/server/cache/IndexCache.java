package io.electra.server.cache;

import io.electra.server.index.Index;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

import java.util.concurrent.TimeUnit;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
public class IndexCache extends AbstractCache<Integer, Index> {

    public IndexCache(long expire, TimeUnit timeUnit, int expectedSize) {
        super(HashIntObjMaps.newMutableMap(expectedSize), expire, timeUnit, expectedSize);
    }

    public IndexCache(int expectedSize) {
        this(-1, null, expectedSize);
    }
}
