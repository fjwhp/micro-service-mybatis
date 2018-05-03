package aljoin.web.ueditor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.hunter.FileManager;
import com.baidu.ueditor.hunter.ImageHunter;

public class ActionEnter {
  private final static Logger logger = LoggerFactory.getLogger(ActionEnter.class);
    private HttpServletRequest request = null;
    private String rootPath = null;
    private String contextPath = null;
    private String actionType = null;
    private ConfigManager configManager = null;

    public ActionEnter(HttpServletRequest request, String rootPath) {
        this.request = request;
        this.rootPath = rootPath;
        this.actionType = request.getParameter("action");
        this.contextPath = request.getContextPath();
        System.out.println("this.rootPath++++++++++++++++++++++++++++"+this.rootPath);
        System.out.println("this.contextPath++++++++++++++++++++++++++++"+this.contextPath);
        this.configManager = ConfigManager.getInstance(this.rootPath, this.contextPath, request.getRequestURI());
        System.out.println("this.configManager++++++++++++++++++++++++++++"+this.configManager);
        // 替换生成img的src属性的路径(原来本地上传)
        //this.configManager.getAllConfig().put("imageUrlPrefix", "http://" + Util.getPropertyByKey("FILE_NGINX_REQ_HOST") + ":" + Util.getPropertyByKey("FILE_NGINX_PORT") + "/");
        // 替换生成img的src属性的路径(http上传)
        try {
          this.configManager.getAllConfig().put("imageUrlPrefix", "");
        } catch (JSONException e) {
          logger.error("", e);
        }
    }

    public String exec() {
        String callbackName = this.request.getParameter("callback");
        if (callbackName != null) {
            if (!validCallbackName(callbackName)) {
                return new BaseState(false, 401).toJSONString();
            }
            return callbackName + "(" + invoke() + ");";
        }
        return invoke();
    }

    public String invoke() {
        if ((this.actionType == null) || (!ActionMap.mapping.containsKey(this.actionType))) {
            return new BaseState(false, 101).toJSONString();
        }
        if ((this.configManager == null) || (!this.configManager.valid())) {
            return new BaseState(false, 102).toJSONString();
        }
        State state = null;

        int actionCode = ActionMap.getType(this.actionType);

        Map<String, Object> conf = null;
        switch (actionCode) {
            case 0:
                return this.configManager.getAllConfig().toString();
            case 1:
            case 2:
            case 3:
            case 4:
                conf = this.configManager.getConfig(actionCode);
            try {
                // isCustomFileName-是否自定义上传的完整文件名
                conf.put("isCustomFileName", this.configManager.getAllConfig().getString("isCustomFileName"));
                // keepLocalFile是否保留服务器本地文件
                conf.put("keepLocalFile", this.configManager.getAllConfig().getString("keepLocalFile"));
            } catch (JSONException e) {
              logger.error("", e);
            }
                state = new Uploader(this.request, conf).doExec();
                break;
            case 5:
                conf = this.configManager.getConfig(actionCode);
                String[] list = this.request.getParameterValues((String) conf.get("fieldName"));
                state = new ImageHunter(conf).capture(list);
                break;
            case 6:
            case 7:
                conf = this.configManager.getConfig(actionCode);
                int start = getStartIndex();
                state = new FileManager(conf).listFile(start);
        }
        return state.toJSONString();
    }

    public int getStartIndex() {
        String start = this.request.getParameter("start");
        try {
            return Integer.parseInt(start);
        } catch (Exception e) {}
        return 0;
    }

    public boolean validCallbackName(String name) {
        if (name.matches("^[a-zA-Z_]+[\\w0-9_]*$")) {
            return true;
        }
        return false;
    }
}
