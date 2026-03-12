package top.openadexchange.openapi.ssp.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public final class LogSamplingUtils {

    // 采样基数 10000，支持到 0.01% 的精度
    private static final int BUCKET_SIZE = 10000;

    /**
     * 判断当前请求 ID 是否命中采样
     *
     * @param requestId 请求唯一ID
     * @param sampleRate 采样率 (0-10000)
     * @return 是否命中
     */
    public static boolean isSampled(String requestId, int sampleRate) {
        if (sampleRate <= 0) {
            return false;
        }
        if (sampleRate >= BUCKET_SIZE) {
            return true;
        }

        // 1. 使用 MurmurHash3 计算 32 位 Hash 值
        // 2. 将 Hash 值转为正数并对基数取模
        int hash = Hashing.murmur3_32_fixed().hashString(requestId, StandardCharsets.UTF_8).asInt();

        // 映射到 [0, 9999] 范围内
        int bucket = Math.abs(hash % BUCKET_SIZE);

        // 3. 判断是否落在采样阈值内
        return bucket < BUCKET_SIZE;
    }
}
