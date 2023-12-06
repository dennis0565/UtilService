package com.xck.util.attck.read;

import com.alibaba.excel.EasyExcel;

import com.xck.util.attck.entity.AttCk;
import com.xck.util.attck.listener.AttckListener;
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
public class ReadAttck {
    public static void main(String[] args) {
        String fileNameA = "C:\\Users\\54157\\Desktop\\文档\\ATT&CK\\2023-11-21\\Att&ck_v14.xlsx";
        AttckListener listener = new AttckListener();
        EasyExcel.read(new File(fileNameA), listener).sheet("模板_最终校对版本").headRowNumber(1).doRead();
        String first="";
        String second="";
        List<AttCk> firstList=new ArrayList<>();
        List<AttCk> secondList=new ArrayList<>();
        List<AttCk> thirdList=new ArrayList<>();
        for (AttCk attCk : listener.getDataList()) {
            if (StringUtils.isNotEmpty(attCk.getDesZnNew())){
                attCk.setDesZn(attCk.getDesZnNew());
            }
            if (StringUtils.isNotEmpty(attCk.getTechniqueNameZnNew())){
                attCk.setTechniqueNameZn(attCk.getTechniqueNameZnNew());
            }
            if (StringUtils.isNotEmpty(attCk.getSubTechniqueNameZnNew())){
                attCk.setSubTechniqueNameZn(attCk.getSubTechniqueNameZnNew());
            }
            if (StringUtils.isEmpty(attCk.getVersion())){
                attCk.setVersion("");
            }
            if (StringUtils.isEmpty(attCk.getPermissionsRequired())){
                attCk.setPermissionsRequired("");
            }
            if (StringUtils.isEmpty(attCk.getPlatforms())){
                attCk.setPlatforms("");
            }
            attCk.setDesZn(attCk.getDesZn().replace("'","\\'"));
            if (StringUtils.isNotEmpty(attCk.getTacticEncode())){
                first=attCk.getTacticNameZn();
                attCk.setAncestors(attCk.getTacticNameZn());
                attCk.setParentCode(null);
                firstList.add(attCk);
            }else if (StringUtils.isNotEmpty(attCk.getTechniqueEncode())){
                second=attCk.getTechniqueNameZn();
                attCk.setAncestors(first+"-"+attCk.getTechniqueNameZn());
                attCk.setParentCode(first);
                secondList.add(attCk);
            }else {
                attCk.setAncestors(first+"-"+second+"-"+attCk.getSubTechniqueNameZn());
                attCk.setParentCode(second);
                thirdList.add(attCk);
            }
        }



        for (int i = 0; i < firstList.size(); i++) {
            AttCk attCk = firstList.get(i);
            System.out.println("INSERT INTO `insight_alarm_type` " +
                    "(`code`, `name`, `description`, `type`, `parent_code`, `enable_status`, `flag`, `category_level`," +
                    " `name_en`, `encode`, `ancestors`, `sort_no`) VALUES ('"+
                    attCk.getTacticNameZn()+"','"+attCk.getTacticNameZn()+"','"+attCk.getDesZn()+"','ATT_CK',null,1,1,1,'"+
                    attCk.getTacticName()+ "','"+ attCk.getTacticEncode()+"','"+attCk.getAncestors()+"',"+i+");");
        }
        for (int i = 0; i < secondList.size(); i++) {
            AttCk attCk = secondList.get(i);
            System.out.println("INSERT INTO `insight_alarm_type` (" +
                    "`code`, `name`, `description`, `type`, `parent_code`, `enable_status`, `flag`, `category_level`, " +
                    " `create_time`, `update_time`, `name_en`, `encode`, `ancestors`, `sort_no`, `version`, `platforms`, " +
                    "`permissions_required`) VALUES ('"+
                    attCk.getTechniqueNameZn()+"','"+attCk.getTechniqueNameZn()+"','"+attCk.getDesZn()+"','ATT_CK','" +
                    attCk.getParentCode()+"',1,1,2,'"+attCk.getCreateTime()+"','"+attCk.getUpdateTime()+"','"+
                    attCk.getTechniqueName()+ "','"+attCk.getTechniqueEncode()+"','"+attCk.getAncestors()+"',"+i+",'"+
                    attCk.getVersion()+"','"+attCk.getPlatforms()+"','"+attCk.getPermissionsRequired()+"');");
        }
        for (int i = 0; i < thirdList.size(); i++) {
            AttCk attCk = thirdList.get(i);
            System.out.println("INSERT INTO `insight_alarm_type` (" +
                    "`code`, `name`, `description`, `type`, `parent_code`, `enable_status`, `flag`, `category_level`, " +
                    " `create_time`, `update_time`, `name_en`, `encode`, `ancestors`, `sort_no`, `version`, `platforms`, " +
                    "`permissions_required`) VALUES ('"+
                    attCk.getSubTechniqueNameZn()+"','"+attCk.getSubTechniqueNameZn()+"','"+attCk.getDesZn()+"','ATT_CK','" +
                    attCk.getParentCode()+"',1,1,3,'"+attCk.getCreateTime()+"','"+attCk.getUpdateTime()+"','"+
                    attCk.getSubTechniqueName()+ "','"+attCk.getSubTechniqueEncode()+"','"+attCk.getAncestors()+"',"+i+",'"+
                    attCk.getVersion()+"','"+attCk.getPlatforms()+"','"+attCk.getPermissionsRequired()+"');");
        }
    }

}
