package com.zhongli.MessageTransferTool.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import static com.mongodb.client.model.Filters.*;

/**
 * 程序入口
 * 
 * @author John
 *
 */
public class testMongoDB {
	public static void main(String[] args) {
		testMongoDB tm = new testMongoDB();
		tm.test();
	}

	/**
	 * test
	 */
	private void test() {
		// 获取链接
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		// 获取数据库
		MongoDatabase database = mongoClient.getDatabase("happycityproject");
		// 进入某个文档集
		MongoCollection<Document> collection = database.getCollection("rawTwitters");

		/********************** 数据插入 ****************************/
		// // 创建新文档
		// Document doc = new Document("name", "MongoDB")
		// .append("type", "database").append("count", 1)
		// .append("info", new Document("x", 203).append("y", 102));
		// // 将文档插入文档集合
		// collection.insertOne(doc);
		//
		// // 创建一个包含多个文档的列表
		// List<Document> documents = new ArrayList<Document>();
		// for (int i = 0; i < 100; i++) {
		// documents.add(new Document("i", i));
		// }
		// // 向文档中插入列表
		// collection.insertMany(documents);

		/***************** 数据读取 ****************************************/
		// // 显示集合中的文档的数量
		 //System.out.println(collection.count());
		//
		// // 查询集合中的第一个文档
//		Document myDoc = collection.find().first();
//		 System.out.println(myDoc.toJson());
		//获取在渥太华发的消息的数量
		BasicDBObject rule=new BasicDBObject();
//		rule.append("place.name", "Canada");
//		rule.append("geo", new BasicDBObject("$ne",null));
		rule.append("extended_entities", new BasicDBObject("$ne",null));
//		rule.append("extended_entities.media.type", new BasicDBObject("$ne","photo"));
//		rule.append("extended_entities.media.type", new BasicDBObject("$ne","animated_gif"));
		rule.append("extended_entities.media.type", new BasicDBObject("$eq","video"));
		rule.append("entities", new BasicDBObject("$ne",null));
		
//		rule.append("place.place_type", "country");
//		rule.append("place.country_code", "US");
		Document myDoc = collection.find(rule).first(); 
//		 //直接读取主属性
//		 String msg_id=myDoc.getString("id_str");
//		 //读取对象中的属性
//		 String placeName=((Document)myDoc.get("place")).getString("name");
//		 //根据服务器所在地算出的时间
//		 Date created_at=Date.from(Instant.ofEpochMilli(Long.parseLong(myDoc.getString("timestamp_ms"))));
//		 System.out.println(created_at);
//		 //GMT 时间
//		 SimpleDateFormat sdf= new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy",Locale.US);
//		 System.out.println(myDoc.getString("created_at"));
//		 try {
//			System.out.println(sdf.parse(myDoc.getString("created_at")));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		 System.out.println(myDoc.toJson());
//		 System.out.println(msg_id+"<>"+placeName);
		//
		// //获取集合中的全部文档
		// MongoCursor<Document> cursor = collection.find().iterator();
		// try {
		// while (cursor.hasNext()) {
		// System.out.println(cursor.next().toJson());
		// }
		// } finally {
		// cursor.close();
		// }
//		BasicDBObject exclude=new BasicDBObject();
////		exclude.append("place.place_type", 1);
////		exclude.append("place.name", 1);
//		exclude.append("extended_entities.media.type", 1);
////		 获取全部文档的另一种方法
//		 for (Document cur : collection.find(rule).projection(exclude)) {
////		 System.out.println(cur.toJson());
//			 System.out.println(cur.toJson());
//		 }

		// // 根据条件获取某分文档 eq:==
		// Document myDoc = collection.find(eq("i", 71)).first();
		// System.out.println(myDoc.toJson());

		// 通过查询语句一次性获取多个数据
		// Block<Document> printBlock = new Block<Document>() {
		// @Override
		// public void apply(final Document document) {
		// System.out.println(document.toJson());
		// }
		// };
		// 获得所有大于50的
		// collection.find(gt("i", 50)).forEach(printBlock);
		// 大于50 小于 100
		// collection.find(and(gt("i", 50), lte("i", 100))).forEach(printBlock);

		// 对输出文档进行排序,-1为递减，1为递增
		// 官方文档的例子有误：http://mongodb.github.io/mongo-java-driver/3.0/driver/getting-started/quick-tour/#sorting-documents
		// Document myDoc = collection.find(exists("i"))
		// .sort(new BasicDBObject("i", -1)).first();
		// System.out.println(myDoc.toJson());

		// 选择性输出结果中的元素，0为不显示，1为显示
		// 官方文档中的例子又不能用：http://mongodb.github.io/mongo-java-driver/3.0/driver/getting-started/quick-tour/#projecting-fields
		// BasicDBObject exclude = new BasicDBObject();
		// exclude.append("_id", 0);
		// // exclude.append("count", 0);
		// exclude.append("name", 1);
		// exclude.append("info", 1);
		// Document myDoc = collection.find().projection(exclude).first();
		// System.out.println(myDoc.toJson());

		/************************* 修改数据库中数据 *************************************/

		// 修改时的参数：
		// $inc 对指定的元素加
		// $mul 乘
		// $rename 修改元素名称
		// $setOnInsert 如果以前没有这个元素则增加这个元素，否则不作任何更改
		// $set 修改制定元素的值
		// $unset 移除特定的元素
		// $min 如果原始数据更大则不修改，否则修改为指定的值
		// $max 与$min相反
		// $currentDate 修改为目前的时间

		// //修改第一个符合条件的数据
		// $set 为修改
		// collection.updateOne(eq("i", 10), new Document("$set", new
		// Document("i", 110)));
		// // 获取全部文档,可以看到以前10的地方变成了110
		// for (Document cur : collection.find()) {
		// System.out.println(cur.toJson());
		// }

		// 批量修改数据并且返回修改的结果，讲所有小于100的结果都加100
		// UpdateResult updateResult = collection.updateMany(lt("i", 100),
		// new Document("$inc", new Document("i", 100)));
		// // 显示发生变化的行数
		// System.out.println(updateResult.getModifiedCount());
		// // 获取全部文档,可以看到除了刚才修改的110其他的全为了100
		// for (Document cur : collection.find()) {
		// System.out.println(cur.toJson());
		// }

		/************************** 删除数据 *****************************/
		// 删除第一个符合条件的数据
		// collection.deleteOne(eq("i", 110));
		// // 获取全部文档,可以看到没有110这个数了
		// for (Document cur : collection.find()) {
		// System.out.println(cur.toJson());
		// }

		// 删除所有符合条件的数据，并且返回结果
		// DeleteResult deleteResult = collection.deleteMany(gte("i", 100));
		// // 输出删除的行数
		// System.out.println(deleteResult.getDeletedCount());
		// // 获取全部文档,所有i>=100的数据都没了
		// for (Document cur : collection.find()) {
		// System.out.println(cur.toJson());
		// }
		/*************************** 程序块，一次执行多条语句 ********************************/
		// 按照语句先后顺序执行
		// collection.bulkWrite(Arrays.asList(new InsertOneModel<>(new Document(
		// "_id", 4)), new InsertOneModel<>(new Document("_id", 5)),
		// new InsertOneModel<>(new Document("_id", 6)),
		// new UpdateOneModel<>(new Document("_id", 1), new Document(
		// "$set", new Document("x", 2))), new DeleteOneModel<>(
		// new Document("_id", 2)),
		// new ReplaceOneModel<>(new Document("_id", 3), new Document(
		// "_id", 3).append("x", 4))));
		// // 获取全部文档
		// for (Document cur : collection.find()) {
		// System.out.println(cur.toJson());
		// }

		// 不按照语句先后顺序执行
		// collection.bulkWrite(Arrays.asList(new InsertOneModel<>(new Document(
		// "_id", 4)), new InsertOneModel<>(new Document("_id", 5)),
		// new InsertOneModel<>(new Document("_id", 6)),
		// new UpdateOneModel<>(new Document("_id", 1), new Document(
		// "$set", new Document("x", 2))), new DeleteOneModel<>(
		// new Document("_id", 2)),
		// new ReplaceOneModel<>(new Document("_id", 3), new Document(
		// "_id", 3).append("x", 4))), new BulkWriteOptions()
		// .ordered(false));
		// 获取全部文档
		// for (Document cur : collection.find()) {
		// System.out.println(cur.toJson());
		// }
		
		
		// 关闭数据库连接
		mongoClient.close();

	}

}
