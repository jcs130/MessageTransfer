package com.zhongli.MessageTransferTool.Dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.result.UpdateResult;
import com.zhongli.MessageTransferTool.Dao.MsgDAO;
import com.zhongli.MessageTransferTool.Model.FullMessage;
import com.zhongli.MessageTransferTool.Model.MarkMessage;

public class MsgDAOImpl implements MsgDAO {
	private MongoDBHelper mongoDB;
	private MySQLHelper_Full mySQL_full;
	private MySQLHelper_Mark mySQL_mark;
	private SimpleDateFormat sdf;

	public MsgDAOImpl() {
		mongoDB = new MongoDBHelper("192.168.1.110", 27017, "happycityproject",
				"rawTwitters");
		mySQL_full = new MySQLHelper_Full();
		mySQL_mark = new MySQLHelper_Mark();
		sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<FullMessage> getNewRawMsg(int limit) {
		HashSet<String> ids = new HashSet<String>();
		Document tmp;
		// 创建筛选规则
		BasicDBObject rule = new BasicDBObject();
		// rule.append("geo", new BasicDBObject("$ne", true));
		rule.append("import", new BasicDBObject("$ne", true));
		rule.append("text", new BasicDBObject("$ne", null));
		// rule.append("in_reply_to_status_id_str", new BasicDBObject("$ne",
		// null));
		BasicDBObject items = new BasicDBObject();
		items.append("_id", 1);
		items.append("in_reply_to_status_id_str", 1);
		items.append("created_at", 1);
		items.append("id_str", 1);
		items.append("timestamp_ms", 1);
		items.append("text", 1);
		items.append("geo", 1);
		items.append("place", 1);
		items.append("entities", 1);
		items.append("extended_entities", 1);
		items.append("lang", 1);
		items.append("import", 1);
		ArrayList<FullMessage> res = new ArrayList<FullMessage>();
		// 对时间进行排序
		for (Document cur : mongoDB.getCollection().find(rule)
				.projection(items).sort(new BasicDBObject("created_at", 1))
				.limit(limit)) {
			// System.out.println(cur.toJson());
			// 查重，如果id重复则跳过
			if (ids.contains(cur.getString("id_str"))) {
				// System.out.println("ID重复，跳过此条");
				// 从mongoDB中删除
				deleteRawMessage(cur.getObjectId("_id"));
				continue;
			}
			ids.add(cur.getString("id_str"));
			// 提取需要的信息组成对象
			FullMessage m = new FullMessage();
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
			// 取得回复对象
			if (cur.getString("in_reply_to_status_id_str") != null) {
				m.setReplay_to(cur.getString("in_reply_to_status_id_str"));
			}
			// 内容
			m.setText(cur.getString("text"));
			// 转发
			if (cur.getString("in_reply_to_status_id_str") != null) {
				m.setReplay_to(cur.getString("in_reply_to_status_id_str"));
				// System.out.println("\n\n\n\n"+m.getReplay_to());
			}
			// 坐标
			tmp = (Document) cur.get("geo");
			if (tmp != null) {
				// 如果有具体坐标存储具体坐标
				m.setGeo_type(tmp.getString("type"));
				if (m.getGeo_coordinates() == null) {
					m.setGeo_coordinates(new ArrayList<Double[]>());
				}
				m.getGeo_coordinates().addAll(
						(ArrayList<Double[]>) tmp.get("coordinates"));
			}
			// 地点
			tmp = (Document) cur.get("place");
			if (tmp != null) {
				String place_type = tmp.getString("place_type");
				m.setPlaceType(place_type);
				String name = tmp.getString("name");
				m.setPlaceName(name);
				String full_name = tmp.getString("full_name");
				m.setPlaceFullName(full_name);
				String country = tmp.getString("country");
				// 根据type来生成不同的值
				if (place_type.equals("city")) {
					m.setCity(name);
					m.setProvince(full_name.split(",")[1].trim());
					m.setCountry(country);
				} else if (place_type.equals("admin")) {
					m.setProvince(name);
					m.setCountry(country);
				} else {
					m.setCountry(country);
				}
				// 存储边界信息
				Document bounding_box = (Document) tmp.get("bounding_box");
				if (bounding_box != null) {
					m.setPlaceBoundingType(bounding_box.getString("type"));
					if (m.getPlaceCoordinates() == null) {
						m.setPlaceCoordinates(new ArrayList<Double[]>());
					}
					m.getPlaceCoordinates().addAll(
							(ArrayList<Double[]>) bounding_box
									.get("coordinates"));
				}
			}

			// 实体
			tmp = (Document) cur.get("entities");
			if (tmp != null) {
				// hashtags
				ArrayList<Document> hashtags = (ArrayList<Document>) tmp
						.get("hashtags");
				if (hashtags != null) {
					for (int i = 0; i < hashtags.size(); i++) {
						Document ht = (Document) hashtags.get(i);
						if (m.getHashtags() == null) {
							m.setHashtags(new ArrayList<String>());
						}
						m.getHashtags().add(ht.getString("text"));
					}
				}
				// media
				ArrayList<Document> medias = (ArrayList<Document>) tmp
						.get("media");
				if (medias != null) {
					for (int i = 0; i < medias.size(); i++) {
						Document media = (Document) medias.get(i);
						if (m.getMedia_urls() == null) {
							m.setMedia_urls(new ArrayList<String>());
						}
						if (m.getMedia_type() == null) {
							m.setMedia_type(new ArrayList<String>());
						}
						if (!m.getMedia_urls().contains(
								media.getString("media_url"))) {
							m.getMedia_urls().add(media.getString("media_url"));
							m.getMedia_type().add(media.getString("type"));
						}
					}
				}

			}
			// 扩展实体
			tmp = (Document) cur.get("extended_entities");
			if (tmp != null) {
				// hashtags
				ArrayList<Document> hashtags = (ArrayList<Document>) tmp
						.get("hashtags");
				if (hashtags != null) {
					for (int i = 0; i < hashtags.size(); i++) {
						Document ht = (Document) hashtags.get(i);
						if (m.getHashtags() == null) {
							m.setHashtags(new ArrayList<String>());
						}
						m.getHashtags().add(ht.getString("text"));
					}
				}
				// media
				ArrayList<Document> medias = (ArrayList<Document>) tmp
						.get("media");
				if (medias != null) {
					for (int i = 0; i < medias.size(); i++) {
						Document media = (Document) medias.get(i);
						if (m.getMedia_urls() == null) {
							m.setMedia_urls(new ArrayList<String>());
						}
						if (m.getMedia_type() == null) {
							m.setMedia_type(new ArrayList<String>());
						}
						// 获得媒体类型
						String mediaType = media.getString("type");
						// 如果和以前的重复则跳过
						if (m.getMedia_urls().contains(
								media.getString("media_url"))
								&& mediaType.equals("photo")) {
							continue;
						}
						m.getMedia_type().add(mediaType);
						if (mediaType.equals("photo")) {
							m.getMedia_urls().add(media.getString("media_url"));
						} else {
							Document video_info = (Document) media
									.get("video_info");
							if (video_info != null) {
								// 提取视频连接
								ArrayList<Document> variants = (ArrayList<Document>) video_info
										.get("variants");
								int maxBitrateIndex = 0;
								int maxBitrate = 0;
								for (int j = 0; j < variants.size(); j++) {
									// 获取bitrate最高的并且格式为mp4的视频的url
									String content_type = variants.get(j)
											.getString("content_type");
									// System.out.println("content_type"
									// + content_type);
									if (content_type.equals("video/mp4")) {
										int bitrate = variants.get(j)
												.getInteger("bitrate");
										if (bitrate > maxBitrate) {
											maxBitrateIndex = j;
										}
									}
								}
								// 将bitrate最大的MP4格式了url保存
								m.getMedia_urls().add(
										variants.get(maxBitrateIndex)
												.getString("url"));

							}
						}
					}
				}
			}
			// 语言
			m.setLang(cur.getString("lang"));
			m.setMessageFrom("twitter");
			// System.out.println(m);
			res.add(m);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public List<FullMessage> getNewRawMsg_skip(int skipNum, int limitNum) {
		HashSet<String> ids = new HashSet<String>();
		Document tmp;
		// 创建筛选规则
		// BasicDBObject rule = new BasicDBObject();
		// // rule.append("geo", new BasicDBObject("$ne", true));
		// rule.append("import", new BasicDBObject("$ne", true));
		// rule.append("text", new BasicDBObject("$ne", null));
		// rule.append("in_reply_to_status_id_str", new BasicDBObject("$ne",
		// null));
		// BasicDBObject items = new BasicDBObject();
		// items.append("_id", 1);
		// items.append("in_reply_to_status_id_str", 1);
		// items.append("created_at", 1);
		// items.append("id_str", 1);
		// items.append("timestamp_ms", 1);
		// items.append("text", 1);
		// items.append("geo", 1);
		// items.append("place", 1);
		// items.append("entities", 1);
		// items.append("extended_entities", 1);
		// items.append("lang", 1);
		// items.append("import", 1);
		ArrayList<FullMessage> res = new ArrayList<FullMessage>();
		// 对时间进行排序
		for (Document cur : mongoDB.getCollection().find().skip(skipNum)
				.limit(limitNum)) {
			// System.out.println(cur.toJson());
			// 查重，如果id重复则跳过
			if (ids.contains(cur.getString("id_str"))) {
				// System.out.println("ID重复，跳过此条");
				// 从mongoDB中删除
				deleteRawMessage(cur.getObjectId("_id"));
				continue;
			}
			ids.add(cur.getString("id_str"));
			// 提取需要的信息组成对象
			FullMessage m = new FullMessage();
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
			// 取得回复对象
			if (cur.getString("in_reply_to_status_id_str") != null) {
				m.setReplay_to(cur.getString("in_reply_to_status_id_str"));
			}
			// 内容
			m.setText(cur.getString("text"));
			// 转发
			if (cur.getString("in_reply_to_status_id_str") != null) {
				m.setReplay_to(cur.getString("in_reply_to_status_id_str"));
				// System.out.println("\n\n\n\n"+m.getReplay_to());
			}
			// 坐标
			tmp = (Document) cur.get("geo");
			if (tmp != null) {
				// 如果有具体坐标存储具体坐标
				m.setGeo_type(tmp.getString("type"));
				if (m.getGeo_coordinates() == null) {
					m.setGeo_coordinates(new ArrayList<Double[]>());
				}
				m.getGeo_coordinates().addAll(
						(ArrayList<Double[]>) tmp.get("coordinates"));
			}
			// 地点
			tmp = (Document) cur.get("place");
			if (tmp != null) {
				String place_type = tmp.getString("place_type");
				m.setPlaceType(place_type);
				String name = tmp.getString("name");
				m.setPlaceName(name);
				String full_name = tmp.getString("full_name");
				m.setPlaceFullName(full_name);
				String country = tmp.getString("country");
				// 根据type来生成不同的值
				if (place_type.equals("city")) {
					m.setCity(name);
					m.setProvince(full_name.split(",")[1].trim());
					m.setCountry(country);
				} else if (place_type.equals("admin")) {
					m.setProvince(name);
					m.setCountry(country);
				} else {
					m.setCountry(country);
				}
				// 存储边界信息
				Document bounding_box = (Document) tmp.get("bounding_box");
				if (bounding_box != null) {
					m.setPlaceBoundingType(bounding_box.getString("type"));
					if (m.getPlaceCoordinates() == null) {
						m.setPlaceCoordinates(new ArrayList<Double[]>());
					}
					m.getPlaceCoordinates().addAll(
							(ArrayList<Double[]>) bounding_box
									.get("coordinates"));
				}
			}

			// 实体
			tmp = (Document) cur.get("entities");
			if (tmp != null) {
				// hashtags
				ArrayList<Document> hashtags = (ArrayList<Document>) tmp
						.get("hashtags");
				if (hashtags != null) {
					for (int i = 0; i < hashtags.size(); i++) {
						Document ht = (Document) hashtags.get(i);
						if (m.getHashtags() == null) {
							m.setHashtags(new ArrayList<String>());
						}
						m.getHashtags().add(ht.getString("text"));
					}
				}
				// media
				ArrayList<Document> medias = (ArrayList<Document>) tmp
						.get("media");
				if (medias != null) {
					for (int i = 0; i < medias.size(); i++) {
						Document media = (Document) medias.get(i);
						if (m.getMedia_urls() == null) {
							m.setMedia_urls(new ArrayList<String>());
						}
						if (m.getMedia_type() == null) {
							m.setMedia_type(new ArrayList<String>());
						}
						if (!m.getMedia_urls().contains(
								media.getString("media_url"))) {
							m.getMedia_urls().add(media.getString("media_url"));
							m.getMedia_type().add(media.getString("type"));
						}
					}
				}

			}
			// 扩展实体
			tmp = (Document) cur.get("extended_entities");
			if (tmp != null) {
				// hashtags
				ArrayList<Document> hashtags = (ArrayList<Document>) tmp
						.get("hashtags");
				if (hashtags != null) {
					for (int i = 0; i < hashtags.size(); i++) {
						Document ht = (Document) hashtags.get(i);
						if (m.getHashtags() == null) {
							m.setHashtags(new ArrayList<String>());
						}
						m.getHashtags().add(ht.getString("text"));
					}
				}
				// media
				ArrayList<Document> medias = (ArrayList<Document>) tmp
						.get("media");
				if (medias != null) {
					for (int i = 0; i < medias.size(); i++) {
						Document media = (Document) medias.get(i);
						if (m.getMedia_urls() == null) {
							m.setMedia_urls(new ArrayList<String>());
						}
						if (m.getMedia_type() == null) {
							m.setMedia_type(new ArrayList<String>());
						}
						// 获得媒体类型
						String mediaType = media.getString("type");
						// 如果和以前的重复则跳过
						if (m.getMedia_urls().contains(
								media.getString("media_url"))
								&& mediaType.equals("photo")) {
							continue;
						}
						m.getMedia_type().add(mediaType);
						if (mediaType.equals("photo")) {
							m.getMedia_urls().add(media.getString("media_url"));
						} else {
							Document video_info = (Document) media
									.get("video_info");
							if (video_info != null) {
								// 提取视频连接
								ArrayList<Document> variants = (ArrayList<Document>) video_info
										.get("variants");
								int maxBitrateIndex = 0;
								int maxBitrate = 0;
								for (int j = 0; j < variants.size(); j++) {
									// 获取bitrate最高的并且格式为mp4的视频的url
									String content_type = variants.get(j)
											.getString("content_type");
									// System.out.println("content_type"
									// + content_type);
									if (content_type.equals("video/mp4")) {
										int bitrate = variants.get(j)
												.getInteger("bitrate");
										if (bitrate > maxBitrate) {
											maxBitrateIndex = j;
										}
									}
								}
								// 将bitrate最大的MP4格式了url保存
								m.getMedia_urls().add(
										variants.get(maxBitrateIndex)
												.getString("url"));

							}
						}
					}
				}
			}
			// 语言
			m.setLang(cur.getString("lang"));
			m.setMessageFrom("twitter");
			// System.out.println(m);
			res.add(m);
		}
		return res;
	}

	@Override
	public void saveSQLMsg_Full(List<FullMessage> sqLmessages) {
		String sqlString = "INSERT INTO savedfullmessages (raw_id_str, creat_at, timestamp_ms, text, media_types, media_urls, country, province, city, geo_type, geo_coordinates, hashtags, replay_to, lang,mongoid,placetype,placename,placefullname,placeboundingtype,placecoordinates,messageFrom) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?)";
		Connection conn = null;
		try {
			conn = mySQL_full.getConnection();
			for (int i = 0; i < sqLmessages.size(); i++) {
				FullMessage msg = sqLmessages.get(i);
				PreparedStatement ps = conn.prepareStatement(sqlString);
				ps.setString(1, msg.getRaw_id_str());
				ps.setString(2, sdf.format(msg.getCreat_at()));
				ps.setLong(3, msg.getTimestamp_ms());
				ps.setString(4, msg.getText());
				ps.setString(5, msg.getMedia_type().toString());
				ps.setString(6, msg.getMedia_urls().toString());
				ps.setString(7, msg.getCountry());
				ps.setString(8, msg.getProvince());
				ps.setString(9, msg.getCity());
				ps.setString(10, msg.getGeo_type());
				ps.setString(11, msg.getGeo_coordinates().toString());
				ps.setString(12, msg.getHashtags().toString());
				ps.setString(13, msg.getReplay_to());
				ps.setString(14, msg.getLang());
				ps.setString(15, msg.getMongoId().toString());
				ps.setString(16, msg.getPlaceType());
				ps.setString(17, msg.getPlaceName());
				ps.setString(18, msg.getPlaceFullName());
				ps.setString(19, msg.getPlaceBoundingType());
				ps.setString(20, msg.getPlaceCoordinates().toString());
				ps.setString(21, msg.getMessageFrom());
				// System.out.println(msg.getPlaceFullName());
				try {
					ps.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
					updateState_import(msg.getMongoId(), true);
				}
				updateState_import(msg.getMongoId(), true);
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	@Override
	public void updateState_import(ObjectId _id, boolean isImport) {
		BasicDBObject rule = new BasicDBObject();
		rule.append("_id", _id);
		BasicDBObject value = new BasicDBObject();
		value.append("$set", new BasicDBObject("import", true));
		UpdateResult updateResult = mongoDB.getCollection().updateOne(rule,
				value);
		// System.out.println("更新原始数据库的值:找到了" + updateResult.getMatchedCount()
		// + "修改了：" + updateResult.getModifiedCount());
	}

	@Override
	public void deleteRawMessage(ObjectId _id) {
		BasicDBObject rule = new BasicDBObject();
		rule.append("_id", _id);
		mongoDB.getCollection().deleteOne(rule);
		// System.out.println("删除重复的值");
	}

	@Override
	public ArrayList<MarkMessage> getNewMarkMessage(int limit,
			String queryOption) {
		// and media_type !='[]'
		String sqlString = "SELECT * FROM savedfullmessages where ismessageimport=false "
				+ queryOption + " ORDER BY rand() LIMIT " + limit + ";";
		System.out.println(sqlString);
		Connection conn = null;

		try {
			conn = mySQL_full.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ArrayList<MarkMessage> res = new ArrayList<MarkMessage>();
			MarkMessage message;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				message = new MarkMessage();
				message.setFull_msg_id(rs.getLong("num_id"));
				message.setText(rs.getString("text"));
				message.setMedia_types(getListFromString(rs
						.getString("media_types")));
				message.setMedia_urls(getListFromString(rs
						.getString("media_urls")));
				message.setLang(rs.getString("lang"));
				message.setMessage_from(rs.getString("messageFrom"));
				System.out.println(message);
				res.add(message);
			}
			return res;
		} catch (SQLException e) {
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * 将ArrayList的toString()之后生成的字符串转化为ArrayList
	 * 
	 * @param string
	 * @return
	 */
	private List<String> getListFromString(String listString) {
		// 去掉首尾的中括号
		String[] temp = listString.substring(1, listString.length() - 1).split(
				", ");
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < temp.length; i++) {
			res.add(temp[i]);
		}

		return res;
	}

	@Override
	public void saveSQLMsg_Mark(List<MarkMessage> markmessages) {
		Connection conn = null;
		String sqlString = "INSERT INTO mark_messages(full_msg_id, text, media_types, media_urls, media_urls_local,lang,message_from) VALUES (?, ?, ?, ?,?,?,?);";

		try {
			conn = mySQL_mark.getConnection();
			for (int i = 0; i < markmessages.size(); i++) {
				MarkMessage msg = markmessages.get(i);
				PreparedStatement ps = conn.prepareStatement(sqlString);
				ps.setLong(1, msg.getFull_msg_id());
				ps.setString(2, msg.getText());
				ps.setString(3, msg.getMedia_types().toString());
				ps.setString(4, msg.getMedia_urls().toString());
				ps.setString(5, msg.getMedia_urls_local().toString());
				ps.setString(6, msg.getLang());
				ps.setString(7, msg.getMessage_from());
				try {
					ps.executeUpdate();
					updateState_Full(msg.getFull_msg_id(), "true");
				} catch (Exception e) {
					e.printStackTrace();
				}
				ps.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public void updateState_Full(long fullMsgID, String state) {
		Connection conn = null;
		String sqlString = "UPDATE savedfullmessages SET ismessageimport='"
				+ state + "' WHERE num_id=" + fullMsgID;
		try {
			conn = mySQL_full.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

}
