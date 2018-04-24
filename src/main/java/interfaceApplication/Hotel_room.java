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

public class Hotel_room {
	private GrapeTreeDBModel hotel_room;
	private int userType;
	private String pkString;
	private String hid;
	private String msg = rMsg.netMSG(0, "");
	private JSONObject usersInfo;

	public Hotel_room() {

		hotel_room = new GrapeTreeDBModel();//数据库对象
        //数据模型
        GrapeDBDescriptionModel gdbField = new GrapeDBDescriptionModel();
        gdbField.importDescription(appsProxy.tableConfig("hotel_room"));
        hotel_room.descriptionModel(gdbField);
        
        //权限模型
        GrapePermissionsModel gperm = new GrapePermissionsModel(appsProxy.tableConfig("hotel_room"));
        hotel_room.permissionsModel(gperm);
  		
        hotel_room.checkMode();
        
        pkString = hotel_room.getPk();
        
        session se = new session();
        usersInfo = se.getDatas();
        if (usersInfo != null && usersInfo.size() > 0) {
	        userType = usersInfo.getInt("userType");
	        hid = usersInfo.getString("hid");// 酒店id
        }
	}

	/**
	 * (基本查询)
	 * @param cond_array
	 * @return
	 */
	public String findRoomClass(String cond_array) {
		if (StringHelper.InvaildString(hid)) { //TODO 1
			return rMsg.netMSG(2, "hid为空");
		}
		hotel_room.eq("hid", hid);
		if (!StringHelper.InvaildString(cond_array)) {//TODO 1
			JSONArray jsonArray = JSONArray.toJSONArray(cond_array);
			if (jsonArray != null) {
				hotel_room.where(jsonArray);
			}
		}
		JSONArray select = hotel_room.eq("deleteable", 0).select();
		if (select != null && select.size() > 0) {
			msg = select.toJSONString();
		}

		return msg;

	}

	/**
	 * (删除酒店房间类型)
	 * @param rids
	 * @return
	 */
	public String deleteRoomClass(String rids) {
//		hotel_room.enableCheck();
		if (userType != plvDef.UserMode.admin) {
			return rMsg.netMSG(1, "请先登录酒店管理员(账号)");
		}
		if (StringHelper.InvaildString(hid)) {//TODO 1
			return rMsg.netMSG(2, "hid为空");
		}
		if (StringHelper.InvaildString(rids)) {
			String[] split = rids.split(",");
			for (String rid : split) {
				hotel_room.or().eq(pkString, rid);
			}
			// deleteAllEx
			hotel_room.deleteAll();
		}

		return msg;

	}

	/**
	 * TODO(更新酒店房间类型,支持批量)
	 * @param data
	 * @return
	 */
	public String updateRoomClass(String data) {
//		hotel_room.enableCheck();
		if (userType != plvDef.UserMode.admin) {
			return rMsg.netMSG(99, "账号权限不对,需要管理员权限");
		}
		if (StringHelper.InvaildString(hid)) {//TODO 1
			return rMsg.netMSG(2, "hid为空");
		}
		JSONArray jsonArray = JSONArray.toJSONArray(data);
		if (jsonArray == null || jsonArray.size() <= 0) {
			return rMsg.netMSG(2, "酒店更新信息为空");
		}

		String msg = rMsg.netMSG(0, "");
		ArrayList<String> arrayList = new ArrayList<String>();
		for (Object object : jsonArray) {
			JSONObject o = (JSONObject) object;
			String _id = o.getString(pkString);
			if (StringHelper.InvaildString(_id)) {
				boolean updateEx = hotel_room.eq(pkString, _id).eq("hid", hid).data(o).updateEx();
				if (!updateEx) {
					arrayList.add(_id);
				}
			}
		}
		msg = arrayList.size() == 0 ? rMsg.netMSG(0, "全部更新成功") : rMsg.netMSG(0, new JSONObject().puts("当前hid", hid).puts("更新失败的rid", arrayList));
		return msg;

	}

	/**
	 * (新增酒店房间类型,支持批量)
	 * @param data
	 * @return
	 */
	public String insertRoomClass(String data) {
//		hotel_room.enableCheck();
		if (userType != plvDef.UserMode.admin) {
			return rMsg.netMSG(1, "请先登录酒店管理员(账号)");
		}

		if (StringHelper.InvaildString(hid)) {//TODO 1
			return rMsg.netMSG(2, "hid为空");
		}
		JSONArray jsonArray = JSONArray.toJSONArray(data);
		if (jsonArray != null && jsonArray.size() > 0) {
			for (Object object : jsonArray) {
				JSONObject o = (JSONObject) object;
				hotel_room.data(o).insertEx();
			}
		}
		return msg;

	}
	/**
	 * TODO(获取对应酒店的评价量最大关键字)
	 * @param uid
	 * @param hid
	 * @return
	 */
	public String getHotelRoom(String uid,String hid) {
		
		return null;
	

	}

}
