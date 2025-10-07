package com.harvey.netty.protocol;

/**
 * 雪花算法
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-01 16:18
 */
public class Snowflake {

    private static final long START_TIMESTAMP = 1625097600000L; // 起始时间戳，用于减少生成的时间戳长度，设置为2021-07-01 00:00:00
    private static final long WORKER_ID_BITS = 5; // 机器ID所占位数
    private static final long DATA_CENTER_ID_BITS = 5; // 数据中心ID所占位数
    private static final long SEQUENCE_BITS = 12; // 序列号所占位数

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS); // 最大机器ID
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS); // 最大数据中心ID

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS; // 机器ID左移位数
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS; // 数据中心ID左移位数
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS; // 时间戳左移位数

    private final long workerId; // 机器ID
    private final long dataCenterId; // 数据中心ID
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L; // 上次生成ID的时间戳

    public Snowflake(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID must be between 0 and " + MAX_WORKER_ID);
        }

        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException("Data Center ID must be between 0 and " + MAX_DATA_CENTER_ID);
        }

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID for " +
                    (lastTimestamp - timestamp) + " milliseconds.");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & ((1 << SEQUENCE_BITS) - 1);

            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    // 对于客户端来说
    public static final Snowflake SNOWFLAKE =  new Snowflake(1, 1); // 传入workerId和dataCenterId
}
