package com.zichang.zcmanage.mapper;

import com.zichang.zcmanage.model.data.ExcelData;
import com.zichang.zcmanage.model.domain.Assets;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author lenvovo
* @description 针对表【assets(设备表)】的数据库操作Mapper
* @createDate 2024-12-02 00:02:49
* @Entity generator.domain.Assets
*/
public interface AssetsMapper extends BaseMapper<Assets> {
    List<ExcelData> selectExcelData();
}




