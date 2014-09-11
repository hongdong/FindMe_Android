package cn.ifanmi.findme.logic;

import java.util.Map;

/*Activity 公用接口*/
public interface IfanFragment {

	/**初始化数据操作*/
	void initData();
	
	/**初始化UI操作*/
	void initView();
	
	/**更新UI*/
	void refresh(int taskId, Map<String, Object> map);
}
