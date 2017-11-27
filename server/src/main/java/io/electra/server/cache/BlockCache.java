package io.electra.server.cache;

import io.electra.server.data.DataBlock;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

import java.util.concurrent.TimeUnit;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
public class BlockCache extends AbstractCache<Integer, DataBlock> {

    public BlockCache(long expire, TimeUnit timeUnit, int expectedSize) {
        super(HashIntObjMaps.newMutableMap(expectedSize), expire, timeUnit, expectedSize);
    }
}
