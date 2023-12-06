package com.xck.util.attck.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.xck.util.attck.entity.DetectionExcel;
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
 * @version Create on: 2023/10/31 11:25
 * 类说明
 */
public class DetectionListener extends AnalysisEventListener<Map<Integer, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MitigationListener.class);


    /**
     * 数据体
     */
    private List<DetectionExcel> dataList = new ArrayList<>();

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
        DetectionExcel detectionExcel = new DetectionExcel();
        detectionExcel.setCode(data.get(0));
        detectionExcel.setName(data.get(1));
        detectionExcel.setNameZh(data.get(2));
        detectionExcel.setDataComponent(data.get(3));
        detectionExcel.setDataComponentZh(data.get(4));
        detectionExcel.setDetects(data.get(5));
        detectionExcel.setDetectsZn(data.get(6));
        dataList.add(detectionExcel);
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


    public List<DetectionExcel> getDataList() {
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
