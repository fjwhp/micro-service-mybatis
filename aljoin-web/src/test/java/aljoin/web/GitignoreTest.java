 package aljoin.web;

import java.io.File;

import aljoin.util.FileUtil;
/**
 * 批量处理git需要忽略的文件
 * @author wuhp
 * @date 2018年3月7日
 */
public class GitignoreTest {

   public static void main(String[] args) {
    
      String dir="D:/elipse_workspace_git_215/aljoin-parent";//目标目录
      String source="D:/elipse_workspace_git_10/aljoin-parent/aljoin-act-dao/.gitignore" ;//源文件
      
      try {
        //把目标文件拷贝到目录下的 指定文件夹下
        for (File file : new File(dir).listFiles()) {
          String pathName=file.getName();
          String fileName = pathName.substring(pathName.lastIndexOf("/")+1);  
          if (fileName.indexOf("aljoin-")>=0 ) {
             FileUtil.copy(new File(source), pathName, ".gitignore");
            //System.out.println(fileName);
          }
        }
        System.out.println("执行成功");
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("执行失败");
      }
    
  }
}
