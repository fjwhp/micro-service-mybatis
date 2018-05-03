package aljoin.util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 文件工具类
 *
 * @author：zhongjy
 * 
 * @date：2017年7月6日 上午10:28:29
 */
public class FileUtil {

    private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 
     * 把字符串保存到文件（第二个参数是制定文件名）.
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年7月6日 上午10:36:23
     */
    public static void str2file(String data, String fileName) {
        FileWriter fileWritter = null;
        BufferedWriter bufferWritter = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            // true-末尾追加，false-覆盖原有
            fileWritter = new FileWriter(file, false);
            bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(data);
            bufferWritter.flush();
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            // 关闭bufferWritter
            if (bufferWritter != null) {
                try {
                    bufferWritter.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * 
     * 读取文件中的字符串并返回字符串
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年7月6日 上午10:34:54
     */
    public static String file2str(String filePath) {

        return null;
    }

    /**
     *
     * 根据输入流上传文件
     *
     * @return：File
     *
     * @author：wangj
     *
     * @date：2017年8月17日
     */
    public static File saveFileFromInputStream(InputStream stream, String path, String filename) {
        // 检查保存上传文件的文件夹是否存在 不存在则创建
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = null;
        FileOutputStream fs = null;
        try {
            file = new File(path + File.separator + filename);
            fs = new FileOutputStream(file);
            // 读取输入流中的文件信息到输出流中
            byte[] buffer = new byte[stream.available()];
            // int bytesum = 0;
            int byteread = stream.read(buffer);
            if (byteread != 0) {
                while (byteread != -1) {
                    fs.write(buffer, 0, byteread);
                    fs.flush();
                    byteread = stream.read(buffer);
                }
            } else {
                fs.write(buffer, 0, byteread);
                fs.flush();
            }

            fs.close();
            stream.close();
        } catch (FileNotFoundException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        return file;
    }

    /**
     * 根据路径删除单个文件
     *
     * @return：boolean
     *
     * @author：wangj
     *
     * @date：2017年8月18日
     */
    public static void deleteFile(String sPath) {
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    /**
     * 根据文件对象删除单个文件
     *
     * @return：boolean
     *
     * @author：wangj
     *
     * @date：2017年8月18日
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {// 否则如果它是一个目录
                File[] files = file.listFiles();// 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) {// 遍历目录下所有的文件
                    deleteFile(files[i]);// 把每个文件用这个方法进行迭代
                }
                file.delete();// 删除文件夹
            }
        }
    }

    /**
     * 格式化文件路径
     *
     * @return：string
     *
     * @author：wangj
     *
     * @date：2017年09月08日
     */
    public static String formatPath(String path) {
        String newPath = "";
        if (path.indexOf("/") > -1) {
            String[] strs = path.split("/");
            if (strs.length > 0) {
                for (String str : strs) {
                    newPath += str + File.separator + File.separator;
                }
            }
        }

        if (path.indexOf("//") > -1) {
            String[] strs = path.split("//");
            if (strs.length > 0) {
                for (String str : strs) {
                    newPath += str + File.separator + File.separator;
                }
            }
        }

        if (path.indexOf("\\") > -1) {
            String[] strs = path.split("/\\/");
            if (strs.length > 0) {
                for (String str : strs) {
                    newPath += str + File.separator + File.separator;
                }
            }
        }

        return newPath;
    }

    /**
     * 拷贝文件到指定路径
     *
     * @return：string
     *
     * @author：wangj
     *
     * @date：2017年09月08日
     */
    public static File copy(File oldfile, String newPath, String fileName) {
        File newFile = null;
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            // int bytesum = 0;
            int byteread = 0;
            if (oldfile.exists()) {
                inStream = new FileInputStream(oldfile);
                File tempPath = new File(newPath);
                if (!tempPath.exists()) {
                    tempPath.mkdirs();
                }

                newFile = new File(newPath + File.separator + File.separator + fileName);
                fs = new FileOutputStream(newFile);
                byte[] buffer = new byte[inStream.available()];
                while ((byteread = inStream.read(buffer)) != -1) {
                    // bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("error  ");
            logger.error("", e);
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        return newFile;
    }

    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB

    /**
     * 转换文件大小为指定单位 sizeType 1 --> B 2 --> KB 3 --> MB 4 --> GB
     *
     * @return：int
     *
     * @author：wangj
     *
     * @date：2017年09月08日
     */
    public static int formatFileSize(long fileSize, int sizeType) {
        DecimalFormat df = new DecimalFormat("#");
        int fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Integer.valueOf(df.format((int)fileSize));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Integer.valueOf(df.format((int)fileSize / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Integer.valueOf(df.format((int)fileSize / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Integer.valueOf(df.format((int)fileSize / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 转换文件大小为指定单位 sizeType 1 --> B 2 --> KB 3 --> MB 4 --> GB
     *
     * @return：double
     *
     * @author：wangj
     *
     * @date：2017年09月08日
     */
    public static double fomatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double)fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double)fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double)fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double)fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }
    

    /**
     * 批量下载时压缩文件
     *
     * @author：caizx
     *
     * @date：2018年03月09日
     */
    public static void doZip(File file, ZipOutputStream zos) {
        FileInputStream fis = null;
        try {
            ZipEntry zet = new ZipEntry(file.getName());
            zos.putNextEntry(zet);
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int r = 0;
            while ((r = fis.read(buffer)) != -1) {
              zos.write(buffer, 0, r);
            }
            zos.flush();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }
    
    /**
     * 将文件内容压缩
     *
     * @author：caizx
     *
     * @date：2018年03月09日
     */
    public static byte[] compress(String str, String encoding){  
        ByteArrayOutputStream out = null;
        GZIPOutputStream gzip = null;
        try {
            if (str == null || str.length() == 0) {  
                return null;  
            }  
            out = new ByteArrayOutputStream();  
            gzip = new GZIPOutputStream(out);  
            gzip.write(str.getBytes(encoding));
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
        return out.toByteArray();  
    }  
    
    /**
     * 输入流转byte数组
     *
     * @author：caizx
     *
     * @date：2018年03月09日
     */
    public static byte[] toByteArray(InputStream in) {
        byte[] byteArray = null;
        ByteArrayOutputStream out =  null;
        try {
            out=new ByteArrayOutputStream();
            byte[] buffer=new byte[512];
            int n=0;
            while ( (n=in.read(buffer)) !=-1) {
                out.write(buffer,0,n);
            }
            byteArray = out.toByteArray();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
        return byteArray;
    }
    
    /**
     * byte数组转文件
     *
     * @author：caizx
     *
     * @date：2018年03月09日
     */
    public static File byte2file(String fileName, byte[] compress){
        File file = new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(compress);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
        return file;
    }
    
    /**
     * 文件复制
     * 
     * @author：caizx
     * 
     * @date：2018年03月09日
     * 
     */
    public static void copyFile(File fromFile,File toFile) {  
        FileInputStream ins = null;
        FileOutputStream out = null;
        try {
            ins = new FileInputStream(fromFile);
            out = new FileOutputStream(toFile);
            byte[] b = new byte[1024];
            int n=0;
            while((n=ins.read(b))!=-1){
                out.write(b, 0, n);
            }
            
            ins.close();
            out.close();
        } catch (Exception e) {
            logger.error("",e);
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
        }
    }  
    

    /**
     * 文件转byte
     *
     * @author：caizx
     *
     * @date：2018年03月16日
     */
    public static byte[] fileToBetyArray(File file) {  
        FileInputStream fileInputStream = null;  
        byte[] bFile = null;  
        try {  
            bFile = new byte[(int) file.length()];  
            fileInputStream = new FileInputStream(file);  
            fileInputStream.read(bFile);  
        } catch (Exception e) {  
            logger.error("",e);
        } finally {  
            if (fileInputStream != null) {
                try {  
                    fileInputStream.close();  
                } catch (Exception e) {  
                    logger.error("",e);  
                }  
            }
            
        }  
        return bFile;  
    }  
    
    /**
     * 字符串压缩
     *
     * @author：caizx
     *
     * @date：2018年03月18日
     */
    public static String gzip(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        GZIPOutputStream gzip=null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(primStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(gzip!=null){
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new sun.misc.BASE64Encoder().encode(out.toByteArray());
    }
    
    /**
     * 字符串解压
     *
     * @author：caizx
     *
     * @date：2018年03月18日
     */
    public static String gunzip(String compressedStr){
        if(compressedStr==null){
            return null;
        }

        ByteArrayOutputStream out= new ByteArrayOutputStream();
        ByteArrayInputStream in=null;
        GZIPInputStream ginzip=null;
        byte[] compressed=null;
        String decompressed = null;
        try {
            compressed = new sun.misc.BASE64Decoder().decodeBuffer(compressedStr);
            in=new ByteArrayInputStream(compressed);
            ginzip=new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed=out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return decompressed;
    }
    
    /**
     * 去除字符串中的空格、回车、换行符、制表符
     *
     * @author：caizx
     *
     * @date：2018年03月18日
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    public static void main(String[] args) throws Exception {
        
    }
}
