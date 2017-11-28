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

package io.electra.client;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class ElectraClientTest {

    public static void main(String[] args) throws Exception {
        //HttpClient httpClient = HttpClients.createDefault();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        HttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

        //HttpGet get = new HttpGet("http://localhost:8080/database/put/test/awesomeness");
        //httpClient.execute(get);

        System.out.println("Testing get");

        HttpGet get = new HttpGet("http://localhost:8080/database/get/test");
        //String execute = httpClient.execute(get, httpResponse -> EntityUtils.toString(httpResponse.getEntity()));
        //System.out.println("Response: " + execute);

        int n = 100000;
        long time = System.nanoTime();
        for (int i = 0; i < n; i++) {
            httpClient.execute(get, httpResponse -> EntityUtils.toString(httpResponse.getEntity()));
        }
        long t = (System.nanoTime() - time);
        System.out.println(n + " get took: " + TimeUnit.NANOSECONDS.toMillis(t) + "ms (average " + (t / n) + "ns)");

        System.out.println("Testing put");

        time = System.nanoTime();
        for (int i = 0; i < n; i++) {
            HttpGet put = new HttpGet("http://localhost:8080/database/put/key" + i + "/value" + i);
            httpClient.execute(put);
        }
        t = (System.nanoTime() - time);
        System.out.println(n + " put took: " + TimeUnit.NANOSECONDS.toMillis(t) + "ms (average " + (t / n) + "ns)");

        System.out.println("Testing remove");

        time = System.nanoTime();
        for (int i = 0; i < n; i++) {
            HttpDelete delete = new HttpDelete("http://localhost:8080/database/remove/key" + i);
            httpClient.execute(delete);
        }
        t = (System.nanoTime() - time);
        System.out.println(n + " remove took: " + TimeUnit.NANOSECONDS.toMillis(t) + "ms (average " + (t / n) + "ns)");
    }
}
