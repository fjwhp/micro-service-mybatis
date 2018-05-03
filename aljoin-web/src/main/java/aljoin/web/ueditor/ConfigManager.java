package aljoin.web.ueditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigManager {
  private final static Logger logger = LoggerFactory.getLogger(ConfigManager.class);
  private final String rootPath;
  private final String originalPath;
  @SuppressWarnings("unused")
  private final String contextPath;
  @SuppressWarnings("unused")
  private static final String CONFIG_FILE_NAME = "config.json";
  @SuppressWarnings("unused")
  private String parentPath = null;
  private JSONObject jsonConfig = null;
  @SuppressWarnings("unused")
  private static final String SCRAWL_FILE_NAME = "scrawl";
  @SuppressWarnings("unused")
  private static final String REMOTE_FILE_NAME = "remote";

  private ConfigManager(String rootPath, String contextPath, String uri)
      throws FileNotFoundException, IOException {
    rootPath = rootPath.replace("\\", "/");

    this.rootPath = rootPath;
    this.contextPath = contextPath;
    if (contextPath.length() > 0) {
      this.originalPath = (this.rootPath + uri.substring(contextPath.length()));
    } else {
      this.originalPath = (this.rootPath + uri);
    }
    initEnv();
  }

  public static ConfigManager getInstance(String rootPath, String contextPath, String uri) {
    System.out.println("getInstance============================"+uri);
    try {
      return new ConfigManager(rootPath, contextPath, uri);
    } catch (Exception e) {
      logger.error("",e);
    }
    return null;
  }

  public boolean valid() {
    return this.jsonConfig != null;
  }

  public JSONObject getAllConfig() {
    return this.jsonConfig;
  }

  public Map<String, Object> getConfig(int type) {
    Map<String, Object> conf = new HashMap<String, Object>();
    String savePath = null;
    switch (type) {
      case 4:
        try {
          conf.put("isBase64", "false");
          conf.put("maxSize", Long.valueOf(this.jsonConfig.getLong("fileMaxSize")));
          conf.put("allowFiles", getArray("fileAllowFiles"));
          conf.put("fieldName", this.jsonConfig.getString("fileFieldName"));
          savePath = this.jsonConfig.getString("filePathFormat");
        } catch (JSONException e) {
          logger.error("", e);
        }
        break;
      case 1:
        try {
          conf.put("isBase64", "false");
          conf.put("maxSize", Long.valueOf(this.jsonConfig.getLong("imageMaxSize")));
          conf.put("allowFiles", getArray("imageAllowFiles"));
          conf.put("fieldName", this.jsonConfig.getString("imageFieldName"));
          savePath = this.jsonConfig.getString("imagePathFormat");
        } catch (JSONException e) {
          logger.error("", e);
        }
        break;
      case 3:
        try {
          conf.put("maxSize", Long.valueOf(this.jsonConfig.getLong("videoMaxSize")));
          conf.put("allowFiles", getArray("videoAllowFiles"));
          conf.put("fieldName", this.jsonConfig.getString("videoFieldName"));
          savePath = this.jsonConfig.getString("videoPathFormat");
        } catch (JSONException e) {
          logger.error("", e);
        }
        break;
      case 2:
        try {
          conf.put("filename", "scrawl");
          conf.put("maxSize", Long.valueOf(this.jsonConfig.getLong("scrawlMaxSize")));
          conf.put("fieldName", this.jsonConfig.getString("scrawlFieldName"));
          conf.put("isBase64", "true");
          savePath = this.jsonConfig.getString("scrawlPathFormat");
        } catch (JSONException e) {
          logger.error("", e);
        }
        break;
      case 5:
        try {
          conf.put("filename", "remote");
          conf.put("filter", getArray("catcherLocalDomain"));
          conf.put("maxSize", Long.valueOf(this.jsonConfig.getLong("catcherMaxSize")));
          conf.put("allowFiles", getArray("catcherAllowFiles"));
          conf.put("fieldName", this.jsonConfig.getString("catcherFieldName") + "[]");
          savePath = this.jsonConfig.getString("catcherPathFormat");
        } catch (JSONException e) {
          logger.error("", e);
        }
        break;
      case 7:
        try {
          conf.put("allowFiles", getArray("imageManagerAllowFiles"));
          conf.put("dir", this.jsonConfig.getString("imageManagerListPath"));
          conf.put("count", Integer.valueOf(this.jsonConfig.getInt("imageManagerListSize")));
        } catch (JSONException e) {
          logger.error("", e);
        }
        break;
      case 6:
        try {
          conf.put("allowFiles", getArray("fileManagerAllowFiles"));
          conf.put("dir", this.jsonConfig.getString("fileManagerListPath"));
          conf.put("count", Integer.valueOf(this.jsonConfig.getInt("fileManagerListSize")));
        } catch (JSONException e) {
          logger.error("", e);
        }
      default:
    	  break; 
    }
    conf.put("savePath", savePath);
    conf.put("rootPath", this.rootPath);

    return conf;
  }
  private String readFile2() throws IOException {
    StringBuilder builder = new StringBuilder();
    try {
      InputStream is = this.getClass().getClassLoader().getResourceAsStream("static/aljoin-act/ueditor/jsp/config.json");
      InputStreamReader reader = new InputStreamReader(is, "UTF-8");
      BufferedReader bfReader = new BufferedReader(reader);

      String tmpContent = null;
      while ((tmpContent = bfReader.readLine()) != null) {
        builder.append(tmpContent);
      }
      bfReader.close();
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
    }
    return filter(builder.toString());
  }

  private void initEnv() throws FileNotFoundException, IOException {
    File file = new File(this.originalPath);
    if (!file.isAbsolute()) {
      file = new File(file.getAbsolutePath());
    }
    this.parentPath = file.getParent();

    //String configContent = readFile(getConfigPath());
    String configContent = readFile2();
    try {
      JSONObject jsonConfig = new JSONObject(configContent);
      this.jsonConfig = jsonConfig;
    } catch (Exception e) {
      this.jsonConfig = null;
    }
  }

  @SuppressWarnings("unused")
  private String getConfigPath() {
    //return this.parentPath + File.separator + "config.json";
    try {
      //aljoin-web/src/main/resources/static/aljoin-act/ueditor/jsp/config.json
      String configPath = this.getClass().getClassLoader().getResource("static/aljoin-act/ueditor/jsp/config.json").toURI().getPath();
      System.out.println("configPath********************"+configPath);
      return configPath;
    } catch (URISyntaxException e) {
      logger.error("", e);
      return null;
    }
  }

  private String[] getArray(String key) {
    JSONArray jsonArray = null;;
    try {
      jsonArray = this.jsonConfig.getJSONArray(key);
    } catch (JSONException e) {
      logger.error("", e);
    }
    String[] result = new String[jsonArray.length()];

    int i = 0;
    for (int len = jsonArray.length(); i < len; i++) {
      try {
        result[i] = jsonArray.getString(i);
      } catch (JSONException e) {
        logger.error("", e);
      }
    }
    return result;
  }
  
  
  @SuppressWarnings("unused")
  private String readFile(String path) throws IOException {
    StringBuilder builder = new StringBuilder();
    try {
      InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
      BufferedReader bfReader = new BufferedReader(reader);

      String tmpContent = null;
      while ((tmpContent = bfReader.readLine()) != null) {
        builder.append(tmpContent);
      }
      bfReader.close();
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
    }
    return filter(builder.toString());
  }

  private String filter(String input) {
    return input.replaceAll("/\\*[\\s\\S]*?\\*/", "");
  }
}
