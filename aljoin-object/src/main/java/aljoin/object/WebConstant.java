package aljoin.object;

/**
 *
 * @author zhongjy
 * @date 2018年2月9日
 */
public class WebConstant {

    /**
     * 消息推送返回码类型--------------------------------
     */
    /**
     * 在线通知消息
     */
    public static final Integer SOCKET_CODE_100 = 100;

    /**
     * 数据字典类型--------------------------------
     */
    /**
     * 来文类型
     */
    public static final String DICT_RECEIVE_FILE_TYPE = "DICT_RECEIVE_FILE_TYPE";
    /**
     * 来文单位
     */
    public static final String DICT_FROM_UNIT = "DICT_FROM_UNIT";
    /**
     * 紧急程度
     */
    public static final String DICT_URGENT_LEVEL = "DICT_URGENT_LEVEL";

    /**
     * 传阅对象-局领导
     */
    public static final String DICT_READ_OBJECT_LEADER = "DICT_READ_OBJECT_LEADER";
    /**
     * 传阅对象-处室领导
     */
    public static final String DICT_READ_OBJECT_OFFICE = "DICT_READ_OBJECT_OFFICE";
    /**
     * 传阅对象-其他传阅对象分类
     */
    public static final String DICT_READ_OBJECT_OTHER = "DICT_READ_OBJECT_OTHER";
    /**
     * 传阅对象-全局人员
     */
    public static final String DICT_READ_OBJECT_ALL = "DICT_READ_OBJECT_ALL";
    /**
     * 传阅对象-局属单位领导
     */
    public static final String DICT_READ_OBJECT_OFFICELEADER = "DICT_READ_OBJECT_OFFICELEADER";
    /**
     * 请假类型
     */
    public static final String DICT_LEAVE_TYPE = "DICT_LEAVE_TYPE";
    /**
     *
     */
    public static final String DICT_ATT_DEPARTMENT = "DICT_ATT_DEPARTMENT";
    /**
     * 考勤补签流程分支-研究所
     */
    public static final String DICT_ATT_DEPARTMENT_LAB = "DICT_ATT_DEPARTMENT_LAB";
    /**
     * 性别设置
     */
    public static final String DATA_SOURCE_RADIOSEX = "DATA_SOURCE_RADIOSEX";
    /**
     * 标签数据设置
     */
    public static final String DATA_SOURCE_LABELNAME = "DATA_SOURCE_LABELNAME";
    /**
     * 请假流程标识
     */
    public static final String RETMSG_OPERATION_LEAVE = "leave";
    /**
     * 有效期设置
     */
    public static final String DATA_SOURCE_SELECT_PERIOD = "DATA_SOURCE_SELECT_PERIOD";
    /**
     * 秘密等级设定
     */
    public static final String DATA_SOURCE_SELECT_DENSE = "DATA_SOURCE_SELECT_DENSE";
    /**
     * 文件类型设置
     */
    public static final String DATA_SOURCE_SELECT_FILE_TYPE = "DATA_SOURCE_SELECT_FILE_TYPE";

    /**
     * 文件公开等级
     */
    public static final String DATA_SOURCE_SELECT_OPEN_TYPE = "DATA_SOURCE_SELECT_OPEN_TYPE";
    /**
     * 秘密等级
     */
    public static final String DATA_SOURCE_SECURITY_LEVEL = "DATA_SOURCE_SECURITY_LEVEL";

    /**
     * 分支条件表达式比较符
     */
    public static final String EXPRESSION_OPERATOR = "EXPRESSION_OPERATOR";

    /**
     * 分支条件表达式关系
     */
    public static final String RELATION_SHIP = "RELATION_SHIP";

    /**
     * 在线消息通知类型定义-------------------------------------------
     */
    /**
     * 新工作计划--在线消息类型
     */
    public static final String ONLINE_MSG_WORKPLAN = "online_msg_workplan_100";
    /**
     * 新工作计划:title前缀
     */
    public static final String ONLINE_MSG_WORKPLAN_TITILE_PRE = "您有新工作计划：";
    /**
     * 协同办公--在线消息类型
     */
    public static final String ONLINE_MSG_TOGETHERWORK = "online_msg_togetherwork_100";
    /**
     * 待办信息title前缀
     */
    public static final String ONLINE_MSG_TOGETHERWORK_TITILE_PRE = "您有新待办信息：";
    /**
     * 邮件--在线消息类型
     */
    public static final String ONLINE_MSG_MAIL = "online_msg_mail_100";
    /**
     * 邮件提醒前缀
     */
    public static final String ONLINE_MSG_MAIL_TITLE_PRE = "您有新邮件：";

    /**
     * 会议
     */
    public static final String ONLINE_MSG_MEETING = "online_msg_meeting_100";
    /**
     * 新会议:title前缀
     */
    public static final String ONLINE_MSG_MEETING_PRE = "您有新会议：";
    /**
     * 变更会议:title前缀
     */
    public static final String ONLINE_MSG_MEETINGUPDATE_PRE = "您所参加的会议有变更：";
    /**
     * 取消会议:title前缀
     */
    public static final String ONLINE_MSG_MEETINGCANCEL_PRE = "您有会议被取消：";

    /**
     * 操作成功
     */
    public static final String RETMSG_OPERATION_SUCCESS = "操作成功";
    /**
     * 操作失败
     */
    public static final String RETMSG_OPERATION_FAIL = "操作失败";

    /**
     * retmsg 成功返回代码 code =0
     */
    public static final Integer RETMSG_SUCCESS_CODE = 0;
    /**
     * retmsg 异常返回代码 code =1
     */
    public static final Integer RETMSG_FAIL_CODE = 1;

    /**
     * --------文件类型 --------beg
     */
    public static final String FILE_MODULE_NAME_USER_ICON = "user_icon";
    /**
     * --------文件类型 --------end
     */

    /**
     * --------表单批量下载名称 --------beg
     */
    public static final String FILE_FORM_BATCH_DOWNLOAD_NAME = "form.zip";
    /**
     * --------文件表单批量下载名称  --------end
     */
    /**
     * --------表单批量下载名称 --------beg
     */
    public static final String PROCESS_BATCH_DOWNLOAD_NAME = "process.zip";
    /**
     * --------文件表单批量下载名称  --------end
     */
    /**
     * 固定表单流程(attendance_supplement:考勤补签)
     */
    public static final String FIXED_FORM_PROCESS_ATTEND_SUPPLEMENT = "attendance_supplement";

    /**
     * 固定表单流程(outside_meeting:外部会议)
     */
    public static final String FIXED_FORM_PROCESS_OUTSIDE_MEETING = "outside_meeting";

    /**
     * 固定表单流程(monthly_report:月报)
     */
    public static final String FIXED_FORM_PROCESS_MONTHLY_REPORT = "monthly_report";
    /**
     * 固定表单流程(ass_info:固定资产)
     */
    public static final String FIXED_FORM_PROCESS_ASS_INFO = "ass_info";
    /**
     * 固定表单流程(goo_purchase:办公用品申购)
     */
    public static final String FIXED_FORM_PROCESS_GOO_PURCHASE = "goo_purchase";
    /**
     * 固定表单流程(veh_use:车船使用申请)
     */
    public static final String FIXED_FORM_PROCESS_VEH_USE = "veh_use";
    /**
     * 固定表单流程(goo_inout:办公用品入库)
     */
    public static final String FIXED_FORM_PROCESS_GOO_IN = "goo_in";
    /**
     * 固定表单流程(goo_inout:办公用品入库测试)
     */
    public static final String FIXED_FORM_PROCESS_GOO_INOUT = "goo_inout";
    /**
     * 固定表单流程(goo_inout:办公用品领用)
     */
    public static final String FIXED_FORM_PROCESS_GOO_OUT = "goo_out";
    /**
     * 固定表单流程(goo_inout:办公用品报溢)
     */
    public static final String FIXED_FORM_PROCESS_GOO_MORE = "goo_more";
    /**
     * 固定表单流程(goo_inout:办公用品报损)
     */
    public static final String FIXED_FORM_PROCESS_GOO_LESS = "goo_less";
    /**
     * 固定表单流程(ass_pur:固定资产申购)
     */
    public static final String FIXED_FORM_PROCESS_ASS_PUR = "ass_pur";
    /**
     * 固定表单流程(ass_rec:固定资产验收)
     */
    public static final String FIXED_FORM_PROCESS_ASS_REC = "ass_rec";
    /**
     * 固定表单流程(ass_use:固定资产领用)
     */
    public static final String FIXED_FORM_PROCESS_ASS_USE = "ass_use";
    /**
     * 固定表单流程(ass_cha:固定资产移交)
     */
    public static final String FIXED_FORM_PROCESS_ASS_CHA = "ass_cha";
    /**
     * 固定表单流程(ass_rui:固定资产报废)
     */
    public static final String FIXED_FORM_PROCESS_ASS_RUI = "ass_rui";

    /**
     * 固定表单流程(read_receipt:收文阅件)
     */
    public static final String FIXED_FORM_PROCESS_READ_RECEIPT = "read_receipt";

    /**
     * --------------------------首页统计对象编码and名称start-----------------------------------
     */
    /**
     * 在线通知
     */
    public static final String AUTDATA_ONLINE_CODE = "onlineMsg";
    public static final String AUTDATA_ONLINE_NAME = "在线通知";
    /**
     * 待阅文件
     */
    public static final String AUTDATA_TOREAD_CODE = "toreadlist";
    public static final String AUTDATA_TOREAD_NAME = "待阅文件";
    /**
     * 通知公告
     */
    public static final String AUTDATA_PUBNOTICE_CODE = "pubnotice";
    public static final String AUTDATA_PUBNOTICE_NAME = "通知公告";
    /**
     * 我的待办
     */
    public static final String AUTDATA_TODOLIST_CODE = "todoList";
    public static final String AUTDATA_TODOLIST_NAME = "我的待办";

    /**
     * --------------------------消息模版调用参数--------------------------------------
     */
    /**
     * 协同办公
     */
    public static final String TEMPLATE_WORK_CODE = "协同办公";
    public static final String TEMPLATE_WORK_NAME = "协同办公";
    /**
     * 工作计划
     */
    public static final String TEMPLATE_PLAN_CODE = "工作计划";
    public static final String TEMPLATE_PLAN_NAME = "工作计划";
    /**
     * 邮箱
     */
    public static final String TEMPLATE_MAIL_CODE = "邮箱";
    public static final String TEMPLATE_MAIL_NAME = "邮箱";
    /**
     * 会议
     */
    public static final String TEMPLATE_MEETING_CODE = "会议";
    public static final String TEMPLATE_MEETING_NAME = "会议";

    /**
     * 消息类型-------------------------------
     */
    /**
     * 在线消息
     */
    public static final String TEMPLATE_TYPE_MSG = "1";
    /**
     * 手机短信
     */
    public static final String TEMPLATE_TYPE_SMS = "2";
    /**
     * 邮件
     */
    public static final String TEMPLATE_TYPE_MAIL = "3";

    /**
     * 消息模版 行为分类
     */
    public static final String TEMPLATE_BEHAVIOR_CB = "催办";
    public static final String TEMPLATE_BEHAVIOR_DB = "待办";
    public static final String TEMPLATE_BEHAVIOR_BJSJ = "办结时间";
    public static final String TEMPLATE_BEHAVIOR_XTRC = "新增日程";
    public static final String TEMPLATE_BEHAVIOR_ZXYJ = "撰写新邮件";
    public static final String TEMPLATE_BEHAVIOR_XJHY = "新建会议";
    public static final String TEMPLATE_BEHAVIOR_HYBG = "会议变更";
    public static final String TEMPLATE_BEHAVIOR_HYQX = "会议取消";
    public static final String TEMPLATE_BEHAVIOR_XTBG = "待办工作";
    public static final String TEMPLATE_BEHAVIOR_TS = "特送";

    /**
     * 弹窗推送打卡
     */
    public static final Integer SOCKET_CODE_200 = 200;
    /**
     * --------------------------首页统计对象编码and名称end--------------------------------------
     */
    /**
     * 表单正文类型
     */
    public static final String MODULE_PAGE_OFFICE = "pag";
    /**
     *  正文套打模板
     */
    public static final String MODULE_PAGE_OFFICE_PRINT = "pag_print";
    /**
     * 正文套红模板
     */
    public static final String MODULE_PAGE_OFFICE_RED_TITLE = "pag_red_title";
    
    /**
     * --------------------------系统管理员userName（推送系统消息时使用）----------------------------
     */
    public static final String SADMIN = "sadmin";
    public static final String TEMPLATE_BEHAVIOR_HYTX = "会议提醒";
    /**
     * http文件上传 参与数据签名
     */
    public static final String UPLOAD_SECRET = "19q0a7z1@5W7S5X735e0d2c7$3E2D6C97&6";

    /**
     * 时间格式
     */
    /**
     * 时间格式begin------------------------------------------------
     */
    public static final String PATTERN_BAR_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_BAR_YYYYMMDD = "yyyy-MM-dd";
    public static final String PATTERN_BAR_YYYYMM = "yyyy-MM";
    public static final String PATTERN_BAR_HHMM = "HH:mm";
    public static final String PATTERN_BAR_HHMMSSYYYYMMDD = "HH:mm:ss yyyy-MM-dd";
    public static final String PATTERN_BAR_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
    /**
     * 时间格式end------------------------------------------------
     */

    /**
     * 考勤模块常量信息begin--------------------------------------------------------------
     */
    /**
     * 上午允许迟到时间(分钟) 参数错误提示
     */
    public static final String ATT_AM_ALLOWLATE_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午允许迟到时间(分钟)";

    /**
     * 上午允许早退时间(分钟) 参数错误提示
     */
    public static final String ATT_AM_LEAVEEARLY_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午允许早退时间(分钟)";

    /**
     * 下午允许迟到时间(分钟) 参数错误提示
     */
    public static final String ATT_PM_ALLOWLATE_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置下午允许迟到时间(分钟)";

    /**
     * 下午允许早退时间(分钟) 参数错误提示
     */
    public static final String ATT_PM_LEAVEEARLY_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置下午允许早退时间(分钟)";

    /**
     * 上午上班打卡时间 参数错误提示
     */
    public static final String ATT_AM_WORK_PUNCH_TIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午上班打卡时间";

    /**
     * 上午下班打卡时间 参数错误提示
     */
    public static final String ATT_AM_OFFWORK_PUNCH_TIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午下班打卡时间";

    /**
     * 下午上班打卡时间 参数错误提示
     */
    public static final String ATT_PM_WORK_PUNCH_TIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置下午上班打卡时间";

    /**
     * 下午下班打卡时间 参数错误提示
     */
    public static final String ATT_PM_OFFWORK_PUNCH_TIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置下午下班打卡时间";

    /**
     * 上午上班开始打卡时间 参数错误提示
     */
    public static final String ATT_AM_WORK_PUNCH_BEGTIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午上班开始打卡时间";
    /**
     * 上午上班结束打卡时间 参数错误提示
     */
    public static final String ATT_AM_WORK_PUNCH_ENDTIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午上班结束打卡时间";

    /**
     * 上午下班开始打卡时间 参数错误提示
     */
    public static final String ATT_AM_OFFWORK_PUNCH_BEGTIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午下班开始打卡时间";

    /**
     * 上午下班结束打卡时间 参数错误提示
     */
    public static final String ATT_AM_OFFWORK_PUNCH_ENDTIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午下班结束打卡时间";

    /**
     * 下午上班开始打卡时间 参数错误提示
     */
    public static final String ATT_PM_WORK_PUNCH_BEGTIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午上班开始打卡时间";

    /**
     * 下午上班结束打卡时间 参数错误提示
     */
    public static final String ATT_PM_WORK_PUNCH_ENDTIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午上班结束打卡时间";

    /**
     * 下午下班开始打卡时间 参数错误提示
     */
    public static final String ATT_PM_OFFWORK_PUNCH_BEGTIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午下班开始打卡时间";

    /**
     * 下午下班结束打卡时间 参数错误提示
     */
    public static final String ATT_PM_OFFWORK_PUNCH_ENDTIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午下班结束打卡时间";
    /**
     * 上午上班打卡弹窗时间 参数错误提示
     */
    public static final String ATT_AM_SIGNIN_POPUP_TIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午上班打卡弹窗时间";

    /**
     * 上午下班打卡弹窗时间 参数错误提示
     */
    public static final String ATT_AM_SIGNOUT_POPUP_TIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置上午下班打卡弹窗时间";

    /**
     * 下午上班打卡弹窗时间 参数错误提示
     */
    public static final String ATT_PM_SIGNIN_POPUP_TIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置下午上班打卡弹窗时间";
    /**
     * 下午下班打卡弹窗时间 参数错误提示
     */
    public static final String ATT_PM_SIGNOUT_POPUP_TIME_ERRORMSG = "请到[系统维护]菜单下的[考勤打卡设置]设置下午下班打卡弹窗时间";

    /**
     * 上午签到错误提示
     */
    public static final String ATT_AM_SIGNIN_ERRORMSG = "您已经签到,请勿重复签到!";

    /**
     * 上午签退错误提示
     */
    public static final String ATT_AM_SIGNOUT_ERRORMSG = "您已经签退,请勿重复签退!";

    /**
     * 下午签到错误提示
     */
    public static final String ATT_PM_SIGNIN_ERRORMSG = "您已经签到,请勿重复签到!";

    /**
     * 下午签退错误提示
     */
    public static final String ATT_PM_SIGNOUT_ERRORMSG = "您已经签退,请勿重复签退!";

    /**
     * 上午签到
     */
    public static final String ATT_AM_SIGNIN = "签到";

    /**
     * 下午签退
     */
    public static final String ATT_AM_SIGNOUT = "签退";

    /**
     * 下午签到
     */
    public static final String ATT_PM_SIGNIN = "签到";

    /**
     * 下午签退
     */
    public static final String ATT_PM_SIGNOUT = "签退";

    /**
     * 准时打卡
     */
    public static final String ATT_CLOCK_ON_TIME = "准时打卡";

    /**
     * 迟到
     */
    public static final String ATT_LATE = "迟到";

    /**
     * 早退
     */
    public static final String ATT_LEAVE_EARLY = "早退";

    /**
     * 无打卡
     */
    public static final String ATT_NO_CLOCKING = "无打卡";

    /**
     * 签到弹窗消息提示
     */
    public static final String ATT_SIGNIN_POPUP_MSG = "友情提示：签到时间已到，请您及时签到";

    /**
     * 签退弹窗消息提示
     */
    public static final String ATT_SIGNOUT_POPUP_MSG = "友情提示：签退时间已到，请您及时签退";

    /**
     * 签到
     */
    public static final String ATT_SIGNIN = "签到";

    /**
     * 签退
     */
    public static final String ATT_SIGNOUT = "签退";

    /**
     * 考勤模块常量信息end--------------------------------------------------------------
     */
    /**
     * 紧急设置 1：一般 2：紧急 3：加急
     */
    public static final String COMMONLY = "一般";

    public static final String URGENT = "紧急";

    public static final String ADD_URENT = "加急";

    /**
     * 流程操作状态 1：提交 2：退回 3：撤回  4：加签  5：分发   6：传阅 7：特送
     */
    public static final Integer PROCESS_OPERATE_STATUS_1 = 1;

    public static final Integer PROCESS_OPERATE_STATUS_2 = 2;

    public static final Integer PROCESS_OPERATE_STATUS_3 = 3;

    public static final Integer PROCESS_OPERATE_STATUS_4 = 4;
    
    public static final Integer PROCESS_OPERATE_STATUS_5 = 5;
    
    public static final Integer PROCESS_OPERATE_STATUS_6 = 6;

    public static final Integer PROCESS_OPERATE_STATUS_7 = 7;
    
    public static final String PROCESS_OPERATE_STATUS_SUB = "提交";

    public static final String PROCESS_OPERATE_STATUS_RET = "退回";

    public static final String PROCESS_OPERATE_STATUS_REV = "撤回";

    public static final String PROCESS_OPERATE_STATUS_SIG = "加签";
    
    public static final String PROCESS_OPERATE_STATUS_DIS = "分发";
    
    public static final String PROCESS_OPERATE_STATUS_CIR = "传阅";

    public static final String PROCESS_OPERATE_STATUS_DEV = "特送";



    public static final Integer FULOPINION_DEF_RANK = 255;

    /**
     * 加签删除任务描述信息
     */
    public static final String ADDSIGN_REASON = "addSign";
    
    /**
     * 文件夹类型-百度编辑器图片文件夹
     */
    public static final String FILE_FOLDER_UEDITOR_PIC = "FILE_FOLDER_UEDITOR_PIC";

}
