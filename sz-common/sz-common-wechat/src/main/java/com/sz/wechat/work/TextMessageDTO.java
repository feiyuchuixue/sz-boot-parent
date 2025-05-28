package com.sz.wechat.work;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TextMessageDTO - 简要描述该类的功能.
 * <p>
 * 详细描述类的功能或用途（可选）。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2025/4/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextMessageDTO {

    private String accessToken;

    private List<String> toUserList;

    private String content;

    private Integer safe;

}
