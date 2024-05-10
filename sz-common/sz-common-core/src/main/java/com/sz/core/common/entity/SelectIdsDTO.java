package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SelectIdsDTO {

    {
        ids = new ArrayList<>();
    }

    @Schema(description = "选择的标识数组")
    private List<?> ids;

    public SelectIdsDTO() {
    }

    public SelectIdsDTO(List<?> ids) {
        this.ids = ids;
    }


}
