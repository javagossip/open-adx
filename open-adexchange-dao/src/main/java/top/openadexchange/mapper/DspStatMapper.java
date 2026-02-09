package top.openadexchange.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mybatisflex.core.BaseMapper;

import top.openadexchange.model.DspStat;

/**
 * Dsp统计表 映射层。
 *
 * @author top.openadexchange
 * @since 2026-01-29
 */
@Mapper
public interface DspStatMapper extends BaseMapper<DspStat> {

    @Insert("""
            <script>
            INSERT INTO dsp_stat (
                dsp_id,
                dsp_code,
                dsp_name,
                imp_count,
                clk_count,
                bid_count,
                win_count,
                req_count,
                cost,
                stat_date
            ) VALUES
            <foreach collection="list" item="item" separator=",">
            (
                #{item.dspId},
                #{item.dspCode},
                #{item.dspName},
                #{item.impCount},
                #{item.clkCount},
                #{item.bidCount},
                #{item.winCount},
                #{item.reqCount},
                #{item.cost},
                #{item.statDate}
            )
            </foreach>
            ON DUPLICATE KEY UPDATE
                imp_count   = VALUES(imp_count),
                clk_count   = VALUES(clk_count),
                bid_count   = VALUES(bid_count),
                win_count   = VALUES(win_count),
                req_count   = VALUES(req_count),
                cost    = VALUES(cost)
            </script>
            """)
    void saveBatchOnDuplicateKeyUpdate(@Param("list") List<DspStat> dspStats);
}
