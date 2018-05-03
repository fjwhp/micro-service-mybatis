package aljoin.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 对方法的功能进行描述，对日志的访问信息进行记录描述
 *
 * @author：zhongjy
 *
 * @date：2017年6月5日 下午8:21:28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FuncObj {
	/**
	 * 
	 * 功能描述
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年6月5日 下午8:22:56
	 */
	public String desc();

}
