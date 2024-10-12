package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SelectIdsDTO {

    {
        ids = new ArrayList<>();
    }

    @Schema(description = "选择的标识数组")
    private List<? extends Serializable> ids;

    public SelectIdsDTO() {
    }

    public SelectIdsDTO(List<? extends Serializable> ids) {
        this.ids = ids;
    }

}
