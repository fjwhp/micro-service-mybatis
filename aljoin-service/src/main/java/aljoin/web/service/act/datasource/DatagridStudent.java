package aljoin.web.service.act.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import aljoin.act.dao.source.DatagridStudentSource;

/**
 * 
 * Datagrid数据源(接口)
 *
 * @author：pengsp
 * 
 * @date：2017年7月25日
 */
public class DatagridStudent extends BaseFormSoure{

	@Override
	public List<Object> execute(Map<String, String> paramMap) throws Exception {
		List<Object> datagrid = new ArrayList<Object>();
		DatagridStudentSource source =new DatagridStudentSource();
		source.setName("zs");
		source.setAge(18);
		source.setScore(98);
		source.setClasses("class 1");
		
		DatagridStudentSource source2 =new DatagridStudentSource();
		source2.setName("zs");
		source2.setAge(17);
		source2.setScore(68);
		source2.setClasses("class 2");
		
		DatagridStudentSource source3 =new DatagridStudentSource();
		source3.setName("mz");
		source3.setAge(18);
		source3.setScore(70);
		source3.setClasses("class 3");
		
		datagrid.add(source);
		datagrid.add(source2);
		datagrid.add(source3);
		
		return datagrid;
	}
	
}
