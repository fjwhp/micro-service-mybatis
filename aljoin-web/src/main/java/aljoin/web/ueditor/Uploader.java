package aljoin.web.ueditor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.baidu.ueditor.define.State;
import com.baidu.ueditor.upload.Base64Uploader;
import com.baidu.ueditor.upload.BinaryUploader;

/**
 * 
 * 自定义文件上传
 *
 * @author：zhongjy
 * 
 * @date：2017年9月20日 上午11:26:46
 */
public class Uploader {
  private HttpServletRequest request = null;
  private Map<String, Object> conf = null;

  public Uploader(HttpServletRequest request, Map<String, Object> conf) {
    this.request = request;
    this.conf = conf;
  }

  public final State doExec() {
    String filedName = (String) this.conf.get("fieldName");
    State state = null;
    if ("true".equals(this.conf.get("isBase64"))) {
      state = Base64Uploader.save(this.request.getParameter(filedName), this.conf);
    } else {
      if ("true".equals(this.conf.get("isCustomFileName"))) {
        state = CustomUploader.save(request, conf);
      } else {
        state = BinaryUploader.save(this.request, this.conf);
      }
    }
    return state;
  }
}
