package com.zichang.zcmanage.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationInfo {

    private String building_code;

    private String building_name;

    private String floor;
}
