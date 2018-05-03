package aljoin.act.service;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.dao.entity.ActAljoinForm;
import aljoin.act.dao.entity.ActAljoinFormAttribute;
import aljoin.act.dao.entity.ActAljoinFormCategory;
import aljoin.act.dao.entity.ActAljoinFormWidget;
import aljoin.act.dao.mapper.ActAljoinFormMapper;
import aljoin.act.dao.object.ActAljoinFormCategoryVO;
import aljoin.act.dao.object.ActAljoinFormShowVO;
import aljoin.act.dao.object.ActAljoinFormVO;
import aljoin.act.dao.object.ActAljoinFormWidgetVO;
import aljoin.act.dao.source.BaseSource;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinFormAttributeService;
import aljoin.act.iservice.ActAljoinFormCategoryService;
import aljoin.act.iservice.ActAljoinFormService;
import aljoin.act.iservice.ActAljoinFormWidgetService;
import aljoin.act.service.util.ActConstant;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sys.dao.entity.SysDocumentNumber;
import aljoin.sys.dao.entity.SysSerialNumber;
import aljoin.sys.iservice.SysDocumentNumberService;
import aljoin.sys.iservice.SysSerialNumberService;
import aljoin.util.FileUtil;

/**
 * 
 * 表单表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-08-31
 */
@Service
public class ActAljoinFormServiceImpl extends ServiceImpl<ActAljoinFormMapper, ActAljoinForm>
    implements ActAljoinFormService {
    private final static String FORM_BATCH_DOWN_NAME = WebConstant.FILE_FORM_BATCH_DOWNLOAD_NAME;
    @Resource
    private ActAljoinFormWidgetService actAljoinFormWidgetService;

    @Resource
    private ActAljoinFormAttributeService actAljoinFormAttributeService;

    @Resource
    private ActAljoinFormCategoryService actAljoinFormCategoryService;

    @Resource
    private ActAljoinBpmnService actAljoinBpmnService;
    
    @Resource
    private SysSerialNumberService sysSerialNumberService;
    
    @Resource
    private SysDocumentNumberService sysDocumentNumberService;

    @Override
    public ActAljoinFormShowVO getAllById(Long id) throws Exception {
        ActAljoinFormShowVO formVO = new ActAljoinFormShowVO();
        List<ActAljoinFormWidgetVO> widgetVOList = new ArrayList<ActAljoinFormWidgetVO>();

        ActAljoinForm formEO = selectById(id);
        if (formEO != null) {
            formVO.setAf(formEO);
        }

        List<String> category = new ArrayList<String>();
        ActAljoinFormCategory ac = actAljoinFormCategoryService.selectById(formEO.getCategoryId());
        category.add(ac.getId() + "");

        for (int i = 0; i < ac.getCategoryLevel(); i++) {
            if (ac.getParentId() != 0L) {
                ac = actAljoinFormCategoryService.selectById(ac.getParentId());
                category.add(ac.getId() + "");
            }
        }
        Collections.reverse(category);
        formVO.setCategoryId(category);

        Where<ActAljoinFormWidget> widget = new Where<ActAljoinFormWidget>();
        widget.eq("form_id", id);
        widget.setSqlSelect("id,widget_type,widget_id,widget_name");
        List<ActAljoinFormWidget> widgetList = actAljoinFormWidgetService.selectList(widget);

        // 先获取所有控件id
        List<Long> widgetIdList = new ArrayList<Long>();
        for (ActAljoinFormWidget actAljoinFormWidget : widgetList) {
            widgetIdList.add(actAljoinFormWidget.getId());
        }

        if (widgetList != null && widgetList.size() > 0) {
            // 根据控件获取控件属性优化，通过一次获取然后构造

            Where<ActAljoinFormAttribute> attribute = new Where<ActAljoinFormAttribute>();
            attribute.in("widget_id", widgetIdList);
            attribute.setSqlSelect("id,widget_id,attr_name,attr_value,attr_desc");
            List<ActAljoinFormAttribute> attrList = actAljoinFormAttributeService.selectList(attribute);
            // 把结果集先放到map
            Map<Long, List<ActAljoinFormAttribute>> attributeMapList =
                new HashMap<Long, List<ActAljoinFormAttribute>>();
            for (ActAljoinFormAttribute actAljoinFormAttribute : attrList) {
                if (attributeMapList.get(actAljoinFormAttribute.getWidgetId()) == null) {
                    List<ActAljoinFormAttribute> tempList = new ArrayList<ActAljoinFormAttribute>();
                    tempList.add(actAljoinFormAttribute);
                    attributeMapList.put(actAljoinFormAttribute.getWidgetId(), tempList);
                } else {
                    attributeMapList.get(actAljoinFormAttribute.getWidgetId()).add(actAljoinFormAttribute);
                }
            }

            for (int i = 0; i < widgetList.size(); i++) {
                ActAljoinFormWidgetVO aw = new ActAljoinFormWidgetVO();
                ActAljoinFormWidget actAljoinFormWidget = widgetList.get(i);
                if (null != actAljoinFormWidget) {
                    aw.setActAljoinFormWidget(actAljoinFormWidget);
                }

                List<ActAljoinFormAttribute> attributeList = attributeMapList.get(actAljoinFormWidget.getId());
                if (attributeList != null && attributeList.size() > 0 && aw != null) {
                    aw.setAttributeList(attributeList);
                    widgetVOList.add(aw);
                    formVO.setActAljoinFormWidgetVO(widgetVOList);
                }
            }
        }

        return formVO;
    }

    @Override
    public Page<ActAljoinFormCategoryVO> list(PageBean pageBean, ActAljoinForm obj) throws Exception {
        Page<ActAljoinFormCategoryVO> page = new Page<ActAljoinFormCategoryVO>();
        List<ActAljoinFormCategoryVO> voList = new ArrayList<ActAljoinFormCategoryVO>();
        Where<ActAljoinForm> where = new Where<ActAljoinForm>();
        if (StringUtils.isNotEmpty(obj.getFormName())) {
            where.like("form_name", obj.getFormName());
        }
        where.setSqlSelect("ID,CATEGORY_ID,FORM_NAME,CREATE_USER_NAME,CREATE_TIME");
        where.orderBy("create_time", false);
        Page<ActAljoinForm> formPage =
            selectPage(new Page<ActAljoinForm>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        List<ActAljoinForm> formList = formPage.getRecords();

        if (formList != null && formList.size() > 0) {
            for (ActAljoinForm form : formList) {
                ActAljoinFormCategoryVO vo = new ActAljoinFormCategoryVO();
                vo.setForm(form);
                ActAljoinFormCategory category = actAljoinFormCategoryService.selectById(form.getCategoryId());
                vo.setCategory(category);
                voList.add(vo);
            }
        }
        page.setRecords(voList);
        page.setTotal(formPage.getTotal());
        page.setSize(formPage.getSize());
        return page;
    }
    @Override
    public Page<ActAljoinFormCategoryVO> retrunList(PageBean pageBean, ActAljoinForm obj) throws Exception {
        Page<ActAljoinFormCategoryVO> page = new Page<ActAljoinFormCategoryVO>();
        List<ActAljoinFormCategoryVO> voList = new ArrayList<ActAljoinFormCategoryVO>();
        Where<ActAljoinForm> where = new Where<ActAljoinForm>();
        if(obj.getCategoryId()!=null && !"".equals(obj.getCategoryId().toString())){
            String cateGoryId=obj.getCategoryId().toString()+",";
            String tmpCateGoryId="";
            do {
                tmpCateGoryId+=cateGoryId;              
                Where<ActAljoinFormCategory>cateGoryWhere=new Where<ActAljoinFormCategory>();
                cateGoryWhere.in("parent_id", cateGoryId);
                cateGoryWhere.setSqlSelect("id");
                List<ActAljoinFormCategory> cateGoryList=actAljoinFormCategoryService.selectList(cateGoryWhere);
                cateGoryId="";
                if(cateGoryList!=null && cateGoryList.size()>0){
                    for (ActAljoinFormCategory actAljoinFormCategory : cateGoryList) {
                        cateGoryId+=actAljoinFormCategory.getId()+",";
                    }                   
                }
            } while (!"".equals(cateGoryId));
            where.in("category_id", tmpCateGoryId);
        }      
        if (StringUtils.isNotEmpty(obj.getFormName())) {
            where.like("form_name", obj.getFormName());
        }
        where.setSqlSelect("ID,CATEGORY_ID,FORM_NAME,CREATE_USER_NAME,CREATE_TIME");
        where.orderBy("create_time", false);
        Page<ActAljoinForm> formPage =
            selectPage(new Page<ActAljoinForm>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        List<ActAljoinForm> formList = formPage.getRecords();

        if (formList != null && formList.size() > 0) {
            for (ActAljoinForm form : formList) {
                ActAljoinFormCategoryVO vo = new ActAljoinFormCategoryVO();
                vo.setForm(form);
                ActAljoinFormCategory category = actAljoinFormCategoryService.selectById(form.getCategoryId());
                vo.setCategory(category);
                voList.add(vo);
            }
        }
        page.setRecords(voList);
        page.setTotal(formPage.getTotal());
        page.setSize(formPage.getSize());
        return page;
    }

    @Override
    @Transactional
    public void delete(ActAljoinForm obj) throws Exception {
        Map<String, Object> formMap = new HashMap<String, Object>();
        Map<String, Object> widgetMap = new HashMap<String, Object>();

        Long id = obj.getId();
        if (id != null) {
            deleteById(obj.getId());
            formMap.put("form_id", id);
        }

        List<ActAljoinFormWidget> awList = actAljoinFormWidgetService.selectByMap(formMap);
        actAljoinFormWidgetService.deleteByMap(formMap);

        if (awList != null && awList.size() > 0) {
            for (ActAljoinFormWidget aw : awList) {
                widgetMap.put("widget_id", aw.getId());
                actAljoinFormAttributeService.deleteByMap(widgetMap);
            }
        }
    }

    @Override
    public List<ActAljoinForm> getAllForm(Long categoryId) throws Exception {
        Where<ActAljoinForm> where = new Where<ActAljoinForm>();
        where.eq("is_active", 1);
        if (categoryId != null) {
            where.eq("CATEGORY_ID", categoryId);
        }
        List<ActAljoinForm> afList = selectList(where);
        return afList;
    }

    @Override
    @Transactional
    public String add(ActAljoinFormShowVO form) throws Exception {
        List<ActAljoinFormWidgetVO> voList = form.getActAljoinFormWidgetVO();
        ActAljoinForm af = form.getAf();
        if (af != null) {
            insert(af);
        }
        if (voList != null && voList.size() > 0) {
            for (int i = 0; i < voList.size(); i++) {

                ActAljoinFormWidget widget = voList.get(i).getActAljoinFormWidget();
                widget.setFormId(form.getAf().getId());
                if (widget != null) {
                    actAljoinFormWidgetService.insert(widget);
                }

                List<ActAljoinFormAttribute> abList = voList.get(i).getAttributeList();
                Long id = widget.getId();
                if (id != null && abList != null && abList.size() > 0) {
                    List<ActAljoinFormAttribute> nameList = attrAdd(id, abList);
                    if (nameList != null && nameList.size() > 0) {
                        actAljoinFormAttributeService.insertBatch(nameList);
                    }
                }
            }
        }
        String formId = String.valueOf(form.getAf().getId());
        return formId;
    }

    @Override
    @Transactional
    public void update(ActAljoinFormShowVO form) throws Exception {
        if (form.getAf() != null) {
            ActAljoinForm af = null;

            Long formId = form.getAf().getId();
            if (null != formId) {
                af = selectById(formId);
            }
            if (null != af) {
                String formName = form.getAf().getFormName();
                if (null != formName) {
                    af.setFormName(formName);
                }

                int isActive = form.getAf().getIsActive();
                if (null != String.valueOf(isActive)) {
                    af.setIsActive(isActive);
                }

                String code = form.getAf().getHtmlCode();
                if (null != code) {
                    af.setHtmlCode(code);
                }

                Long categoryId = form.getAf().getCategoryId();
                if (null != categoryId) {
                    af.setCategoryId(categoryId);
                }
                updateById(af);
                // 修改表单，同步修改数据到该表单相关的流程，提示流程对应的表单被修改了，表单需要重新分配权限
                // add by zhongjy
                actAljoinBpmnService.updateFormProcess(af.getId());
            }

            // 删除控件表以及控件属性表信息
            Map<String, Object> formMap = new HashMap<String, Object>();
            Map<String, Object> widgetMap = new HashMap<String, Object>();
            if (formId != null) {
                formMap.put("form_id", formId);
            }
            List<ActAljoinFormWidget> awList = actAljoinFormWidgetService.selectByMap(formMap);
            if (awList != null && awList.size() > 0) {
                if (formMap != null) {
                    actAljoinFormWidgetService.deleteByMap(formMap);
                }

                for (ActAljoinFormWidget aw : awList) {
                    if (aw != null && aw.getId() != null) {
                        widgetMap.put("widget_id", aw.getId());
                        actAljoinFormAttributeService.deleteByMap(widgetMap);
                    }
                }
            }

            List<ActAljoinFormWidgetVO> voList = form.getActAljoinFormWidgetVO();
            if (voList != null && voList.size() > 0) {
                for (int i = 0; i < voList.size(); i++) {

                    ActAljoinFormWidget widget = voList.get(i).getActAljoinFormWidget();
                    if (widget != null) {
                        widget.setFormId(formId);
                        // 控件的版本和表单的版本一致
                        widget.setVersion(af.getVersion());
                        actAljoinFormWidgetService.insert(widget);
                    }
                    List<ActAljoinFormAttribute> abList = voList.get(i).getAttributeList();
                    Long id = widget.getId();
                    if (id != null && abList != null && abList.size() > 0) {
                        List<ActAljoinFormAttribute> nameList = attrAdd(id, abList);
                        if (nameList != null && nameList.size() > 0) {
                            // 版本号和表单版本号一致
                            for (ActAljoinFormAttribute actAljoinFormAttribute : nameList) {
                                actAljoinFormAttribute.setVersion(af.getVersion());
                            }
                            actAljoinFormAttributeService.insertBatch(nameList);
                        }
                    }
                }
            }
        }
    }

    public List<ActAljoinFormAttribute> attrAdd(Long id, List<ActAljoinFormAttribute> actAljoinFormWidgetVO) {
        List<ActAljoinFormAttribute> nameList = new ArrayList<ActAljoinFormAttribute>();
        List<ActAljoinFormAttribute> valueList = new ArrayList<ActAljoinFormAttribute>();
        List<ActAljoinFormAttribute> descList = new ArrayList<ActAljoinFormAttribute>();

        for (ActAljoinFormAttribute attr : actAljoinFormWidgetVO) {
            if (attr != null) {
                String[] name = null;
                String names = attr.getAttrName();
                if (names != null) {
                    if (names.indexOf("&") > -1) {
                        if (names.endsWith("&")) {
                            name = names.substring(0, names.lastIndexOf("&")).split("&");
                        } else {
                            name = names.split("&");
                        }
                    }
                }
                String[] value = null;
                String values = attr.getAttrValue();
                if (values != null) {
                    if (values.indexOf("&") > -1) {
                        if (values.endsWith("&")) {
                            value = values.substring(0, values.lastIndexOf("&")).split("&");
                        } else {
                            value = values.split("&");
                        }
                    }
                }

                String[] desc = null;
                String descs = attr.getAttrDesc();
                if (descs != null) {
                    if (descs.indexOf("&") > -1) {
                        if (descs.endsWith("&")) {
                            desc = descs.substring(0, descs.lastIndexOf("&")).split("&");
                        } else {
                            desc = values.split("&");
                        }
                    }
                }

                if (name != null) {
                    for (String attrName : name) {
                        ActAljoinFormAttribute attribute = new ActAljoinFormAttribute();
                        attribute.setWidgetId(id);
                        attribute.setAttrName(attrName);
                        nameList.add(attribute);
                    }
                }

                if (value != null) {
                    for (String attrValue : value) {
                        ActAljoinFormAttribute attribute = new ActAljoinFormAttribute();
                        attribute.setAttrValue(attrValue);
                        valueList.add(attribute);
                    }
                }

                if (desc != null) {
                    for (String attrDesc : desc) {
                        ActAljoinFormAttribute attribute = new ActAljoinFormAttribute();
                        attribute.setAttrDesc(attrDesc);
                        descList.add(attribute);
                    }
                }
            }
        }

        for (int i = 0; i <= nameList.size() - 1; i++) {
            if (nameList != null && nameList.size() > 0) {
                nameList.get(i).setIsActive(1);

                if (valueList != null && descList != null && valueList.size() > 0 && descList.size() > 0) {
                    nameList.get(i).setAttrValue(valueList.get(i).getAttrValue());
                    nameList.get(i).setAttrDesc(descList.get(i).getAttrDesc());
                }
            }
        }
        return nameList;
    }

    @Override
    public List<BaseSource> getAllType(String type) throws Exception {
        List<BaseSource> result = ActConstant.RESULT_SOURCE.get(type);
        return result;
    }
    
    @Override
    @Transactional
    public RetMsg copy(ActAljoinForm obj) {
        RetMsg retMsg = new RetMsg();
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(obj != null && obj.getId() != null) {
            Where<ActAljoinForm> where = new Where<ActAljoinForm>();
            where.eq("id", obj.getId());
            where.setSqlSelect("form_name,is_active,html_code,category_id");
            ActAljoinForm af = selectOne(where);
            if (af != null) {
                String formName = af.getFormName() + "-副本";
                if (formName.length() > 100) {
                    retMsg.setCode(1);
                    retMsg.setMessage("表单名称过长");
                    return retMsg;
                }
                map.put("form_name", formName);
                //验证表单名是否唯一
                List<ActAljoinForm> list= selectByMap(map);
                if(!list.isEmpty()){
                    retMsg.setCode(1);
                    retMsg.setMessage(formName+"已存在");
                    return retMsg;
                }
                af.setFormName(formName);
                af.setId(null);
                //复制表单
                insert(af);
                Long formId = af.getId();
                //复制控件
                Where<ActAljoinFormWidget> widgetWhere = new Where<ActAljoinFormWidget>();
                widgetWhere.eq("form_id", obj.getId());
                widgetWhere.setSqlSelect("id,widget_type,widget_id,widget_name,is_active");
                List<ActAljoinFormWidget> widgetList = actAljoinFormWidgetService.selectList(widgetWhere);
                for (ActAljoinFormWidget widget : widgetList) {
                    widget.setFormId(formId);
                    Long oldId = widget.getId();
                    widget.setId(null);
                    widget.setVersion(af.getVersion());
                    actAljoinFormWidgetService.insert(widget);
                    Long newId = widget.getId();
                    //复制控件属性
                    Where<ActAljoinFormAttribute> attributeWhere = new Where<ActAljoinFormAttribute>();
                    attributeWhere.eq("widget_id", oldId);
                    attributeWhere.setSqlSelect("widget_id,attr_name,attr_value,attr_desc,is_active");
                    List<ActAljoinFormAttribute> attributeList = actAljoinFormAttributeService.selectList(attributeWhere);
                    for (ActAljoinFormAttribute formAttribute : attributeList) {
                        formAttribute.setWidgetId(newId);
                        formAttribute.setVersion(af.getVersion());
                    }
                    actAljoinFormAttributeService.insertBatch(attributeList);
                }
            } 
        }
        retMsg.setCode(0);
        retMsg.setMessage("复制成功！");
        return retMsg;
    }

    @Override
    public void export(HttpServletResponse response, ActAljoinFormVO obj) throws Exception {
        if (obj != null) {
            String[] arr = obj.getIds().split(";");
            List<Long> ids = new ArrayList<Long>();
            for(int i = 0; i < arr.length; i++) {
              ids.add(Long.valueOf(arr[i]));
            }
            //获取表单信息
            Where<ActAljoinForm> where = new Where<ActAljoinForm>();
            where.in("id", ids);
            where.setSqlSelect("id,html_code,form_name,category_id");
            List<ActAljoinForm> formList = selectList(where);
            //用于批量下载
            ZipOutputStream zos = null;
            if (formList != null && formList.size() > 1) {
                String zipFileName = FORM_BATCH_DOWN_NAME;
                zos = new ZipOutputStream(response.getOutputStream());
                response.reset();
                response.setContentType("application/force-download");
                response.setHeader("content-Disposition",
                        "attachment;filename=" + URLEncoder.encode(zipFileName, "utf-8"));
            }
            if (formList != null && formList.size() > 0 ) {
                for (ActAljoinForm form : formList) {
                    StringBuilder html = new StringBuilder();
                    //拼接html内容
                    html.append("<div id='actAljoinForm' form_name='"+form.getFormName()+"' html_code='"+form.getHtmlCode()+"' category_id='"+form.getCategoryId()+"'>");
                    //获取控件信息
                    Where<ActAljoinFormWidget> widgetWhere = new Where<ActAljoinFormWidget>();
                    widgetWhere.eq("form_id", form.getId());
                    widgetWhere.setSqlSelect("id,widget_type,widget_id,widget_name");
                    List<ActAljoinFormWidget> widgetList = actAljoinFormWidgetService.selectList(widgetWhere);
                    if (widgetList != null && widgetList.size() > 0) {
                        for (int i = 0; i < widgetList.size(); i++) {
                            //拼接html内容
                            html.append("<div id='actAljoinFormWidget["+i+"]' widgetType='"+widgetList.get(i).getWidgetType()+"' widgetId='"+widgetList.get(i).getWidgetId()+"' widgetName='"+widgetList.get(i).getWidgetName()+"'>");
                            //获取控件属性
                            Where<ActAljoinFormAttribute> attributeWhere = new Where<ActAljoinFormAttribute>();
                            Long id = widgetList.get(i).getId();
                            attributeWhere.eq("widget_id", id);
                            attributeWhere.setSqlSelect("attr_name,attr_value,attr_desc");
                            List<ActAljoinFormAttribute> attributeList = actAljoinFormAttributeService.selectList(attributeWhere);
                            if (attributeList != null && attributeList.size() > 0) {
                                //属性拼接
                                ArrayList<String> mergeArrList = mergeArr(attributeList);
                                html.append("<div id='actAljoinFormWidget["+i+"].attributeList' attrDesc='"+mergeArrList.get(0)+"' attrName='"+mergeArrList.get(1)+"' attrValue='"+mergeArrList.get(2)+"'></div></div>");
                            }
                        }
                    }
                    html.append("</div>");
                    //读取表单模板文件
                    ClassPathResource classPathResource = new ClassPathResource("formExportTemplate.html");
                    InputStream is = classPathResource .getInputStream();
                    File templateFile = FileUtil.byte2file("formExportTemplate",FileUtil.toByteArray(is));
                    Document doc = Jsoup.parse(templateFile, "UTF-8", "http://www.baidu.com");
                    //插入预览内容
                    doc.select("#form-content-div-form").html(new String(Base64.decode(form.getHtmlCode().getBytes())));
                    //插入导入内容
                    doc.select("div").first().html(FileUtil.gzip(html.toString()));
                    //插入文件标记
                    doc.select("#flagId").html("aljoinExportFile");
                    String exportStr = doc.toString();
                    //下载单个文件
                    if (formList.size() == 1) {
                        response.reset();
                        response.setContentType("application/force-download");
                        String formName = new String((form.getFormName()+".html").getBytes(), "ISO-8859-1"); 
                        response.setHeader("content-Disposition",
                            String.format("attachment; filename=\"%s\"", formName));
                        ServletOutputStream out = response.getOutputStream();
                        try {
                            out.write(exportStr.getBytes());
                        } finally {
                            if (out != null) {
                                out.close();
                            }
                        } 
                        return;
                    } else {
                       //批量下载
                       String fileName = form.getFormName() + ".html";
                       File file = FileUtil.byte2file(fileName, exportStr.getBytes());
                       FileUtil.doZip(file, zos);
                       FileUtil.deleteFile(file);
                    }
                }
            }
            zos.close();
        }
    }

    public ArrayList<String> mergeArr(List<ActAljoinFormAttribute> attributeList) {
        ArrayList<String> attrList = new ArrayList<String>();
        StringBuilder attrDesc = new StringBuilder();
        StringBuilder attrName = new StringBuilder();
        StringBuilder attrValue = new StringBuilder();
        
        for (int i = 0; i < attributeList.size(); i++) {
            attrDesc.append(attributeList.get(i).getAttrDesc()).append("&");
            attrName.append(attributeList.get(i).getAttrName()).append("&");
            attrValue.append(attributeList.get(i).getAttrValue()).append("&");
        }
        
        attrList.add(attrDesc.toString());
        attrList.add(attrName.toString());
        attrList.add(attrValue.toString());
        return attrList;
        
    }

    @Override
    public RetMsg fileImport(MultipartHttpServletRequest request) throws Exception {
        RetMsg retMsg = new RetMsg();
        HashMap<String, Object> map = new HashMap<>();
        MultipartFile mFile = request.getFile("file");
        String filename = mFile.getOriginalFilename();
        if (!filename.substring(filename.lastIndexOf(".")+1).equals("html")) {
            retMsg.setCode(1);
            retMsg.setMessage("只能上传html文件");
            return retMsg;
        }
        InputStream is = mFile.getInputStream();
        byte[] bs = FileUtil.toByteArray(is);
        File file = FileUtil.byte2file(mFile.getOriginalFilename(), bs);
        String formName = mFile.getOriginalFilename().substring(0,mFile.getOriginalFilename().lastIndexOf("."));
        if (formName.length()<3) {
            formName = formName + "导入";
        }
        File tempFile = File.createTempFile(formName, ".html");
        File file2 = new File(formName+".html");
        FileUtil.copyFile(file,tempFile);
        FileUtil.deleteFile(file2);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        map.put("path", tempFile.getAbsolutePath());
        map.put("fileName",formName);
        retMsg.setObject(map);
        return retMsg;
    }
    
    @Override
    @Transactional
    public RetMsg fileSubmit(ActAljoinFormVO form) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (StringUtils.isEmpty(form.getPath())) {
            retMsg.setCode(1);
            retMsg.setMessage("文件还未上传");
            return retMsg;
        }
        Long formId = null;
        String filePath = form.getPath();
        File file = new File(filePath);
        //解析临时文件
        Document doc = Jsoup.parse(file, "UTF-8", "http://www.baidu.com");
        if (doc != null) {
            if (doc.select("div").first() == null) {
                retMsg.setCode(1);
                retMsg.setMessage("非本系统导出的表单文件");
                return retMsg;
            }
            if (doc.select("#flagId") == null  || !doc.select("#flagId").text().equals("aljoinExportFile"))  {
                retMsg.setCode(1);
                retMsg.setMessage("非本系统导出的表单文件");
                return retMsg;
            }
            //读取要导入的数据
            String importStr = doc.select("div").first().text();
            //去除空格换行符
            String dataStr = FileUtil.replaceBlank(importStr);
            //解压字符串
            String gunzip = FileUtil.gunzip(dataStr);
            Document importDoc = Jsoup.parse(gunzip);
            Element formEle = importDoc.getElementById("actAljoinForm");
            if (formEle != null) {
                //插入表单数据
                ActAljoinForm af = new ActAljoinForm();
                if (form != null && form.getAf() != null) {
                    //根据用户填写信息修改表单
                    af.setFormName(form.getAf().getFormName());
                    af.setCategoryId(form.getAf().getCategoryId());
                    af.setHtmlCode(formEle.attr("html_code"));
                    af.setIsActive(1);
                    insert(af);
                    formId = af.getId();
                    int count = 0;
                    Element widgetEle = null;
                    do {
                        //插入控件数据
                        ActAljoinFormWidget formWidget = new ActAljoinFormWidget();
                        widgetEle = formEle.getElementById("actAljoinFormWidget["+count+"]");
                        if (widgetEle != null) {
                            formWidget.setWidgetType(widgetEle.attr("widgetType"));
                            formWidget.setWidgetName(widgetEle.attr("widgetName"));
                            formWidget.setWidgetId(widgetEle.attr("widgetId"));
                            formWidget.setFormId(formId);
                            formWidget.setVersion(af.getVersion());
                            formWidget.setIsActive(1);
                            actAljoinFormWidgetService.insert(formWidget);
                            Long widgetId = formWidget.getId();
                            //插入控件属性数据
                            Element attrEle = widgetEle.getElementById("actAljoinFormWidget["+count+"].attributeList");
                            if (attrEle != null) {
                                List<ActAljoinFormAttribute> formAttrList = new ArrayList<ActAljoinFormAttribute>();
                                ActAljoinFormAttribute formAttribute = new ActAljoinFormAttribute();
                                formAttribute.setAttrDesc(attrEle.attr("attrDesc"));
                                formAttribute.setAttrName(attrEle.attr("attrName"));
                                formAttribute.setAttrValue(attrEle.attr("attrValue"));
                                formAttrList.add(formAttribute);
                                List<ActAljoinFormAttribute> nameList = attrAdd(widgetId, formAttrList);
                                if (nameList != null && nameList.size() > 0) {
                                    // 版本号和表单版本号一致
                                    for (ActAljoinFormAttribute actAljoinFormAttribute : nameList) {
                                        actAljoinFormAttribute.setVersion(af.getVersion());
                                    }
                                    actAljoinFormAttributeService.insertBatch(nameList);
                                }
                            }
                            count ++;
                        }
                    } while (widgetEle != null);
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setObject(String.valueOf(formId));
        retMsg.setMessage("操作成功");
        //删除临时文件
        FileUtil.deleteFile(file);
        return retMsg;
    }

    @Override
    public List<String> validateSerialNum(String formIds) {
        String[] formIdArr = formIds.split(",");
        List<String> serialNums = new ArrayList<String>();
        if (null != formIdArr && formIdArr.length > 0) {
            for (String formId : formIdArr) {
                //添加流水号id
                Where<ActAljoinFormWidget> widgetWhere = new Where<ActAljoinFormWidget>();
                widgetWhere.eq("form_id", formId);
                widgetWhere.eq("widget_type", "waternum");
                widgetWhere.setSqlSelect("id");
                ActAljoinFormWidget widget = actAljoinFormWidgetService.selectOne(widgetWhere);
                if (null != widget) {
                    Where<ActAljoinFormAttribute> attributeWhere = new Where<ActAljoinFormAttribute>();
                    attributeWhere.eq("attr_name","aljoin-sys-data-lshlist");
                    attributeWhere.eq("widget_id", widget.getId());
                    attributeWhere.setSqlSelect("id,attr_value");
                    ActAljoinFormAttribute attribute = actAljoinFormAttributeService.selectOne(attributeWhere);
                    if (null != attribute && !"null".equals(attribute.getAttrValue()) && StringUtils.isNotEmpty(attribute.getAttrValue())) {
                        serialNums.add(attribute.getAttrValue());
                    } else {
                        Where<ActAljoinFormAttribute> attributeWhere1 = new Where<ActAljoinFormAttribute>();
                        attributeWhere1.eq("attr_name","aljoin-data-lshclass");
                        attributeWhere1.eq("widget_id", widget.getId());
                        attributeWhere1.setSqlSelect("id,attr_value");
                        ActAljoinFormAttribute attribute1 = actAljoinFormAttributeService.selectOne(attributeWhere1);
                        if (null != attribute1 && !"null".equals(attribute1.getAttrValue())) {
                            SysSerialNumber sysSerialNumber = new SysSerialNumber();
                            sysSerialNumber.setCategoryId(Long.parseLong(attribute1.getAttrValue()));
                            List<SysSerialNumber> list = sysSerialNumberService.getList(sysSerialNumber);
                            if (null != list && list.size() > 0) {
                                for (int i=1; i<list.size(); i++) {
                                    serialNums.add(list.get(i).getId().toString());
                                }
                            }
                        }
                    }
                }
               
            }
        }
         return serialNums;
    }

    @Override
    public List<String> validateDocumentNum(String formIds) {
        String[] formIdArr = formIds.split(",");
        List<String> documentNums = new ArrayList<String>();
        if (null != formIdArr && formIdArr.length > 0) {
            for (String formId : formIdArr) {
                //添加流水号id
                Where<ActAljoinFormWidget> widgetWhere = new Where<ActAljoinFormWidget>();
                widgetWhere.eq("form_id", formId);
                widgetWhere.eq("widget_type", "writing");
                widgetWhere.setSqlSelect("id");
                ActAljoinFormWidget widget = actAljoinFormWidgetService.selectOne(widgetWhere);
                if (null != widget) {
                    Where<ActAljoinFormAttribute> attributeWhere = new Where<ActAljoinFormAttribute>();
                    attributeWhere.eq("attr_name","aljoin-sys-data-whlist");
                    attributeWhere.eq("widget_id", widget.getId());
                    attributeWhere.setSqlSelect("id,attr_value");
                    ActAljoinFormAttribute attribute = actAljoinFormAttributeService.selectOne(attributeWhere);
                    if (null != attribute && !"null".equals(attribute.getAttrValue()) && StringUtils.isNotEmpty(attribute.getAttrValue())) {
                        documentNums.add(attribute.getAttrValue());
                    } else {
                        Where<ActAljoinFormAttribute> attributeWhere1 = new Where<ActAljoinFormAttribute>();
                        attributeWhere1.eq("attr_name","aljoin-data-whclass");
                        attributeWhere1.eq("widget_id", widget.getId());
                        attributeWhere1.setSqlSelect("id,attr_value");
                        ActAljoinFormAttribute attribute1 = actAljoinFormAttributeService.selectOne(attributeWhere1);
                        if (null != attribute1 && !"null".equals(attribute1.getAttrValue())) {
                            SysDocumentNumber sysDocumentNumber = new SysDocumentNumber();
                            sysDocumentNumber.setCategoryId(Long.parseLong(attribute1.getAttrValue()));
                            List<SysDocumentNumber> list = sysDocumentNumberService.getList(sysDocumentNumber);
                            if (null != list && list.size() > 0) {
                                for (int i=1; i<list.size(); i++) {
                                    documentNums.add(list.get(i).getId().toString());
                                }
                            }
                        }
                    }
                }
               
            }
        }
        return documentNums;
    }
}
