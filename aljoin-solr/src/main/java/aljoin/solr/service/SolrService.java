package aljoin.solr.service;

import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

/**
 * 
 * @描述：solr查询服务
 *
 * @作者：wuhp
 * 
 * @时间：2018年3月5日 上午11:22:23
 */

public interface SolrService {

    /**
     * 
     * @描述：向solr插入数据
     *
     * @参数: @param coreName 核心名称
     * @参数: @param input 数据封装
     * @参数: @return
     *
     * @返回：boolean
     *
     * @作者：wuhp
     *
     * @时间：2018年4月19日 下午2:47:26
     */
    public boolean pushDataIntoSolr(String coreName, SolrInputDocument input);

    /**
     * 
     * @描述：按条件查询搜索引擎
     *
     * @参数: @param coreName
     * @参数: @param query
     * @参数: @return
     *
     * @返回：SolrDocumentList 返回查询集合
     *
     * @作者：wuhp
     *
     * @时间：2018年4月19日 下午2:46:35
     */
    public SolrDocumentList querySolrIndex(String coreName, String query);

}
