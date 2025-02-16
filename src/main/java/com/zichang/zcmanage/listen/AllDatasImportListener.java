/*
package com.yupi.springbootinit.listen;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yupi.springbootinit.model.data.ExcelData;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AllDatasImportListener extends AnalysisEventListener<ExcelData> {


    private final ExcelAllDataImport excelAllDataImport;

    private static final Logger LOGGER = LoggerFactory.getLogger(AllDatasImportListener.class);



    public AllDatasImportListener(ExcelAllDataImport excelAllDataImport) {
        this.excelAllDataImport = excelAllDataImport;
    }


    //临时存储数据
    private List<ExcelData> excelDataList=new ArrayList<>();




    @Override
    public void invoke(ExcelData excelData, AnalysisContext analysisContext) {
       excelDataList.add(excelData);
    }



    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {


        List<ExcelData> excelData = excelAllDataImport.savaBatch(excelDataList);
        if(excelData.size()>0){

        }
        System.out.println("需要重新插入的数据有"+excelData.size());
        for (ExcelData excelData1 : excelData) {
            System.out.println("分别有:"+excelData1);
        }

    }


}
*/
