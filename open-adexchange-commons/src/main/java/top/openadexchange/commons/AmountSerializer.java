package top.openadexchange.commons;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 金额序列化器
 * 将微单位（1分=100_0000 MicroUnit）的Long类型转换为格式化的字符串（元）
 * 例如：10000 -> "100.00"
 */
public class AmountSerializer extends JsonSerializer<Long> {

    private static final BigDecimal HUNDRED = new BigDecimal("100000000");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        BigDecimal yuan = new BigDecimal(value).divide(HUNDRED, 2, BigDecimal.ROUND_HALF_UP);
        gen.writeString(DECIMAL_FORMAT.format(yuan));
    }
}
