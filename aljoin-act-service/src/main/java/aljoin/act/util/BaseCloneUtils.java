package aljoin.act.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * 实现对象的克隆功能
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public abstract class BaseCloneUtils {
	private final static Logger logger = LoggerFactory.getLogger(BaseCloneUtils.class);
	public static void copyFields(Object source, Object target, String... fieldNames) {
		for (String fieldName : fieldNames) {
			try {
				Field field = FieldUtils.getField(source.getClass(), fieldName, true);
				field.setAccessible(true);
				field.set(target, field.get(source));
			} catch (Exception e) {
				logger.error("",e.getMessage());
			}
		}
	}
}
