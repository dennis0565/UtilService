package com.xck.util.attck;


import com.alibaba.excel.EasyExcel;
import com.xck.entity.Constant;
import com.xck.util.attck.entity.AttCk;
import com.xck.util.attck.entity.LinkType;
import com.xck.util.attck.entity.Relation;
import com.xck.util.translate.aliyun.ALiYunTranslate;
import com.xck.util.translate.baidu.TransApi;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Copyright 2023 IDSS
 * <p>
 * All right reserved.
 *
 * @author 夏城柯 E-mail: xiack@idss-cn.com
 * @version Create on: 2023/10/18 14:00
 * 类说明
 */

public class CrawNsfocusScript {

    private String encode;
    private String token = "_gid=GA1.2.1346293388.1697609401; _gat_gtag_UA_62667723_1=1; _ga=GA1.1.891573726.1696735178; _ga_C8EHW4DS2X=GS1.1.1697609400.7.1.1697609409.0.0.0";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat oldFormat = new SimpleDateFormat("dd MM yyyy");
    private Set<LinkType> mitigations = new HashSet<>();
    private Set<String> mitigationId = new HashSet<>();

    private Set<LinkType> detection = new HashSet<>();
    private Set<String> detectionId = new HashSet<>();

    private Set<LinkType> procedureExamples = new HashSet<>();
    private Set<String> procedureExamplesId = new HashSet<>();
    private List<AttCk> attCks = new ArrayList<>();
    private List<String> relation = new ArrayList<>();
    private static final String APP_ID = "xxxxxxxxx";
    private static final String SECURITY_KEY = "xxxxxxxxxxx";
//    private static TransApi api = new TransApi(APP_ID, SECURITY_KEY);


    //阿里云的翻译id和key配置在代码中
    private static ALiYunTranslate api = new ALiYunTranslate();


    public List<String> crawTemplate(String encode, CrawNsfocusScript crawNsfocusScript) throws Exception {
        this.encode = encode;
        List<String> urlList = new ArrayList<String>();
        String bugInfoUrl = "https://attack.mitre.org/techniques/"
                + encode + "/";
        List<LinkType> linkTypes = new ArrayList<>();
        try {
            Document document = Jsoup
                    .connect(bugInfoUrl)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("Cookie", token)
                    .header("If-Modified-Since", "Fri, 01 Sep 2023 15:50:17 GMT")
                    .header("If-None-Match", "W/\"64f20839-4bea4\"")
                    .header("Keep-Alive", "timeout=5, max=100")
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
                    .timeout(500000).get();
            String returnReson = this.parseWebPage(document.toString());
            if (returnReson.equals("0")) {
                throw new Exception("服务器被禁，等待下周期再爬取漏洞！");
            }
            AttCk attCk = new AttCk();
            attCk.setTechniqueEncode(encode);
            String nameEn = document.select("h1").html().replace("-", " ");
            StringBuilder attdes = getStringBuilder(document);
            attCk.setDes(attdes.toString());
//            attCk.setDesZn(api.getTransResult(attdes.toString()));
            attCk.setTechniqueName(nameEn.trim());
//            attCk.setTechniqueNameZn(api.getTransResult(nameEn.trim()));
            Elements elementsByClass = document.getElementsByClass("col-md-11 pl-0");
            Element subtechnique = null;
            for (Element byClass : elementsByClass) {
                String title = byClass.select("span").html().replace(":&nbsp;", "");
                switch (title) {
                    case Constant.CREATE_TIME: {
                        String createTime = (((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0];
                        attCk.setCreateTime(dateFormat.format(new Date(Date.parse(createTime))));
                    }
                    break;
                    case Constant.UPDATE_TIME: {
                        String updateTime = (((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0];
                        attCk.setUpdateTime(dateFormat.format(new Date(Date.parse(updateTime))));
                    }
                    break;
                    case Constant.VERSION: {
                        attCk.setVersion((((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0]);

                    }
                    break;
                    case Constant.PLATFORMS: {
                        attCk.setPlatforms((((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0]);

                    }
                    break;
                    case Constant.PERMISSIONS_REQUIRED: {
                        attCk.setPermissionsRequired((((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0]);

                    }
                    break;
                    case Constant.SUB_TECHNIQUE: {
                        subtechnique = byClass;

                    }
                    break;
                    default: {

                    }
                }

            }
            Elements elementsByClass1 = document.getElementsByClass("table table-bordered table-alternate mt-2");
            for (Element element : elementsByClass1) {
                Elements select = element.select("tbody").select("tr");
                for (Element element1 : select) {
                    LinkType linkType = new LinkType();
                    Elements td = element1.select("td");
                    String code = td.get(0).select("a").html();
                    String name = td.get(1).select("a").html();
                    linkType.setCode(code);
                    linkType.setName(name);
                    if (code.startsWith("M")) {
                        linkType.setType("1");
                        StringBuilder des = new StringBuilder();
                        for (Node node : td) {
                            try {
                                String wholeText = ((TextNode) node).getWholeText();
                                des.append(wholeText);
                            } catch (Exception e) {
                                String html = ((Element) node).select("a").html().split("\n")[0];
                                if (html.contains("[")) {
                                    continue;
                                }
                                des.append(html);
                            }
                        }
                        linkType.setDescription(des.toString());
                        if (!mitigationId.contains(linkType.getCode())){
                            mitigations.add(linkType);
                            mitigationId.add(linkType.getCode());
                        }
                    } else {
                        linkType.setType("2");
                        List<Node> p = td.select("p").get(0).childNodes();
                        StringBuilder des = new StringBuilder();
                        for (Node node : p) {
                            try {
                                String wholeText = ((TextNode) node).getWholeText();
                                des.append(wholeText);
                            } catch (Exception e) {
                                String html = ((Element) node).select("a").html().split("\n")[0];
                                if (html.contains("[")) {
                                    continue;
                                }
                                des.append(html);
                            }
                        }
                        linkType.setDescription(des.toString());
                        if (!procedureExamplesId.contains(linkType.getCode())){
                            procedureExamples.add(linkType);
                            procedureExamplesId.add(linkType.getCode());
                        }

                    }
                    linkTypes.add(linkType);
                }
            }
            Elements elementsByClass2 = document.getElementsByClass("table datasources-table table-bordered");
            if (elementsByClass2.size() > 0) {
                Elements select = elementsByClass2.get(0).select("tbody").select("tr");

                LinkType linkType = new LinkType();
                for (int i = 0; i < select.size(); i++) {
                    Elements td = select.get(i).select("td");
                    String code = td.get(0).select("a").html();
                    String name = td.get(1).select("a").html();
                    if (StringUtils.isEmpty(code) || StringUtils.isEmpty(name)) {

                    } else {
                        if (i != 0) {
                            if (!detectionId.contains(linkType.getCode())){
                                detection.add(linkType);
                                detectionId.add(linkType.getCode());
                            }
                            linkTypes.add(linkType);
                        }
                        linkType = new LinkType();
                        linkType.setType("3");
                        linkType.setCode(code);
                        linkType.setName(name);
                    }

                    String dataCode = td.get(2).select("a").html();
                    String dataName = td.get(3).select("p").html();
                    LinkType.DataComponent dataComponent = new LinkType.DataComponent();
                    dataComponent.setName(dataCode);
                    dataComponent.setDescription(dataName);
                    linkType.addData(dataComponent);
                }

                if (!detectionId.contains(linkType.getCode())){
                    detection.add(linkType);
                    detectionId.add(linkType.getCode());
                }
                linkTypes.add(linkType);
            }

            List<String> mitigationsdata = new ArrayList<>();
            List<String> detectiondata = new ArrayList<>();
            List<String> procedureExamplesdata = new ArrayList<>();
            for (LinkType linkType : linkTypes) {
                if (linkType.getType().equals("1")) {
                    mitigationsdata.add(linkType.getCode());
                }
                if (linkType.getType().equals("2")) {
                    procedureExamplesdata.add(linkType.getCode());
                }
                if (linkType.getType().equals("3")) {
                    detectiondata.add(linkType.getCode());
                }
                relation.add("( '" +encode+
                        "', '" +linkType.getCode()+
                        "')");
            }
            attCk.setProcedureExamples(procedureExamplesdata);
            attCk.setDetection(detectiondata);
            attCk.setMitigations(mitigationsdata);
            attCks.add(attCk);
//            System.out.println(attCk);


            if (subtechnique != null) {
                for (Element a : subtechnique.select("a")) {
                    Thread.sleep(1000L);
                    crawNsfocusScript.sub(a.html());
                }
            }
        } catch (Exception e) {
            System.out.println(encode);
            e.printStackTrace();
            return null;
        }
        return urlList;
    }

    public List<String> tactic(String encode, CrawNsfocusScript crawNsfocusScript) throws Exception {
        this.encode = encode;
        List<String> urlList = new ArrayList<String>();
        String bugInfoUrl = "https://attack.mitre.org/tactics/"
                + encode + "/";
        List<LinkType> linkTypes = new ArrayList<>();
        try {
            Document document = Jsoup
                    .connect(bugInfoUrl)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("Cookie", token)
                    .header("If-Modified-Since", "Fri, 01 Sep 2023 15:50:17 GMT")
                    .header("If-None-Match", "W/\"64f20839-4bea4\"")
                    .header("Keep-Alive", "timeout=5, max=100")
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
                    .timeout(500000).get();
            String returnReson = this.parseWebPage(document.toString());
            if (returnReson.equals("0")) {
                throw new Exception("服务器被禁，等待下周期再爬取漏洞！");
            }
            AttCk attCk = new AttCk();
            attCk.setTacticEncode(encode.trim());
            StringBuilder attdes = getStringBuilder(document);
            attCk.setDes(attdes.toString());
//            attCk.setDesZn(api.getTransResult(attdes.toString()));
            String nameEn = document.select("h1").html().replace("-", " ");
            attCk.setTacticName(nameEn.trim());
//            attCk.setTacticNameZn(api.getTransResult(nameEn.trim()));
            Elements elementsByClass = document.getElementsByClass("col-md-11 pl-0");
            for (Element byClass : elementsByClass) {
                String title = byClass.select("span").html().replace(":&nbsp;", "");
                switch (title) {
                    case Constant.CREATE_TIME: {
                        String createTime = (((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0];
                        attCk.setCreateTime(dateFormat.format(new Date(Date.parse(createTime))));
                    }
                    break;
                    case Constant.UPDATE_TIME: {
                        String updateTime = (((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0];
                        attCk.setUpdateTime(dateFormat.format(new Date(Date.parse(updateTime))));
                    }
                    break;
                    case Constant.VERSION: {
                        attCk.setVersion((((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0]);

                    }
                    break;
                    case Constant.PLATFORMS: {
                        attCk.setPlatforms((((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0]);

                    }
                    break;
                    case Constant.PERMISSIONS_REQUIRED: {
                        attCk.setPermissionsRequired((((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0]);

                    }
                    break;
                    default: {

                    }
                }

            }

            attCks.add(attCk);
            for (Element techniques : document.getElementsByClass("technique")) {
                String html = techniques.select("td").get(0).select("a").html();
                if (StringUtils.isNotEmpty(html)) {
                    Thread.sleep(1000L);
                    try {
                        crawNsfocusScript.crawTemplate(html, crawNsfocusScript);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
            System.out.println(attCk);
        } catch (Exception e) {
            System.out.println(encode);
            e.printStackTrace();
            return null;
        }
        return urlList;
    }

    @NotNull
    private static StringBuilder getStringBuilder(Document document) {
        StringBuilder attdes = new StringBuilder();
        for (Element element : document.getElementsByClass("description-body").select("p")) {
            for (Node node : element.childNodes()) {
                try {
                    String wholeText = ((TextNode) node).getWholeText();
                    attdes.append(wholeText);
                } catch (Exception e) {
                    String html = ((Element) node).select("a").html().split("\n")[0];
                    if (html.contains("[")) {
                        continue;
                    }
                    attdes.append(html);
                }
            }
            attdes.append("\n");
        }
        return attdes;
    }



    public List<String> sub(String encode) throws Exception {
        this.encode = encode;
        String[] split = encode.split("\\.");
        List<String> urlList = new ArrayList<String>();
        String bugInfoUrl = "https://attack.mitre.org/techniques/"
                + split[0] + "/" + split[1] + "/";
        List<LinkType> linkTypes = new ArrayList<>();
        try {
            Document document = Jsoup
                    .connect(bugInfoUrl)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("Cookie", token)
                    .header("If-Modified-Since", "Fri, 01 Sep 2023 15:50:17 GMT")
                    .header("If-None-Match", "W/\"64f20839-4bea4\"")
                    .header("Keep-Alive", "timeout=5, max=100")
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
                    .timeout(500000).get();
            String returnReson = this.parseWebPage(document.toString());
            if (returnReson.equals("0")) {
                throw new Exception("服务器被禁，等待下周期再爬取漏洞！");
            }
            AttCk attCk = new AttCk();
            attCk.setSubTechniqueEncode(encode);
            String nameEn = "";
            for (Node node : document.select("h1").get(0).childNodes()) {
                try {
                    nameEn = ((TextNode) node).getWholeText().replace("-", " ");
                } catch (Exception e) {
                    continue;
                }
            }
            attCk.setSubTechniqueName(nameEn.trim());
//            attCk.setSubTechniqueNameZn(api.getTransResult(nameEn.trim()));
            StringBuilder attdes = getStringBuilder(document);
            attCk.setDes(attdes.toString());
//            attCk.setDesZn(api.getTransResult(attdes.toString()));
            Elements elementsByClass = document.getElementsByClass("col-md-11 pl-0");
            for (Element byClass : elementsByClass) {
                String title = byClass.select("span").html().replace(":&nbsp;", "");
                switch (title) {
                    case Constant.CREATE_TIME: {
                        String createTime = (((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0];
                        attCk.setCreateTime(dateFormat.format(new Date(Date.parse(createTime))));
                    }
                    break;
                    case Constant.UPDATE_TIME: {
                        String updateTime = (((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0];
                        attCk.setUpdateTime(dateFormat.format(new Date(Date.parse(updateTime))));
                    }
                    break;
                    case Constant.VERSION: {
                        attCk.setVersion((((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0]);

                    }
                    break;
                    case Constant.PLATFORMS: {
                        attCk.setPlatforms((((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0]);

                    }
                    break;
                    case Constant.PERMISSIONS_REQUIRED: {
                        attCk.setPermissionsRequired((((TextNode) byClass.childNodes().get(2)).getWholeText()).split("\n")[0]);

                    }
                    break;
                    default: {

                    }
                }

            }
            Elements elementsByClass1 = document.getElementsByClass("table table-bordered table-alternate mt-2");
            for (Element element : elementsByClass1) {
                Elements select = element.select("tbody").select("tr");
                for (Element element1 : select) {
                    LinkType linkType = new LinkType();
                    Elements td = element1.select("td");
                    String code = td.get(0).select("a").html();
                    String name = td.get(1).select("a").html();
                    linkType.setCode(code);
                    linkType.setName(name);
                    if (code.startsWith("M")) {
                        linkType.setType("1");
                        StringBuilder des = new StringBuilder();
                        for (Node node : td) {
                            try {
                                String wholeText = ((TextNode) node).getWholeText();
                                des.append(wholeText);
                            } catch (Exception e) {
                                String html = ((Element) node).select("a").html().split("\n")[0];
                                if (html.contains("[")) {
                                    continue;
                                }
                                des.append(html);
                            }
                        }
                        linkType.setDescription(des.toString());
                        if (!mitigationId.contains(linkType.getCode())){
                            mitigations.add(linkType);
                            mitigationId.add(linkType.getCode());
                        }
                    } else {
                        linkType.setType("2");
                        List<Node> p = td.select("p").get(0).childNodes();
                        StringBuilder des = new StringBuilder();
                        for (Node node : p) {
                            try {
                                String wholeText = ((TextNode) node).getWholeText();
                                des.append(wholeText);
                            } catch (Exception e) {
                                String html = ((Element) node).select("a").html().split("\n")[0];
                                if (html.contains("[")) {
                                    continue;
                                }
                                des.append(html);
                            }
                        }
                        linkType.setDescription(des.toString());
                        if (!procedureExamplesId.contains(linkType.getCode())){
                            procedureExamples.add(linkType);
                            procedureExamplesId.add(linkType.getCode());
                        }
                    }
                    linkTypes.add(linkType);
                }
            }
            Elements elementsByClass2 = document.getElementsByClass("table datasources-table table-bordered");
            if (elementsByClass2.size() > 0) {
                Elements select = elementsByClass2.get(0).select("tbody").select("tr");

                LinkType linkType = new LinkType();
                for (int i = 0; i < select.size(); i++) {
                    Elements td = select.get(i).select("td");
                    String code = td.get(0).select("a").html();
                    String name = td.get(1).select("a").html();
                    if (StringUtils.isEmpty(code) || StringUtils.isEmpty(name)) {

                    } else {
                        if (i != 0) {
                            if (!detectionId.contains(linkType.getCode())){
                                detection.add(linkType);
                                detectionId.add(linkType.getCode());
                            }
                            linkTypes.add(linkType);
                        }
                        linkType = new LinkType();
                        linkType.setType("3");
                        linkType.setCode(code);
                        linkType.setName(name);
                    }

                    String dataCode = td.get(2).select("a").html();
                    String dataName = td.get(3).select("p").html();
                    LinkType.DataComponent dataComponent = new LinkType.DataComponent();
                    dataComponent.setName(dataCode);
                    dataComponent.setDescription(dataName);
                    linkType.addData(dataComponent);
                }
                if (!detectionId.contains(linkType.getCode())){
                    detection.add(linkType);
                    detectionId.add(linkType.getCode());
                }
                linkTypes.add(linkType);
            }

            List<String> mitigationsdata = new ArrayList<>();
            List<String> detectiondata = new ArrayList<>();
            List<String> procedureExamplesdata = new ArrayList<>();
            for (LinkType linkType : linkTypes) {
                if (linkType.getType().equals("1")) {
                    mitigationsdata.add(linkType.getCode());
                }
                if (linkType.getType().equals("2")) {
                    procedureExamplesdata.add(linkType.getCode());
                }
                if (linkType.getType().equals("3")) {
                    detectiondata.add(linkType.getCode());
                }
                relation.add("( '" +encode+
                        "', '" +linkType.getCode()+
                        "')");
            }
            attCk.setProcedureExamples(procedureExamplesdata);
            attCk.setDetection(detectiondata);
            attCk.setMitigations(mitigationsdata);
            attCks.add(attCk);
//            System.out.println(attCk);
        } catch (Exception e) {
            System.out.println(encode);
            e.printStackTrace();
            return null;
        }
        return urlList;
    }

    /**
     * 加判断如果服务器被禁止，提示失败信息
     */
    private String parseWebPage(String pageInfo) {
        String returnReason = "1";
        if (pageInfo.contains("服务器忙")) {
            return "0";
        }
        return returnReason;
    }

    public static void main(String[] args) {
        CrawNsfocusScript crawNsfocusScript = new CrawNsfocusScript();

//        String query = "Reconnaissance";
//        System.out.println(api.getTransResult(query, "auto", "zh"));
        String[] tactic = new String[]{
                "TA0043", "TA0042", "TA0001", "TA0002", "TA0003", "TA0004", "TA0005", "TA0006", "TA0007", "TA0008", "TA0009", "TA0011", "TA0010", "TA0040"
        };

        for (String string : tactic) {
            try {
                Thread.sleep(1000L);
                crawNsfocusScript.tactic(string, crawNsfocusScript);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

//        try {
//            Thread.sleep(1000L);
//            crawNsfocusScript.crawTemplate("T1598", crawNsfocusScript);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String fileNameA = "C:\\Users\\54157\\Desktop\\test\\Att&ck.xlsx";
        String fileNameR = "C:\\Users\\54157\\Desktop\\test\\relation.xlsx";
        String fileNameD = "C:\\Users\\54157\\Desktop\\test\\Detection.xlsx";
        String fileNameM = "C:\\Users\\54157\\Desktop\\test\\Mitigations.xlsx";
        String fileNameP = "C:\\Users\\54157\\Desktop\\test\\ProcedureExamples.xlsx";
        List<Relation> list=new ArrayList<>();
        int count =1;
        StringBuilder sb=new StringBuilder();
        sb.append("INSERT INTO `insight_alarm_link_type_relation` (`type_code`, `link_type_code`) VALUES ");
        for (String s : crawNsfocusScript.relation) {
            count++;
            if (count%100==0){
                sb.append(s).append(";");
                Relation relation = new Relation();
                relation.setSql(sb.toString());
                list.add(relation);

                sb=new StringBuilder();
                sb.append("INSERT INTO `insight_alarm_link_type_relation` (`type_code`, `link_type_code`) VALUES ");

            }else {
                sb.append(s).append(" , ");
            }

        }
        EasyExcel.write(fileNameR, Relation.class)
                .sheet("模板")
                .doWrite(list);


//        List<DetectionExcel> detectionExcels = new ArrayList<>();
//        List<MitigationExcel> mitigationExcels = new ArrayList<>();
//        List<ProcedureExamplesExcel> procedureExamplesExcels = new ArrayList<>();
//        try {
//            for (LinkType linkType : crawNsfocusScript.detection) {
//                if (linkType.getDataComponent()!=null&&linkType.getDataComponent().size()>0){
//                    for (int i = 0; i < linkType.getDataComponent().size(); i++) {
//                        LinkType.DataComponent dataComponent=linkType.getDataComponent().get(i);
//                        DetectionExcel excel = new DetectionExcel();
//                        if (i==0){
//                            excel.setCode(linkType.getCode());
//                            excel.setName(linkType.getName());
//                            excel.setNameZh(api.getTransResult(linkType.getName()));
//                        }
//                        excel.setDataComponent(dataComponent.getName());
//                        excel.setDataComponentZh(api.getTransResult(dataComponent.getName()));
//                        excel.setDetects(dataComponent.getDescription());
//                        excel.setDetectsZn(api.getTransResult(dataComponent.getDescription()));
//                        detectionExcels.add(excel);
//                    }
//                }else {
//                    DetectionExcel excel = new DetectionExcel();
//                    excel.setCode(linkType.getCode());
//                    excel.setName(linkType.getName());
//                    excel.setNameZh(api.getTransResult(linkType.getName()));
//                    detectionExcels.add(excel);
//                }
//            }
//            for (LinkType linkType : crawNsfocusScript.mitigations) {
//                MitigationExcel excel = new MitigationExcel();
//                excel.setCode(linkType.getCode());
//                excel.setName(linkType.getName());
//                excel.setNameZh(api.getTransResult(linkType.getName()));
//                excel.setDescription(linkType.getDescription());
//                excel.setDescriptionZh(api.getTransResult(linkType.getDescription()));
//                mitigationExcels.add(excel);
//            }
//            for (LinkType linkType : crawNsfocusScript.procedureExamples) {
//                ProcedureExamplesExcel excel = new ProcedureExamplesExcel();
//                excel.setCode(linkType.getCode());
//                excel.setName(linkType.getName());
//                excel.setNameZh(api.getTransResult(linkType.getName()));
//                excel.setDescription(linkType.getDescription());
//                excel.setDescriptionZh(api.getTransResult(linkType.getDescription()));
//                procedureExamplesExcels.add(excel);
//            }
//        }catch (Exception e){
//
//        }

//        EasyExcel.write(fileNameA, AttCk.class)
//                .sheet("模板")
//                .doWrite(crawNsfocusScript.attCks);
//                EasyExcel.write(fileNameD, DetectionExcel.class)
//                .sheet("模板")
//                .doWrite(detectionExcels);
//                EasyExcel.write(fileNameP, ProcedureExamplesExcel.class)
//                .sheet("模板")
//                .doWrite(procedureExamplesExcels);
//                EasyExcel.write(fileNameM, MitigationExcel.class)
//                .sheet("模板")
//                .doWrite(mitigationExcels);
    }
}

