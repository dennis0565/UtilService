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
 * @version Create on: 2023/10/26 9:56
 * 类说明
 */
@Data
@EqualsAndHashCode
public class MitigationExcel {
    @ExcelProperty("缓解措施编码")
    private String code;
    @ExcelProperty("缓解措施名称")
    private String name;
    @ExcelProperty("缓解措施中文名称")
    private String nameZh;
    @ExcelProperty("缓解措施描述")
    private String description;
    @ExcelProperty("缓解措施描述中文")
    private String descriptionZh;
}
