package aljoin.off.service;

import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffDailylog;
import aljoin.off.dao.entity.OffMonthReport;
import aljoin.off.dao.entity.OffMonthReportDetail;
import aljoin.off.dao.mapper.OffMonthReportDetailMapper;
import aljoin.off.iservice.OffDailylogService;
import aljoin.off.iservice.OffMonthReportDetailService;
import aljoin.off.iservice.OffMonthReportService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 工作月报详情表(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-11
 */
@Service
public class OffMonthReportDetailServiceImpl extends ServiceImpl<OffMonthReportDetailMapper, OffMonthReportDetail> implements OffMonthReportDetailService {

  @Resource
  private OffMonthReportDetailMapper mapper;

  @Resource
  private OffDailylogService offDailylogService;

  @Resource
  private OffMonthReportService offMonthReportService;

  @Override
  public Page<OffMonthReportDetail> list(PageBean pageBean, OffMonthReportDetail obj) throws Exception {
	Where<OffMonthReportDetail> where = new Where<OffMonthReportDetail>();
	where.orderBy("create_time", false);
	Page<OffMonthReportDetail> page = selectPage(new Page<OffMonthReportDetail>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  @Override
  public void copyObject(OffMonthReportDetail obj) throws Exception{
  	mapper.copyObject(obj);
  }

    @Override
    @Transactional
    public RetMsg update(OffMonthReportDetail obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if(null != obj && null != obj.getId()){
            OffMonthReportDetail monthReportDetail = selectById(obj);
            if(null != monthReportDetail){
                if(StringUtils.isNotEmpty(obj.getContent())){
                    monthReportDetail.setContent(obj.getContent());
                }
                updateById(monthReportDetail);
                if(null != monthReportDetail.getDailylogId()){
                    Where<OffDailylog> where = new Where<OffDailylog>();
                    where.eq("id",monthReportDetail.getDailylogId());
                    OffDailylog offDailylog = offDailylogService.selectOne(where);
                    if(null != offDailylog){
                        if(StringUtils.isNotEmpty(obj.getContent())){
                            offDailylog.setContent(obj.getContent());
                        }
                        offDailylogService.updateById(offDailylog);
                    }
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg delete(OffMonthReportDetail obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if(null != obj && null != obj.getId()){
            OffMonthReportDetail monthReportDetail = selectById(obj);
            if(null != monthReportDetail){
                deleteById(obj.getId());
                if(null != monthReportDetail.getDailylogId()){
                    Where<OffDailylog> where = new Where<OffDailylog>();
                    where.eq("id",monthReportDetail.getDailylogId());
                    OffDailylog offDailylog = offDailylogService.selectOne(where);
                    if(null != offDailylog){
                        offDailylogService.deleteById(offDailylog.getId());
                    }
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public RetMsg saveComment(OffMonthReport obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if(null != obj && null != obj.getId()){
            OffMonthReportDetail monthReportDetail = selectById(obj);
            if(null != monthReportDetail){
                if(null != monthReportDetail.getMonthReportId()){
                    Where<OffMonthReport> where = new Where<OffMonthReport>();
                    where.eq("id",monthReportDetail.getMonthReportId());
                    OffMonthReport offMonthReport = offMonthReportService.selectOne(where);
                    if(null != offMonthReport){
                        if(StringUtils.isNotEmpty(obj.getComment())){
                            if(StringUtils.isNotEmpty(offMonthReport.getComment())){
                                offMonthReport.setComment(obj.getComment());
                            }else{
                                offMonthReport.setComment("");
                            }
                            offMonthReportService.updateById(offMonthReport);
                        }
                    }
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }
}
