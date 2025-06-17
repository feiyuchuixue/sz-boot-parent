package com.sz.ssoadmin.system.service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface CommonService {

    void tempDownload(String templateName, HttpServletResponse response) throws IOException;
}
