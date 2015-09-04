package com.zhongli.MessageTransferTool.Dao;

import java.util.List;

import org.bson.types.ObjectId;

import com.zhongli.MessageTransferTool.Model.FullMessage;

/**
 * 对数据库的操作接口类
 * 
 * @author zhonglili
 *
 */
public interface MsgDAO {

	/*******************将数据从非结构化数据库中存储到结构化数据库中的方法*****************/
	// 从NoSQL数据库中读取没存到数据库的消息并组装成格式化的对象
	public List<FullMessage> getNewRawMsg(int limit);

	// 相关系型数据库中存储结构化后的数据
	public void saveSQLMsg(List<FullMessage> sqLmessages);

	// 修改状态为已标记
	public void updateState_import(ObjectId _id, boolean isImport);

	// 删除原始数据库中的数据
	public void deleteRawMessage(ObjectId _id);
	/*******************将数据从*****************/


}
