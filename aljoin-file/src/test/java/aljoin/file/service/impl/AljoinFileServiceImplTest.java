package aljoin.file.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import aljoin.file.factory.AljoinFileFactory;
import aljoin.file.object.AljoinFile;
import aljoin.file.object.UploadParam;
import aljoin.file.service.AljoinFileService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AljoinFileServiceImplTest {

    @Resource
    private AljoinFileService aljoinFileService;
    @Resource
    private AljoinFileFactory aljoinFileFactory;

    @Test
    public void test() throws Exception {
        try {
            aljoinFileFactory.initConfig();
            StorageClient client = aljoinFileFactory.getStorageClient();
            String[] result = client.upload_file("C:\\Users\\蔡志欣\\脚本.txt", "txt", null);
            System.out.println("=============");
            System.out.println(result[0]);
            System.out.println(result[1]);
            System.out.println("=============");
            StorageClient1 client1 = aljoinFileFactory.getStorageClient1();
            String[] result2 = client1.upload_file("C:\\\\Users\\\\蔡志欣\\\\脚本.txt", "txt", null);
            System.out.println("=============");
            System.out.println(result2[0]);
            System.out.println(result2[1]);
            System.out.println("=============");
            String[] result3 = client1.upload_appender_file("C:\\\\Users\\\\蔡志欣\\\\脚本.txt", "txt", null);
            System.out.println("=============");
            System.out.println(result3[0]);
            System.out.println(result3[1]);
            System.out.println("=============");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        try {
            aljoinFileFactory.initConfig();
            StorageClient client = aljoinFileFactory.getStorageClient();
            Path path = Paths.get("C:\\Users\\zhongjy\\Desktop\\林芬相关账号.txt");
            byte[] data = Files.readAllBytes(path);
            String[] result = client.upload_file(data, "txt", null);
            System.out.println("=============");
            System.out.println(result[0]);
            System.out.println(result[1]);
            System.out.println("=============");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test3() {
        try {
            aljoinFileFactory.initConfig();
            StorageClient client = aljoinFileFactory.getStorageClient();
            File file = new File("C:\\Users\\zhongjy\\Desktop\\ABCD.png");
            byte[] data = Files.readAllBytes(file.toPath());
            NameValuePair[] metaList = new NameValuePair[4];
            metaList[0] = new NameValuePair("width", "800");
            metaList[1] = new NameValuePair("heigth", "600");
            metaList[2] = new NameValuePair("bgcolor", "#FFFFFF");
            metaList[3] = new NameValuePair("author", "Mike");
            String[] result = client.upload_file(data, "png", metaList);
            /* group1
            M00/00/00/wKgA31rDPcqAHB6qAAABJ2btJw8735.txt*/
            System.out.println("=============");
            System.out.println(result[0]);
            System.out.println(result[1]);
            System.out.println("=============");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test4() {
        try {
            aljoinFileFactory.initConfig();
            StorageClient client = aljoinFileFactory.getStorageClient();
            FileInfo fileInfo = client.get_file_info("group1", "M00/00/00/wKgA31rDPcqAHB6qAAABJ2btJw8735.txt");
            NameValuePair[] pair = client.get_metadata("group1", "M00/00/00/wKgA31rDPcqAHB6qAAABJ2btJw8735.txt");
            /* 
            */
            System.out.println("=============");
            System.out.println(fileInfo);
            System.out.println("=============");
            System.out.println(pair);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test5() {
        try {
            aljoinFileFactory.initConfig();
            StorageClient client = aljoinFileFactory.getStorageClient();
            byte[] bytes = client.download_file("group1", "M00/00/00/wKgA31rDPcqAHB6qAAABJ2btJw8735.txt");
            /* 
            */
            System.out.println("=============");
            System.out.println(bytes);
            System.out.println("=============");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test6() {
        try {
            aljoinFileFactory.initConfig();
            // StorageClient client = AljoinFileFactory.storageClient;
            // http://192.168.0.223:8888/group1/M00/00/00/wKgA31rDR-GASikyAAABJ2btJw8862.txt
            // String fileName = "M00/00/00/wKgA31rDR-GASikyAAABJ2btJw8862.txt";
            String fileName = "M00/00/00/wKgA31rEKkCAd7FAAADal41D_Nc174.png";
            int ts = (int)(System.currentTimeMillis() / 1000);
            String token = ProtoCommon.getToken(fileName, ts, ClientGlobal.getG_secret_key());
            /* 
            */
            System.out.println("=============");
            System.out.println("ts:" + ts);
            System.out.println("token:" + token);
            System.out.println("http://192.168.0.223:8888/group1/" + fileName + "?token=" + token + "&ts=" + ts);
            System.out.println("=============");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test7() {
        try {
            aljoinFileFactory.initConfig();
            StorageClient client = aljoinFileFactory.getStorageClient();
            byte[] bytes = client.download_file("group1", "M00/00/00/wKgA31rDPcqAHB6qAAABJ2btJw8735.txt");
            /* 
            */
            System.out.println("=============");
            System.out.println(bytes);
            System.out.println("=============");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test8() {
        try {
            aljoinFileFactory.initConfig();
            UploadParam uploadParam = new UploadParam();
            List<AljoinFile> fileList = new ArrayList<AljoinFile>();
            // fileList.add(new File("C:\\Users\\zhongjy\\Desktop\\A9原型.rar"));
            File f1 = new File("C:\\Users\\zhongjy\\Desktop\\表单图片文件.png");
            File f2 = new File("C:\\Users\\zhongjy\\Desktop\\文件上传测试.txt");
            fileList.add(new AljoinFile(Files.readAllBytes(f1.toPath()), f1.getName(), f1.length()));
            fileList.add(new AljoinFile(Files.readAllBytes(f2.toPath()), f2.getName(), f2.length()));
            uploadParam.setAllowTypeList(Arrays.asList("txt", "jpg", "png", "rar"));
            uploadParam.setFileList(fileList);
            uploadParam.setMaxSize(163);
            //aljoinFileService.upload(uploadParam);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
