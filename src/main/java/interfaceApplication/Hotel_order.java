package interfaceApplication;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.interfaceModel.GrapeDBDescriptionModel;
import common.java.interfaceModel.GrapePermissionsModel;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.session.session;
import common.java.string.StringHelper;

public class Hotel_order {
	private GrapeTreeDBModel hotel_order;
	private String pkString;
	private String uid;
	private String hid;
	private session se;
	private JSONObject usersInfo;

	public Hotel_order() {
		hotel_order = new GrapeTreeDBModel();//数据库对象
        //数据模型
        GrapeDBDescriptionModel gdbField = new GrapeDBDescriptionModel();
        gdbField.importDescription(appsProxy.tableConfig("hotel_order"));
        hotel_order.descriptionModel(gdbField);
        
        //权限模型
        GrapePermissionsModel gperm = new GrapePermissionsModel(appsProxy.tableConfig("hotel_order"));
        hotel_order.permissionsModel(gperm);
  		
        hotel_order.checkMode();
        
        pkString = hotel_order.getPk();
        
        se = new session();
        usersInfo = se.getDatas();
        if (usersInfo != null && usersInfo.size() > 0) {
        	
	        hid = usersInfo.getString("hid");// 酒店id
	        uid = usersInfo.getString(pkString);// 用户主键id
        }
    }

	/**
	 * TODO(修改订单, 用户可以在订单未被确认之前修改自己发起的订单)
	 * 
	 * @param oid
	 *            订单ID
	 * @param data
	 *            更新 json
	 * @return
	 */
	public String updateOrder(String oid, String data) {

		if (!StringHelper.InvaildString(uid)) {
			return rMsg.netMSG(99, "用户未登录");
		}
		JSONObject json = JSONObject.toJSON(data);
		if (json == null || json.size() < 1) {
			return rMsg.netMSG(96, "json非法");
		}
		JSONObject find = hotel_order.eq(pkString, oid).find();
		if (find == null || find.size() < 1) {
			return rMsg.netMSG(98, "订单id不存在");
		}
		String uid1 = find.getString("uid");
		if (!uid.equals(uid1)) {
			return rMsg.netMSG(95, "该订单不属于当前用户");
		}
		int state = find.getInt("state");
		if (state != 0) {
			return rMsg.netMSG(97, "订单状态非法");
		}
		boolean updateEx = hotel_order.eq(pkString, oid).data(json).updateEx();
		if (updateEx) {
			return rMsg.netMSG(0, "更新成功");
		} else {
			return rMsg.netMSG(1, "更新失败");
		}

	}

	/**
	 * TODO(查询订单, 酒店可以查询自己接收的订单)
	 * 
	 * @param time
	 *            预定时间段
	 * @return
	 */
	public String findOrder_by_hotel(String time) {
		if (StringHelper.InvaildString(uid)) {//TODO 1
			return rMsg.netMSG(99, "用户未登录");
		}
		if (StringHelper.InvaildString(hid)) {//TODO 1
			return rMsg.netMSG(97, "会话里的酒店id为空");
		} else {
			String find = new Hotel().find(hid);
			if (find == null) {
				return rMsg.netMSG(96, "会话里的酒店id已经删除");
			}
		}
		JSONObject json = JSONObject.toJSON(time);
		if (!json.containsKey("start") || !json.containsKey("end")) {
			return rMsg.netMSG(98, "起始或者末尾时间未设置");

		}
		long start = json.getLong("start");
		long end = json.getLong("end");
		JSONArray select = hotel_order.eq("hid", hid).gte("time", start).lte("end", end).eq("deleteable", 0).select();
		return rMsg.netMSG(0, select.toJSONString());

	}

	/**
	 * (查询订单, 用户可以查询自己发起的订单)
	 * 
	 * @param time
	 *            预定时间段
	 * @param time
	 *            酒店id
	 * @return
	 */
	public String findOrder_by_user(String time, String hid) {
		if (StringHelper.InvaildString(uid)) {//TODO 1
			return rMsg.netMSG(99, "用户未登录");
		}
		JSONObject json = JSONObject.toJSON(time);
		if (!json.containsKey("start") || !json.containsKey("end")) {
			return rMsg.netMSG(98, "起始或者末尾时间未设置");

		}
		long start = json.getLong("start");
		long end = json.getLong("end");
		JSONArray select = hotel_order.eq("hid", hid).gte("time", start).lte("end", end).eq("deleteable", 0).select();
		return rMsg.netMSG(0, select.toJSONString());

	}

}
