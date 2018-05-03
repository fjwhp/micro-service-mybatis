package aljoin.res.service;

import aljoin.res.dao.entity.ResResource;
import aljoin.res.dao.entity.ResResourceType;
import aljoin.res.dao.mapper.ResResourceMapper;
import aljoin.res.dao.object.ResResourceVO;
import aljoin.res.iservice.ResResourceService;
import aljoin.res.iservice.ResResourceTypeService;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.dao.config.Where;
import aljoin.file.object.UploadParam;
import aljoin.file.object.UploadResult;
import aljoin.file.service.AljoinFileService;
import aljoin.object.FileSystemConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 资源管理表(服务实现类).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-05
 */
@Service
public class ResResourceServiceImpl extends ServiceImpl<ResResourceMapper, ResResource> implements ResResourceService {
	
	@Resource
	private ResResourceService resResourceService;
	@Resource
	private ResResourceTypeService resResourceTypeService;
	@Resource
	private AljoinFileService aljoinFileService;
	
	@Override
	public Page<ResResource> list(PageBean pageBean, ResResource obj) throws Exception {
		
		Where<ResResource> where = new Where<ResResource>();
		if(StringUtils.isNotEmpty(obj.getOrgnlFileName())){
			where.like("resource_name", obj.getOrgnlFileName()); 
			where.or("resource_desc LIKE {0}","%" + obj.getOrgnlFileName() +"%");
		}
		where.orderBy("create_time", false);
		Page<ResResource> page = selectPage(new Page<ResResource>(pageBean.getPageNum(), pageBean.getPageSize()),
				where);
		return page;
	}


    @Override
    @Transactional
    public RetMsg addResourceList(ResResourceVO resVO) {
        RetMsg retMsg = new RetMsg();
     // 拼裝前端传来的ResourceType 和List<ResResource>
        List<ResResource> objList = resVO.getResResourceList();
        ResResource resResource = resVO.getResResource();
        List<ResResource> resoruceList = new ArrayList<ResResource>();
        List<ResResource> orginalResources = new ArrayList<ResResource>();
        for (int i = 0; i < objList.size(); i++) {
            Where<ResResource> where = new Where<ResResource>();
            where.eq("group_name", objList.get(i).getGroupName());
            where.eq("file_name", objList.get(i).getFileName());
            ResResource orginalResource = resResource.selectOne(where);
            if (null != orginalResource) {
                orginalResource.setFileDesc(resResource.getFileDesc());
                orginalResource.setFileTypeId(resResource.getFileTypeId());
                orginalResources.add(orginalResource);
            } else {
                ResResource res = new ResResource();
                res.setFileTypeId(resResource.getFileTypeId());
                res.setGroupName(objList.get(i).getGroupName());
                res.setFileName(objList.get(i).getFileName());
                res.setOrgnlFileName(objList.get(i).getOrgnlFileName());
                res.setFileType(objList.get(i).getFileType());
                res.setUploadTime(new Date());
                res.setFileSize(objList.get(i).getFileSize());
                // 前端有写备注则存，没有则备注无
                if (resResource.getFileDesc() != null && !"".equals(resResource.getFileDesc())) {
                    res.setFileDesc(resResource.getFileDesc());
                } else {
                    res.setFileDesc("资源维护模块上传文件");
                }
                // 是否激活默认设为1
                res.setIsActive(1);
                resoruceList.add(res);
            }
        }
        if (null != orginalResources && orginalResources.size() > 0) {
            updateBatchById(orginalResources);
        }
        if (null != resoruceList && resoruceList.size() > 0) {
            insertBatch(resoruceList);
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }




    @Override
    @Transactional
    public RetMsg updateResource(ResResource obj) {
        RetMsg retMsg = new RetMsg();
        ResResource orgnlObj = resResourceService.selectById(obj.getId());
        orgnlObj.setFileDesc(orgnlObj.getFileDesc());
        // 资源描述如果为空则存“无”，否则存备注
        if (null != obj.getFileDesc() && !"".equals(obj.getFileDesc())) {
            orgnlObj.setFileDesc(obj.getFileDesc());
        } else {
            orgnlObj.setFileDesc("无");
        }
        // 如果修改分类则更新分类id
        if (obj.getFileTypeId() != null && !"".equals(obj.getFileTypeId())) {
            orgnlObj.setFileTypeId(obj.getFileTypeId());
        }
        resResourceService.updateById(orgnlObj);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
         
    }


    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public RetMsg upload(UploadParam uploadParam, String fileModuleName) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (StringUtils.isEmpty(fileModuleName)) {
            retMsg.setCode(1);
            retMsg.setMessage("未指定上传模块");
            return retMsg;
        }
        String resourceTypeId = FileSystemConstant.RESOURCE_TYPE_ID.get(fileModuleName);
        ResResourceType resourceType = resResourceTypeService.selectById(Long.parseLong(resourceTypeId));
        if (null == resourceType) {
            retMsg.setCode(1);
            retMsg.setMessage("资源分类不存在");
            return retMsg;
        }
        retMsg = aljoinFileService.upload(uploadParam);
        List<UploadResult> resultList = (ArrayList<UploadResult>)retMsg.getObject();
        List<ResResource> resResources = new ArrayList<ResResource>();
        if (null != resultList && resultList.size() > 0) {
            for (UploadResult uploadResult : resultList) {
                ResResource resResource = new ResResource();
                //将数据写入资源表
                resResource.setFileDesc("无");
                resResource.setFileName(uploadResult.getFileName());
                resResource.setFileSize(Integer.parseInt(String.valueOf(uploadResult.getFileSize())));
                resResource.setFileType(uploadResult.getFileType());
                resResource.setFileTypeId(resourceType.getId());
                resResource.setGroupName(uploadResult.getGroupName());
                resResource.setIsActive(1);
                resResource.setOrgnlFileName(uploadResult.getOrgnlFileName());
                resResource.setUploadTime(new Date());
                resResource.setBizId(0L);
                resResource.setType(fileModuleName);
                resResources.add(resResource);
            }
        }
        resResourceService.insertBatch(resResources);
        retMsg.setCode(0);
        retMsg.setObject(resResources);
        retMsg.setMessage("操作成功");
        return retMsg;
    }


    @Override
    public RetMsg delete(ResResource obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        /*ResResource orgnal = resResourceService.selectById(obj.getId());
        if (orgnal != null) {
            aljoinFileService.delete(orgnal.getGroupName(), orgnal.getFileName());
        }*/
        resResourceService.deleteById(obj.getId());
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }


    @Override
    @Transactional
    public RetMsg deleteBatchById(List<Long> resourcesIds) throws Exception {
        RetMsg retMsg = new RetMsg();
        /*List<ResResource> originals = resResourceService.selectBatchIds(resourcesIds);
        if (null != originals && originals.size() > 0) {
            for (ResResource resResource : originals) {
                aljoinFileService.delete(resResource.getGroupName(), resResource.getFileName());
            }
        }*/
        resResourceService.deleteBatchIds(resourcesIds);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }


    @Override
    public void saveFile(String attachId, byte[] fileBytes) throws Exception {
        ResResource resResource = resResourceService.selectById(attachId);
        if (resResource == null) {
            throw new Exception(attachId + "附件不存在");
        }
        RetMsg result = aljoinFileService.saveOffice(resResource.getOrgnlFileName(), fileBytes);
        UploadResult uploadResult = (UploadResult)result.getObject();
        resResource.setOrgnlFileName(uploadResult.getFileName());
        resResource.setGroupName(uploadResult.getGroupName());
        resResource.setFileName(uploadResult.getFileName());
        resResourceService.updateById(resResource);
    }


    @Override
    public RetMsg saveBlankFile(String fileName, byte[] fileBytes, int fileSize) throws Exception {
        RetMsg retMsg = new RetMsg();
        String resourceTypeId = FileSystemConstant.RESOURCE_TYPE_ID.get("PAG_OFFICE");
        ResResource resResource = new ResResource();
        ResResourceType resourceType = resResourceTypeService.selectById(Long.parseLong(resourceTypeId));
        if (null == resourceType) {
            retMsg.setCode(1);
            retMsg.setMessage("资源分类不存在");
            return retMsg;
        }
        RetMsg result = aljoinFileService.saveOffice(fileName, fileBytes);
        UploadResult uploadResult = (UploadResult)result.getObject();
        resResource.setGroupName(uploadResult.getGroupName());
        resResource.setFileName(uploadResult.getFileName());
        resResource.setFileDesc("文件正文新增文件");
        resResource.setFileType("doc");
        resResource.setFileTypeId(Long.valueOf(resourceTypeId));
        resResource.setOrgnlFileName(fileName);
        resResource.setType("PAG_OFFICE");
        resResource.setBizId(0L);
        resResource.setIsActive(1);
        resResource.setUploadTime(new Date());
        resResource.setFileSize(fileSize);
        insert(resResource);
        retMsg.setObject(resResource);
        return retMsg;
    }


}
