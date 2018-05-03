package aljoin.file.factory;

import java.util.Properties;

import javax.annotation.Resource;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import aljoin.config.FastDFSConfig;
import aljoin.file.service.impl.AljoinFileServiceImpl;

/**
 * 文件系统工厂
 * 
 * @author zhongjy
 * @date 2018/04/03
 */
@Component
public class AljoinFileFactory {
    private final static Logger logger = LoggerFactory.getLogger(AljoinFileServiceImpl.class);
    @Resource
    private FastDFSConfig fastDFSConfig;

    /**
     * 初始化配置
     * 
     * @throws Exception
     */
    public void initConfig() throws Exception {

        Properties props = new Properties();
        props.put(ClientGlobal.PROP_KEY_CONNECT_TIMEOUT_IN_SECONDS, fastDFSConfig.getConnectTimeoutInSeconds());
        props.put(ClientGlobal.PROP_KEY_NETWORK_TIMEOUT_IN_SECONDS, fastDFSConfig.getNetworkTimeoutInSeconds());
        props.put(ClientGlobal.PROP_KEY_CHARSET, fastDFSConfig.getCharset());
        props.put(ClientGlobal.PROP_KEY_HTTP_ANTI_STEAL_TOKEN, fastDFSConfig.getHttpAntiStealToken());
        props.put(ClientGlobal.PROP_KEY_HTTP_SECRET_KEY, fastDFSConfig.getHttpSecretKey());
        props.put(ClientGlobal.PROP_KEY_HTTP_TRACKER_HTTP_PORT, fastDFSConfig.getHttpTrackerHttpPort());
        props.put(ClientGlobal.PROP_KEY_TRACKER_SERVERS, fastDFSConfig.getTrackerServers());
        ClientGlobal.initByProperties(props);
        logger.info("文件系统初始化完成...");

    }

    /**
     * 获取存储服务
     * 
     * @return
     * @throws Exception
     */
    public StorageClient getStorageClient() throws Exception {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient client = new StorageClient(trackerServer, storageServer);
        return client;
    }

    /**
     * 获取存储服务
     * 
     * @return
     * @throws Exception
     */
    public StorageClient1 getStorageClient1() throws Exception {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        return client;
    }
}
