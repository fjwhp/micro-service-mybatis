package aljoin.aut.service.app;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.mapper.AutUserPubMapper;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.iservice.app.AppAutUserPubService;
import aljoin.aut.security.CustomPasswordEncoder;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 用户公共信息表(服务实现类).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
@Service
public class AppAutUserPubServiceImpl extends ServiceImpl<AutUserPubMapper, AutUserPub>
		implements AppAutUserPubService {
  private final static Logger logger = LoggerFactory.getLogger(AppAutUserPubServiceImpl.class);
	@Resource
	private AutUserPubMapper mapper;

	@Resource
	private AutUserService autUserService;

	@Resource
	private AutDepartmentUserService autDepartmentUserService;

	@Resource
	private AutPositionService autPositionService;

	@Resource
	private AutUserPositionService autUserPositionService;

	@Resource
	private AutUserPubService autUserPubService;
	
	@Resource
	private CustomPasswordEncoder customPasswordEncoder;

	@Override
	public Page<AutUserPub> list(PageBean pageBean, AutUserPubVO obj) throws Exception {

		Where<AutUserPub> where = new Where<AutUserPub>();
		where.orderBy("create_time", false);
		Page<AutUserPub> page = selectPage(new Page<AutUserPub>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		return page;
	}

	@Override
	public void physicsDeleteById(Long id) throws Exception {
		mapper.physicsDeleteById(id);
	}

	@Override
	public void copyObject(AutUserPub obj) throws Exception {
		mapper.copyObject(obj);
	}

	@Override
	public RetMsg updatePwd(AutUserPubVO obj,AutAppUserLogin autAppUserLogin) throws Exception {
		AutUser user = obj.getAutUser();
		//用户输入的老密码
		String oldPwd = user.getUserPwd();
		//新密码
		String newPwd = obj.getNewUserPwd();
		//新密码确认
		String newPwdConfirm = obj.getNewUserPwdConfirm();
		AutUser autUser = autUserService.selectById(autAppUserLogin.getUserId());
		RetMsg retMsg = new RetMsg();
		// 校验原密码是否正确
		if (customPasswordEncoder.matches(oldPwd, autUser.getUserPwd())) {
			// 比对前后输入的2次新密码是否相同
			if(newPwd==newPwdConfirm || newPwd.equals(newPwdConfirm)){
				autUser.setUserPwd(customPasswordEncoder.encode(newPwd));
				autUserService.updateById(autUser);
				retMsg.setCode(0);
				retMsg.setMessage("操作成功");
			}else{
				retMsg.setCode(1);
				retMsg.setMessage("前后两次新密码不一致");
			}
		}else{
			retMsg.setCode(1);
			retMsg.setMessage("原密码不正确");
		}
		return retMsg;
	}

	@Override
	public List<AutUserPubVO> getAutUserPubVOList(AutDepartment autDepartment) throws Exception {

		Long deptId = autDepartment.getId();

		// 根据部门id找到部门下的所有用户id
		Where<AutDepartmentUser> aduWhere = new Where<AutDepartmentUser>();
		aduWhere.setSqlSelect("user_id");
		aduWhere.eq("dept_id", deptId);
		List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(aduWhere);
		// 获得userIdList
		List<Long> userIdList = new ArrayList<Long>();
		for (AutDepartmentUser deptUser : deptUserList) {
			userIdList.add(deptUser.getUserId());
		}

		// 根据用户id找aut_user获获得所有full_name、user_email
		Where<AutUser> auWhere = new Where<AutUser>();
		auWhere.setSqlSelect("id,full_name,user_email");
		auWhere.orderBy("id");
		auWhere.in("id", userIdList);
		List<AutUser> autUserList = autUserService.selectList(auWhere);

		// 根据userId找aut_user_position，获得用户所有的所在岗位（1用户可能多岗）
		Where<AutUserPosition> aupWhere = new Where<AutUserPosition>();
		aupWhere.setSqlSelect("user_id,position_id");
		aupWhere.in("user_id", userIdList);
		List<AutUserPosition> autUserPositionList = autUserPositionService.selectList(aupWhere);

		// 根据部门id，找aut_position获得该部门下的所有岗位id(有deptId,positionId)
		Where<AutPosition> apWhere = new Where<AutPosition>();
		apWhere.setSqlSelect("id,position_name,dept_id");
		apWhere.eq("dept_id", deptId);
		List<AutPosition> autPositionList = autPositionService.selectList(apWhere);

		// 根据userIdList查询相关所有用户公共信息
		Where<AutUserPub> auPubWhere = new Where<AutUserPub>();
		auPubWhere.setSqlSelect("user_id,phone_number,tel_number,fax_number,law_number,chest_card_number,user_icon");
		auPubWhere.in("user_id", userIdList);
		List<AutUserPub> autUserPubList = autUserPubService.selectList(auPubWhere);

		// 拼接返回值VOList
		List<AutUserPubVO> autUserPubVOList = new ArrayList<AutUserPubVO>();

		// 1、拼接用户进voList
		for (AutUser autUser : autUserList) {
			AutUserPubVO vo = new AutUserPubVO();
			vo.setAutUser(autUser);
			autUserPubVOList.add(vo);
		}
		// 2、拼接用户-岗位关联表
		for (AutUserPubVO vo : autUserPubVOList) {
			for (AutUserPosition autUserPosition : autUserPositionList) {
				if (vo.getAutUser().getId().equals(autUserPosition.getUserId())) {
					vo.setAutUserPosition(autUserPosition);
				}
			}
		}
		// 3、岗位表
		for (AutUserPubVO vo : autUserPubVOList) {
			for (AutPosition autPosition : autPositionList) {
				if (vo.getAutUserPosition().getPositionId().equals(autPosition.getId())) {
					vo.setAutPosition(autPosition);
				}
			}
		}
		// 4、拼接公共信息
		for (AutUserPubVO vo : autUserPubVOList) {
			for (AutUserPub autUserPub : autUserPubList) {
				if (vo.getAutUser().getId().equals(autUserPub.getUserId())) {
					vo.setAutUserPub(autUserPub);
				}
			}
		}

		return autUserPubVOList;
	}

	@Override
	public RetMsg add(AutUserPubVO obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		try {
			AutUser autUser = obj.getAutUser();
			AutUserPub autUserPub = obj.getAutUserPub();
			if (StringUtils.checkValNotNull(autUser)) {
				autUserService.insertOrUpdate(autUser);
			}
			if (StringUtils.checkValNotNull(autUserPub)) {
				autUserPub.setUserId(autUser.getId());
				if (StringUtils.checkValNull(autUserPub.getMaxMailSize())) {
					// 设置默认邮件大小1G
					autUserPub.setMaxMailSize(1073741824);
				}
				autUserPubService.insert(autUserPub);
			}
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
		  logger.error("",e);
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	@Override
	public RetMsg update(AutUserPubVO obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		try {
			AutUser autUser = obj.getAutUser();
			AutUserPub autUserPub = obj.getAutUserPub();

			if (StringUtils.checkValNotNull(autUser)) {
				AutUser orgnlObj = autUserService.selectById(autUser.getId());
				orgnlObj.setUserEmail(autUser.getUserEmail());
				autUserService.updateById(orgnlObj);
			}
			// 前端传来的AutUserPub如果非空则插入，如果为空则不处理
			if (StringUtils.checkValNotNull(autUserPub)) {
				AutUserPub autUserPub2 = autUserPubService.selectById(autUserPub.getId());
				// 1、有查到用户信息，则更新
				if (StringUtils.checkValNotNull(autUserPub2)) {
					autUserPub2.setPhoneNumber(autUserPub.getPhoneNumber());
					autUserPub2.setTelNumber(autUserPub.getTelNumber());
					autUserPub2.setFaxNumber(autUserPub.getFaxNumber());
					autUserPub2.setLawNumber(autUserPub.getLawNumber());
					autUserPub2.setChestCardNumber(autUserPub.getChestCardNumber());
					autUserPub2.setUserIcon(autUserPub.getUserIcon());
					autUserPubService.updateById(autUserPub2);
					// 2、没查到用户信息，则新增
				} else {
					add(obj);
				}
			}
			retMsg.setCode(0);
			retMsg.setMessage("修改成功");
		} catch (Exception e) {
		  logger.error("",e);
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}
}
