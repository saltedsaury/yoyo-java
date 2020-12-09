package cn.idachain.finance.batch.common.util;

/**
 * A Snowflake like ID generator
 */
public class Snowflake {

    //  private static final int NODE_SHIFT = 10;
    public static final int NODE_SHIFT = 8;
    public static final int SEQ_SHIFT = 12;
    public static final int BIZ_SHIFT = 2;

    private static final short MAX_NODE = 256;
    private static final short MAX_SEQUENCE = 4096;
    private static final short MAX_BIZ = 4;


    private short sequence;
    private long referenceTime;

    private static int node;
    private static int bizType;


    public Snowflake(int node) {
        if (node < 0 || node > MAX_NODE) {
            throw new IllegalArgumentException(String.format("node must be between %s and %s", 0, MAX_NODE));
        }
        this.node = node;
    }

    public Snowflake() {
    }

    public static void setNode(int node) {

        if (node < 0 || node > MAX_NODE) {
            throw new IllegalArgumentException(String.format("node must be between %s and %s", 0, MAX_NODE));
        }
        Snowflake.node = node;
    }

    public static void setBizType(int bizType) {

        if (bizType < 0 || bizType > MAX_BIZ) {
            throw new IllegalArgumentException(String.format("bizType must be between %s and %s", 0, MAX_BIZ));
        }
        Snowflake.bizType = bizType;
    }

    synchronized public long next() {

        long currentTime = new DateTime().getTime();

        if (currentTime > referenceTime) {
            sequence = 1;
        } else {
            if (sequence < Snowflake.MAX_SEQUENCE) {
                sequence++;
            } else {
                currentTime = waitNextMill();
                sequence = 0;
            }
        }
        referenceTime = currentTime;

        return currentTime << NODE_SHIFT << BIZ_SHIFT << SEQ_SHIFT | node
                << BIZ_SHIFT << SEQ_SHIFT | bizType
                << sequence | sequence;
    }

    private long waitNextMill() {
        long cur;
        do {
            cur = System.currentTimeMillis();
        } while (cur <= referenceTime);
        return cur;
    }

    private static void main(String[] args) {

        new Snowflake(1).next();
    }

}
