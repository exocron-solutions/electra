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

package io.electra.core.factory;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ThreadFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test the behaviour of the {@link ElectraThreadFactory}.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class ElectraThreadFactoryTest {

    /**
     * The test prefix. As we start counting the thread ids with '1', the result should be:
     * <p>
     * PREFIX + 1
     */
    private static final String PREFIX = "TestPrefix #";

    /**
     * The tested thread factory.
     */
    private ThreadFactory threadFactory;

    @Before
    public void setUp() throws Exception {
        threadFactory = new ElectraThreadFactory(PREFIX);
    }

    @Test
    public void testNewThread() throws Exception {
        Runnable runnable = () -> {
        };

        Thread thread = threadFactory.newThread(runnable);

        assertNotNull(thread);
        assertEquals(PREFIX + 1, thread.getName());
    }
}