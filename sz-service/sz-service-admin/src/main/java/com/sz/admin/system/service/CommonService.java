package com.sz.admin.system.service;

import com.sz.admin.system.pojo.dto.common.SelectorQueryDTO;
import com.sz.admin.system.pojo.vo.common.SelectorVO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface CommonService {

    void tempDownload(String templateName, HttpServletResponse response) throws IOException;

    SelectorVO querySelector(SelectorQueryDTO queryDTO);
}
