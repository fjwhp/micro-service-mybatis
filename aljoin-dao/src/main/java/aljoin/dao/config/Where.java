package aljoin.dao.config;

import com.baomidou.mybatisplus.mapper.EntityWrapper;

/**
 * 
 * 自定义条件构造器
 *
 * @author：zhongjy
 *
 * @date：2017年5月20日 下午4:52:29
 */
public class Where<T> extends EntityWrapper<T> {

    /**
     * 
     */
    private static final long serialVersionUID = -3066529006012043236L;

    public Where() {
        super.isWhere(false);
    }

}
