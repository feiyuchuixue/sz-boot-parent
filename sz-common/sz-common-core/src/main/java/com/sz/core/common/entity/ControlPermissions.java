package com.sz.core.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ControlPermissions {

    private String[] permissions;

    private String mode;

}
