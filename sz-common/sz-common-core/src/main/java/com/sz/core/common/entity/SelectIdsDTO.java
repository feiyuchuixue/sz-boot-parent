package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SelectIdsDTO {

    @Schema(description = "选择的标识数组")
    private List<Long> ids = new ArrayList<>();

    public SelectIdsDTO() {
    }

    public SelectIdsDTO(List<Long> ids) {
        this.ids = ids;
    }

}
