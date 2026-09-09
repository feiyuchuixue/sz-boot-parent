package com.sz.resource.processor;

import com.sz.core.common.entity.PageResult;
import com.sz.resource.annotation.OssUrlFill;
import com.sz.resource.service.ResourceService;
import com.sz.resource.spi.OssUrlResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OSS URL 填充处理器。
 *
 * <p>
 * 将 {@link OssUrlFill} 标注字段的 objectKey 原地替换为完整可访问 URL，前后端共用同一字段名。
 * </p>
 *
 * <p>
 * 使用场景：
 * </p>
 * <ul>
 * <li>Controller 返回值：由 {@code OssUrlFillAspect} 自动触发，无需手动调用</li>
 * <li>非 Controller 场景（消息 handler、定时任务等）：注入本 Bean，调用
 * {@link #process(Object)}</li>
 * </ul>
 *
 * <p>
 * 字段反射结果缓存于 {@link #FIELD_CACHE}，同一类型仅扫描一次；无注解类缓存空列表直接跳过。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssUrlFillProcessor {

    private final ResourceService resourceService;

    private final ApplicationContext applicationContext;

    private static final ConcurrentHashMap<Class<?>, List<FieldEntry>> FIELD_CACHE = new ConcurrentHashMap<>(64);

    private record FieldEntry(Field field, OssUrlFill annotation) {
    }

    /**
     * 对任意对象执行 OSS URL 原地解析。 支持 {@link PageResult}（处理
     * rows）、{@link Collection}（处理每个元素）及单个对象。
     */
    public void process(Object obj) {
        if (obj == null)
            return;
        if (obj instanceof PageResult<?> pageResult) {
            List<?> rows = pageResult.getRows();
            if (rows != null)
                rows.forEach(this::resolveFields);
        } else if (obj instanceof Collection<?> collection) {
            collection.forEach(this::resolveFields);
        } else {
            resolveFields(obj);
        }
    }

    private void resolveFields(Object obj) {
        if (obj == null)
            return;
        List<FieldEntry> entries = getFieldEntries(obj.getClass());
        if (entries.isEmpty())
            return;
        for (FieldEntry entry : entries) {
            try {
                String objectKey = (String) entry.field().get(obj);
                if (objectKey == null || objectKey.isBlank())
                    continue;
                // 已是完整 URL（http/https 开头）则跳过，兼容历史数据
                if (objectKey.startsWith("http://") || objectKey.startsWith("https://"))
                    continue;
                String url = resolveUrl(entry.annotation(), obj, objectKey);
                if (url != null)
                    entry.field().set(obj, url);
            } catch (ClassCastException e) {
                log.warn("[OssUrlFill] resolver 类型不匹配，字段={}.{}，resolver={}", obj.getClass().getSimpleName(), entry.field().getName(),
                        resolverDesc(entry.annotation()), e);
            } catch (Exception e) {
                log.warn("[OssUrlFill] 解析失败，字段={}.{}", obj.getClass().getSimpleName(), entry.field().getName(), e);
            }
        }
    }

    private String resolveUrl(OssUrlFill annotation, Object vo, String objectKey) {
        Class<? extends OssUrlResolver<?>> resolverClass = annotation.resolverClass();
        if (resolverClass != OssUrlResolver.None.class) {
            return applicationContext.getBean(resolverClass).resolveUnchecked(annotation.sceneCode(), vo, objectKey);
        }
        String beanName = annotation.customResolver();
        if (!beanName.isBlank()) {
            return applicationContext.getBean(beanName, OssUrlResolver.class).resolveUnchecked(annotation.sceneCode(), vo, objectKey);
        }
        return resourceService.resolveUrl(annotation.sceneCode(), objectKey);
    }

    private static String resolverDesc(OssUrlFill annotation) {
        if (annotation.resolverClass() != OssUrlResolver.None.class)
            return annotation.resolverClass().getSimpleName();
        return annotation.customResolver().isBlank() ? "default" : annotation.customResolver();
    }

    private List<FieldEntry> getFieldEntries(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, this::buildFieldEntries);
    }

    private List<FieldEntry> buildFieldEntries(Class<?> clazz) {
        List<FieldEntry> entries = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                OssUrlFill annotation = field.getAnnotation(OssUrlFill.class);
                if (annotation == null)
                    continue;
                if (!String.class.equals(field.getType())) {
                    log.warn("[OssUrlFill] 仅支持 String 类型字段，已跳过：{}.{}", clazz.getSimpleName(), field.getName());
                    continue;
                }
                field.setAccessible(true);
                entries.add(new FieldEntry(field, annotation));
            }
            current = current.getSuperclass();
        }
        return entries.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(entries);
    }
}
