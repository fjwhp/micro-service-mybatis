package aljoin.web.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * web api效率统计
 *
 * @author：zhongjy
 * 
 * @date：2017年6月5日 下午3:43:57
 */
@Aspect
@Component
public class WebApiAspect {

  private static Logger logger = LoggerFactory.getLogger(WebApiAspect.class);

  /**
   * 
   * auto.deploy.web.controller包下的所有函数
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017年6月5日 下午3:53:56
   */
  @Pointcut("execution(public * aljoin.*.controller..*.*(..))")
  public void webApi() {}

  /**
   * 
   * 接口访问时间控制
   *
   * @return：Object
   *
   * @author：zhongjy
   *
   * @date：2017年11月15日 下午4:13:38
   */
  @Around("webApi()")
  public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method targetMethod = methodSignature.getMethod();
    String apiName = targetMethod.getName();
    String apiClass = targetMethod.getDeclaringClass().getName();

    long beg = System.currentTimeMillis();
    Object obj = joinPoint.proceed();
    long end = System.currentTimeMillis();
    long period = end - beg;
    if (period > 500) {
      logger.error(
          "\n\n------------------------------------请优化------------------------------------\n\n接口耗时太长(超过500ms)，接口名称："
              + apiClass + "." + apiName + "(请求时间：" + period
              + "ms)\n\n----------------------------------------------------------------------------\n");
    }
    logger.info("\n接口名称：" + apiClass + "." + apiName + "(请求时间：" + period + "ms)");
    return obj;
  }



}
