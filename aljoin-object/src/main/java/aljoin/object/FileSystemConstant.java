 package aljoin.object;

import java.util.HashMap;
import java.util.Map;


public class FileSystemConstant {
     
     public final static Map<String, String> RESOURCE_TYPE_ID = new HashMap<String, String>();
     
     static {
         /**
          * --------------------------文件系统附件上传模块与资源分类id对应关系start-----------------------------------
          */
         /**
          * 内部会议草稿箱新增附件
          */
         RESOURCE_TYPE_ID.put("MEE_INSIDE_MEETING_DRAFT", "988297869630300161");
         /**
          * 内部会议新增附件
          */
         RESOURCE_TYPE_ID.put("MEE_INSIDE_MEETING", "988297792576741377");
         /**
          * 外部会议新增附件
          */
         RESOURCE_TYPE_ID.put("MEE_OUTSIDE_MEETING_DRAFT", "988297930858749953");
         /**
          * 外部会议草稿箱新增附件
          */
         RESOURCE_TYPE_ID.put("MEE_OUTSIDE_MEETING", "988298026723762177");
         
         /**
          * 收文阅件新增附件
          */
         RESOURCE_TYPE_ID.put("IOA_RECEIVE_FILE_ADD", "988722082824572930");
         /**
          * 待阅文件详情页面新增附件
          */
         RESOURCE_TYPE_ID.put("IOA_RECEIVE_FILE_DETAIL", "988722759407751170");
         
         /**
          * 工作日志新增附件
          */
         RESOURCE_TYPE_ID.put("OFF_DAILYLOG_ADD", "988998496656281602");
         
         /**
          * 工作日志编辑页面新增附件
          */
         RESOURCE_TYPE_ID.put("OFF_DAILYLOG_EDITOR", "988998670870892545");
         
         /**
          * 工作月报新增附件
          */
         RESOURCE_TYPE_ID.put("OFF_MONTH_REPORT", "988998797912166401");
         
         /**
          * 公共信息新增附件
          */
         RESOURCE_TYPE_ID.put("PUB_PUBLIC_INFO_ADD", "989067869081264129");
         
         /**
          * 公共信息草稿新增附件
          */
         RESOURCE_TYPE_ID.put("PUB_PUBLIC_INFO_DRAFT_ADD", "989067939851755522");
         
         /**
          * 车船信息管理图片上传
          */
         RESOURCE_TYPE_ID.put("VEH_INFO_IMAGE_ADD", "989121445803581443");
         
         /**
          * 车船信息编辑图片上传
          */
         RESOURCE_TYPE_ID.put("VEH_INFO_IMAGE_EDITOR", "989301963560030210");
         
         /**
          * 百度编辑器图片上传
          */
         RESOURCE_TYPE_ID.put("UEDITOR_IMAGE_ADD", "989162402752782338");
         
         /**
          * 个人信息图片上传
          */
         RESOURCE_TYPE_ID.put("AUT_USER_IMAGE_ADD", "989302207110680578");
         
         /**
          * 撰写新邮件新增附件
          */
         RESOURCE_TYPE_ID.put("MAI_WRITE_ADD", "989305488906076162");
         
         /**
          * 邮件草稿箱新增附件
          */
         RESOURCE_TYPE_ID.put("MAI_SCRAP_BOX_ADD", "989309780702478338");
         
         /**
          * 发件箱新增附件
          */
         RESOURCE_TYPE_ID.put("MAI_SEND_BOX_EDITOR", "989441888292958209");
         
         /**
          * 收件箱转发/回复附件
          */
         RESOURCE_TYPE_ID.put("MAI_RECEIVE_BOX_ADD", "989824899755474946");
         
         /**
          * 资源维护上传附件
          */
         RESOURCE_TYPE_ID.put("RES_RESOURCE", "989464082733572098");
         
         /**
          * 表单上传附件
          */
         RESOURCE_TYPE_ID.put("IOA_FORM_ADD", "989476192070819842");
         
         /**
          * 文件正文上传附件
          */
         RESOURCE_TYPE_ID.put("PAG_OFFICE", "988999807912166401");
         /**
          * --------------------------文件系统附件上传模块与资源分类id对应关系end-----------------------------------
          */
         
     }
     
}
