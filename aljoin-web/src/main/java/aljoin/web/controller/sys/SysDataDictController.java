package aljoin.web.controller.sys;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.iservice.SysDataDictService;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;

/**
 * 
 * 数据字典表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-05-27
 */
@Controller
@RequestMapping("/sys/sysDataDict")
public class SysDataDictController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(SysDataDictController.class);
	@Resource
	private SysDataDictService sysDataDictService;

	/**
	 * 
	 * 数据字典表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/sysDataDictPage")
	public String sysDataDictPage(HttpServletRequest request, HttpServletResponse response) {

		return "sys/sysDataDictPage";
	}

	/**
	 * 
	 * 数据字典表(分页列表).
	 *
	 * @return：Page<SysDataDict>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[数据字典]-[搜索]")
	public Page<SysDataDict> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, SysDataDict obj) {
		Page<SysDataDict> page = null;
		try {
			page = sysDataDictService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 数据字典表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[数据字典]-[新增]")
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SysDataDict obj, String[] dictKey1, String[] dictValue1,
			String[] isActive1,Integer[] dictRank1) {
		RetMsg retMsg = new RetMsg();

		// 判断字典编码是否已经存在
		Where<SysDataDict> where = new Where<SysDataDict>();
		where.eq("dict_code", obj.getDictCode());
		if (sysDataDictService.selectCount(where) > 0) {
			retMsg.setCode(1);
			retMsg.setMessage("字典编码已被占用");
		} else {
			for (int i = 0; i < dictKey1.length; i++) {
				SysDataDict dataDict = new SysDataDict();
				dataDict.setDictCode(obj.getDictCode());
				dataDict.setDictName(obj.getDictName());
				dataDict.setDictKey(dictKey1[i]);
				dataDict.setDictValue(dictValue1[i]);
				dataDict.setIsActive(Integer.parseInt(isActive1[i]));
				dataDict.setDictRank(dictRank1[i]);
				dataDict.setDictType(obj.getDictType());
				sysDataDictService.insert(dataDict);
			}
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		}
		return retMsg;
	}

	/**
	 * 
	 * 数据字典表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[数据字典]-[删除]")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SysDataDict obj) {
		RetMsg retMsg = new RetMsg();

		sysDataDictService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 数据字典表(根据dictCode删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/deleteByCode")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[数据字典]-[删除]")
	public RetMsg deleteByCode(HttpServletRequest request, HttpServletResponse response, SysDataDict obj) {
		RetMsg retMsg = new RetMsg();

		Where<SysDataDict> where = new Where<SysDataDict>();
		where.eq("dict_code", obj.getDictCode());
		sysDataDictService.delete(where);

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 数据字典表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[数据字典]-[修改]")
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, SysDataDict obj, String[] dictKey1, String[] dictValue1,
			String[] isActive1, String[] id1,Integer[] dictRank1) {
		RetMsg retMsg = new RetMsg();
		try {
			sysDataDictService.update(obj, dictKey1, dictValue1, isActive1, id1,dictRank1);
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			retMsg.setCode(1);
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 数据字典表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[数据字典]-[详情]")
	public SysDataDict getById(HttpServletRequest request, HttpServletResponse response, SysDataDict obj) {
		return sysDataDictService.selectById(obj.getId());
	}

	/**
	 * 
	 * 数据字典表(根据dictCode获取对象列表).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/getListByCode")
	@ResponseBody
	public List<SysDataDict> getListByCode(HttpServletRequest request, HttpServletResponse response, SysDataDict obj) {
		Where<SysDataDict> where = new Where<SysDataDict>();
		where.eq("dict_code", obj.getDictCode());
		where.orderBy("dict_rank");
		List<SysDataDict> list = sysDataDictService.selectList(where);
		return list;
	}

}
