package com.zichang.zcmanage.controller;

import cn.hutool.core.collection.CollUtil;
import com.zichang.zcmanage.common.ErrorCode;
import com.zichang.zcmanage.constant.UserConstant;
import com.zichang.zcmanage.exception.ThrowUtils;
import com.zichang.zcmanage.listen.EasyExcelService;
import com.zichang.zcmanage.model.data.ExcelData;
import com.zichang.zcmanage.model.domain.User;
import com.zichang.zcmanage.service.AssetsService;
import com.zichang.zcmanage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/excel")
@Slf4j
public class EasyExcelController {

    @Resource
    private EasyExcelService easyExcelService;

    @Resource
    private UserService userService;

    @Resource
    private AssetsService assetsService;

    /**
     * 表格导入数据库
     * @param multipartFile
     * @return
     */
    @PostMapping("/read")
    public void readExcel(@RequestParam(value = "multipartFile") MultipartFile multipartFile, HttpServletResponse response) throws IOException {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        List<ExcelData> errorDataList = easyExcelService.importAllDatas(multipartFile);
        if (CollUtil.isNotEmpty(errorDataList)) {
            log.error("导入失败的数据：{}", errorDataList);
            // 将 errorDataList 导出为表格并且浏览器自动下载此表格文件
            easyExcelService.extracted("需要重新导入的数据", errorDataList, response);
        } else {
            // 返回 JSON 响应
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            // 构建 JSON 响应内容
            String jsonResponse = "{\"code\": 0, \"data\": true, \"message\": \"所有数据均成功导入\"}";
            try (PrintWriter writer = response.getWriter()) {
                writer.write(jsonResponse);
                writer.flush();
            }
        }
    }

    /**
     * 导出为表格
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/export")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User loginUser = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new RuntimeException("无权限");
        }
        //获取所有资产数据
        List<ExcelData> excelDataList = assetsService.selectAllData();
        String fileName = "资产信息表";
        //执行表格导出
        easyExcelService.extracted(fileName, excelDataList, response);
    }


}
