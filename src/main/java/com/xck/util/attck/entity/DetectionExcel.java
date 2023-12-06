package com.xck.util.attck.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Copyright 2023 IDSS
 * <p>
 * All right reserved.
 *
 * @author 夏城柯 E-mail: xiack@idss-cn.com
 * @version Create on: 2023/10/26 9:58
 * 类说明
 */
@Data
@EqualsAndHashCode
public class DetectionExcel {
    @ExcelProperty("检测方法编码")
    private String code;
    @ExcelProperty("数据源名称")
    private String name;
    @ExcelProperty("数据源中文名称")
    private String nameZh;
    @ExcelProperty("数据成分名称")
    private String dataComponent;
    @ExcelProperty("数据成分中文名称")
    private String dataComponentZh;
    @ExcelProperty("发现")
    private String detects;
    @ExcelProperty("发现中文")
    private String detectsZn;
}
