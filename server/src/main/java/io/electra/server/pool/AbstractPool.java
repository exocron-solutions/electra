/*
 * MIT License
 *
 * Copyright (c) 2017 Felix Klauke, JackWhite20
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.electra.server.pool;

import com.google.common.collect.Queues;

import java.util.Queue;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public abstract class AbstractPool<PooledType> implements Pool<PooledType> {

    private final Queue<PooledType> pooledInstances;

    public AbstractPool() {
        this(Queues.newLinkedBlockingQueue());
    }

    public AbstractPool(Queue<PooledType> pooledInstances) {
        this.pooledInstances = pooledInstances;
    }

    @Override
    public PooledType acquire() {
        synchronized (pooledInstances) {
            if (pooledInstances.isEmpty()) {
                pooledInstances.add(createInstance());
            }

            return pooledInstances.poll();
        }
    }

    abstract PooledType createInstance();

    @Override
    public void clear() {
        synchronized (pooledInstances) {
            pooledInstances.clear();
        }
    }

    void release(PooledType pooled) {
        synchronized (pooledInstances) {
            pooledInstances.add(pooled);
        }
    }
}
