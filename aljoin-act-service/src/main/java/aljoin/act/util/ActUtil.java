package aljoin.act.util;

import java.util.Map;

import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;

/**
 * 
 * 流程模块工具类
 *
 * @author：zhongjy
 * 
 * @date：2017年12月27日 下午1:27:18
 */
public class ActUtil {

    /**
     * 
     * 根据参数判断JUEL表达式是否成立
     *
     * @return：boolean
     *
     * @author：zhongjy
     *
     * @date：2017年12月27日 下午1:27:40
     */
    public static boolean isCondition(Map<String, String> param, String el) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            context.setVariable(entry.getKey(), factory.createValueExpression(entry.getValue(), String.class));
        }
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (Boolean)e.getValue(context);
    }
}
