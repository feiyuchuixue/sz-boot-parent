package com.sz.admin.system.business;

import com.sz.admin.system.controller.SysDictController;
import com.sz.admin.system.mapper.SysDictMapper;
import com.sz.admin.system.mapper.SysDictTypeMapper;
import com.sz.admin.system.script.AdminScriptExportService;
import com.sz.admin.system.service.SysDictService;
import com.sz.admin.system.service.SysDictTypeService;
import com.sz.admin.system.service.impl.SysDictServiceImpl;
import com.sz.core.common.dict.DictLoaderFactory;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.DictVO;
import com.sz.redis.RedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DictionaryBusinessAcceptanceIT {

    @Test
    void dictListReturnsLoaderDataDirectlyWhenTypeAlreadyLoaded() {
        DictLoaderFactory loaderFactory = mock(DictLoaderFactory.class);
        SysDictMapper mapper = mock(SysDictMapper.class);
        Map<String, List<DictVO>> loaded = Map.of("account_status", List.of(dict("1000001", "normal", "account_status")));
        when(loaderFactory.loadAllDict()).thenReturn(loaded);
        SysDictServiceImpl service = dictService(loaderFactory, mapper, mock(RedisCache.class));

        Map<String, List<DictVO>> result = service.dictList("account_status");

        assertThat(result).isSameAs(loaded);
        verify(mapper, never()).listDict("account_status");
    }

    @Test
    void dictListFallsBackToMapperAndCachesWhenTypeIsMissingFromLoader() {
        DictLoaderFactory loaderFactory = mock(DictLoaderFactory.class);
        SysDictMapper mapper = mock(SysDictMapper.class);
        RedisCache redisCache = mock(RedisCache.class);
        List<DictVO> rows = List.of(dict("1000001", "normal", "account_status"), dict("1000002", "disabled", "account_status"));
        when(loaderFactory.loadAllDict()).thenReturn(Map.of());
        when(mapper.listDict("account_status")).thenReturn(rows);
        SysDictServiceImpl service = dictService(loaderFactory, mapper, redisCache);

        Map<String, List<DictVO>> result = service.dictList("account_status");

        assertThat(result).containsOnlyKeys("account_status");
        assertThat(result.get("account_status")).containsExactlyElementsOf(rows);
        verify(redisCache).setDict("account_status", rows);
    }

    @Test
    void labelAndValueConversionUseDictLoaderAndReturnBlankForUnknownValues() {
        DictLoaderFactory loaderFactory = mock(DictLoaderFactory.class);
        when(loaderFactory.getDictByType("account_status"))
                .thenReturn(List.of(dict("1000001", "normal", "account_status"), dict("1000002", "disabled", "account_status")));
        SysDictServiceImpl service = dictService(loaderFactory, mock(SysDictMapper.class), mock(RedisCache.class));

        assertThat(service.getDictLabel("account_status", "1000001", ",")).isEqualTo("normal");
        assertThat(service.getDictLabel("account_status", "9999999", ",")).isEmpty();
        assertThat(service.getDictValue("account_status", "disabled", ",")).isEqualTo("1000002");
        assertThat(service.getDictValue("account_status", "missing", ",")).isEmpty();
    }

    @Test
    void controllerDelegatesStaticSingleAndBatchDictQueries() {
        SysDictService service = mock(SysDictService.class);
        SysDictController controller = new SysDictController(service);
        List<DictVO> accountStatus = List.of(dict("1000001", "normal", "account_status"));
        Map<String, List<DictVO>> staticDict = Map.of("account_status", accountStatus);
        when(service.dictStatic()).thenReturn(staticDict);
        when(service.getDictByType("account_status")).thenReturn(accountStatus);
        when(service.getDictByCode(List.of("account_status", "yes_no"))).thenReturn(staticDict);

        ApiResult<Map<String, List<DictVO>>> staticResult = controller.listStaticDict();
        ApiResult<List<DictVO>> singleResult = controller.getDictDataByType("account_status");
        ApiResult<Map<String, List<DictVO>>> batchResult = controller.listDictByCode(List.of("account_status", "yes_no"));

        assertThat(staticResult.getData()).isSameAs(staticDict);
        assertThat(singleResult.getData()).isSameAs(accountStatus);
        assertThat(batchResult.getData()).isSameAs(staticDict);
    }

    @Test
    void batchDictQueryDeduplicatesAndIgnoresBlankCodesFromApiInput() {
        DictLoaderFactory loaderFactory = mock(DictLoaderFactory.class);
        List<DictVO> accountStatus = List.of(dict("1000001", "normal", "account_status"));
        List<DictVO> yesNo = List.of(dict("1", "yes", "yes_no"));
        when(loaderFactory.getDictByType("account_status")).thenReturn(accountStatus);
        when(loaderFactory.getDictByType("yes_no")).thenReturn(yesNo);
        SysDictServiceImpl service = dictService(loaderFactory, mock(SysDictMapper.class), mock(RedisCache.class));

        assertThat(service.getDictByCode(null)).isEmpty();
        Map<String, List<DictVO>> result = service.getDictByCode(Arrays.asList("account_status", "", " account_status ", "yes_no", null, " "));

        assertThat(result).containsOnlyKeys("account_status", "yes_no");
        assertThat(result.get("account_status")).isSameAs(accountStatus);
        assertThat(result.get("yes_no")).isSameAs(yesNo);
        verify(loaderFactory, times(1)).getDictByType("account_status");
        verify(loaderFactory, times(1)).getDictByType("yes_no");
    }

    private static SysDictServiceImpl dictService(DictLoaderFactory loaderFactory, SysDictMapper mapper, RedisCache redisCache) {
        SysDictServiceImpl service = new SysDictServiceImpl(mock(SysDictTypeMapper.class), mock(SysDictTypeService.class), redisCache,
                mock(AdminScriptExportService.class), loaderFactory, mock(com.sz.platform.socket.SocketService.class));
        ReflectionTestUtils.setField(service, "mapper", mapper);
        return service;
    }

    private static DictVO dict(String id, String name, String typeCode) {
        return DictVO.builder().id(id).codeName(name).sysDictTypeCode(typeCode).build();
    }
}
