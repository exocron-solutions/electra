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

package io.electra.core;

import org.json.JSONObject;

import java.nio.file.Path;

/**
 * The basic interface showing what our database will be capable of.
 *
 * To create instances of this class you should take a look at {@link DatabaseFactory#createDatabase(Path, Path)}. The
 * default implementation for this class can be found at {@link DefaultDatabaseImpl}.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public interface Database {

    /**
     * Save the given byte array as data for the given key.
     *
     * @param key   The key.
     * @param bytes The value.
     */
    void save(String key, byte[] bytes);

    /**
     * Save the given json object under the given key.
     *
     * @param key        The key.
     * @param jsonObject The value.
     */
    void save(String key, JSONObject jsonObject);

    /**
     * Save the given value for the given key. Will implicitly convert the value to a byte array and store it
     * via {@link #save(String, byte[])}.
     *
     * @param key The key.
     * @param value The value.
     */
    void save(String key, String value);

    /**
     * Update the data under the given key.
     *
     * @param key The key.
     * @param value The value.
     */
    void update(String key, byte[] value);

    /**
     * Update the data under given key with the given delta.
     *
     * @param key        The key.
     * @param jsonObject The json object.
     */
    void update(String key, JSONObject jsonObject);

    /**
     * Query for the given key.
     *
     * @param key The key.
     *
     * @return The value or null.
     */
    byte[] get(String key);

    /**
     * Get the json object under the given key.
     *
     * @param key The key.
     * @return The json object.
     */
    JSONObject getJson(String key);

    /**
     * Delete the value for the given key.
     *
     * @param key The key.
     */
    void remove(String key);

    /**
     * Close all resources.
     */
    void close();
}
