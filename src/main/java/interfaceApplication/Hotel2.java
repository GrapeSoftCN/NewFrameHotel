package interfaceApplication;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.java.Concurrency.distributedLocker;
import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.cache.CacheHelper;
import common.java.database.dbFilter;
import common.java.interfaceModel.GrapeDBDescriptionModel;
import common.java.interfaceModel.GrapePermissionsModel;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.interfaceType.apiType;
import common.java.interfaceType.apiType.type;
import common.java.session.session;
import common.java.string.StringHelper;

public class Hotel2 {
	private GrapeTreeDBModel hotel;
	private int userType;
	private String pkString;
	private JSONObject usersInfo;
	
	public Hotel2() {

		hotel = new GrapeTreeDBModel();//数据库对象
        //数据模型
        GrapeDBDescriptionModel gdbField = new GrapeDBDescriptionModel();
        gdbField.importDescription(appsProxy.tableConfig("hotel"));
        hotel.descriptionModel(gdbField);
        
        //权限模型
        GrapePermissionsModel gperm = new GrapePermissionsModel(appsProxy.tableConfig("hotel"));
        hotel.permissionsModel(gperm);
  		
        hotel.checkMode();
        
        pkString = hotel.getPk();
        
        session se = new session();
        usersInfo = se.getDatas();
        if (usersInfo != null && usersInfo.size() > 0) {
	        userType = usersInfo.getInt("userType");
        }
	}

	/**
	 * (恢复酒店，让该酒店可以显示在页面前端; 管理员权限下恢复任意酒店; 酒店管理员权限下恢复自己所经营的酒店)
	 * 
	 * @param 酒店IDs
	 * @return
	 */
	@apiType(type.sessionApi)
	public String resumeHotel(String hids) {
		ArrayList<String> arrayList = new ArrayList<String>();
		if (!StringHelper.InvaildString(hids)) {//TODO 1
			String[] hids_arr = hids.split(",");
			for (String hid : hids_arr) {
				//boolean updateEx = hotel.eq(pkString, hid).data(new JSONObject().puts("visable", 0)).updateEx();
				//上面这句改成
				boolean updateEx = hotel.eq(pkString, hid).show();
				if (!updateEx) {
					arrayList.add(hid);
				}
			}
		} else {
			return rMsg.netMSG(99, "非法参数");
		}

		if (arrayList.size() == 0) {
			return rMsg.netMSG(0, "更新成功");
		} else {
			return rMsg.netMSG(1, "更新失败的有以下   " + arrayList);
		}
	}

	
	/**
	 * (获得酒店列表（按距离最近最火的条件）; 管理员权限下可以看到全部酒店信息; 未登录或者用户权限下仅可以看到未被冻结的酒店信息)
	 * 上面的理解是错的
	 * 1：用户可以获得酒店列表（按距离条件获得指定距离范围的酒店，按酒店距离用户距离排序）;
	 * 2：酒店管理员可以获得受其管理的酒店列表
	 * 3：总管理员可以获得全部酒店列表
	 * @param idx
	 * @param pagesize
	 * @param 包含了搜索距离（前台固定写死的距离），搜索排序条件（距离，最热，评分），酒店类型（星级，类型）等条件
	 * @return
	 */
	public String listHotel(int idx,int pagesize,int cond) {
		String netMSG = rMsg.netMSG(0, "条件不对,请选择1或0");
		if(cond==1) {
			distributedLocker countLocker = distributedLocker.newLocker("Hotel_hot");
			String rString = "";
			if (countLocker != null) {
				boolean flag = countLocker.lock();
				if (flag) {// 如果锁定成功
					CacheHelper ch = new CacheHelper();
					rString = ch.get("Hotel_hot");
//					if (rString == null || rString.equals("")) {//TODO 1
					if (StringHelper.InvaildString(rString)) {
						JSONObject json = new JSONObject();
						
						rString = json.toJSONString();
						ch.setget("Hotel_hot", rString, 86400);
					}
					countLocker.unlock();
				}
			}
		
			return rString;
		}
		if(cond==0) {
			distributedLocker countLocker = distributedLocker.newLocker("Hotel_near");
			String rString = "";
			if (countLocker != null) {
				boolean flag = countLocker.lock();
				if (flag) {// 如果锁定成功
					CacheHelper ch = new CacheHelper();
					rString = ch.get("Hotel_near");
//					if (rString == null || rString.equals("")) {
					if (StringHelper.InvaildString(rString)) {
						JSONObject json = new JSONObject();
						
						rString = json.toJSONString();
						ch.setget("Hotel_near", rString, 86400);
					}
					countLocker.unlock();
				}
			}
		
			return rString;
		}
		return netMSG;


	}

	/**
	 * (冻结酒店，让该酒店无法显示在页面前端 ;管理员权限下冻结任意酒店; 酒店管理员权限下冻结自己所经营的酒店)
	 * 
	 * @param 酒店IDs
	 * @return
	 */
	@apiType(type.sessionApi)
	public String kickHotel(String hids) {
		ArrayList<String> arrayList = new ArrayList<String>();
		if (!StringHelper.InvaildString(hids)) {//TODO 1
			String[] hids_arr = hids.split(",");
			for (String hid : hids_arr) {
				//boolean updateEx = hotel.eq(pkString, hid).data(new JSONObject().puts("visable", 1)).updateEx();
				//上面是错的应该使用
				boolean updateEx = hotel.eq(pkString, hid).hide();
				if (!updateEx) {
					arrayList.add(hid);
				}
			}
		} else {
			return rMsg.netMSG(99, "非法参数");
		}

		if (arrayList.size() == 0) {
			return rMsg.netMSG(0, "更新成功");
		} else {
			return rMsg.netMSG(1, "更新失败的有以下   " + arrayList);
		}
	}

	/**
	 * (删除酒店; 需要管理员权限)
	 * 
	 * @param 酒店ID
	 * @return
	 */
	@apiType(type.sessionApi)
	public String deleteHotel(String hids) {
		ArrayList<String> arrayList = new ArrayList<String>();
		if (!StringHelper.InvaildString(hids)) {//TODO 1
			String[] hids_arr = hids.split(",");
			for (String hid : hids_arr) {
				//boolean updateEx = hotel.eq(pkString, hid).data(new JSONObject().puts("isdelete", 1)).updateEx();
				//上面是错误的，应该使用
				boolean updateEx = hotel.eq(pkString, hid).deleteEx();
				if (!updateEx) {
					arrayList.add(hid);
				}
			}
		} else {
			return rMsg.netMSG(99, "非法参数");
		}

		if (arrayList.size() == 0) {
			return rMsg.netMSG(0, "删除成功");
		} else {
			return rMsg.netMSG(1, "删除失败的有以下   " + arrayList);
		}

	}

	/**
	 * (更新酒店数据; 在管理员权限下可以更改所有信息; 在酒店管理员权限下无法修改酒店名称)
	 * 
	 * @param 基本酒店数据
	 * @return
	 */
	@apiType(type.sessionApi)
	public String updateHotel(String data) {
		boolean update = false;
		JSONObject json = JSONObject.toJSON(data);
		if (json != null) {
			String _id = json.getString(pkString);
			if (!StringHelper.InvaildString(_id)) {//TODO 1
				if (userType == 11000) {
				}
				if (userType == 10000) {
					json.remove("name");
				}
				update = hotel.eq(pkString, _id).data(json).updateEx();
			}
		}
		return rMsg.netMSG(0, update);
	}

	/**
	 * (新增酒店数据 管理员账号特有)
	 * 
	 * @param 基本酒店数据
	 * @return
	 */
	@apiType(type.sessionApi)
	public String insertHotel(String data) {
		
		Object ob = null;
		JSONObject jsonObj = JSONObject.toJSON(data);
		if (jsonObj != null) {
//			hotel.checkMode();
			/*
			这里逻辑有问题，设置的3个条件的ugid是添加者，也就是管理员的
			然而实际上酒店查询权限不应该是仅有管理员可见
			编辑权限也不仅有管理员可用，这里有严重错误
			*/
//			JSONObject rMode = new JSONObject(plvType.chkType, plvType.powerVal).puts(plvType.chkVal, 100);// 设置默认查询权限
//			JSONObject uMode = new JSONObject(plvType.chkType, plvType.groupOwn).puts(plvType.chkVal, ugid);
//			JSONObject dMode = new JSONObject(plvType.chkType, plvType.groupOwn).puts(plvType.chkVal, ugid);
//			jsonObj.puts("rMode", rMode.toJSONString()); // 添加默认查看权限
//			jsonObj.puts("uMode", uMode.toJSONString()); // 添加默认修改权限
//			jsonObj.puts("dMode", dMode.toJSONString()); // 添加默认删除权限
			ob = hotel.data(jsonObj).autoComplete().insertEx();
		}
		return rMsg.netMSG(ob != null, (String) ob);
	}

	/**
	 * (获取对应条件的酒店数据)
	 * 
	 * @param 城市编码
	 * @param 行政区编码
	 * @param 品牌ID
	 * @param 各类基本条件的筛选
	 *
	 * 这里接口不符合设计预期，品牌ID只是可选项，但是在接口实现里成了必选项
	 * @return
	 */
	public String getHotelList(String cityid, String areaid, String brandid, String cond) {

		//TODO 1
		if (StringHelper.InvaildString(cityid) || StringHelper.InvaildString(cityid) || StringHelper.InvaildString(cityid)) {
			return rMsg.netMSG(99, "条件不足");
		}
		JSONArray jsonArray = JSONArray.toJSONArray(cond);
		hotel.eq("city", cityid).eq("area", areaid).eq("brand", brandid);
		if (jsonArray != null && jsonArray.size() > 0) {
			hotel.where(jsonArray);
		}
		JSONArray select = hotel.select();
		return rMsg.netMSG(0, select);

	}

	/**
	 * (获取对应坐标附近的酒店的列表) 经度最大是180° 最小是0° 纬度最大是 90° 最小是0°
	 * 这里一样需要传入范围距离参数，但是该参数是可选项，非必选
	 * 实现方位没有按领导要求实现，代码IO与执行效率低下，不符合实际商业使用环境
	 * @param 经度
	 * @param 纬度
	 * @param 各类基本条件的筛选
	 * @return
	 */
	public String getAreaHotel(double longitude, double latitude, String cond) {
		JSONArray jsonArray = JSONArray.toJSONArray(cond);
		if (jsonArray != null && jsonArray.size() > 0) {
			hotel.where(jsonArray);
			long count = hotel.dirty().count();
			if (count < 1) {
				return rMsg.netMSG(99, "没有满足条件的酒店");
			}
		}
		JSONArray select = new JSONArray();
		double j= 0.5;
		double w= 0.25;
		for (;;) {
			double longitude_min = longitude - j;
			double longitude_max = longitude + j;
			double latitude_min = latitude - w;
			double latitude_max = latitude + w;
			dbFilter dbFilter = new dbFilter();
			dbFilter.gte("longitude", longitude_min).lte("longitude", longitude_max).gte("latitude", latitude_min).lte("latitude", latitude_max);
			JSONArray build = dbFilter.build();
			select = hotel.where(build).select();
			if (select.size() > 0 || j >= 180||w>=90) {
				break;
			}
			j += j;
			w += w;
		}

		return select.toJSONString();

	}

}
