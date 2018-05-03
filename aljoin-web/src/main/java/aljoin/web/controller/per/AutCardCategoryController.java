package aljoin.web.controller.per;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutCardCategory;
import aljoin.aut.iservice.AutCardCategoryService;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 名片分类表(控制器).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
@Controller
@RequestMapping(value = "/per/autCardCategory", method = RequestMethod.POST)
@Api(value = "名片分类Controller", description = "个人中心->名片分类接口")
public class AutCardCategoryController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AutCardCategoryController.class);
    @Resource
    private AutCardCategoryService autCardCategoryService;

    /**
     * 
     * 名片分类表(页面).
     *
     * @return：String
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping(value = "/autCardCategoryPage", method = RequestMethod.GET)
    @ApiOperation(value = "名片分类接口")
    public String actCardCategoryPage(HttpServletRequest request, HttpServletResponse response) {

        return "per/autCardCategoryPage";
    }

    /**
     * 
     * 名片分类表(分页列表).
     *
     * @return：Page<ActCardCategory>
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/list")
    @ResponseBody
    public Page<AutCardCategory> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        AutCardCategory obj) {
        Page<AutCardCategory> page = null;
        try {
            CustomUser customUser = getCustomDetail();
            page = autCardCategoryService.list(pageBean, obj, customUser.getUserId());
        } catch (Exception e) {
            logger.error("查询名片分类失败", e.getMessage());
        }
        return page;
    }

    /**
     * 
     * 名片分类表(新增).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @ApiOperation(value = "新增名片分类接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "categoryName", value = "名片分类名称", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "isActive", value = "是否激活", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "categoryRank", value = "(同级)分类排序", required = true, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "parentId", value = "父级分类ID(新增1级分类，父id为0)", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "categoryLevel", value = "分类级别", required = true, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "userId", value = "分类归属用户ID", required = true, dataType = "string",
            paramType = "query")})
    public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutCardCategory obj) {

        CustomUser customUser = getCustomDetail();
        RetMsg retMsg = new RetMsg();

        try {
            retMsg = autCardCategoryService.add(obj, customUser.getUserId());
        } catch (Exception e) {
            logger.error("添加名片分类失败", e.getMessage());
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage(WebConstant.RETMSG_OPERATION_FAIL);
        }
        return retMsg;
    }

    /**
     * 
     * 名片分类表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutCardCategory obj) {
        RetMsg retMsg = new RetMsg();

        autCardCategoryService.deleteById(obj.getId());

        retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
        retMsg.setMessage(WebConstant.RETMSG_OPERATION_SUCCESS);
        return retMsg;
    }

    /**
     * 
     * 名片分类表(根据ID修改对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/update")
    @ResponseBody
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutCardCategory obj) {
        RetMsg retMsg = new RetMsg();
        AutCardCategory orgnlObj = autCardCategoryService.selectById(obj.getId());
        orgnlObj.setCategoryName(obj.getCategoryName());
        orgnlObj.setCategoryRank(obj.getCategoryRank());
        autCardCategoryService.updateById(orgnlObj);
        retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
        retMsg.setMessage(WebConstant.RETMSG_OPERATION_SUCCESS);
        return retMsg;
    }

    /**
     * 
     * 名片分类表(根据ID获取对象).
     *
     * @return：AutUser
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/getById")
    @ResponseBody
    public AutCardCategory getById(HttpServletRequest request, HttpServletResponse response, AutCardCategory obj) {
        return autCardCategoryService.selectById(obj.getId());
    }

    /**
     * 
     * 个人中心-名片夹分类-列表
     *
     * @return：List<AutCardCategory>
     *
     * @author：laijy
     *
     * @date：2017年10月27日 上午10:34:52
     */
    @RequestMapping("/getCardCategoryList")
    @ResponseBody
    public List<AutCardCategory> getCardCategoryList() throws Exception {

        CustomUser customUser = getCustomDetail();
        List<AutCardCategory> cardCategoryList = autCardCategoryService.getCardCategoryList(customUser.getUserId());

        return cardCategoryList;
    }

}
