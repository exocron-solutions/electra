package io.electra.common.server;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public enum Action {

    GET((byte) 0),
    PUT((byte) 1),
    REMOVE((byte) 2),
    UPDATE((byte) 3),
    CREATE_STORAGE((byte) 4),
    DELETE_STORAGE((byte) 5);

    private byte value;

    Action(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static Action of(byte value) {
        return values()[value];
    }
}
