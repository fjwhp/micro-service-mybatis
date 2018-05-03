package aljoin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * 线程池配置
 *
 * @author：zhongjy
 *
 * @date：2017年6月5日 下午6:38:51
 */
@ConfigurationProperties(prefix = "aljoin.fastdfs")
public class FastDFSConfig {
    /**
     * 连接超时时间
     */
    private String connectTimeoutInSeconds;
    /**
     * 网络超时时间
     */
    private String networkTimeoutInSeconds;
    /**
     * 字符集
     */
    private String charset;
    /**
     * 防盗链Token
     */
    private String httpAntiStealToken;
    /**
     * 密钥
     */
    private String httpSecretKey;
    /**
     * 提供HTTP服务的端口
     */
    private String httpTrackerHttpPort;
    /**
     * tracker服务器地址，逗号分隔
     */
    private String trackerServers;

    public String getConnectTimeoutInSeconds() {
        return connectTimeoutInSeconds;
    }

    public void setConnectTimeoutInSeconds(String connectTimeoutInSeconds) {
        this.connectTimeoutInSeconds = connectTimeoutInSeconds;
    }

    public String getNetworkTimeoutInSeconds() {
        return networkTimeoutInSeconds;
    }

    public void setNetworkTimeoutInSeconds(String networkTimeoutInSeconds) {
        this.networkTimeoutInSeconds = networkTimeoutInSeconds;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getHttpAntiStealToken() {
        return httpAntiStealToken;
    }

    public void setHttpAntiStealToken(String httpAntiStealToken) {
        this.httpAntiStealToken = httpAntiStealToken;
    }

    public String getHttpSecretKey() {
        return httpSecretKey;
    }

    public void setHttpSecretKey(String httpSecretKey) {
        this.httpSecretKey = httpSecretKey;
    }

    public String getHttpTrackerHttpPort() {
        return httpTrackerHttpPort;
    }

    public void setHttpTrackerHttpPort(String httpTrackerHttpPort) {
        this.httpTrackerHttpPort = httpTrackerHttpPort;
    }

    public String getTrackerServers() {
        return trackerServers;
    }

    public void setTrackerServers(String trackerServers) {
        this.trackerServers = trackerServers;
    }

}
