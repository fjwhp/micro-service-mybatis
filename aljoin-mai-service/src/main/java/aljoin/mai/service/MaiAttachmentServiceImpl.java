package aljoin.mai.service;

import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.mapper.MaiAttachmentMapper;
import aljoin.mai.iservice.MaiAttachmentService;
import aljoin.object.PageBean;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 
 * 邮箱附件表(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-20
 */
@Service
public class MaiAttachmentServiceImpl extends ServiceImpl<MaiAttachmentMapper, MaiAttachment> implements MaiAttachmentService {

	@Override
	public Page<MaiAttachment> list(PageBean pageBean, MaiAttachment obj) throws Exception {
		Where<MaiAttachment> where = new Where<MaiAttachment>();
		where.orderBy("create_time", false);
		Page<MaiAttachment> page = selectPage(new Page<MaiAttachment>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		return page;
	}	
}
