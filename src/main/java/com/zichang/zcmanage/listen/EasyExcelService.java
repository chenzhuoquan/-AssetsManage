package com.zichang.zcmanage.listen;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.zichang.zcmanage.exception.BusinessException;
import com.zichang.zcmanage.model.data.ExcelData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EasyExcelService {

    private static final Logger log = LoggerFactory.getLogger(EasyExcelService.class);


    @Resource
    private ExcelAllDataImport excelAllDataImport;


    public List<ExcelData> importAllDatas(MultipartFile multipartFile) {

        //临时存储需要插入数据
        List<ExcelData> excelDataList = new ArrayList<>();

        Map<String, List<ExcelData>> map = new LinkedHashMap<>();

        //存储错误数据
        List<ExcelData> errorDataList = new ArrayList<>();

        try {
            EasyExcel.read(multipartFile.getInputStream(), ExcelData.class, new PageReadListener<ExcelData>(dataList -> {
                for (ExcelData excelData : dataList) {
                    map.computeIfAbsent(excelData.getDevice_code(), k -> new ArrayList<>()).add(excelData);
                }
            })).sheet().doRead();

        } catch (Exception e) {
            throw new BusinessException(401, "表格插入失败，请检查表格格式是否有问题");
        }


        for (Map.Entry<String, List<ExcelData>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                errorDataList.addAll(entry.getValue());
            } else {
                excelDataList.add(entry.getValue().get(0));
            }
        }

        //对device_code进行排序
        //excelDataList.sort(Comparator.comparing(ExcelData::getDevice_code).reversed());

        List<ExcelData> needManageList = excelAllDataImport.savaBatch(excelDataList);

        if (CollUtil.isNotEmpty(needManageList)) {
            errorDataList.addAll(needManageList);
        }

        return errorDataList;
    }

    public void extracted(String excelName,List<ExcelData> errorDataList, HttpServletResponse response) throws IOException {
        // 注意 使用swagger 会导致各种问题，easyexcel官方文档推荐直接用浏览器或者用postman测试
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码,和easyexcel没有关系
        String fileName = URLEncoder.encode(excelName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExcelData.class).sheet(excelName).doWrite(errorDataList);
    }


}