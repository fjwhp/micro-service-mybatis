package aljoin.web.aspect;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import aljoin.aut.security.CustomUser;
import aljoin.sys.dao.entity.SysOperateLog;
import aljoin.web.annotation.FuncObj;
import aljoin.web.task.WebTask;

/**
 * 
 * web日志统一处理
 *
 * @author：zhongjy
 * 
 * @date：2017年6月5日 下午3:43:57
 */
@Aspect
@Component
public class WebLogAspect {

	@Resource
	private WebTask webTask;

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
	public void webLog() {
	}

	/**
	 * 
	 * 访问目标方法前处理
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年6月5日 下午10:26:22
	 */
	@Before("webLog()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {
		// 获取目标方法对象
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method targetMethod = methodSignature.getMethod();
		FuncObj funcObj = targetMethod.getAnnotation(FuncObj.class);
		// 如果有FuncObj注解则记录操作日志
		if (funcObj != null) {
			// 接收到请求，记录请求内容
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = attributes.getRequest();
			// 操作日志对象
			SysOperateLog sysOperateLog = new SysOperateLog();
			Date date = new Date();
			Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (object instanceof CustomUser) {
				CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				sysOperateLog.setOperateUserId(customUser.getUserId());
				sysOperateLog.setOperateUserName(customUser.getUsername());
			} else {
				sysOperateLog.setOperateUserId(0L);
				sysOperateLog.setOperateUserName("anonymous");
			}
			// sysOperateLog.setOperateUserIp(request.getRemoteAddr());
			sysOperateLog.setOperateUserIp(getIpAddress(request));
			sysOperateLog.setOperateTime(date);
			sysOperateLog.setRequestAddress(request.getRequestURL().toString());
			sysOperateLog.setMethodName(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

			sysOperateLog.setOperateLogName(funcObj.desc());
			sysOperateLog.setOperateDetailDesc(funcObj.desc());
			// 调用异步线程
			webTask.webLogTask(sysOperateLog);
		}

	}

	/**
	 * 
	 * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年6月6日 下午12:47:16
	 */
	private String getIpAddress(HttpServletRequest request) throws IOException {
		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}

}
