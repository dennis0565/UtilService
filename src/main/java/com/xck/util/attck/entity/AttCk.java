package com.xck.util.attck.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Copyright 2023 IDSS
 * <p>
 * All right reserved.
 *
 * @author 夏城柯 E-mail: xiack@idss-cn.com
 * @version Create on: 2023/10/19 16:09
 * 类说明
 */
@Data
@EqualsAndHashCode
public class AttCk {
    @ExcelProperty("战术编码")
    private String tacticEncode;
    @ExcelProperty("战术名称")
    private String tacticName;
    @ExcelProperty("战术中文名称")
    private String tacticNameZn;

    @ExcelProperty("技术编码")
    private String techniqueEncode;
    @ExcelProperty("技术名称")
    private String techniqueName;
    @ExcelProperty("技术中文名称")
    private String techniqueNameZn;
    @ExcelProperty("技术名称新翻译")
    private String techniqueNameZnNew;

    @ExcelProperty("子技术编码")
    private String subTechniqueEncode;
    @ExcelProperty("子技术名称")
    private String subTechniqueName;
    @ExcelProperty("子技术中文名称")
    private String subTechniqueNameZn;
    @ExcelProperty("子技术名称新翻译")
    private String subTechniqueNameZnNew;

    @ExcelProperty("平台")
    private String platforms;
    @ExcelProperty("权限需求")
    private String permissionsRequired;
    @ExcelProperty("版本")
    private String version;
    @ExcelProperty("创建时间")
    private String createTime;
    @ExcelProperty("更新时间")
    private String updateTime;
    @ExcelProperty("描述")
    private String des;
    @ExcelProperty("描述中文名称")
    private String desZn;
    @ExcelProperty("新翻译")
    private String desZnNew;

    @ExcelIgnore
    private List<String> mitigations;
    @ExcelIgnore
    private List<String> detection;
    @ExcelIgnore
    private List<String> procedureExamples;
    @ExcelIgnore
    private String parentCode;
    @ExcelIgnore
    private String ancestors;
}
