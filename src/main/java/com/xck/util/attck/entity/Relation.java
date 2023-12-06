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
 * @version Create on: 2023/10/31 11:00
 * 类说明
 */
@Data
@EqualsAndHashCode
public class Relation {
    @ExcelProperty("sql")
    private String sql;
}
