package aljoin.web.controller.sol;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import aljoin.solr.po.request.PushDataIntoSolrRequest;
import aljoin.solr.po.request.QuerySolrIndexRequest; 
//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
//带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SolrTests {

  private MockMvc mockMvc;   
  @Autowired  
      protected WebApplicationContext wac;  
  @Before()    
      public void setup() {  
          mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();  //初始化MockMvc对象  
      }  
  @Test  
  public void pushDataIntoSolr() { //推送solr测试方法  
      Map<String, Object> map = new HashMap<>();    
      map.put("id", 3);  
          map.put("solr_title", "发大水法的");  
          map.put("solr_content", "f发生大幅是2344");  
          PushDataIntoSolrRequest request = new PushDataIntoSolrRequest();  
          ObjectMapper mapper = new ObjectMapper();  
          ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();  
          request.setInput(map);  
          request.setCoreName("new_core");  
          MvcResult result;  
      try {  
          java.lang.String requestJson = ow.writeValueAsString(request);  
          result = mockMvc.perform(post("/solr/pushDataIntoSolr").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson))  
                  .andExpect(status().isOk())     
                  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))  
                  .andReturn();  
          System.out.println("=======================");
          System.out.println(result.getResponse().getContentAsString());     
      } catch (Exception e) {  
          e.printStackTrace();  
      }  
  }  
  //查询solr方法  
  @Test  
  public void querySolrIndex(){  
      MvcResult result;  
      ObjectMapper mapper = new ObjectMapper();  
          ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();  
          QuerySolrIndexRequest request = new QuerySolrIndexRequest();  
      try {  
          request.setCoreName("new_core");  
          request.setQuery("id:2");  
          java.lang.String requestJson = ow.writeValueAsString(request);  
          result = mockMvc.perform(get("/solr/querySolrIndex").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson))  
                  .andExpect(status().isOk())  
                  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8      
                  .andReturn(); 
          System.out.println("=======================");
          System.out.println(result.getResponse().getContentAsString());     
      } catch (Exception e) {  
          e.printStackTrace();  
      }  
  }  
}
