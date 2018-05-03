package aljoin.res.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.file.object.UploadParam;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.dao.object.ResResourceVO;

/**
 * 
 * 资源管理表(服务类).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-05
 */
public interface ResResourceService extends IService<ResResource> {

	/**
	 * 
	 * 资源管理表(分页列表).
	 *
	 * @return：Page<ResResource>
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	public Page<ResResource> list(PageBean pageBean, ResResource obj) throws Exception;
	
	/**
	 * 
	 * 上传的文件新增进ResResource表
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月8日 下午1:06:43
	 */
	public RetMsg addResourceList(ResResourceVO resVO);
	
    
    /**
     * 
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017年9月8日 下午1:06:43
     */
    public RetMsg updateResource(ResResource obj);
    
    /**
     * 
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018年4月20日
     */
    public RetMsg upload(UploadParam uploadParam, String fileModuleName) throws Exception;
    
    /**
     * 
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018年4月20日
     */
    public RetMsg delete(ResResource obj) throws Exception;
        
    /**
     * 
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018年4月23日
     */
    public RetMsg deleteBatchById(List<Long> resourcesIds) throws Exception;
    
    /**
     * 文件正文保存文件
     * 
     * @return：void
     * 
     * @author：caizx
     * 
     * @date：2018年4月23日
     */
    public void saveFile(String attachId, byte[] fileBytes) throws Exception ;
    
    /**
     * 文件正文保存新增文件
     * 
     * @return：RetMsg
     * 
     * @author：caizx
     * 
     * @date：2018年4月23日
     */
    public RetMsg saveBlankFile(String fileName, byte[] fileBytes, int fileSize) throws Exception;

	
}
