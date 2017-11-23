package io.electra.server.pool;

import java.nio.*;

/**
 * Created by JackWhite20 on 23.11.2017.
 */
public class PooledByteBuffer {

    private ByteBuffer byteBuffer;

    boolean used;

    private PooledByteBuffer(ByteBuffer byteBuffer, boolean used) {
        this.byteBuffer = byteBuffer;
        this.used = used;
    }

    PooledByteBuffer(ByteBuffer byteBuffer) {
        this(byteBuffer, false);
    }

    public ByteBuffer nio() {
        return byteBuffer;
    }

    boolean isUsed() {
        return used;
    }

    public void release() {
        ByteBufferPool.release(this);
    }

    public ByteBuffer slice() {
        return byteBuffer.slice();
    }

    public ByteBuffer duplicate() {
        return byteBuffer.duplicate();
    }

    public ByteBuffer asReadOnlyBuffer() {
        return byteBuffer.asReadOnlyBuffer();
    }

    public byte get() {
        return byteBuffer.get();
    }

    public ByteBuffer put(byte b) {
        return byteBuffer.put(b);
    }

    public byte get(int index) {
        return byteBuffer.get(index);
    }

    public ByteBuffer put(int index, byte b) {
        return byteBuffer.put(index, b);
    }

    public ByteBuffer get(byte[] dst, int offset, int length) {
        return byteBuffer.get(dst, offset, length);
    }

    public ByteBuffer get(byte[] dst) {
        return byteBuffer.get(dst);
    }

    public ByteBuffer put(ByteBuffer src) {
        return byteBuffer.put(src);
    }

    public ByteBuffer put(byte[] src, int offset, int length) {
        return byteBuffer.put(src, offset, length);
    }

    public ByteBuffer put(byte[] src) {
        return byteBuffer.put(src);
    }

    public boolean hasArray() {
        return byteBuffer.hasArray();
    }

    public byte[] array() {
        return byteBuffer.array();
    }

    public int arrayOffset() {
        return byteBuffer.arrayOffset();
    }

    public ByteBuffer compact() {
        return byteBuffer.compact();
    }

    public boolean isDirect() {
        return byteBuffer.isDirect();
    }

    public int compareTo(ByteBuffer that) {
        return byteBuffer.compareTo(that);
    }

    public ByteOrder order() {
        return byteBuffer.order();
    }

    public ByteBuffer order(ByteOrder bo) {
        return byteBuffer.order(bo);
    }

    public char getChar() {
        return byteBuffer.getChar();
    }

    public ByteBuffer putChar(char value) {
        return byteBuffer.putChar(value);
    }

    public char getChar(int index) {
        return byteBuffer.getChar(index);
    }

    public ByteBuffer putChar(int index, char value) {
        return byteBuffer.putChar(index, value);
    }

    public CharBuffer asCharBuffer() {
        return byteBuffer.asCharBuffer();
    }

    public short getShort() {
        return byteBuffer.getShort();
    }

    public ByteBuffer putShort(short value) {
        return byteBuffer.putShort(value);
    }

    public short getShort(int index) {
        return byteBuffer.getShort(index);
    }

    public ByteBuffer putShort(int index, short value) {
        return byteBuffer.putShort(index, value);
    }

    public ShortBuffer asShortBuffer() {
        return byteBuffer.asShortBuffer();
    }

    public int getInt() {
        return byteBuffer.getInt();
    }

    public ByteBuffer putInt(int value) {
        return byteBuffer.putInt(value);
    }

    public int getInt(int index) {
        return byteBuffer.getInt(index);
    }

    public ByteBuffer putInt(int index, int value) {
        return byteBuffer.putInt(index, value);
    }

    public IntBuffer asIntBuffer() {
        return byteBuffer.asIntBuffer();
    }

    public long getLong() {
        return byteBuffer.getLong();
    }

    public ByteBuffer putLong(long value) {
        return byteBuffer.putLong(value);
    }

    public long getLong(int index) {
        return byteBuffer.getLong(index);
    }

    public ByteBuffer putLong(int index, long value) {
        return byteBuffer.putLong(index, value);
    }

    public LongBuffer asLongBuffer() {
        return byteBuffer.asLongBuffer();
    }

    public float getFloat() {
        return byteBuffer.getFloat();
    }

    public ByteBuffer putFloat(float value) {
        return byteBuffer.putFloat(value);
    }

    public float getFloat(int index) {
        return byteBuffer.getFloat(index);
    }

    public ByteBuffer putFloat(int index, float value) {
        return byteBuffer.putFloat(index, value);
    }

    public FloatBuffer asFloatBuffer() {
        return byteBuffer.asFloatBuffer();
    }

    public double getDouble() {
        return byteBuffer.getDouble();
    }

    public ByteBuffer putDouble(double value) {
        return byteBuffer.putDouble(value);
    }

    public double getDouble(int index) {
        return byteBuffer.getDouble(index);
    }

    public ByteBuffer putDouble(int index, double value) {
        return byteBuffer.putDouble(index, value);
    }

    public DoubleBuffer asDoubleBuffer() {
        return byteBuffer.asDoubleBuffer();
    }

    public int capacity() {
        return byteBuffer.capacity();
    }

    public int position() {
        return byteBuffer.position();
    }

    public Buffer position(int newPosition) {
        return byteBuffer.position(newPosition);
    }

    public int limit() {
        return byteBuffer.limit();
    }

    public Buffer limit(int newLimit) {
        return byteBuffer.limit(newLimit);
    }

    public Buffer mark() {
        return byteBuffer.mark();
    }

    public Buffer reset() {
        return byteBuffer.reset();
    }

    public Buffer clear() {
        return byteBuffer.clear();
    }

    public Buffer flip() {
        return byteBuffer.flip();
    }

    public Buffer rewind() {
        return byteBuffer.rewind();
    }

    public int remaining() {
        return byteBuffer.remaining();
    }

    public boolean hasRemaining() {
        return byteBuffer.hasRemaining();
    }

    public boolean isReadOnly() {
        return byteBuffer.isReadOnly();
    }
}
