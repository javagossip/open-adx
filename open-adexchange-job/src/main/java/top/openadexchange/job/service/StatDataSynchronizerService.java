package top.openadexchange.job.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mybatisflex.core.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import org.springframework.util.StringUtils;

import top.openadexchange.dao.AdSlotStatDao;
import top.openadexchange.dao.DspDao;
import top.openadexchange.dao.DspStatDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dao.SiteDao;
import top.openadexchange.job.handler.StatDataSynchronizer.StatDataSynchronizerParam;
import top.openadexchange.model.AdSlotStat;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspStat;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;

@Service
@Slf4j
public class StatDataSynchronizerService {

    private static final int BATCH_SIZE = 100;

    @Resource(name = "oaxStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private AdSlotStatDao adSlotStatDao;
    @Resource
    private SiteAdPlacementDao siteAdPlacementDao;
    @Resource
    private SiteDao siteDao;
    @Resource
    private DspDao dspDao;
    @Autowired
    private DspStatDao dspStatDao;

    public void syncAdSlotStatData(StatDataSynchronizerParam param) {
        String syncDate = (param == null || !StringUtils.hasText(param.getSyncDate())) ? LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")) : param.getSyncDate();
        String hashKey = String.format("imp:adslot:%s", syncDate);
        ScanOptions options = ScanOptions.scanOptions().match("*").count(BATCH_SIZE).build();

        // 2. 执行 scan 操作
        // 注意：使用 try-with-resources 自动关闭 Cursor 释放连接
        int count = 0;
        //<adSlotId, statData> - 广告位ID, 统计数据
        Map<String, String> slotStatMap = new HashMap<>();
        try (Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(hashKey, options)) {
            while (cursor.hasNext()) {
                count++;
                Map.Entry<Object, Object> entry = cursor.next();
                String adSlotId = (String) entry.getKey();
                String impCountStr = (String) entry.getValue();

                // 确保值不是null或空字符串
                if (adSlotId != null && impCountStr != null && !impCountStr.trim().isEmpty()) {
                    slotStatMap.put(adSlotId, impCountStr);
                }

                if (count % BATCH_SIZE == 0) {
                    internalSyncAdSlotStatData(slotStatMap, syncDate);
                    slotStatMap.clear();
                }
            }
            if (!slotStatMap.isEmpty()) {
                internalSyncAdSlotStatData(slotStatMap, syncDate);
                slotStatMap.clear();
            }
        } catch (Exception ex) {
            log.error("syncAdSlotStatData error", ex);
        }
    }

    // 添加同步Crid统计数据的方法
    public void syncCridStatData(StatDataSynchronizerParam param) {
        // TODO: 根据实际业务需求实现crid统计数据同步
        log.warn("syncCridStatData is not implemented yet");
    }

    private void internalSyncAdSlotStatData(Map<String, String> slotStatMap, String syncDate) {
        if (slotStatMap == null || slotStatMap.isEmpty()) {
            return;
        }
        String clickHashKey = String.format("clk:adslot:%s", syncDate);
        List<String> adSlotIds = slotStatMap.keySet().stream().toList();
        List<SiteAdPlacement> siteAdPlacements =
                siteAdPlacementDao.list(QueryWrapper.create().in(SiteAdPlacement::getCode, adSlotIds));
        List<Long> siteIds =
                siteAdPlacements.stream().filter(Objects::nonNull).map(SiteAdPlacement::getSiteId).toList();

        List<Site> sites = siteDao.list(QueryWrapper.create().in(Site::getId, siteIds));
        Map<Long, Site> siteMap = sites.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Site::getId, Function.identity(), (a, b) -> a));

        Map<String, SiteAdPlacement> siteAdPlacementMap = siteAdPlacements.stream()
                .collect(Collectors.toMap(SiteAdPlacement::getCode, Function.identity(), (a, b) -> a));
        List<Object> clickCounts = redisTemplate.opsForHash().multiGet(clickHashKey, new ArrayList<>(adSlotIds));

        List<AdSlotStat> adSlotStats = new ArrayList<>(slotStatMap.size());
        for (int i = 0, size = adSlotIds.size(); i < size; i++) {
            SiteAdPlacement siteAdPlacement = siteAdPlacementMap.get(adSlotIds.get(i));
            if (siteAdPlacement == null) {
                continue;
            }
            Site site = siteMap.get(siteAdPlacement.getSiteId());
            if (site == null) {
                continue;
            }
            AdSlotStat adSlotStat = new AdSlotStat();
            String adSlotId = adSlotIds.get(i);
            adSlotStat.setAdSlotId(adSlotId);
            adSlotStat.setStatDate(Integer.parseInt(syncDate));
            adSlotStat.setImpCount(Optional.ofNullable(slotStatMap.get(adSlotId)).map(Long::parseLong).orElse(0L));
            adSlotStat.setClickCount(Optional.ofNullable(clickCounts.get(i))
                    .map(String::valueOf)
                    .map(Long::parseLong)
                    .orElse(0L));
            adSlotStat.setSiteId(siteAdPlacement.getSiteId());
            adSlotStat.setPublisherId(site.getPublisherId());
            adSlotStats.add(adSlotStat);
        }
        adSlotStatDao.saveBatchOnDuplicateKeyUpdate(adSlotStats);
    }

    public void syncDspStatData(StatDataSynchronizerParam param) {
        String syncDate = (param == null || !StringUtils.hasText(param.getSyncDate())) ? LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")) : param.getSyncDate();
        String hashKey = String.format("imp:dsp:%s", syncDate);
        ScanOptions options = ScanOptions.scanOptions().match("*").count(BATCH_SIZE).build();

        int count = 0;
        //<dspId, statData> - 广告位ID, 统计数据
        Map<String, String> dspStatMap = new HashMap<>();
        try (Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(hashKey, options)) {
            while (cursor.hasNext()) {
                count++;
                Map.Entry<Object, Object> entry = cursor.next();
                String hashField = (String) entry.getKey();
                String impCountStr = (String) entry.getValue();

                // 确保值不是null或空字符串
                if (hashField != null && impCountStr != null && !impCountStr.trim().isEmpty()) {
                    dspStatMap.put(hashField, impCountStr);
                }

                if (count % BATCH_SIZE == 0) {
                    internalSyncDspStatData(dspStatMap, syncDate);
                    dspStatMap.clear();
                }
            }
            if (!dspStatMap.isEmpty()) {
                internalSyncAdSlotStatData(dspStatMap, syncDate);
                dspStatMap.clear();
            }
        } catch (Exception ex) {
            log.error("syncAdSlotStatData error", ex);
        }
    }

    private void internalSyncDspStatData(Map<String, String> dspStatMap, String syncDate) {
        if (dspStatMap == null || dspStatMap.isEmpty()) {
            return;
        }
        String clickHashKey = String.format("clk:dsp:%s", syncDate);
        List<Object> dspCodes = new ArrayList<>(dspStatMap.keySet());

        List<Dsp> dspList = dspDao.list(QueryWrapper.create().in(Dsp::getDspId, dspCodes));
        Map<String, Dsp> dspMap = dspList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Dsp::getDspId, Function.identity(), (a, b) -> a));

        List<Object> clickCounts = redisTemplate.opsForHash().multiGet(clickHashKey, new ArrayList<>(dspCodes));

        List<DspStat> dspStats = new ArrayList<>(dspCodes.size());
        for (int i = 0, size = dspCodes.size(); i < size; i++) {
            DspStat dspStat = new DspStat();
            String dspCode = (String) dspCodes.get(i);
            Dsp dsp = dspMap.get(dspCode);
            if (dsp == null) {
                continue;
            }
            String impCountStr = dspStatMap.getOrDefault(dspCode, "0");
            String clickCountStr = (String) clickCounts.get(i);
            if (clickCountStr == null || clickCountStr.trim().isEmpty()) {
                clickCountStr = "0";
            }
            dspStat.setDspId(dsp.getId());
            dspStat.setDspCode(dsp.getDspId());
            dspStat.setImpCount(Long.parseLong(impCountStr));
            dspStat.setClkCount(Long.parseLong(clickCountStr));
            dspStat.setStatDate(Integer.parseInt(syncDate));
            dspStats.add(dspStat);
        }
        dspStatDao.saveBatchOnDuplicateKeyUpdate(dspStats);
    }
}