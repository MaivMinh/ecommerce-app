package com.minh.common.kafka;

public final class KafkaUtils {
    private KafkaUtils() {} ///Ngăn chặn việc tạo mới. Mục đích của lớp này chỉ làm thư viện hằng số.

    public static final Integer NUM_SHARDS = 3;

    public static String generateShardKey(String eventId) {
        /// Trả về một shard key hoàn chỉnh. Shard key phải chứa event id để tính toán sau này.
        int shardId = Math.abs(eventId.hashCode()) % NUM_SHARDS;
        return String.format("shard-%d-%s", shardId, eventId);
    }
}
