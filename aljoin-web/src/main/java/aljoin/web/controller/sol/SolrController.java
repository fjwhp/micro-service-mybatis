package aljoin.web.controller.sol;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.solr.po.request.BaseResponse;
import aljoin.solr.po.request.PushDataIntoSolrRequest;
import aljoin.solr.po.request.QuerySolrIndexRequest;
import aljoin.solr.service.SolrService;

/**
 * 
 * @描述：solr客户端
 *
 * @作者：wuhp
 * 
 * @时间：2018年3月5日 下午1:04:42
 */
@Controller
@RequestMapping("/solr")
public class SolrController {

  @Resource
  private SolrService solrService;

  /**
   * 
   * @描述：添加入solr索引
   *
   * @返回：BaseResponse
   *
   * @作者：wuhp
   *
   * @时间：2018年3月5日 下午1:03:28
   */
  @RequestMapping(value = "/pushDataIntoSolr", method = RequestMethod.POST)
  public BaseResponse pushDataIntoSolr(@RequestBody PushDataIntoSolrRequest request) {
    BaseResponse response = new BaseResponse();
    SolrInputDocument input = new SolrInputDocument();
    for (Map.Entry<String, Object> entry : request.getInput().entrySet()) {
      input.addField(entry.getKey(), entry.getValue());
    }
    if (!solrService.pushDataIntoSolr(request.getCoreName(), input)) {
      response.setErrorCode("500");
      response.setErrorMsg("插入失败，请稍候重试。");
    } else {
      response.setSubMsg("插入成功");
    }
    return response;
  }
  /**
   * 
   * @描述：按条件查询搜索引擎 
   *
   * @返回：SolrDocumentList
   *
   * @作者：wuhp
   *
   * @时间：2018年3月5日 下午1:11:28
   */
  @RequestMapping(value = "/querySolrIndex", method = RequestMethod.GET)
  public SolrDocumentList querySolrIndex(@RequestBody QuerySolrIndexRequest request) {
    return solrService.querySolrIndex(request.getCoreName(), request.getQuery());
  }
  @RequestMapping("/quetyTitle")
  @ResponseBody
  public SolrDocumentList quetyTitle(HttpServletRequest request){
      
      String title= request.getParameter("solr_title");
      
      QuerySolrIndexRequest qrequest = new QuerySolrIndexRequest();  
      qrequest.setCoreName("new_core");  
      qrequest.setQuery("solr_title:"+title);  
      
      return solrService.querySolrIndex(qrequest.getCoreName(), qrequest.getQuery());
  }
  @RequestMapping("/index")
  public String solrIndex(){
      return "/solr/solr_index";
  }
}

