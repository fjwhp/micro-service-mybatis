package aljoin.solr.po.request;

import java.io.Serializable;

/**
 * 
 * @描述：solr索引请求类封装
 *
 * @作者：wuhp
 * 
 * @时间：2018年4月19日 下午6:20:06
 */
public class QuerySolrIndexRequest implements Serializable {

 private static final long serialVersionUID = 1L;

 /**
  * 核心名称
  */
 private String coreName ;
 
 /**
  * 查询条件
  */
 private String query;

 public String getCoreName() {
     return coreName;
 }

 public void setCoreName(String coreName) {
     this.coreName = coreName;
 }

 public String getQuery() {
     return query;
 }

 public void setQuery(String query) {
     this.query = query;
 }
}
