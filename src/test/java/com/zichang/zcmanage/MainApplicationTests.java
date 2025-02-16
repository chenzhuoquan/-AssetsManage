package com.zichang.zcmanage;

import com.zichang.zcmanage.mapper.AssetsMapper;
import com.zichang.zcmanage.model.data.ExcelData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 主类测试
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private AssetsMapper assetsMapper;

    @Test
    void selectAllData() {
        List<ExcelData> excelDataList = assetsMapper.selectExcelData();
        for (ExcelData excelData : excelDataList) {
            System.out.println(excelData);
        }
    }

}
