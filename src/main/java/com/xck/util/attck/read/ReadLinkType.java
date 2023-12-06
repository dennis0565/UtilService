package com.xck.util.attck.read;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONArray;

import com.xck.util.attck.entity.DetectionExcel;
import com.xck.util.attck.entity.LinkType;
import com.xck.util.attck.entity.MitigationExcel;
import com.xck.util.attck.entity.ProcedureExamplesExcel;
import com.xck.util.attck.listener.DetectionListener;
import com.xck.util.attck.listener.MitigationListener;
import com.xck.util.attck.listener.ProcedureExamplesListener;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2023 IDSS
 * <p>
 * All right reserved.
 *
 * @author 夏城柯 E-mail: xiack@idss-cn.com
 * @version Create on: 2023/10/31 11:03
 * 类说明
 */
public class ReadLinkType {

    public static void main(String[] args) {
        String fileNameD = "C:\\Users\\54157\\Desktop\\文档\\ATT&CK\\2023-11-21\\Detection.xlsx";
        String fileNameM = "C:\\Users\\54157\\Desktop\\文档\\ATT&CK\\2023-11-21\\Mitigations.xlsx";
        String fileNameP = "C:\\Users\\54157\\Desktop\\文档\\ATT&CK\\2023-11-21\\ProcedureExamples.xlsx";
        MitigationListener mitigationListener = new MitigationListener();
        ProcedureExamplesListener procedureExamplesListener = new ProcedureExamplesListener();
        DetectionListener detectionListener = new DetectionListener();
        EasyExcel.read(new File(fileNameM), mitigationListener).sheet("模板").headRowNumber(1).doRead();
        EasyExcel.read(new File(fileNameD), detectionListener).sheet("模板").headRowNumber(1).doRead();
        EasyExcel.read(new File(fileNameP), procedureExamplesListener).sheet("模板").headRowNumber(1).doRead();
        for (MitigationExcel mitigationExcel : mitigationListener.getDataList()) {
            System.out.println("INSERT INTO `insight_alarm_link_type` (`code`, `name`, `description`, `type`, `flag`, `create_time`, `update_time`) VALUES (" +
                    " '" +mitigationExcel.getCode()+
                    "', '" +mitigationExcel.getNameZh()+
                    "', '" +mitigationExcel.getDescriptionZh().replace("'","\\'")+
                    "', '1', '1', '2023-11-21 00:00:00', '2023-11-21 00:00:00');");
        }
        for (ProcedureExamplesExcel mitigationExcel : procedureExamplesListener.getDataList()) {
            if (StringUtils.isNotEmpty(mitigationExcel.getDescriptionZhNew())){
                System.out.println("INSERT INTO `insight_alarm_link_type` (`code`, `name`, `description`, `type`, `flag`, `create_time`, `update_time`) VALUES (" +
                        " '" +mitigationExcel.getCode()+
                        "', '" +mitigationExcel.getNameZh()+
                        "', '" +mitigationExcel.getDescriptionZhNew().replace("'","\\'")+
                        "', '2', '1', '2023-11-21 00:00:00', '2023-11-21 00:00:00');");
            }else {
                System.out.println("INSERT INTO `insight_alarm_link_type` (`code`, `name`, `description`, `type`, `flag`, `create_time`, `update_time`) VALUES (" +
                        " '" +mitigationExcel.getCode()+
                        "', '" +mitigationExcel.getNameZh()+
                        "', '" +mitigationExcel.getDescriptionZh().replace("'","\\'")+
                        "', '2', '1', '2023-11-21 00:00:00', '2023-11-21 00:00:00');");
            }

        }
        List<LinkType> linkTypeList = new ArrayList<>();
        LinkType linkType =new LinkType();
        for (DetectionExcel detectionExcel : detectionListener.getDataList()) {
            if (StringUtils.isNotEmpty(detectionExcel.getCode())){
                if (StringUtils.isNotEmpty(linkType.getCode())){
                    linkTypeList.add(linkType);
                }
                if (linkTypeList.size()!=0){
                    linkType =new LinkType();
                }
                linkType.setCode(detectionExcel.getCode());
                linkType.setName(detectionExcel.getNameZh());
            }
            LinkType.DataComponent dataComponent =new LinkType.DataComponent();
            dataComponent.setName(detectionExcel.getDataComponentZh());
            dataComponent.setDescription(detectionExcel.getDetectsZn());
            linkType.addData(dataComponent);
        }
        for (LinkType type : linkTypeList) {
            System.out.println("INSERT INTO `insight_alarm_link_type` (`code`, `name`, `description`, `type`, `flag`, `create_time`, `update_time`) VALUES (" +
                    " '" +type.getCode()+
                    "', '" +type.getName()+
                    "', '" + JSONArray.toJSONString(type.getDataComponent()).replace("'","\\'")+
                    "', '3', '1', '2023-11-21 00:00:00', '2023-11-21 00:00:00');");
        }
    }
}
