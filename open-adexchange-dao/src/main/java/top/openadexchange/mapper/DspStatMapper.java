package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.mybatisflex.core.BaseMapper;

import org.apache.ibatis.annotations.Param;

import top.openadexchange.model.DspStat;

import java.util.List;

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
                stat_date, 
                imp_count, 
                clk_count
            ) VALUES 
            <foreach collection='list' item='item' separator=','>
                (#{item.dspId}, #{item.dspCode}, #{item.statDate}, #{item.impCount}, #{item.clkCount})
            </foreach>
            ON DUPLICATE KEY UPDATE 
                imp_count = VALUES(imp_count), 
                clk_count = VALUES(clk_count), 
                dsp_id = VALUES(dsp_id), 
                dsp_code = VALUES(dsp_code)
            </script>
            """)
    void saveBatchOnDuplicateKeyUpdate(@Param("list") List<DspStat> dspStats);
}
