package aljoin.act.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aljoin.act.dao.source.BaseSource;

/**
 * 
 * activiti相关常量类
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:01:17
 */
public class ActConstant {
    public final static Map<String, List<BaseSource>> RESULT_SOURCE = new HashMap<String, List<BaseSource>>();
    public final static Map<String, String> WIDGET_MAP = new HashMap<String, String>();

    // 单行文字
    public final static String FORM_TYPE_TEXT = "text";
    static {
        List<BaseSource> textList = new ArrayList<BaseSource>();
        BaseSource source = new BaseSource();
        source.setText("登陆人");
        source.setValue("TextLoginPerson");
        textList.add(source);

        BaseSource source2 = new BaseSource();
        source2.setText("当前时间");
        source2.setValue("TextCurrentTime");
        textList.add(source2);

        BaseSource source3 = new BaseSource();
        source3.setText("创建人所在岗位");
        source3.setValue("TextUserPosition");
        textList.add(source3);

        BaseSource source4 = new BaseSource();
        source4.setText("获取当前用户部门");
        source4.setValue("TextUserDepartment");
        textList.add(source4);
        RESULT_SOURCE.put(FORM_TYPE_TEXT, textList);
    }

    // 多行文字
    public final static String FORM_TYPE_AREA_TEXT = "area_text";

    // 单选
    public final static String FORM_TYPE_RADIO = "radio";
    static {
        List<BaseSource> radioList = new ArrayList<BaseSource>();
        BaseSource source = new BaseSource();
        source.setText("性别数据源");
        source.setValue("RadioSex");
        radioList.add(source);
        RESULT_SOURCE.put(FORM_TYPE_RADIO, radioList);
    }

    // 多选
    public final static String FORM_TYPE_CHECKBOX = "checkbox";
    static {
        List<BaseSource> checkboxList = new ArrayList<BaseSource>();
        BaseSource source = new BaseSource();
        source.setText("收文阅件局领导");
        source.setValue("CheckLeader");
        checkboxList.add(source);

        BaseSource source2 = new BaseSource();
        source2.setText("收文阅件局领导");
        source2.setValue("CheckOffice");
        checkboxList.add(source2);
        RESULT_SOURCE.put(FORM_TYPE_CHECKBOX, checkboxList);
    }

    // 下拉
    public final static String FORM_TYPE_SELECT = "select";
    static {
        List<BaseSource> selectList = new ArrayList<BaseSource>();
        BaseSource source = new BaseSource();
        source.setText("保密等级");
        source.setValue("SelectSecurityLevel");
        selectList.add(source);

        BaseSource source2 = new BaseSource();
        source2.setText("保密期限");
        source2.setValue("SelectConfidentialityPeriod");
        selectList.add(source2);

        BaseSource source3 = new BaseSource();
        source3.setText("公开类型");
        source3.setValue("SelectOpenType");
        selectList.add(source3);

        BaseSource source4 = new BaseSource();
        source4.setText("密级");
        source4.setValue("SelectDense");
        selectList.add(source4);

        BaseSource source5 = new BaseSource();
        source5.setText("文件类型");
        source5.setValue("SelectFileType");
        selectList.add(source5);

        BaseSource source6 = new BaseSource();
        source6.setText("来文单位");
        source6.setValue("SelectReferenceUnit");
        selectList.add(source6);

        BaseSource source7 = new BaseSource();
        source7.setText("缓急");
        source7.setValue("SelectUrgency");
        selectList.add(source7);

        BaseSource source8 = new BaseSource();
        source8.setText("请假类型");
        source8.setValue("SelectLeaveType");
        selectList.add(source8);
        RESULT_SOURCE.put(FORM_TYPE_SELECT, selectList);
    }

    // 二级联动下拉
    public final static String FORM_TYPE_CONTACT_SELECT = "contact_select";
    static {
        List<BaseSource> contactList = new ArrayList<BaseSource>();
        BaseSource source = new BaseSource();
        source.setText("城市数据源");
        source.setValue("ContactCity");
        contactList.add(source);
        RESULT_SOURCE.put(FORM_TYPE_CONTACT_SELECT, contactList);
    }

    // 三级联动下拉
    public final static String FORM_TYPE_CONTACT_SELECT_THREE = "contact_select_three";
    static {
        List<BaseSource> contactList = new ArrayList<BaseSource>();
        BaseSource source = new BaseSource();
        source.setText("城市数据源");
        source.setValue("ContactThreeCity");
        contactList.add(source);
        RESULT_SOURCE.put(FORM_TYPE_CONTACT_SELECT_THREE, contactList);
    }

    // 四级联动下拉
    public final static String FORM_TYPE_CONTACT_SELECT_FOUR = "contact_select_four";
    static {
        List<BaseSource> contactList = new ArrayList<BaseSource>();
        BaseSource source = new BaseSource();
        source.setText("城市数据源");
        source.setValue("ContactFourCity");
        contactList.add(source);
        RESULT_SOURCE.put(FORM_TYPE_CONTACT_SELECT_FOUR, contactList);
    }

    // 五级联动下拉
    public final static String FORM_TYPE_CONTACT_SELECT_FIVE = "contact_select_five";
    static {
        List<BaseSource> contactList = new ArrayList<BaseSource>();
        BaseSource source = new BaseSource();
        source.setText("城市数据源");
        source.setValue("ContactFiveCity");
        contactList.add(source);
        RESULT_SOURCE.put(FORM_TYPE_CONTACT_SELECT_FIVE, contactList);
    }

    // 数字
    public final static String FORM_TYPE_NUMBER = "number";
    // 邮箱
    public final static String FORM_TYPE_EMAIL = "email";
    // 金额
    public final static String FORM_TYPE_MONEY = "money";
    // 电话
    public final static String FORM_TYPE_TELEPHONE = "telephone";
    // 手机
    public final static String FORM_TYPE_MOBILEPHONE = "mobilephone";
    // 日期
    public final static String FORM_TYPE_DATE = "date";
    // 时间
    public final static String FORM_TYPE_TIME = "time";
    // 日期时间
    public final static String FORM_TYPE_DATETIME = "datetime";
    // 图片
    public final static String FORM_TYPE_IMAGE = "image";
    // 附件
    public final static String FORM_TYPE_ATTACH = "attach";

    // 数据表格
    public final static String FORM_TYPE_DATAGRID = "datagrid";
    static {
        List<BaseSource> datagridList = new ArrayList<BaseSource>();
        BaseSource source = new BaseSource();
        source.setText("学生源");
        source.setValue("DatagridStudent");
        datagridList.add(source);
        RESULT_SOURCE.put(FORM_TYPE_DATAGRID, datagridList);
    }

    // 文本编辑器
    public final static String FORM_TYPE_EDITOR = "editor";
    // 网址
    public final static String FORM_TYPE_HREF = "href";
    // 数据统计
    public final static String FORM_TYPE_STATISTICS = "statistics";

    // 文本标签
    public final static String FORM_TYPE_LABEL = "label";
    static {
        List<BaseSource> labelList = new ArrayList<BaseSource>();
        BaseSource source = new BaseSource();
        source.setText("默认标签数据源");
        source.setValue("LabelName");
        labelList.add(source);
        RESULT_SOURCE.put(FORM_TYPE_LABEL, labelList);
    }

    // 小数
    public final static String FORM_TYPE_DECIMAL = "decimal";
    // 时间区间
    public final static String FORM_TYPE_DATETIME_PERIOD = "datetime_period";
    // 日期区间
    public final static String FORM_TYPE_DATE_PERIOD = "date_period";
    // 办结时间
    public final static String FORM_TYPE_BIZ_FINISH_TIME = "biz_finish_time";
    // 正文
    public final static String FORM_TYPE_BIZ_TEXT_OFFICE = "biz_text_office";
    // 标题
    public final static String FORM_TYPE_BIZ_TITLE = "biz_title";

    static {
        WIDGET_MAP.put(FORM_TYPE_TEXT, "单行文字");
        WIDGET_MAP.put(FORM_TYPE_AREA_TEXT, "多行文字");
        WIDGET_MAP.put(FORM_TYPE_RADIO, "单选");
        WIDGET_MAP.put(FORM_TYPE_CHECKBOX, "多选");
        WIDGET_MAP.put(FORM_TYPE_SELECT, "下拉");
        WIDGET_MAP.put(FORM_TYPE_CONTACT_SELECT, "二级联动下拉");
        WIDGET_MAP.put(FORM_TYPE_CONTACT_SELECT_THREE, "三级联动下拉");
        WIDGET_MAP.put(FORM_TYPE_CONTACT_SELECT_FOUR, "四级联动下拉");
        WIDGET_MAP.put(FORM_TYPE_CONTACT_SELECT_FIVE, "五级联动下拉");
        WIDGET_MAP.put(FORM_TYPE_NUMBER, "数字");
        WIDGET_MAP.put(FORM_TYPE_EMAIL, "邮箱");
        WIDGET_MAP.put(FORM_TYPE_MONEY, "金额");
        WIDGET_MAP.put(FORM_TYPE_TELEPHONE, "电话");
        WIDGET_MAP.put(FORM_TYPE_MOBILEPHONE, "手机");
        WIDGET_MAP.put(FORM_TYPE_DATE, "日期");
        WIDGET_MAP.put(FORM_TYPE_TIME, "时间");
        WIDGET_MAP.put(FORM_TYPE_DATETIME, "日期时间");
        WIDGET_MAP.put(FORM_TYPE_IMAGE, "图片");
        WIDGET_MAP.put(FORM_TYPE_ATTACH, "附件");
        WIDGET_MAP.put(FORM_TYPE_DATAGRID, "数据表格");
        WIDGET_MAP.put(FORM_TYPE_EDITOR, "文本编辑器");
        WIDGET_MAP.put(FORM_TYPE_HREF, "网址");
        WIDGET_MAP.put(FORM_TYPE_LABEL, "文本标签");
        WIDGET_MAP.put(FORM_TYPE_DECIMAL, "小数");
        WIDGET_MAP.put(FORM_TYPE_DATETIME_PERIOD, "时间区间");
        WIDGET_MAP.put(FORM_TYPE_DATE_PERIOD, "日期区间");
        WIDGET_MAP.put(FORM_TYPE_BIZ_FINISH_TIME, "办结时间");
        WIDGET_MAP.put(FORM_TYPE_BIZ_TEXT_OFFICE, "正文");
        WIDGET_MAP.put(FORM_TYPE_BIZ_TITLE, "标题");
    }

    public final static String BIZ_TYPE_IOA = "ioa";

    /**
     * 内置流程变量(数组)
     */
    // 是否通过
    public final static String SYS_PROCESS_VAL_PASS = "pass";
    public final static String[] SYS_PROCESS_VAL_ARR = {SYS_PROCESS_VAL_PASS};
}
