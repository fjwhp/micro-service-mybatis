package aljoin.mai.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.mai.dao.entity.MaiAttachment;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 邮箱附件表(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-20
 */
public interface MaiAttachmentService extends IService<MaiAttachment> {

	/**
	 * 
	 * 邮箱附件表(分页列表).
	 *
	 * @return：Page<MaiAttachment>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	public Page<MaiAttachment> list(PageBean pageBean, MaiAttachment obj) throws Exception;
	}
