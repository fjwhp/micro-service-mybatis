package aljoin.act.iservice;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.act.dao.entity.ActAljoinForm;
import aljoin.act.dao.object.ActAljoinFormCategoryVO;
import aljoin.act.dao.object.ActAljoinFormShowVO;
import aljoin.act.dao.object.ActAljoinFormVO;
import aljoin.act.dao.source.BaseSource;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 表单表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-08-31
 */
public interface ActAljoinFormService extends IService<ActAljoinForm> {

    /**
     * 
     * 表单表(分页列表).
     *
     * @return：Page<ActAljoinForm>
     *
     * @author：zhongjy
     *
     * @date：2017-08-31
     */
    public Page<ActAljoinFormCategoryVO> list(PageBean pageBean, ActAljoinForm obj) throws Exception;

    /**
     * 
     * 表单新增
     * @return 
     *
     * @return：String
     *
     * @author：pengsp
     *
     * @date：2017-08-31
     */
    public String add(ActAljoinFormShowVO form) throws Exception;

    /**
     * 
     * 表单删除
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-08-31
     */
    public void delete(ActAljoinForm obj) throws Exception;

    /**
     * 
     * 根据表单分类获取所有表单
     *
     * @return：List<ActAljoinForm>
     *
     * @author：pengsp
     *
     * @date：2017-08-31
     */
    public List<ActAljoinForm> getAllForm(Long categoryId) throws Exception;

    /**
     * 
     * 根据表单id预览所有
     *
     * @return：ActAljoinFormVO
     *
     * @author：pengsp
     *
     * @date：2017-08-31
     */
    public ActAljoinFormShowVO getAllById(Long id) throws Exception;

    /**
     * 
     * 根据表单id修改表单内容
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-08-31
     */
    public void update(ActAljoinFormShowVO form) throws Exception;

    /**
     * 
     * 获取数据源列表
     *
     * @return：List<BaseSource>
     *
     * @author：pengsp
     *
     * @date：2017-08-31
     */
    List<BaseSource> getAllType(String type) throws Exception;
    
    /**
     * 
     * 表单复制
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-03-02
     */
	public RetMsg copy(ActAljoinForm obj);
	
	/**
     * 
     * 表单导出
	 * 
	 * @throws Exception 
     *
     * @return：void
     *
     * @author：caizx
     *
     * @date：2018-03-03
     */
	public void export(HttpServletResponse response, ActAljoinFormVO obj) throws Exception;
	
	/**
     * 
     * 表单导入
     * @return 
     * 
     * @throws Exception 
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-03-03
     */
    public RetMsg fileImport(MultipartHttpServletRequest request) throws Exception;
    
    /**
     * 
     * 表单提交
     * @return 
     * 
     * @throws Exception 
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-03-16
     */
    public RetMsg fileSubmit(ActAljoinFormVO form) throws Exception;
    
	public Page<ActAljoinFormCategoryVO> retrunList(PageBean pageBean, ActAljoinForm obj) throws Exception;
	
	/**
     * 
     * 验证表单中是否存在流水号
     * 
     * @return：List<String>
     *
     * @author：caizx
     *
     * @date：2018-03-30
     */
    public List<String> validateSerialNum(String formIds);

    /**
     * 
     * 验证表单中是否存在文号
     * 
     * @return：List<String>
     *
     * @author：caizx
     *
     * @date：2018-03-30
     */
    public List<String> validateDocumentNum(String formIds);
	
	
	
}
