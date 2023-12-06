package com.xck.util.attck.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.xck.util.attck.entity.AttCk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2023 IDSS
 * <p>
 * All right reserved.
 *
 * @author 夏城柯 E-mail: xiack@idss-cn.com
 * @version Create on: 2023/10/31 14:02
 * 类说明
 */
public class AttckListener extends AnalysisEventListener<Map<Integer, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MitigationListener.class);


    /**
     * 数据体
     */
    private List<AttCk> dataList = new ArrayList<>();

    private List<Object> headList = new ArrayList<>();

    private List<String> errorList = new ArrayList<>();


    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        LOGGER.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
        headList.add(headMap);
    }


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {

        LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
        AttCk attCk = new AttCk();
        attCk.setTacticEncode(data.get(0));
        attCk.setTacticName(data.get(1));
        attCk.setTacticNameZn(data.get(2));
        attCk.setTechniqueEncode(data.get(3));
        attCk.setTechniqueName(data.get(4));
        attCk.setTechniqueNameZn(data.get(5));
        attCk.setTechniqueNameZnNew(data.get(6));
        attCk.setSubTechniqueEncode(data.get(7));
        attCk.setSubTechniqueName(data.get(8));
        attCk.setSubTechniqueNameZn(data.get(9));
        attCk.setSubTechniqueNameZnNew(data.get(10));
        attCk.setPlatforms(data.get(11));
        attCk.setPermissionsRequired(data.get(12));
        attCk.setVersion(data.get(13));
        attCk.setCreateTime(data.get(14));
        attCk.setUpdateTime(data.get(15));
        attCk.setDes(data.get(16));
        attCk.setDesZn(data.get(17));
        attCk.setDesZnNew(data.get(19));
        dataList.add(attCk);
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        LOGGER.info("所有数据解析完成！");
    }


    public List<AttCk> getDataList() {
        return dataList;
    }


    public List<Object> getHeadList() {
        return headList;
    }

    public void setHeadList(List<Object> headList) {
        this.headList = headList;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

}
