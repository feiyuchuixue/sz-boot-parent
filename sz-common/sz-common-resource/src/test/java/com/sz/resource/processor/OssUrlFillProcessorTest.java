package com.sz.resource.processor;

import com.sz.core.common.entity.PageResult;
import com.sz.resource.annotation.OssUrlFill;
import com.sz.resource.driver.LocalResourceStorageDriver;
import com.sz.resource.driver.OssResourceStorageDriver;
import com.sz.resource.service.ResourceService;
import com.sz.resource.spi.OssUrlResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.StaticApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OssUrlFillProcessorTest {

    @Test
    void processReplacesObjectKeyForSingleObjectCollectionAndPageRows() {
        FakeResourceService resourceService = new FakeResourceService(false);
        OssUrlFillProcessor processor = new OssUrlFillProcessor(resourceService, new StaticApplicationContext());

        AvatarVO single = new AvatarVO("avatars/u1.png");
        AvatarVO inCollection = new AvatarVO("avatars/u2.png");
        AvatarVO inPage = new AvatarVO("avatars/u3.png");

        processor.process(single);
        processor.process(List.of(inCollection));
        processor.process(new PageResult<>(1, 10, 1, 1, List.of(inPage)));

        assertThat(single.avatar).isEqualTo("https://cdn.example/avatars/u1.png");
        assertThat(inCollection.avatar).isEqualTo("https://cdn.example/avatars/u2.png");
        assertThat(inPage.avatar).isEqualTo("https://cdn.example/avatars/u3.png");
        assertThat(resourceService.calls).containsExactly("avatar:avatars/u1.png", "avatar:avatars/u2.png", "avatar:avatars/u3.png");
    }

    @Test
    void processKeepsObjectKeyWhenUrlCannotBeResolvedAndSkipsBlankOrExistingUrl() {
        FakeResourceService resourceService = new FakeResourceService(true);
        OssUrlFillProcessor processor = new OssUrlFillProcessor(resourceService, new StaticApplicationContext());
        AvatarVO vo = new AvatarVO("avatars/token-only.png");
        vo.cover = "https://legacy.example/cover.png";
        vo.empty = " ";

        processor.process(vo);

        assertThat(vo.avatar).isEqualTo("avatars/token-only.png");
        assertThat(vo.cover).isEqualTo("https://legacy.example/cover.png");
        assertThat(vo.empty).isBlank();
        assertThat(resourceService.calls).containsExactly("avatar:avatars/token-only.png");
    }

    @Test
    void resolverClassHasPriorityOverNamedResolverAndDefaultResourceService() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("classResolver", new ClassResolver());
        context.getBeanFactory().registerSingleton("namedResolver", new NamedResolver());
        FakeResourceService resourceService = new FakeResourceService(false);
        OssUrlFillProcessor processor = new OssUrlFillProcessor(resourceService, context);
        ResolverPriorityVO vo = new ResolverPriorityVO();

        processor.process(vo);

        assertThat(vo.avatar).isEqualTo("class-resolved:avatars/u1.png");
        assertThat(resourceService.calls).isEmpty();
    }

    @Test
    void namedResolverIsUsedWhenResolverClassIsNotSpecified() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("namedResolver", new NamedResolver());
        OssUrlFillProcessor processor = new OssUrlFillProcessor(new FakeResourceService(false), context);
        NamedResolverVO vo = new NamedResolverVO();

        processor.process(vo);

        assertThat(vo.avatar).isEqualTo("named-resolved:avatars/u1.png");
    }

    private static class AvatarVO {

        @OssUrlFill(sceneCode = "avatar")
        private String avatar;

        @OssUrlFill(sceneCode = "cover")
        private String cover;

        @OssUrlFill(sceneCode = "empty")
        private String empty;

        @OssUrlFill(sceneCode = "invalid")
        private Long nonString = 1L;

        AvatarVO(String avatar) {
            this.avatar = avatar;
        }
    }

    private static class ResolverPriorityVO {

        @OssUrlFill(sceneCode = "avatar", resolverClass = ClassResolver.class, customResolver = "namedResolver")
        private String avatar = "avatars/u1.png";
    }

    private static class NamedResolverVO {

        @OssUrlFill(sceneCode = "avatar", customResolver = "namedResolver")
        private String avatar = "avatars/u1.png";
    }

    private static class ClassResolver implements OssUrlResolver<ResolverPriorityVO> {

        @Override
        public String resolve(String sceneCode, ResolverPriorityVO vo, String fieldValue) {
            return "class-resolved:" + fieldValue;
        }
    }

    private static class NamedResolver implements OssUrlResolver<Object> {

        @Override
        public String resolve(String sceneCode, Object vo, String fieldValue) {
            return "named-resolved:" + fieldValue;
        }
    }

    private static class FakeResourceService extends ResourceService {

        private final boolean returnNull;

        private final List<String> calls = new ArrayList<>();

        FakeResourceService(boolean returnNull) {
            super(null, (LocalResourceStorageDriver) null, new EmptyOssDriverProvider(), null, null);
            this.returnNull = returnNull;
        }

        @Override
        public String resolveUrl(String sceneCode, String objectKey) {
            calls.add(sceneCode + ":" + objectKey);
            return returnNull ? null : "https://cdn.example/" + objectKey;
        }
    }

    private static class EmptyOssDriverProvider implements ObjectProvider<OssResourceStorageDriver> {

        @Override
        public OssResourceStorageDriver getObject(Object... args) {
            return null;
        }

        @Override
        public OssResourceStorageDriver getIfAvailable() {
            return null;
        }

        @Override
        public OssResourceStorageDriver getIfUnique() {
            return null;
        }

        @Override
        public OssResourceStorageDriver getObject() {
            return null;
        }

        @Override
        public Iterator<OssResourceStorageDriver> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public Stream<OssResourceStorageDriver> stream() {
            return Stream.empty();
        }

        @Override
        public Stream<OssResourceStorageDriver> orderedStream() {
            return Stream.empty();
        }
    }
}
