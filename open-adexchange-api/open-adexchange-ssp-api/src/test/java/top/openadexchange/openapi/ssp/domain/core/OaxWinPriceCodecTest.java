package top.openadexchange.openapi.ssp.domain.core;

import com.google.common.io.BaseEncoding;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import top.openadexchange.model.Dsp;

import static org.junit.jupiter.api.Assertions.*;

class OaxWinPriceCodecTest {

    private OaxWinPriceCodec codec;
    private Dsp testDsp;

    @BeforeEach
    void setUp() {
        codec = new OaxWinPriceCodec();
        testDsp = Dsp.builder()
                .id(1)
                .dspId("test-dsp")
                .name("Test DSP")
                .encryptionKey("dGVzdEVuY3J5cHRpb25LZXlfMTIzNDU2Nzg5MDEyMzQ1Ng==")
                .integrityKey("dGVzdEludGVncml0eUtleV8xMjM0NTY3ODkwMTIzNDU2Nzg5MA==")
                .build();
    }

    @Test
    void testEncodeDecodeNormalPrice() {
        long originalPrice = 1000L;

        String encrypted = codec.encode(originalPrice, testDsp);
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());

        long decrypted = codec.decode(encrypted, testDsp);
        assertEquals(originalPrice, decrypted);
    }

    @Test
    void testEncodeDecodeWithZeroPrice() {
        long originalPrice = 0L;

        String encrypted = codec.encode(originalPrice, testDsp);
        assertNotNull(encrypted);

        long decrypted = codec.decode(encrypted, testDsp);
        assertEquals(originalPrice, decrypted);
    }

    @Test
    void testEncodeDecodeWithMaxPrice() {
        long originalPrice = Long.MAX_VALUE;

        String encrypted = codec.encode(originalPrice, testDsp);
        assertNotNull(encrypted);

        long decrypted = codec.decode(encrypted, testDsp);
        assertEquals(originalPrice, decrypted);
    }

    @Test
    void testEncodeDecodeWithMinPrice() {
        long originalPrice = Long.MIN_VALUE;

        String encrypted = codec.encode(originalPrice, testDsp);
        assertNotNull(encrypted);

        long decrypted = codec.decode(encrypted, testDsp);
        assertEquals(originalPrice, decrypted);
    }

    @Test
    void testEncodeProducesDifferentResultsEachTime() {
        long price = 500L;

        String encrypted1 = codec.encode(price, testDsp);
        String encrypted2 = codec.encode(price, testDsp);

        // 由于使用了随机IV，每次加密结果应该不同
        assertNotEquals(encrypted1, encrypted2);

        // 但是解密后应该得到相同的价格
        long decrypted1 = codec.decode(encrypted1, testDsp);
        long decrypted2 = codec.decode(encrypted2, testDsp);
        assertEquals(price, decrypted1);
        assertEquals(price, decrypted2);
    }

    @Test
    void testDecodeWithDifferentKeysShouldFail() {
        long originalPrice = 1000L;
        String encrypted = codec.encode(originalPrice, testDsp);

        Dsp differentDsp = Dsp.builder()
                .id(2)
                .dspId("different-dsp")
                .name("Different DSP")
                .encryptionKey("ZGlmZmVyZW50RW5jcnlwdGlvbktleV8xMjM0NTY3ODkwMTIzNDU2Nzg5MA==")
                .integrityKey("ZGlmZmVyZW50SW50ZWdyaXR5S2V5XzEyMzQ1Njc4OTAxMjM0NTY3ODkw")
                .build();

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            codec.decode(encrypted, differentDsp);
        });

        assertTrue(exception.getMessage().contains("integrity check failed"));
    }

    @Test
    //这个测试用例有误，防篡改需要串改价格，否则在解密第一步的 base64就已经错误了
    void testDecodeWithTamperedDataShouldFail() {
        long originalPrice = 1000L;
        String encrypted = codec.encode(originalPrice, testDsp);


        // 篡改加密数据
        //byte[] tamperedBytes = encrypted.getBytes();
        byte[] tamperedBytes = BaseEncoding.base64Url().decode(encrypted);
        if (tamperedBytes.length > 0) {
            tamperedBytes[0] = (byte) (tamperedBytes[0] ^ 0xFF);
        }
        String tamperedEncrypted = BaseEncoding.base64Url().encode(tamperedBytes);

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            codec.decode(tamperedEncrypted, testDsp);
        });

        assertTrue(exception.getMessage().contains("integrity check failed"));
    }

    @Test
    void testDecodeWithInvalidLengthShouldFail() {
        String invalidLengthEncrypted = "invalid-length-encrypted-price";

        assertThrows(IllegalArgumentException.class, () -> {
            codec.decode(invalidLengthEncrypted, testDsp);
        });
    }

    @Test
    void testDecodeWithEmptyStringShouldFail() {
        String emptyEncrypted = "";

        assertThrows(IllegalArgumentException.class, () -> {
            codec.decode(emptyEncrypted, testDsp);
        });
    }

    @Test
    void testEncodeDecodeWithVariousPrices() {
        long[] testPrices = {
                1L, 100L, 1000L, 10000L, 100000L, 1000000L, -1L, -100L, -1000L
        };

        for (long price : testPrices) {
            String encrypted = codec.encode(price, testDsp);
            assertNotNull(encrypted);

            long decrypted = codec.decode(encrypted, testDsp);
            assertEquals(price, decrypted);
        }
    }

    @Test
    void testEncodeDecodeConsistency() {
        long originalPrice = 999999L;

        String encrypted1 = codec.encode(originalPrice, testDsp);
        long decrypted1 = codec.decode(encrypted1, testDsp);
        String encrypted2 = codec.encode(decrypted1, testDsp);
        long decrypted2 = codec.decode(encrypted2, testDsp);

        assertEquals(originalPrice, decrypted1);
        assertEquals(originalPrice, decrypted2);
    }

    @Test
    void testDecodeWithWrongIntegrityKeyShouldFail() {
        long originalPrice = 1000L;
        String encrypted = codec.encode(originalPrice, testDsp);

        Dsp wrongIntegrityDsp = Dsp.builder()
                .id(3)
                .dspId("wrong-integrity-dsp")
                .name("Wrong Integrity DSP")
                .encryptionKey(testDsp.getEncryptionKey())
                .integrityKey("d3JvbmdJbnRlZ3JpdHlLZXlfMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=")
                .build();

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            codec.decode(encrypted, wrongIntegrityDsp);
        });

        assertTrue(exception.getMessage().contains("integrity check failed"));
    }

    @Test
    void testEncodeProducesBase64UrlFormat() {
        long price = 500L;
        String encrypted = codec.encode(price, testDsp);

        // Base64 URL-safe 字符包含字母、数字、- 和 _，不包含 = (omitPadding)
        assertTrue(encrypted.matches("[A-Za-z0-9_-]+"));
        assertFalse(encrypted.contains("="));
        assertFalse(encrypted.contains("+"));
        assertFalse(encrypted.contains("/"));
    }

    @Test
    void testDecodeWithCorruptedBase64Characters() {
        String corruptedBase64 = "This is not valid base64!@#$%";

        assertThrows(IllegalArgumentException.class, () -> {
            codec.decode(corruptedBase64, testDsp);
        });
    }

    @Test
    void testEncodeDecodeMultipleTimesConsistency() {
        long originalPrice = 777L;

        for (int i = 0; i < 10; i++) {
            String encrypted = codec.encode(originalPrice, testDsp);
            long decrypted = codec.decode(encrypted, testDsp);
            assertEquals(originalPrice, decrypted);
        }
    }

    @Test
    void testDifferentEncryptionsOfSamePriceDecodeCorrectly() {
        long price = 888L;

        String[] encryptedPrices = new String[5];
        for (int i = 0; i < 5; i++) {
            encryptedPrices[i] = codec.encode(price, testDsp);
        }

        // 验证所有加密结果都不同
        for (int i = 0; i < encryptedPrices.length; i++) {
            for (int j = i + 1; j < encryptedPrices.length; j++) {
                assertNotEquals(encryptedPrices[i], encryptedPrices[j]);
            }
        }

        // 验证所有加密结果都能正确解密
        for (String encrypted : encryptedPrices) {
            long decrypted = codec.decode(encrypted, testDsp);
            assertEquals(price, decrypted);
        }
    }

    @Test
    void testEncodeDecodeWithNegativePrices() {
        long[] negativePrices = {
                -1L, -100L, -1000L, -10000L, Long.MIN_VALUE
        };

        for (long price : negativePrices) {
            String encrypted = codec.encode(price, testDsp);
            assertNotNull(encrypted);

            long decrypted = codec.decode(encrypted, testDsp);
            assertEquals(price, decrypted);
        }
    }

    @Test
    void testDecodeWithTruncatedData() {
        String truncated = "dGVzdA"; // Valid base64 but too short

        assertThrows(IllegalArgumentException.class, () -> {
            codec.decode(truncated, testDsp);
        });
    }

    @Test
    void testDecodeWithExtraData() {
        long originalPrice = 1000L;
        String encrypted = codec.encode(originalPrice, testDsp);

        // 添加额外的字符
        String extraData = encrypted + "extra";

        assertThrows(IllegalArgumentException.class, () -> {
            codec.decode(extraData, testDsp);
        });
    }
}
