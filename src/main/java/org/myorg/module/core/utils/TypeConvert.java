package org.myorg.module.core.utils;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.function.Function;

public class TypeConvert {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static byte[] pack(Integer value) {
        return executeWithNullCheck(Ints::toByteArray, value);
    }

    public static byte[] pack(Long value) {
        return executeWithNullCheck(Longs::toByteArray, value);
    }

    public static byte[] pack(Double value) {
        return executeWithNullCheck(v -> pack(Double.doubleToLongBits(v)), value);
    }

    public static byte[] pack(Boolean value) {
        return executeWithNullCheck(v -> new byte[] { v ? (byte)1 : (byte)0 }, value);
    }

    public static byte[] pack(Instant value) {
        return executeWithNullCheck(v -> pack(v.toEpochMilli()), value);
    }

    public static byte[] pack(String value) {
        return executeWithNullCheck(v -> v.getBytes(CHARSET), value);
    }

    //----------------

    public static Integer unpackInteger(byte[] value) {
        return executeWithNullCheck(Ints::fromByteArray, value);
    }

    public static Long unpackLong(byte[] value) {
        return executeWithNullCheck(Longs::fromByteArray, value);
    }

    public static Double unpackDouble(byte[] value) {
        return executeWithNullCheck(v -> Double.longBitsToDouble(unpackLong(v)), value);
    }

    public static Boolean unpackBoolean(byte[] value) {
        return executeWithNullCheck(v -> v[0] == 1, value);
    }

    public static Instant unpackInstant(byte[] value) {
        return executeWithNullCheck(v -> Instant.ofEpochMilli(unpackLong(value)), value);
    }

    public static String unpackString(byte[] value) {
        return executeWithNullCheck(v -> new String(value, CHARSET), value);
    }

    //----------------

    private static  <T, U> U executeWithNullCheck(Function<T, U> function, T value) {
        return value != null ? function.apply(value) : null;
    }
}
