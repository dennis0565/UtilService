package com.xck.util.attck.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2023 IDSS
 * <p>
 * All right reserved.
 *
 * @author 夏城柯 E-mail: xiack@idss-cn.com
 * @version Create on: 2023/10/19 17:26
 * 类说明
 */
@Data
public class LinkType {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private String type;
    private List<DataComponent> dataComponent;

    @Data
    public static class DataComponent{
        private String name;
        private String description;
    }
    public void addData(DataComponent data){
        if (this.dataComponent==null){
            List<DataComponent> list=new ArrayList<>();
            list.add(data);
            this.dataComponent= list;
        }else {
            if (data!=null){
                this.dataComponent.add(data);
            }
        }

    }
}
