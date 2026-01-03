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

import top.openadexchange.commons.exception.ValidateException;
import top.openadexchange.dao.AdvertiserDao;
import top.openadexchange.dao.AdvertiserIndustryLicenseDao;
import top.openadexchange.model.Advertiser;
import top.openadexchange.model.AdvertiserIndustryLicense;
import top.openadexchange.openapi.dsp.application.converter.AdvertiserConverter;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserAuditResultDto;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserDto;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserIndustryLicenseDto;
import top.openadexchange.openapi.dsp.application.validator.AdvertiserValidator;
import top.openadexchange.openapi.dsp.commons.ApiErrorCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdvertiserService 单元测试")
class AdvertiserServiceTest {

    @Mock
    private AdvertiserConverter advertiserConverter;

    @Mock
    private AdvertiserValidator advertiserValidator;

    @Mock
    private AdvertiserDao advertiserDao;

    @Mock
    private AdvertiserIndustryLicenseDao advertiserIndustryLicenseDao;

    @InjectMocks
    private AdvertiserService advertiserService;

    private AdvertiserDto advertiserDto;
    private Advertiser advertiser;
    private List<AdvertiserIndustryLicense> advertiserIndustryLicenses;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        advertiserDto = new AdvertiserDto();
        advertiserDto.setAdvertiserId("dsp-adv-001");
        advertiserDto.setAdvertiserName("测试广告主");
        advertiserDto.setCompanyName("测试公司");
        advertiserDto.setIndustryCode("IT001");
        advertiserDto.setBusinessLicenseNo("91110000MA01234567");
        advertiserDto.setBusinessLicenseUrl("http://example.com/license.jpg");
        advertiserDto.setLegalPersonName("张三");
        advertiserDto.setRegisteredAddress("北京市朝阳区");
        advertiserDto.setContactName("李四");
        advertiserDto.setContactPhone("13800138000");
        advertiserDto.setContactEmail("test@example.com");
        advertiserDto.setLegalPersonIdUrl("http://example.com/id.jpg");

        // 设置行业资质
        List<AdvertiserIndustryLicenseDto> licenseDtos = new ArrayList<>();
        AdvertiserIndustryLicenseDto licenseDto = new AdvertiserIndustryLicenseDto();
        licenseDto.setIndustryCode("IT001");
        licenseDto.setLicenseName("互联网信息服务许可证");
        licenseDto.setLicenseUrl("http://example.com/license1.jpg");
        licenseDtos.add(licenseDto);
        advertiserDto.setAdvertiserIndustryLicenses(licenseDtos);

        advertiser = new Advertiser();
        advertiser.setId(1L);
        advertiser.setCode("ADV-001");
        advertiser.setDspAdvertiserId("dsp-adv-001");
        advertiser.setAdvertiserName("测试广告主");
        advertiser.setAuditStatus("PENDING");
        advertiser.setAuditReason(null);
        advertiser.setAuditTime(LocalDateTime.now());

        advertiserIndustryLicenses = new ArrayList<>();
        AdvertiserIndustryLicense license = new AdvertiserIndustryLicense();
        license.setIndustryCode("IT001");
        license.setLicenseName("互联网信息服务许可证");
        license.setLicenseUrl("http://example.com/license1.jpg");
        advertiserIndustryLicenses.add(license);
    }

    @Test
    @DisplayName("添加广告主 - 成功")
    void testAddAdvertiser_Success() {
        // Given
        when(advertiserConverter.from(advertiserDto)).thenReturn(advertiser);
        when(advertiserConverter.fromAdvertiserLicenses(advertiserDto.getAdvertiserIndustryLicenses()))
                .thenReturn(advertiserIndustryLicenses);
        doNothing().when(advertiserValidator).validateForAddAdvertiser(advertiserDto);
        when(advertiserDao.save(any(Advertiser.class))).thenReturn(true);

        // When
        String result = advertiserService.addAdvertiser(advertiserDto);

        // Then
        assertEquals("ADV-001", result);
        verify(advertiserValidator, times(1)).validateForAddAdvertiser(advertiserDto);
        verify(advertiserConverter, times(1)).from(advertiserDto);
        verify(advertiserConverter, times(1))
                .fromAdvertiserLicenses(advertiserDto.getAdvertiserIndustryLicenses());
        verify(advertiserDao, times(1)).save(advertiser);
        verify(advertiserIndustryLicenseDao, times(1))
                .saveAdvertiserIndustryLicenses(advertiser.getId(), advertiserIndustryLicenses);
    }

    @Test
    @DisplayName("添加广告主 - 验证失败(广告主ID为空)")
    void testAddAdvertiser_ValidationFailed() {
        // Given
        ValidateException exception = new ValidateException(
                ApiErrorCode.ADVERTISER_ID_IS_REQUIRED.getCode(),
                ApiErrorCode.ADVERTISER_ID_IS_REQUIRED.getMessage());
        doThrow(exception).when(advertiserValidator).validateForAddAdvertiser(advertiserDto);

        // When & Then
        assertThrows(ValidateException.class, () -> advertiserService.addAdvertiser(advertiserDto));
        verify(advertiserValidator, times(1)).validateForAddAdvertiser(advertiserDto);
        verify(advertiserConverter, never()).from(any());
        verify(advertiserDao, never()).save(any());
    }

    @Test
    @DisplayName("添加广告主 - 验证失败(广告主已存在)")
    void testAddAdvertiser_AdvertiserExists() {
        // Given
        ValidateException exception = new ValidateException(
                ApiErrorCode.ADVERTISER_EXISTS.getCode(),
                ApiErrorCode.ADVERTISER_EXISTS.getMessage());
        doThrow(exception).when(advertiserValidator).validateForAddAdvertiser(advertiserDto);

        // When & Then
        ValidateException thrown = assertThrows(ValidateException.class,
                () -> advertiserService.addAdvertiser(advertiserDto));
        assertEquals(ApiErrorCode.ADVERTISER_EXISTS.getCode(), thrown.getCode());
        verify(advertiserValidator, times(1)).validateForAddAdvertiser(advertiserDto);
        verify(advertiserConverter, never()).from(any());
    }

    @Test
    @DisplayName("更新广告主 - 成功")
    void testUpdateAdvertiser_Success() {
        // Given
        doNothing().when(advertiserValidator).validateForUpdateAdvertiser(advertiserDto);

        // When
        Boolean result = advertiserService.updateAdvertiser(advertiserDto);

        // Then
        assertFalse(result);
        verify(advertiserValidator, times(1)).validateForUpdateAdvertiser(advertiserDto);
    }

    @Test
    @DisplayName("更新广告主 - 验证失败(广告主不存在)")
    void testUpdateAdvertiser_AdvertiserNotExist() {
        // Given
        ValidateException exception = new ValidateException(
                ApiErrorCode.ADVERTISER_NOT_EXIST.getCode(),
                ApiErrorCode.ADVERTISER_NOT_EXIST.getMessage());
        doThrow(exception).when(advertiserValidator).validateForUpdateAdvertiser(advertiserDto);

        // When & Then
        ValidateException thrown = assertThrows(ValidateException.class,
                () -> advertiserService.updateAdvertiser(advertiserDto));
        assertEquals(ApiErrorCode.ADVERTISER_NOT_EXIST.getCode(), thrown.getCode());
        verify(advertiserValidator, times(1)).validateForUpdateAdvertiser(advertiserDto);
    }

    @Test
    @DisplayName("获取单个广告主审核状态 - 成功")
    void testGetAuditStatus_Success() {
        // Given
        String advertiserId = "dsp-adv-001";
        AdvertiserAuditResultDto auditResultDto = new AdvertiserAuditResultDto();
        auditResultDto.setAdvertiserId(advertiserId);

        when(advertiserDao.getByDspAdvertiserId(advertiserId)).thenReturn(advertiser);
        when(advertiserConverter.toAdvertiserAuditResultDto(advertiser)).thenReturn(auditResultDto);

        // When
        AdvertiserAuditResultDto result = advertiserService.getAuditStatus(advertiserId);

        // Then
        assertNotNull(result);
        assertEquals(advertiserId, result.getAdvertiserId());
        verify(advertiserDao, times(1)).getByDspAdvertiserId(advertiserId);
        verify(advertiserConverter, times(1)).toAdvertiserAuditResultDto(advertiser);
    }

    @Test
    @DisplayName("获取单个广告主审核状态 - 广告主ID为null")
    void testGetAuditStatus_AdvertiserIdIsNull() {
        // When & Then
        assertThrows(ValidateException.class, () -> advertiserService.getAuditStatus(null));
        verify(advertiserDao, never()).getByDspAdvertiserId(anyString());
    }

    @Test
    @DisplayName("获取单个广告主审核状态 - 广告主ID为空字符串")
    void testGetAuditStatus_AdvertiserIdIsEmpty() {
        // When & Then
        assertThrows(ValidateException.class, () -> advertiserService.getAuditStatus(""));
        verify(advertiserDao, never()).getByDspAdvertiserId(anyString());
    }

    @Test
    @DisplayName("获取单个广告主审核状态 - 广告主ID为空白字符串")
    void testGetAuditStatus_AdvertiserIdIsBlank() {
        // When & Then
        assertThrows(ValidateException.class, () -> advertiserService.getAuditStatus("   "));
        verify(advertiserDao, never()).getByDspAdvertiserId(anyString());
    }

    @Test
    @DisplayName("获取单个广告主审核状态 - 广告主不存在")
    void testGetAuditStatus_AdvertiserNotFound() {
        // Given
        String advertiserId = "dsp-adv-999";
        when(advertiserDao.getByDspAdvertiserId(advertiserId)).thenReturn(null);
        when(advertiserConverter.toAdvertiserAuditResultDto(null)).thenReturn(null);

        // When
        AdvertiserAuditResultDto result = advertiserService.getAuditStatus(advertiserId);

        // Then
        assertNull(result);
        verify(advertiserDao, times(1)).getByDspAdvertiserId(advertiserId);
        verify(advertiserConverter, times(1)).toAdvertiserAuditResultDto(null);
    }

    @Test
    @DisplayName("批量获取广告主审核状态 - 成功")
    void testGetAuditStatusList_Success() {
        // Given
        List<String> advertiserIds = Arrays.asList("dsp-adv-001", "dsp-adv-002");
        Advertiser advertiser2 = new Advertiser();
        advertiser2.setId(2L);
        advertiser2.setDspAdvertiserId("dsp-adv-002");
        advertiser2.setAdvertiserName("测试广告主2");
        advertiser2.setAuditStatus("APPROVED");
        advertiser2.setAuditTime(LocalDateTime.now());

        List<Advertiser> advertisers = Arrays.asList(advertiser, advertiser2);

        AdvertiserAuditResultDto auditResultDto1 = new AdvertiserAuditResultDto();
        auditResultDto1.setAdvertiserId("dsp-adv-001");
        AdvertiserAuditResultDto auditResultDto2 = new AdvertiserAuditResultDto();
        auditResultDto2.setAdvertiserId("dsp-adv-002");

        when(advertiserDao.getAdvertisersByDspAdvertiserIds(advertiserIds)).thenReturn(advertisers);
        when(advertiserConverter.toAdvertiserAuditResultDto(advertiser)).thenReturn(auditResultDto1);
        when(advertiserConverter.toAdvertiserAuditResultDto(advertiser2)).thenReturn(auditResultDto2);

        // When
        List<AdvertiserAuditResultDto> result = advertiserService.getAuditStatusList(advertiserIds);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("dsp-adv-001", result.get(0).getAdvertiserId());
        assertEquals("dsp-adv-002", result.get(1).getAdvertiserId());
        verify(advertiserDao, times(1)).getAdvertisersByDspAdvertiserIds(advertiserIds);
        verify(advertiserConverter, times(1)).toAdvertiserAuditResultDto(advertiser);
        verify(advertiserConverter, times(1)).toAdvertiserAuditResultDto(advertiser2);
    }

    @Test
    @DisplayName("批量获取广告主审核状态 - 包含null结果会被过滤")
    void testGetAuditStatusList_WithNullResults() {
        // Given
        List<String> advertiserIds = Arrays.asList("dsp-adv-001", "dsp-adv-002");
        List<Advertiser> advertisers = Arrays.asList(advertiser, advertiser);

        AdvertiserAuditResultDto auditResultDto1 = new AdvertiserAuditResultDto();
        auditResultDto1.setAdvertiserId("dsp-adv-001");

        when(advertiserDao.getAdvertisersByDspAdvertiserIds(advertiserIds)).thenReturn(advertisers);
        when(advertiserConverter.toAdvertiserAuditResultDto(advertiser))
                .thenReturn(auditResultDto1)
                .thenReturn(null);

        // When
        List<AdvertiserAuditResultDto> result = advertiserService.getAuditStatusList(advertiserIds);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("dsp-adv-001", result.get(0).getAdvertiserId());
    }

    @Test
    @DisplayName("批量获取广告主审核状态 - 数据库返回空列表")
    void testGetAuditStatusList_EmptyResultFromDb() {
        // Given
        List<String> advertiserIds = Arrays.asList("dsp-adv-999");
        when(advertiserDao.getAdvertisersByDspAdvertiserIds(advertiserIds)).thenReturn(new ArrayList<>());

        // When
        List<AdvertiserAuditResultDto> result = advertiserService.getAuditStatusList(advertiserIds);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(advertiserDao, times(1)).getAdvertisersByDspAdvertiserIds(advertiserIds);
    }

    @Test
    @DisplayName("批量获取广告主审核状态 - 单个广告主ID")
    void testGetAuditStatusList_SingleId() {
        // Given
        List<String> advertiserIds = Arrays.asList("dsp-adv-001");
        List<Advertiser> advertisers = Arrays.asList(advertiser);

        AdvertiserAuditResultDto auditResultDto = new AdvertiserAuditResultDto();
        auditResultDto.setAdvertiserId("dsp-adv-001");

        when(advertiserDao.getAdvertisersByDspAdvertiserIds(advertiserIds)).thenReturn(advertisers);
        when(advertiserConverter.toAdvertiserAuditResultDto(advertiser)).thenReturn(auditResultDto);

        // When
        List<AdvertiserAuditResultDto> result = advertiserService.getAuditStatusList(advertiserIds);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("dsp-adv-001", result.get(0).getAdvertiserId());
    }
}
