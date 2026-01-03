package top.openadexchange.openapi.dsp.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mybatisflex.core.query.QueryWrapper;

import top.openadexchange.commons.exception.ValidateException;
import top.openadexchange.dao.CreativeDao;
import top.openadexchange.domain.entity.CreativeAggregate;
import top.openadexchange.model.Creative;
import top.openadexchange.openapi.dsp.application.converter.CreativeConverter;
import top.openadexchange.openapi.dsp.application.dto.CreativeAuditResultDto;
import top.openadexchange.openapi.dsp.application.dto.CreativeDto;
import top.openadexchange.openapi.dsp.application.validator.CreativeValidator;
import top.openadexchange.openapi.dsp.commons.ApiErrorCode;
import top.openadexchange.openapi.dsp.repository.CreativeAggregateRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreativeService 单元测试")
class CreativeServiceTest {

    @Mock
    private CreativeDao creativeDao;

    @Mock
    private CreativeValidator creativeValidator;

    @Mock
    private CreativeConverter creativeConverter;

    @Mock
    private CreativeAggregateRepository creativeAggregateRepository;

    @InjectMocks
    private CreativeService creativeService;

    private CreativeDto creativeDto;
    private Creative creative;
    private CreativeAggregate creativeAggregate;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        creativeDto = new CreativeDto();
        creativeDto.setCreativeId("dsp-creative-001");
        creativeDto.setAdvertiserId("dsp-adv-001");
        creativeDto.setName("测试创意");
        creativeDto.setCreativeType("BANNER");
        creativeDto.setCreativeUrl("http://example.com/creative.jpg");
        creativeDto.setLandingPage("http://example.com/landing");
        creativeDto.setWidth(300);
        creativeDto.setHeight(250);
        creativeDto.setMimes("image/jpeg,image/png");
        creativeDto.setDuration(null);

        creative = new Creative();
        creative.setId(1L);
        creative.setCreativeId("CRE-001");
        creative.setDspCreativeId("dsp-creative-001");
        creative.setDspAdvertiserId("dsp-adv-001");
        creative.setName("测试创意");
        creative.setCreativeType("BANNER");
        creative.setCreativeUrl("http://example.com/creative.jpg");
        creative.setLandingUrl("http://example.com/landing");
        creative.setWidth(300);
        creative.setHeight(250);
        creative.setMimes("image/jpeg,image/png");
        creative.setAuditStatus("PENDING");
        creative.setAuditReason(null);
        creative.setAuditTime(LocalDateTime.now());
        creative.setCreatedAt(LocalDateTime.now());
        creative.setUpdatedAt(LocalDateTime.now());

        creativeAggregate = new CreativeAggregate();
        creativeAggregate.setCreative(creative);
        creativeAggregate.setCreativeAssets(new ArrayList<>());
    }

    @Test
    @DisplayName("添加创意 - 成功")
    void testAddCreative_Success() {
        // Given
        doNothing().when(creativeValidator).validateForAddCreative(creativeDto);
        when(creativeConverter.from(creativeDto)).thenReturn(creativeAggregate);
        doNothing().when(creativeAggregateRepository).saveOrUpdateCreativeAggregate(creativeAggregate);

        // When
        String result = creativeService.addCreative(creativeDto);

        // Then
        assertEquals("CRE-001", result);
        verify(creativeValidator, times(1)).validateForAddCreative(creativeDto);
        verify(creativeConverter, times(1)).from(creativeDto);
        verify(creativeAggregateRepository, times(1)).saveOrUpdateCreativeAggregate(creativeAggregate);
    }

    @Test
    @DisplayName("添加创意 - 验证失败(创意ID为空)")
    void testAddCreative_ValidationFailed() {
        // Given
        ValidateException exception = new ValidateException(
                ApiErrorCode.DSP_CREATIVE_ID_IS_REQUIRED.getCode(),
                ApiErrorCode.DSP_CREATIVE_ID_IS_REQUIRED.getMessage());
        doThrow(exception).when(creativeValidator).validateForAddCreative(creativeDto);

        // When & Then
        ValidateException thrown = assertThrows(ValidateException.class,
                () -> creativeService.addCreative(creativeDto));
        assertEquals(ApiErrorCode.DSP_CREATIVE_ID_IS_REQUIRED.getCode(), thrown.getCode());
        verify(creativeValidator, times(1)).validateForAddCreative(creativeDto);
        verify(creativeConverter, never()).from(any());
        verify(creativeAggregateRepository, never()).saveOrUpdateCreativeAggregate(any());
    }

    @Test
    @DisplayName("添加创意 - 无效的创意类型")
    void testAddCreative_InvalidCreativeType() {
        // Given
        ValidateException exception = new ValidateException(
                ApiErrorCode.INVALID_CREATIVE_TYPE.getCode(),
                ApiErrorCode.INVALID_CREATIVE_TYPE.getMessage());
        doNothing().when(creativeValidator).validateForAddCreative(creativeDto);
        when(creativeConverter.from(creativeDto)).thenThrow(exception);

        // When & Then
        ValidateException thrown = assertThrows(ValidateException.class,
                () -> creativeService.addCreative(creativeDto));
        assertEquals(ApiErrorCode.INVALID_CREATIVE_TYPE.getCode(), thrown.getCode());
    }

    @Test
    @DisplayName("更新创意 - 成功")
    void testUpdateCreative_Success() {
        // Given
        doNothing().when(creativeValidator).validateForUpdateCreative(creativeDto);
        when(creativeConverter.from(creativeDto)).thenReturn(creativeAggregate);
        doNothing().when(creativeAggregateRepository).saveOrUpdateCreativeAggregate(creativeAggregate);

        // When
        Boolean result = creativeService.updateCreative(creativeDto);

        // Then
        assertTrue(result);
        verify(creativeValidator, times(1)).validateForUpdateCreative(creativeDto);
        verify(creativeConverter, times(1)).from(creativeDto);
        verify(creativeAggregateRepository, times(1)).saveOrUpdateCreativeAggregate(creativeAggregate);
    }

    @Test
    @DisplayName("更新创意 - 验证失败")
    void testUpdateCreative_ValidationFailed() {
        // Given
        ValidateException exception = new ValidateException(
                ApiErrorCode.DSP_CREATIVE_ID_IS_REQUIRED.getCode(),
                ApiErrorCode.DSP_CREATIVE_ID_IS_REQUIRED.getMessage());
        doThrow(exception).when(creativeValidator).validateForUpdateCreative(creativeDto);

        // When & Then
        assertThrows(ValidateException.class, () -> creativeService.updateCreative(creativeDto));
        verify(creativeValidator, times(1)).validateForUpdateCreative(creativeDto);
        verify(creativeConverter, never()).from(any());
        verify(creativeAggregateRepository, never()).saveOrUpdateCreativeAggregate(any());
    }

    @Test
    @DisplayName("获取单个创意审核状态 - 成功")
    void testGetCreativeAuditStatus_Success() {
        // Given
        String creativeId = "dsp-creative-001";
        CreativeAuditResultDto auditResultDto = new CreativeAuditResultDto();
        auditResultDto.setCreativeId(creativeId);

        when(creativeDao.getOne(any(QueryWrapper.class))).thenReturn(creative);
        when(creativeConverter.toCreativeAuditResultDto(creative)).thenReturn(auditResultDto);

        // When
        CreativeAuditResultDto result = creativeService.getCreativeAuditStatus(creativeId);

        // Then
        assertNotNull(result);
        assertEquals(creativeId, result.getCreativeId());
        verify(creativeDao, times(1)).getOne(any(QueryWrapper.class));
        verify(creativeConverter, times(1)).toCreativeAuditResultDto(creative);
    }

    @Test
    @DisplayName("获取单个创意审核状态 - 创意ID为null")
    void testGetCreativeAuditStatus_CreativeIdIsNull() {
        // When & Then
        assertThrows(ValidateException.class, () -> creativeService.getCreativeAuditStatus(null));
        verify(creativeDao, never()).getOne(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取单个创意审核状态 - 创意ID为空字符串")
    void testGetCreativeAuditStatus_CreativeIdIsEmpty() {
        // When & Then
        assertThrows(ValidateException.class, () -> creativeService.getCreativeAuditStatus(""));
        verify(creativeDao, never()).getOne(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取单个创意审核状态 - 创意ID为空白字符串")
    void testGetCreativeAuditStatus_CreativeIdIsBlank() {
        // When & Then
        assertThrows(ValidateException.class, () -> creativeService.getCreativeAuditStatus("   "));
        verify(creativeDao, never()).getOne(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取单个创意审核状态 - 创意不存在")
    void testGetCreativeAuditStatus_CreativeNotFound() {
        // Given
        String creativeId = "dsp-creative-999";
        when(creativeDao.getOne(any(QueryWrapper.class))).thenReturn(null);
        when(creativeConverter.toCreativeAuditResultDto(null)).thenReturn(null);

        // When
        CreativeAuditResultDto result = creativeService.getCreativeAuditStatus(creativeId);

        // Then
        assertNull(result);
        verify(creativeDao, times(1)).getOne(any(QueryWrapper.class));
        verify(creativeConverter, times(1)).toCreativeAuditResultDto(null);
    }

    @Test
    @DisplayName("批量获取创意审核状态 - 成功")
    void testGetCreativeAuditStatusList_Success() {
        // Given
        List<String> creativeIds = Arrays.asList("dsp-creative-001", "dsp-creative-002");
        Creative creative2 = new Creative();
        creative2.setId(2L);
        creative2.setDspCreativeId("dsp-creative-002");
        creative2.setName("测试创意2");
        creative2.setAuditStatus("APPROVED");
        creative2.setAuditTime(LocalDateTime.now());

        List<Creative> creatives = Arrays.asList(creative, creative2);

        CreativeAuditResultDto auditResultDto1 = new CreativeAuditResultDto();
        auditResultDto1.setCreativeId("dsp-creative-001");
        CreativeAuditResultDto auditResultDto2 = new CreativeAuditResultDto();
        auditResultDto2.setCreativeId("dsp-creative-002");

        when(creativeDao.getCreativesByDspCreativeIds(creativeIds)).thenReturn(creatives);
        when(creativeConverter.toCreativeAuditResultDto(creative)).thenReturn(auditResultDto1);
        when(creativeConverter.toCreativeAuditResultDto(creative2)).thenReturn(auditResultDto2);

        // When
        List<CreativeAuditResultDto> result = creativeService.getCreativeAuditStatusList(creativeIds);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("dsp-creative-001", result.get(0).getCreativeId());
        assertEquals("dsp-creative-002", result.get(1).getCreativeId());
        verify(creativeDao, times(1)).getCreativesByDspCreativeIds(creativeIds);
        verify(creativeConverter, times(1)).toCreativeAuditResultDto(creative);
        verify(creativeConverter, times(1)).toCreativeAuditResultDto(creative2);
    }

    @Test
    @DisplayName("批量获取创意审核状态 - 包含null结果会被过滤")
    void testGetCreativeAuditStatusList_WithNullResults() {
        // Given
        List<String> creativeIds = Arrays.asList("dsp-creative-001", "dsp-creative-002");
        List<Creative> creatives = Arrays.asList(creative, creative);

        CreativeAuditResultDto auditResultDto1 = new CreativeAuditResultDto();
        auditResultDto1.setCreativeId("dsp-creative-001");

        when(creativeDao.getCreativesByDspCreativeIds(creativeIds)).thenReturn(creatives);
        when(creativeConverter.toCreativeAuditResultDto(creative))
                .thenReturn(auditResultDto1)
                .thenReturn(null);

        // When
        List<CreativeAuditResultDto> result = creativeService.getCreativeAuditStatusList(creativeIds);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("dsp-creative-001", result.get(0).getCreativeId());
    }

    @Test
    @DisplayName("批量获取创意审核状态 - 数据库返回空列表")
    void testGetCreativeAuditStatusList_EmptyResultFromDb() {
        // Given
        List<String> creativeIds = Arrays.asList("dsp-creative-999");
        when(creativeDao.getCreativesByDspCreativeIds(creativeIds)).thenReturn(new ArrayList<>());

        // When
        List<CreativeAuditResultDto> result = creativeService.getCreativeAuditStatusList(creativeIds);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(creativeDao, times(1)).getCreativesByDspCreativeIds(creativeIds);
    }

    @Test
    @DisplayName("批量获取创意审核状态 - 创意ID列表为null")
    void testGetCreativeAuditStatusList_CreativeIdsIsNull() {
        // When & Then
        assertThrows(ValidateException.class, () -> creativeService.getCreativeAuditStatusList(null));
        verify(creativeDao, never()).getCreativesByDspCreativeIds(anyList());
    }

    @Test
    @DisplayName("批量获取创意审核状态 - 创意ID列表为空")
    void testGetCreativeAuditStatusList_CreativeIdsIsEmpty() {
        // When & Then
        assertThrows(ValidateException.class, () -> creativeService.getCreativeAuditStatusList(new ArrayList<>()));
        verify(creativeDao, never()).getCreativesByDspCreativeIds(anyList());
    }

    @Test
    @DisplayName("批量获取创意审核状态 - 单个创意ID")
    void testGetCreativeAuditStatusList_SingleId() {
        // Given
        List<String> creativeIds = Arrays.asList("dsp-creative-001");
        List<Creative> creatives = Arrays.asList(creative);

        CreativeAuditResultDto auditResultDto = new CreativeAuditResultDto();
        auditResultDto.setCreativeId("dsp-creative-001");

        when(creativeDao.getCreativesByDspCreativeIds(creativeIds)).thenReturn(creatives);
        when(creativeConverter.toCreativeAuditResultDto(creative)).thenReturn(auditResultDto);

        // When
        List<CreativeAuditResultDto> result = creativeService.getCreativeAuditStatusList(creativeIds);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("dsp-creative-001", result.get(0).getCreativeId());
    }
}
