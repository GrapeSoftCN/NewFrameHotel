package interfaceApplication;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.authority.plvDef;
import common.java.interfaceModel.GrapeDBDescriptionModel;
import common.java.interfaceModel.GrapePermissionsModel;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.session.session;
import common.java.string.StringHelper;

public class Hotel_brand {
	private session se;
	private JSONObject usersInfo;
//	private String GrapeSid;
//	private String currentWeb;
//	private String userName;
//	private String userId;
	private int userType;
	private String pkString;
//	private String ugid;
	private String msg = rMsg.netMSG(0, "");
//	private String uid;
	private GrapeTreeDBModel hotel_brand;

	public Hotel_brand() {
		hotel_brand = new GrapeTreeDBModel();//数据库对象
        //数据模型
        GrapeDBDescriptionModel gdbField = new GrapeDBDescriptionModel();
        gdbField.importDescription(appsProxy.tableConfig("hotel_brand"));
        hotel_brand.descriptionModel(gdbField);
        
        //权限模型
        GrapePermissionsModel gperm = new GrapePermissionsModel(appsProxy.tableConfig("hotel_brand"));
  		hotel_brand.permissionsModel(gperm);
  		
  		hotel_brand.checkMode();
  		
  		pkString = hotel_brand.getPk();

		se = new session();
		usersInfo = se.getDatas();
//		GrapeSid = session.getSID();
		if (usersInfo != null && usersInfo.size() != 0) {
//			currentWeb = usersInfo.getString("currentWeb"); // 当前用户所属网站id
//			userName = usersInfo.getString("name"); // 当前用户姓名
//			userId = usersInfo.getString("id"); // 当前用户用户名
			userType = usersInfo.getInt("userType");
//			ugid = usersInfo.getString("ugid");
//			uid = usersInfo.getString(pkString);//用户主键id
		}
		
		
	}

	/**
	 * TODO(改)
	 * 
	 * @param bid
	 * @param data
	 * @return
	 */
	public String update_brand(String bid, String data) {
//		hotel_brand.enableCheck();
		if (userType != plvDef.UserMode.root) {
			return rMsg.netMSG(1, "请先登录管理员(账号)");
		}
//		if (!StringHelper.InvaildString(bid)) {//TODO 1
		if (StringHelper.InvaildString(bid)) {
			return rMsg.netMSG(3, "id为空");
		}
		JSONObject json = JSONObject.toJSON(data);
		if (json == null || json.size() <= 0) {
			return rMsg.netMSG(2, "json非法");
		}
		boolean updateEx = hotel_brand.eq(pkString, bid).data(json).eq("deleteable", 0).updateEx();
		msg = updateEx ? rMsg.netMSG(0, "更新成功") : rMsg.netMSG(99, "更新失败");
		return msg;
	}

	/**
	 * TODO(增)
	 * 
	 * @param bid
	 * @param data
	 * @return
	 */
	public String insert_brand(String data) {
//		hotel_brand.enableCheck();
		if (userType != plvDef.UserMode.root) {
			return rMsg.netMSG(1, "请先登录管理员(账号)");
		}
		JSONArray jsonArray = JSONArray.toJSONArray(data);
		if (jsonArray == null || jsonArray.size() <= 0) {
			return rMsg.netMSG(2, "jsonArray非法");
		}
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		int i = 0;
		for (Object object : jsonArray) {
			JSONObject o = (JSONObject) object;
			Object insertEx = hotel_brand.data(o).insertEx();
			if (insertEx == null) {
				arrayList.add(++i);
			}
		}
		msg = arrayList.size() > 0 ? rMsg.netMSG(3, "以下顺序的json新增失败" + arrayList) : rMsg.netMSG(0, "全部新增成功");
		return msg;

	}

	/**
	 * TODO(删)
	 * 
	 * @param bid
	 * @param data
	 * @return
	 */
	public String delete_brand(String bid) {
//		hotel_brand.enableCheck();
		if (userType != plvDef.UserMode.root) {
			return rMsg.netMSG(1, "请先登录管理员(账号)");
		}
		if (StringHelper.InvaildString(bid)) {
			return rMsg.netMSG(3, "id为空");
		}
		boolean updateEx = hotel_brand.eq(pkString, bid).eq("deleteable", 0).deleteEx();
		msg = updateEx ? rMsg.netMSG(0, "删除成功") : rMsg.netMSG(99, "删除失败");
		return msg;

	}

	/**
	 * TODO(分页)
	 * 
	 * @param bid
	 * @param data
	 * @return
	 */
	public String page_brand(int idx, int pagesize) {
//		hotel_brand.enableCheck();
		if (userType != plvDef.UserMode.root) {
			return rMsg.netMSG(1, "请先登录管理员(账号)");
		}
		if (idx <= 0) {
			return rMsg.netMSG(false, "页码错误");
		}
		if (pagesize <= 0) {
			return rMsg.netMSG(false, "页长度错误");
		}
		JSONArray page = hotel_brand.eq("deleteable", 0).page(idx, pagesize);
		msg = rMsg.netMSG(0, page);
		return msg;

	}

	/**
	 * TODO(获得全部品牌)
	 * 
	 * @param bid
	 * @param data
	 * @return
	 */
	public String getBrand() {

		JSONArray page = hotel_brand.eq("deleteable", 0).select();
		msg = rMsg.netMSG(0, page);
		return msg;

	}
}
