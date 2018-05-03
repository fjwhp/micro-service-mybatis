package aljoin.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 参数相关注解
 *
 * @author：zhongjy
 *
 * @date：2017年6月5日 下午8:21:28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamObj {

  /**
   * 
   * 加密参数(逗号分隔)
   *
   *                @return：String
   *
   * @author：zhongjy
   *
   * @date：2017年10月23日 上午9:18:44
   */
  public String encryptAttrs();

  /**
   * 
   * 加密方式
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017年10月23日 上午9:18:44
   */
  public String encryptType();

}
