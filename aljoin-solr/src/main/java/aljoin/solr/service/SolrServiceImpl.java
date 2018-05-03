package aljoin.solr.service;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * @描述：TODO
 *
 * @作者：wuhp
 * 
 * @时间：2018年4月19日 下午2:48:32
 */
@Service
public class SolrServiceImpl implements SolrService {

  private Logger logger = LoggerFactory.getLogger(SolrServiceImpl.class);    
  
  @Value("${aljoin.solr.httpSolrClient.url}")
  private String httpSolrClient;    
      
  private static SolrClient solr;    
      
  public String getHttpSolrClient() {    
      return httpSolrClient;    
  }    
  
  public void setHttpSolrClient(String httpSolrClient) {    
      this.httpSolrClient = httpSolrClient;    
  }    
      
  private HttpSolrClient connetHttpSolrClientServer(String coreName){    
      HttpSolrClient server = new HttpSolrClient(httpSolrClient + coreName);    
      server.setSoTimeout(5000);     
      server.setConnectionTimeout(1000);     
      server.setDefaultMaxConnectionsPerHost(1000);     
      server.setMaxTotalConnections(5000);    
      return server;    
  }    
      
  /**
   * 
   */
  @Override    
  public boolean pushDataIntoSolr(String coreName, SolrInputDocument input) {    
      boolean flag = false;    
      try {    
          solr = connetHttpSolrClientServer(coreName);    
          solr.add(input);    
          solr.commit();    
          flag = true;    
      } catch (Exception e) {    
          e.printStackTrace();    
          logger.error(e.getMessage());    
      } finally {    
          try {    
              solr.close();    
          } catch (IOException e) {    
              e.printStackTrace();    
              logger.error(e.getMessage());    
          }    
      }    
      return flag;    
  }    
  
      
  /**  
   *@ClassDescribe:按条件查询搜索引擎  
   *@author:wuhp
   *  2018-04-19 18:32
   *@param query solr查询条件  
   *@return 返回查询集合  
   */    
  
  @Override    
  public SolrDocumentList querySolrIndex(String coreName, String query) {    
      SolrDocumentList list = null;    
      try {    
          solr = connetHttpSolrClientServer(coreName);    
          QueryResponse rsp = null;    
          SolrQuery queryStr = new SolrQuery("*:*");    
          queryStr.addFilterQuery(query);    
          rsp = solr.query(queryStr);    
          list = rsp.getResults();    
      } catch (IOException | SolrServerException e) {    
          e.printStackTrace();    
          logger.error(e.getMessage());    
      } catch (Exception e) {    
          e.printStackTrace();    
          logger.error(e.getMessage());    
      } finally {    
          try {    
              solr.close();    
          } catch (IOException e) {    
              e.printStackTrace();    
              logger.error(e.getMessage());    
          }    
      }    
      return list;    
  }  

}
