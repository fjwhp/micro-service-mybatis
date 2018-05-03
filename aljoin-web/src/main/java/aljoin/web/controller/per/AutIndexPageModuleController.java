package aljoin.web.controller.per;

import java.util.ArrayList;
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

import aljoin.aut.dao.entity.AutIndexPageModule;
import aljoin.aut.iservice.AutIndexPageModuleService;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.iservice.SysDataDictService;
import aljoin.sys.iservice.SysParameterService;
import aljoin.web.controller.BaseController;

/**
 * 
 * 首页模块定制表(控制器).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-12
 */
@Controller
@RequestMapping("/per/autIndexPageModule")
public class AutIndexPageModuleController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AutIndexPageModuleController.class);
    @Resource
    private AutIndexPageModuleService autIndexPageModuleService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private SysDataDictService sysDataDictService;

    /**
     * 
     * 首页模块定制表(页面).
     *
     * @return：String
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/autIndexPageModulePage")
    public String autIndexPageModulePage(HttpServletRequest request, HttpServletResponse response) {

        return "per/autIndexPageModulePage";
    }

    /**
     * 
     * 首页模块定制表(分页列表).
     *
     * @return：Page<AutIndexPageModule>
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/list")
    @ResponseBody
    public Page<AutIndexPageModule> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        AutIndexPageModule obj) {
        Page<AutIndexPageModule> page = null;
        try {
            page = autIndexPageModuleService.list(pageBean, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 首页模块定制表(初始化首页及个性定制页数据).
     *
     * @return：RetMsg
     *
     * @author：
     *
     * @date：2017-11-07
     */
    @RequestMapping("/listgage")
    @ResponseBody
    public Page<AutIndexPageModule> listgage(HttpServletRequest request, HttpServletResponse response) {
        Page<AutIndexPageModule> page = new Page<AutIndexPageModule>();
        CustomUser user = getCustomDetail();
        RetMsg retMsg = new RetMsg();
        List<AutIndexPageModule> adpmList = new ArrayList<AutIndexPageModule>();
        try {
            adpmList = autIndexPageModuleService.selectUser(user.getUserId().toString());
            if (adpmList != null && adpmList.size() > 0) {

            } else {
                List<SysDataDict> lDataList = sysDataDictService.getByCode("adminLeftPage");
                if (lDataList != null && lDataList.size() > 0) {

                } else {
                    // retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                    // retMsg.setMessage("请设置数据字典 左页面配置编码为：adminLeftPage");
                    throw new Exception("请设置数据字典右页面配置编码为：adminRightPage");
                    // return retMsg;
                }
                List<SysDataDict> rDataList = sysDataDictService.getByCode("adminRightPage");
                if (rDataList != null && rDataList.size() > 0) {

                } else {
                    // retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                    // retMsg.setMessage("请设置数据字典右页面配置编码为：adminRightPage");
                    throw new Exception("请设置数据字典右页面配置编码为：adminRightPage");
                }

                for (SysDataDict sysDataDict : rDataList) {
                    AutIndexPageModule obj = new AutIndexPageModule();
                    obj.setIsDelete(0);
                    obj.setIsActive(1);
                    obj.setIsHide(0);
                    obj.setModuleRank(sysDataDict.getDictRank());
                    obj.setModuleName(sysDataDict.getDictKey());
                    obj.setModuleCode("R&" + sysDataDict.getDictValue());
                    autIndexPageModuleService.insert(obj);
                }
                for (SysDataDict sysDataDict : lDataList) {
                    AutIndexPageModule obj = new AutIndexPageModule();
                    obj.setIsDelete(0);
                    obj.setIsActive(1);
                    obj.setIsHide(0);
                    obj.setModuleRank(sysDataDict.getDictRank());
                    obj.setModuleName(sysDataDict.getDictKey());
                    obj.setModuleCode("L&" + sysDataDict.getDictValue());
                    autIndexPageModuleService.insert(obj);
                }
                adpmList = autIndexPageModuleService.selectUser(user.getUserId().toString());
            }
            page.setRecords(adpmList);
            page.setSize(10);
            page.setTotal(1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("", e);
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage(WebConstant.RETMSG_OPERATION_FAIL + e.getMessage());
        }

        return page;
    }

    /**
     * 
     * 首页模块定制表(初始化首页及个性定制页数据).
     *
     * @return：RetMsg
     *
     * @author：
     *
     * @date：2017-11-07
     */
    @RequestMapping("/init")
    @ResponseBody
    public RetMsg init(HttpServletRequest request, HttpServletResponse response) {

        CustomUser user = getCustomDetail();
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = autIndexPageModuleService.init(user.getUserId());
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }

        return retMsg;
    }

    /**
     * 
     * 首页模块定制表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutIndexPageModule obj) {
        RetMsg retMsg = new RetMsg();
        autIndexPageModuleService.deleteById(obj.getId());
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 首页模块定制表(修改对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/update")
    @ResponseBody
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutIndexPageModule obj) {
        RetMsg retMsg = new RetMsg();
        CustomUser user = getCustomDetail();
        try {
            retMsg = autIndexPageModuleService.updateModule(obj, user.getUserId());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("", e);
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("更新失败" + e.getMessage());
        }
        return retMsg;
    }

    /**
     * 
     * 首页模块定制表(移动模块重新排序).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/removeUpdate")
    @ResponseBody
    public RetMsg removeUpdate(HttpServletRequest request, HttpServletResponse response) {
        RetMsg retMsg = new RetMsg();
        CustomUser user = getCustomDetail();
        try {
            String codes = request.getParameter("module_code");
            String type = request.getParameter("type");
            if (type == null && "".equals(type)) {
                retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                retMsg.setMessage("参数为空");
            }
            if (codes == null && "".equals(codes)) {
                retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                retMsg.setMessage("参数为空");
            }
            retMsg = autIndexPageModuleService.removeUpdate(codes, type, user.getUserId());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("", e);
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("更新失败" + e.getMessage());
        }
        return retMsg;
    }

    /**
     * 
     * 首页模块定制表(根据ID获取对象).
     *
     * @return：AutUser
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/getById")
    @ResponseBody
    public AutIndexPageModule getById(HttpServletRequest request, HttpServletResponse response,
        AutIndexPageModule obj) {
        return autIndexPageModuleService.selectById(obj.getId());
    }

}
