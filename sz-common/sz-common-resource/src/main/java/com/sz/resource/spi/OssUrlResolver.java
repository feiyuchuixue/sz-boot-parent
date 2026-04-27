package com.sz.resource.spi;

/**
 * OSS URL 自定义解析器接口。
 *
 * <p>
 * 当默认 {@link com.sz.resource.service.ResourceService#resolveUrl} 无法满足需求时实现此接口，
 * 注册为 Spring Bean 后通过
 * {@link com.sz.resource.annotation.OssUrlFill#resolverClass()} 引用。
 * </p>
 *
 * <pre>
 * 
 * {
 *     &#64;code
 *     &#64;Component
 *     public class SsoAvatarUrlResolver implements OssUrlResolver<SsoUserVO> {
 * 
 *         @Override
 *         public String resolve(String sceneCode, SsoUserVO vo, String fieldValue) {
 *             if (AVATAR_SOURCE_OAUTH.equals(vo.getAvatarsSource())) {
 *                 return vo.getAvatarsOauthUrl();
 *             }
 *             return resourceService.resolveUrl(sceneCode, fieldValue);
 *         }
 *     }
 * }
 * </pre>
 *
 * @param <T>
 *            VO 类型，使 {@link #resolve} 第二个参数类型安全，无需实现类内部强转
 */
public interface OssUrlResolver<T> {

    /**
     * {@link com.sz.resource.annotation.OssUrlFill#resolverClass()} 的默认值占位符，
     * 表示"未指定自定义解析器"，不可实例化调用。
     */
    final class None implements OssUrlResolver<Object> {

        private None() {
        }

        @Override
        public String resolve(String sceneCode, Object vo, String fieldValue) {
            throw new UnsupportedOperationException("OssUrlResolver.None 仅作占位符，不可调用");
        }
    }

    /**
     * 解析并返回完整回显 URL。
     *
     * @param sceneCode
     *            场景码
     * @param vo
     *            当前 VO 对象（类型安全）
     * @param fieldValue
     *            objectKey 字段当前值
     * @return 完整 URL；返回 {@code null} 时框架跳过覆写
     */
    String resolve(String sceneCode, T vo, String fieldValue);

    /**
     * 框架内部调用入口，屏蔽泛型擦除。类型不匹配时抛 {@link ClassCastException}，来源明确。
     */
    @SuppressWarnings("unchecked")
    default String resolveUnchecked(String sceneCode, Object vo, String fieldValue) {
        return resolve(sceneCode, (T) vo, fieldValue);
    }
}
