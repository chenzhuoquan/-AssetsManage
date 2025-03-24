package com.zichang.zcmanage.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
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
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

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
    public void exportExcel(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 权限校验
        User loginUser = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new RuntimeException("无权限");
        }
        // 设置响应头
        String fileName = "资产信息表";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");


        // 分页参数
        int pageSize = 1000;
        int pageNum = 1;

        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), ExcelData.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(fileName).build();

            while (true) {
                // 分页查询
                List<ExcelData> pageData = assetsService.selectAllData(pageNum, pageSize);
                if (pageData.isEmpty()) {
                    break;
                }
                // 写入当前页
                excelWriter.write(pageData, writeSheet);
                pageNum++;
                // 手动清理内存
                pageData.clear();
            }
        } catch (Exception e) {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> result = Map.of("error", "导出失败: " + e.getMessage());
            response.getWriter().println(JSONUtil.toJsonStr(result));
        }
        stopWatch.stop();
        System.out.println("导出接口总耗时: " + stopWatch.getTotalTimeMillis() + "ms");
    }


}
