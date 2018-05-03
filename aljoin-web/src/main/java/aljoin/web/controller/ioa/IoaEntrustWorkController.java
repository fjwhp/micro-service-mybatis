package aljoin.web.controller.ioa;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.dao.entity.ActAljoinDelegate;
import aljoin.act.dao.entity.ActAljoinDelegateInfoHis;
import aljoin.act.dao.object.ActAljoinDelegateVO;
import aljoin.act.dao.object.AllTaskDataShowVO;
import aljoin.act.iservice.ActAljoinDelegateInfoHisService;
import aljoin.act.iservice.ActAljoinDelegateService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import aljoin.web.service.act.ActAljoinDelegateWebService;
import aljoin.web.task.WebTask;

/**
 * 
 * 委托工作
 *
 * @author：zhongjy
 * 
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "/ioa/ioaEntrustWork")
public class IoaEntrustWorkController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(IoaEntrustWorkController.class);
	@Resource
	private ActAljoinDelegateService actAljoinDelegateService;
	@Resource
    private ActAljoinDelegateInfoHisService actAljoinDelegateInfoHisService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private ActAljoinDelegateWebService actAljoinDelegateWebService;
	@Resource
	private WebTask webTask;

	/**
	 * 
	 * 委托工作页面
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年10月12日 上午8:42:57
	 */
	@RequestMapping("/ioaEntrustWorkPage")
	public String ioaMonitorPage(HttpServletRequest request) {
		return "ioa/ioaEntrustWorkPage";
	}

	/**
	 * 
	 * 分页
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-24
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinDelegate> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			ActAljoinDelegate obj) {
		Page<ActAljoinDelegate> page = null;
		try {
			page = actAljoinDelegateService.list(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 任务委托表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-24
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			Long userId = customUser.getUserId();
			obj.setOwnerUserId(userId.toString());
			AutUser autUser = autUserService.selectById(userId);
			obj.setOwnerUserName(autUser.getUserName());
			obj.setOwnerUserFullname(autUser.getFullName());
			// actAljoinDelegateService.add(obj);
//			actAljoinDelegateWebService.add(obj);
			// 由于执行新增委托时，有可能时间很长，所以用异步线程处理
			//插入业务表数据
			ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.add(obj);
			if(null != actAljoinDelegate){
			  // 进行真正的委托处理
			  webTask.addDelegateBiz(actAljoinDelegate);
			}
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 任务委托表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-24
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, String ids) {
		RetMsg retMsg = new RetMsg();
		String[] idsArr = ids.split(",");
		try {
			for (String id : idsArr) {
				Long delegateId = Long.parseLong(id);
				// actAljoinDelegateService.deleteById(delegateId);
				// 流程删除（以上的修改操作已经包含在以下的方法中）
				ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.selectById(delegateId);
				// 对于3-已结束（时间结束），4-已终止（人为终止）'的数据,只需要删除本表数据即可
				if (actAljoinDelegate.getDelegateStatus().intValue() == 3
						|| actAljoinDelegate.getDelegateStatus().intValue() == 4) {
				    Where<ActAljoinDelegateInfoHis> where = new Where<ActAljoinDelegateInfoHis>();
				    where.eq("owner_user_id", actAljoinDelegate.getOwnerUserId());
				    where.eq("has_do", 1);
				    where.andNew().eq("assignee_user_id", actAljoinDelegate.getAssigneeUserIds()).or().like("delegate_user_ids", actAljoinDelegate.getAssigneeUserIds().toString());
				    where.andNew().between("create_time", actAljoinDelegate.getBegTime(),actAljoinDelegate.getEndTime());
				    where.like("delegate_ids", actAljoinDelegate.getId().toString());
				    int count = actAljoinDelegateInfoHisService.selectCount(where);
				    if(count > 0){
				        retMsg.setCode(1);
			            retMsg.setMessage("有委托内容的委托记录不能删除");
			            return retMsg;
				    }else{
				        actAljoinDelegateService.deleteById(actAljoinDelegate.getId());
				    }
				} else {
					// 删除委托数据
				    actAljoinDelegateWebService.stopDelegateBiz(actAljoinDelegate, true);
				    // 在异步线程中终止委托
				    webTask.stopDelegateBiz(actAljoinDelegate, 3);

				}
			}
			retMsg.setCode(0);
			retMsg.setMessage("删除成功");
		} catch (Exception e) {
			retMsg.setCode(1);
            retMsg.setMessage(e.toString());
            logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 委托终止
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-24
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, String ids) {
		RetMsg retMsg = new RetMsg();

		try {
			String[] result = null;
			if (!StringUtils.isEmpty(ids)) {
				if (ids.indexOf(",") > -1) {
					if (ids.endsWith(",")) {
						result = ids.substring(0, ids.lastIndexOf(",")).split(",");
					} else {
						result = ids.split(",");
					}
				} else {
					result = ids.split(",");
				}

				if (result != null) {
					for (String id : result) {
						Long delegateId = Long.parseLong(id);
						ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.selectById(delegateId);
						// actAljoinDelegate.setDelegateStatus(4);
						// actAljoinDelegateService.updateById(actAljoinDelegate);
						// 流程中止（以上的修改操作已经包含在以下的方法中）
						actAljoinDelegateWebService.stopDelegateBiz(actAljoinDelegate, false);
						webTask.stopDelegateBiz(actAljoinDelegate, 2);
					}

					retMsg.setCode(0);
					retMsg.setMessage("中止成功");
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 任务委托表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-24
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinDelegate getById(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegate obj) {
		return actAljoinDelegateService.selectById(obj.getId());
	}

	/**
	 * 
	 * 任务委托列表
	 *
	 * @return：Page<ActAljoinDelegate>
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-24
	 */
	@RequestMapping("/getAllEntrustWork")
	@ResponseBody
	public Page<ActAljoinDelegateVO> getAllEntrustWork(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, ActAljoinDelegateVO obj) {
		Page<ActAljoinDelegateVO> page = null;
		try {
			CustomUser customUser = getCustomDetail();
			page = actAljoinDelegateService.getAllEntrustWork(pageBean, obj, customUser.getUserId());
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 委托任务列表
	 *
	 * @return：Page<ActAljoinDelegate>
	 *
	 * @author：huangw
	 *
	 * @date：2017-10-24
	 */
	@RequestMapping("/getAllEntrustWorkDate")
	@ResponseBody
	public Page<AllTaskDataShowVO> getAllEntrustWorkDate(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, ActAljoinDelegateVO obj) {
		Page<AllTaskDataShowVO> page = null;
		try {
			CustomUser customUser = getCustomDetail();
			page = actAljoinDelegateService.getAllEntrustWorkData(pageBean, obj, customUser.getUserId());
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}
}
