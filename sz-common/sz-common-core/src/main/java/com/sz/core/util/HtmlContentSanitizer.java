package com.sz.core.util;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

/**
 * 通用 HTML 内容安全净化器。
 *
 * <p>
 * 适用于需要保存或展示 HTML 片段的业务，例如富文本编辑器内容、CMS 文章、公告说明、 Markdown 转换后的 HTML，以及从外部系统导入的
 * HTML 片段。它会保留常用的标题、段落、 列表、引用、表格、图片、链接和受控的行内样式，同时移除脚本、事件属性、危险 URL、 可执行嵌入内容、外部
 * CSS 资源和可覆盖页面的定位样式。
 * </p>
 *
 * <p>
 * 本工具不适用于完整 HTML 页面模板，也不适用于必须保留 JavaScript、iframe、SVG、 MathML、远程 CSS
 * 背景资源或绝对/固定定位的内容。它不能替代接口鉴权、输出编码、 Content Security Policy 等其他安全措施。
 * </p>
 */
@Component
public class HtmlContentSanitizer {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.BLOCKS).and(Sanitizers.LINKS).and(Sanitizers.TABLES).and(Sanitizers.IMAGES)
            .and(HtmlContentPolicy.create());

    /**
     * 净化一个可为空的 HTML 片段。
     *
     * @param html
     *            待净化的 HTML 片段；传入 {@code null} 时直接返回 {@code null}
     * @return 仅包含允许元素、属性、URL 和样式的 HTML 片段
     */
    public String sanitize(String html) {
        return html == null ? null : POLICY.sanitize(html);
    }
}
