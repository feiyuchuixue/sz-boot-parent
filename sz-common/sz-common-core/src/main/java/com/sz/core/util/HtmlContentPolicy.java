package com.sz.core.util;

import org.owasp.html.AttributePolicy;
import org.owasp.html.CssSchema;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlStreamEventProcessor;
import org.owasp.html.HtmlStreamEventReceiver;
import org.owasp.html.PolicyFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * {@link HtmlContentSanitizer} 的内部 OWASP 策略。
 *
 * <p>
 * 该策略在 OWASP 默认文本样式规则上补充常见 HTML 内容布局能力，例如渐变文字裁剪、 Flex
 * 换行、间距、内容宽度和居中属性。补充规则仍采用属性和值白名单，不允许 CSS 外链、 绝对或固定定位等可能突破内容展示区域的能力。
 * </p>
 */
final class HtmlContentPolicy {

    // CssSchema.Property 的公开构造器使用该位表示长度、百分比等数量值；聚焦测试固定当前 OWASP 版本的行为。
    private static final int CSS_QUANTITY = 1;

    private static final String[] HTML_CONTENT_ELEMENTS = {"a", "abbr", "b", "blockquote", "br", "code", "del", "div", "em", "figcaption", "figure", "h1", "h2",
            "h3", "h4", "h5", "h6", "hr", "i", "img", "li", "ol", "p", "pre", "s", "span", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th",
            "thead", "tr", "u", "ul"};

    private static final String[] ALIGNABLE_ELEMENTS = {"div", "p", "h1", "h2", "h3", "h4", "h5", "h6", "table", "thead", "tbody", "tfoot", "tr", "th", "td"};

    private static final Pattern SAFE_CLASS_NAMES = Pattern.compile("[A-Za-z0-9_-]+(?:[\\t\\n\\f\\r ]+[A-Za-z0-9_-]+)*");

    private static final Pattern GRADIENT_TEXT_BACKGROUND = Pattern.compile(
            "(?:^|;)\\s*background\\s*:\\s*(?:repeating-)?(?:linear|radial)-gradient\\([^;]*\\)\\s+text" + "(?:\\s*!important)?\\s*(?:;|$)",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern BACKGROUND_CLIP = Pattern.compile("(?:^|;)\\s*background-clip\\s*:", Pattern.CASE_INSENSITIVE);

    private static final AttributePolicy SAFE_CLASS_POLICY = (elementName, attributeName, value) -> {
        String normalized = value == null ? "" : value.trim().replaceAll("\\s+", " ");
        return normalized.length() <= 512 && SAFE_CLASS_NAMES.matcher(normalized).matches() ? normalized : null;
    };

    private static final AttributePolicy SAFE_ALIGN_POLICY = (elementName, attributeName, value) -> {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return Set.of("left", "center", "right", "justify").contains(normalized) ? normalized : null;
    };

    private static final AttributePolicy SAFE_TARGET_POLICY = (elementName, attributeName, value) -> {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return Set.of("_blank", "_self").contains(normalized) ? normalized : null;
    };

    private HtmlContentPolicy() {
    }

    static PolicyFactory create() {
        return new HtmlPolicyBuilder().allowElements(HTML_CONTENT_ELEMENTS).allowAttributes("class").matching(SAFE_CLASS_POLICY)
                .onElements(HTML_CONTENT_ELEMENTS).allowAttributes("align").matching(SAFE_ALIGN_POLICY).onElements(ALIGNABLE_ELEMENTS).allowAttributes("target")
                .matching(SAFE_TARGET_POLICY).onElements("a").requireRelsOnLinks("noopener", "noreferrer").allowStyling(createHtmlContentCssSchema())
                .withPreprocessor(normalizeGradientTextBackground()).toFactory();
    }

    private static CssSchema createHtmlContentCssSchema() {
        Set<String> defaultProperties = new HashSet<>(CssSchema.DEFAULT.allowedProperties());
        defaultProperties.remove("width");

        Map<String, CssSchema.Property> compatibleProperties = new HashMap<>();
        compatibleProperties.put("background-clip", keywords("border-box", "content-box", "padding-box", "text"));
        compatibleProperties.put("display", keywords("block", "inline", "inline-block", "flex", "inline-flex", "grid", "inline-grid", "none"));
        compatibleProperties.put("flex-wrap", keywords("nowrap", "wrap", "wrap-reverse"));
        compatibleProperties.put("justify-content", keywords("normal", "start", "end", "center", "flex-start", "flex-end", "left", "right", "space-between",
                "space-around", "space-evenly", "stretch"));
        compatibleProperties.put("gap", quantities("normal", "initial", "inherit", "unset"));
        compatibleProperties.put("width", quantities("auto", "inherit", "initial", "unset", "max-content", "min-content", "fit-content"));

        return CssSchema.union(CssSchema.withProperties(defaultProperties), CssSchema.withProperties(List.of("overflow-wrap")),
                CssSchema.withProperties(compatibleProperties));
    }

    private static CssSchema.Property keywords(String... values) {
        return new CssSchema.Property(0, Set.of(values), Map.of());
    }

    private static CssSchema.Property quantities(String... values) {
        return new CssSchema.Property(CSS_QUANTITY, Set.of(values), Map.of());
    }

    private static HtmlStreamEventProcessor normalizeGradientTextBackground() {
        return receiver -> new HtmlStreamEventReceiver() {

            @Override
            public void openDocument() {
                receiver.openDocument();
            }

            @Override
            public void closeDocument() {
                receiver.closeDocument();
            }

            @Override
            public void openTag(String elementName, List<String> attributes) {
                List<String> normalizedAttributes = new ArrayList<>(attributes);
                for (int index = 0; index + 1 < normalizedAttributes.size(); index += 2) {
                    if ("style".equals(normalizedAttributes.get(index))) {
                        String style = normalizedAttributes.get(index + 1);
                        normalizedAttributes.set(index + 1, normalizeGradientTextBackground(style));
                    }
                }
                receiver.openTag(elementName, normalizedAttributes);
            }

            @Override
            public void closeTag(String elementName) {
                receiver.closeTag(elementName);
            }

            @Override
            public void text(String text) {
                receiver.text(text);
            }
        };
    }

    private static String normalizeGradientTextBackground(String style) {
        if (style == null || BACKGROUND_CLIP.matcher(style).find() || !GRADIENT_TEXT_BACKGROUND.matcher(style).find()) {
            return style;
        }
        return style + (style.stripTrailing().endsWith(";") ? "" : ";") + "background-clip:text";
    }
}
