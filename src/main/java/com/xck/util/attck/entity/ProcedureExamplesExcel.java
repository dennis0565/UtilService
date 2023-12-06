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
public class ProcedureExamplesExcel {
    @ExcelProperty("攻击案例编码")
    private String code;
    @ExcelProperty("攻击案例名称")
    private String name;
    @ExcelProperty("攻击案例中文名称")
    private String nameZh;
    @ExcelProperty("攻击案例描述")
    private String description;
    @ExcelProperty("攻击案例描述中文")
    private String descriptionZh;
    @ExcelProperty("新翻译")
    private String descriptionZhNew;
}
