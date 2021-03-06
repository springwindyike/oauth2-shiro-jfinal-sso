package com.model;

import com.auth.base.MyConstant;
import com.auth.util.Empty;
import com.auth.util.TimeUtil;
import com.auth.util.MyUUID;
import com.model.base.BaseRes;
import com.pojo.ZTree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Res extends BaseRes<Res> {
	public static final Res dao = new Res();

	/**
	 * 通过用户名或手机号查找该用户权限
	 * @param username
	 * @return
	 */
	public Set findPermissionsByUser(String username){
		String sql = "SELECT res.* FROM "+ MyConstant.TABLE_PREFIX+"user AS u,"+ MyConstant.TABLE_PREFIX+"user_role AS ur,"
				+ MyConstant.TABLE_PREFIX+"role AS r,"+ MyConstant.TABLE_PREFIX+"role_res AS rr,"+ MyConstant.TABLE_PREFIX+"res AS res WHERE" +
				" u.user_id = ur.user_id AND ur.role_id = r.role_id AND r.role_id = rr.role_id AND rr.res_id = res.res_id AND u.user_username = ?";
		List<Res> list = dao.find(sql,username);
		Set set = new HashSet();
		if(list != null){
			for(int i=0;i<list.size();i++){
				set.add(list.get(i).getResUrl());
			}
		}
		return set;
	}

	/**
	 * 通过用户id获得用户菜单
	 * @param user_id
	 * @return
	 */
	public List Menu(String user_id){
		if(Empty.isEmpty(user_id)){
			return null;
		}
		String sql = "SELECT res.* FROM "+ MyConstant.TABLE_PREFIX+"user AS u,"+ MyConstant.TABLE_PREFIX+"user_role AS ur,"
				+ MyConstant.TABLE_PREFIX+"role AS r,"+ MyConstant.TABLE_PREFIX+"res AS res,"+ MyConstant.TABLE_PREFIX+"role_res AS rr WHERE "
				+ " res.res_type='1' AND u.user_id = ur.user_id AND ur.role_id = r.role_id AND r.role_id = rr.role_id AND rr.res_id = res.res_id AND u.user_id = ?";
		return dao.find(sql,user_id);
	}

	/**
	 * 获得资源列表
	 * @param pageNumber
	 * @param pageSize
	 * @param keyword
	 * @return
	 */
	public com.jfinal.plugin.activerecord.Page<Res> list(int pageNumber,int pageSize ,String keyword){
		String sql = "SELECT * ";
		String where = "FROM "+ MyConstant.TABLE_PREFIX+"res  "+keyword;
		return dao.paginate(pageNumber, pageSize, sql, where);
	}

	/**
	 * 获得一级资源
	 * @return
	 */
	public List pidList(){
		String sql = "SELECT * FROM "+ MyConstant.TABLE_PREFIX+"res WHERE  res_pid = '0' ";
		List list = dao.find(sql);
		return list;
	}

	/**
	 * 组建权限资源树
	 * @param id
	 * @return
	 */
	public List getResById(String id ){
		String sql = "select * from "+ MyConstant.TABLE_PREFIX+"res where res_id in "
				+"( SELECT res_id FROM "+ MyConstant.TABLE_PREFIX+"role_res crr " +
				"WHERE role_id =?)";
		List<Res> list = null;
		if(null!=id&&!"".equals(id)){
			list = Res.dao.find(sql, id);
		}
		String allsql = "select * from "+ MyConstant.TABLE_PREFIX+"res ";
		List<Res>alllist = Res.dao.find(allsql);
		List<ZTree> ztree = new ArrayList();

		if(list != null){
			for(int j=0;j<alllist.size();j++){
				ZTree zv = new ZTree(alllist.get(j).getResId(),alllist.get(j).getResPid(),alllist.get(j).getResName());
				for(int k=0;k<list.size();k++){
					if(alllist.get(j).getResId().equals(list.get(k).getResId())){
						zv.checked = true;
						break;
					}
				}
				ztree.add(zv);
			}
		}else{
			for(int n=0;n<alllist.size();n++){
				ZTree zv = new ZTree(alllist.get(n).getResId(),alllist.get(n).getResPid(),alllist.get(n).getResName());
				ztree.add(zv);
			}
		}
		return ztree;
	}


	/**
	 * 保存或者修改
	 * @param res
	 * @return
	 */
	public boolean save(Res res){
		boolean is_success = false;
		if(!Empty.isEmpty(res.getResId())){
			//resid你为空，更新
			is_success = res.set("updated_at", TimeUtil.getTime()).update();
		}else{
			//保存
			is_success = res.set("res_id", MyUUID.getUUID()).set("created_at", TimeUtil.getTime()).save();
		}
		return is_success;
	}


}
