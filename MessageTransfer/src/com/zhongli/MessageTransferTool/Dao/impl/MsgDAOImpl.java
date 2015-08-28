package com.zhongli.MessageTransferTool.Dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.zhongli.MessageTransferTool.Dao.MsgDAO;
import com.zhongli.MessageTransferTool.Model.SQLmessage;

public class MsgDAOImpl implements MsgDAO {
	private MongoDBHelper mongoDB;
	private MySQLHelper mySQL;

	public MsgDAOImpl() {
		mongoDB = new MongoDBHelper("localhost", 27017, "happycityproject",
				"rawTwitters");
		mySQL = new MySQLHelper();
	}

	@Override
	public List<SQLmessage> getNewRawMsg(int limit) {
		Document tmp;
		// 创建筛选规则
		BasicDBObject rule = new BasicDBObject();
		rule.append("geo", new BasicDBObject("$ne", true));
		rule.append("import", new BasicDBObject("$ne", true));
		ArrayList<SQLmessage> res = new ArrayList<SQLmessage>();
		// 对时间进行排序
		for (Document cur : mongoDB.getCollection().find(rule)
				.sort(new BasicDBObject("created_at", 1)).limit(limit)) {
			System.out.println(cur.toJson());
			// 提取需要的信息组成对象
			SQLmessage m = new SQLmessage();
			m.setMongoId(cur.getObjectId("_id"));
			m.setRaw_id_str(cur.getString("id_str"));
			// GMT 时间
			SimpleDateFormat sdf = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
			
			try {
				m.setCreat_at(sdf.parse(cur.getString("created_at")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 时间戳
			m.setTimestamp_ms(Long.parseLong(cur.getString("timestamp_ms")));
			// 内容
			m.setText(cur.getString("text"));
			// 转发
			if (cur.getString("in_reply_to_status_id_str") != null) {
				m.setReplay_to("in_reply_to_status_id_str");
			}
			// 坐标
			tmp = (Document) cur.get("geo");
			if (tmp != null) {
				// 如果有具体坐标存储具体坐标
				m.setGeo_type(tmp.getString("type"));
				if (m.getGeo_coordinates() == null) {
					m.setGeo_coordinates(new ArrayList<Double>());
				}
				m.getGeo_coordinates().addAll(
						(ArrayList<Double>) tmp.get("coordinates"));
			}
			// 地点
			tmp = (Document) cur.get("place");
			if (tmp != null) {
				String place_type = tmp.getString("place_type");
				String name = tmp.getString("name");
				String full_name = tmp.getString("full_name");
				String country = tmp.getString("country");
				// 根据type来生成不同的值
				if (place_type.equals("city")) {
					m.setCity("name");
					m.setProvince(full_name.split(",")[1].trim());
					m.setCountry(country);
				} else if (place_type.equals("admin")) {
					m.setProvince(name);
					m.setCountry(country);
				} else if (place_type.equals("country")) {
					m.setCountry(country);
				} else {
					System.out.println("new place type:" + place_type);
				}
			}
			// 实体
			tmp = (Document) cur.get("entities");
			if (tmp != null) {
				// hashtags
				ArrayList<Document> hashtags =  (ArrayList<Document>) tmp.get("hashtags");
				for (int i = 0; i < hashtags.size(); i++) {
					Document ht = (Document) hashtags.get(i);
					if (m.getHashtags() == null) {
						m.setHashtags(new ArrayList<String>());
					}
					m.getHashtags().add(ht.getString("text"));
				}

			}
			System.out.println(m);
			res.add(m);
		}
		return res;
	}

	@Override
	public void saveSQLMsg(List<SQLmessage> sqLmessages) {
		// TODO Auto-generated method stub

	}

}
