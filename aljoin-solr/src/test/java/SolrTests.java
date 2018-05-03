import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import aljoin.solr.po.request.PushDataIntoSolrRequest;
import aljoin.solr.po.request.QuerySolrIndexRequest; 
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
      map.put("id", 2);  
          map.put("title", 2);  
          map.put("content", 2);  
          PushDataIntoSolrRequest request = new PushDataIntoSolrRequest();  
          ObjectMapper mapper = new ObjectMapper();  
          ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();  
          request.setInput(map);  
          request.setCoreName("test_core");  
          MvcResult result;  
      try {  
          java.lang.String requestJson = ow.writeValueAsString(request);  
          result = mockMvc.perform(post("/solr/pushDataIntoSolr").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson))  
                  .andExpect(status().isOk())     
                  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))  
                  .andReturn();  
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
          request.setCoreName("test_core");  
          request.setQuery("analyzeTime:2");  
          java.lang.String requestJson = ow.writeValueAsString(request);  
          result = mockMvc.perform(get("/solr/querySolrIndex").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson))  
                  .andExpect(status().isOk())  
                  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8      
                  .andReturn();  
          System.out.println(result.getResponse().getContentAsString());     
      } catch (Exception e) {  
          e.printStackTrace();  
      }  
  }  
}
