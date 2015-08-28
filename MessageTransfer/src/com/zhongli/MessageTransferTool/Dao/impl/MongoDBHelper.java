package com.zhongli.MessageTransferTool.Dao.impl;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBHelper {
	MongoCollection<Document> collection;
	MongoClient mongoClient;
	MongoDatabase database;

	public MongoDBHelper(String ip, int port, String dbName,
			String collectionName) {
		// 获取链接
		mongoClient = new MongoClient(ip, port);
		// 获取数据库
		database = mongoClient.getDatabase(dbName);
		// 进入某个文档集
		collection = database.getCollection(collectionName);
	}

	/**
	 * 默认构造器，属性为默认 
	 * mongoClient = new MongoClient("localhost", 27017); 
	 * database =mongoClient.getDatabase("happycityproject"); 
	 * collection =database.getCollection("rawTwitters");
	 */
	public MongoDBHelper() {
		mongoClient = new MongoClient("localhost", 27017);
		database = mongoClient.getDatabase("happycityproject");
		collection = database.getCollection("rawTwitters");
	}

	public MongoCollection<Document> getCollection() {
		return collection;
	}
}
