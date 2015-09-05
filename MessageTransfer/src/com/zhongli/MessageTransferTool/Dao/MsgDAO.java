package com.zhongli.MessageTransferTool.Dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.zhongli.MessageTransferTool.Model.FullMessage;
import com.zhongli.MessageTransferTool.Model.MarkMessage;

/**
 * 对数据库的操作接口类
 * 
 * @author zhonglili
 *
 */
public interface MsgDAO {

	/******************* 将数据从非结构化数据库中存储到结构化数据库中的方法 *****************/
	/**
	 * 从NoSQL数据库中读取没存到数据库的消息并组装成格式化的对象
	 * 
	 * @param limit
	 * @return
	 */
	public List<FullMessage> getNewRawMsg(int limit);

	/**
	 * 向关系型数据库中存储结构化后的数据
	 * 
	 * @param sqLmessages
	 */
	public void saveSQLMsg_Full(List<FullMessage> sqLmessages);

	/**
	 * 修改状态为已标记
	 * 
	 * @param _id
	 * @param isImport
	 */
	public void updateState_import(ObjectId _id, boolean isImport);

	/**
	 * 删除原始数据库中的数据
	 * 
	 * @param _id
	 */
	public void deleteRawMessage(ObjectId _id);

	/******************* 将数据从完整信息表中提取到标注信息表中的方法 *****************/

	/**
	 * 从完整的数据库中获取精简后的消息消息
	 * 
	 * @param limit
	 * @return
	 */
	public ArrayList<MarkMessage> getNewMarkMessage(int limit, String mediaOption);

	/**
	 * 将精简的待标记的信息存入数据库
	 * 
	 * @param markmessages
	 */
	public void saveSQLMsg_Mark(List<MarkMessage> markmessages);


	/**
	 * 更新完全消息的数据库状态
	 * @param fullMsgID
	 * @param state 如果引入为true，如果已被删除则为delete
	 */
	public void updateState_Full(long fullMsgID, String state);

}
