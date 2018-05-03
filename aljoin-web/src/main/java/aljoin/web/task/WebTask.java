package aljoin.web.task;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import aljoin.act.dao.entity.ActAljoinDelegate;
import aljoin.act.iservice.ActAljoinDelegateService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.security.CustomUser;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.dao.object.MeeOutsideMeetingVO;
import aljoin.off.dao.object.OffSchedulingVO;
import aljoin.pub.dao.entity.PubPublicInfo;
import aljoin.pub.dao.object.PubPublicInfoVO;
import aljoin.pub.iservice.PubPublicInfoService;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.iservice.SmsShortMessageService;
import aljoin.sys.dao.entity.SysOperateLog;
import aljoin.sys.iservice.SysOperateLogService;
import aljoin.web.service.act.ActAljoinDelegateWebService;
import aljoin.web.service.act.ActAljoinSendMsgWebService;
import aljoin.web.service.mai.MaiSendBoxWebService;
import aljoin.web.service.mee.MeeInsideMeetingWebService;
import aljoin.web.service.mee.MeeOutsideMeetingWebService;
import aljoin.web.service.off.OffSchedulingWebService;
import aljoin.web.service.pub.PubPublicInfoWebService;

/**
 * 
 * 异步线程类
 *
 * @author：zhongjy
 *
 * @date：2017年6月5日 下午7:01:17
 */
@Component
public class WebTask {

	private final static Logger logger = LoggerFactory.getLogger(WebTask.class);

	@Resource
	private SysOperateLogService sysOperateLogService;
	@Resource
	private MaiSendBoxService maiSendBoxService;
	@Resource
	private MaiSendBoxWebService maiSendBoxWebService;
	@Resource
	private PubPublicInfoWebService pubPublicInfoWebService;
	@Resource
	private OffSchedulingWebService offSchedulingWebService;
	@Resource
	private MeeInsideMeetingWebService meeInsideMeetingWebService;
	@Resource
	private AutMsgOnlineService autMsgOnlineService;
	@Resource
	private SmsShortMessageService smsShortMessageService;
	@Resource
	private MeeOutsideMeetingWebService meeOutsideMeetingWebService;
	@Resource
	private PubPublicInfoService pubPublicInfoService;
	@Resource
	private ActAljoinSendMsgWebService actAljoinSendMsgWebService;
	@Resource
	private ActAljoinDelegateWebService actAljoinDelegateWebService;
	@Resource
	private ActAljoinDelegateService actAljoinDelegateService;
	/**
	 * 
	 * 日志处理线程
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年6月5日 下午7:08:59
	 */
	@Async("taskAsyncPool")
	public void webLogTask(SysOperateLog sysOperateLog) throws InterruptedException {
		sysOperateLogService.insert(sysOperateLog);
		logger.info("操作日志记录成功...");
	}

	/**
	 * 
	 * 邮件发送处理线程
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年6月5日 下午7:08:59
	 */
	@Async("taskAsyncPool")
	public void maiSendTask(MaiWriteVO obj, AutUser user) throws InterruptedException {
		try {
			maiSendBoxWebService.add(obj, user);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("邮件发送成功...");
	}

	/**
	 * 
	 * 草稿发送处理线程
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月4日 下午7:08:59
	 */
	@Async("taskAsyncPool")
	public void maiDraftSendTask(MaiWriteVO obj, AutUser user) throws InterruptedException {
		try {
			maiSendBoxService.add(obj, user);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("邮件发送成功...");
	}

	/**
	 * 
	 * 公共信息新建线程
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月5日 上午10:51:25
	 */
	@Async("taskAsyncPool")
	public void pubPublicInfoTask(PubPublicInfoVO obj) throws InterruptedException {
		try {
			pubPublicInfoWebService.add(obj,null);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("公共信息新建成功...");
	}

	/**
	 * 
	 * 日程安排线程
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月5日 下午1:21:13
	 */
	@Async("taskAsyncPool")
	public void offTask(OffSchedulingVO obj, AutUser user) throws InterruptedException {
		try {
			offSchedulingWebService.add(obj, user);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("日程安排发布成功...");
	}

	/**
	 * 
	 * 内部会议
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月5日 下午1:21:50
	 */
	@Async("taskAsyncPool")
	public void meeInsideTask(MeeInsideMeetingVO obj, AutUser user) throws InterruptedException {
		try {
			meeInsideMeetingWebService.add(obj, user);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("内部会议发布成功...");
	}

	/**
	 * 
	 * 供调用的在线消息线程
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月6日 上午11:21:02
	 */
	@Async("taskAsyncPool")
	public void pushMessageToUserList(AutMsgOnlineRequestVO msgRequest) throws InterruptedException {
		try {
			autMsgOnlineService.pushMessageToUserList(msgRequest);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("在线消息发送成功...");
	}

	/**
	 * 
	 * @param user 
	 * 供调用的短信提醒线程
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月7日 上午9:20:08
	 */
	@Async("taskAsyncPool")
	public void shortMsgAdd(SmsShortMessage obj, AutUser user) throws InterruptedException {
		try {
			smsShortMessageService.add(obj,user);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("短信发送成功...");
	}

	/**
	 * 
	 * 供调用的邮件提醒线程
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月7日 上午9:20:38
	 */
	@Async("taskAsyncPool")
	public void maiTask(MaiWriteVO obj, AutUser user) throws InterruptedException {
		try {
			maiSendBoxWebService.addMsg(obj, user);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("邮件发送成功...");
	}

	/**
	 * 
	 * 外部会议新增线程
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月7日 上午11:27:02
	 */
	@Async("taskAsyncPool")
	public void meeOutsideTask(MeeOutsideMeetingVO obj, CustomUser createuser) throws InterruptedException {
		try {
			meeOutsideMeetingWebService.add(obj, createuser);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("外部会议提交成功...");
	}
	
	/**
	 * 
	 * @param user 
	 * 公共信息详情查看维护已读未读信息
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月7日 上午11:27:02
	 */
	@Async("taskAsyncPool")
	public void pubDetail(PubPublicInfo pubPublicInfo, AutUser user) throws InterruptedException {
		try {
			pubPublicInfoService.isReadPub(pubPublicInfo,user);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("公共信息已读未读信息维护成功...");
	}
	
	/**
	 * 
	 * 流程相关环节跳转，发送待办消息给相应办理人(发送在线消息给下一任务节点候选人（固定模块+收文阅卷）确定下一环节只有一个节点不可能出现并行的流程使用)
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017年12月7日 上午11:27:02
	 */
	@Async("taskAsyncPool")
	public void sendOnlineMsg(String processInstanceId, String handle, AutUser user) throws InterruptedException {
		try {
			actAljoinSendMsgWebService.sendOnlineMsg(processInstanceId, handle, user);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("待办信息发送相关用户成功...");
	}
	
	/**
	 * 
	 * 下一任务节点是有可能是多个任务的发送消息-不跟固定模块写在一起
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017年12月7日 上午11:27:02
	 */
	@Async("taskAsyncPool")
	public void sendOnlineMsg4MulTask(Map<String, String> param, AutUser user) throws InterruptedException {
		try {
			actAljoinSendMsgWebService.sendOnlineMsg4MulTask(param, user);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("待办信息发送相关用户成功...");
	}
	
	/**
     * 
     * 收文阅卷使用，因为收文阅卷详情页面跟待办不一样，单独写一个给他
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017年12月7日 上午11:27:02
     */
    @Async("taskAsyncPool")
    public void sendOnlineMsg4IoaRead(Map<String, Object> param, AutUser user) throws InterruptedException {
        try {
            actAljoinSendMsgWebService.sendOnlineMsg4IoaRead(param, user);
        } catch (Exception e) {
            logger.error("", e);
        }
        logger.info("待办信息发送相关用户成功...");
    }

    /**
     *
     * 新增委托处理
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017年12月7日 上午11:27:02
     */
    @Async("taskAsyncPool")
    public void addDelegateBiz(ActAljoinDelegate actAljoinDelegate) throws InterruptedException {
        try {
          actAljoinDelegateWebService.addDelegateBiz(actAljoinDelegate);
        } catch (Exception e) {
            logger.error("", e);
        }
        logger.info("新增委托处理成功...");
    }

    /**
     *
     * 终止/删除委托处理
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017年12月7日 上午11:27:02
     */
    @Async("taskAsyncPool")
    public void stopDelegateBiz(ActAljoinDelegate actAljoinDelegate, int stopType) throws InterruptedException {
        try {
          actAljoinDelegateService.stopDelegateBiz(actAljoinDelegate, stopType);
        } catch (Exception e) {
            logger.error("", e);
        }
        logger.info("终止/删除委托处理成功...");
    }

	/**
	 *
	 * 特送人员在线消息提醒
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017年12月7日 上午11:27:02
	 */
	@Async("taskAsyncPool")
	public void sendMutilOnlineMsg4Delivery(String processInstanceIds, Map<String,String> handleMap, AutUser user) throws InterruptedException {
		try {
			actAljoinSendMsgWebService.sendMutilOnlineMsg4Delivery(processInstanceIds, handleMap, user);
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("特送人员信息发送相关用户成功...");
	}
}
