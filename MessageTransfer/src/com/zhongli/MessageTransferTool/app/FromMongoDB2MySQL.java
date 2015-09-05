package com.zhongli.MessageTransferTool.app;

import java.util.List;

import com.zhongli.MessageTransferTool.Dao.impl.MsgDAOImpl;
import com.zhongli.MessageTransferTool.Model.FullMessage;


public class FromMongoDB2MySQL {
	public static void main(String[] args) {
		FromMongoDB2MySQL tm = new FromMongoDB2MySQL();
		tm.testDo();
	}

	/**
	 * 格式转换，每次检测1万条数据
	 */
	private void testDo() {
		MsgDAOImpl msgDao = new MsgDAOImpl();
		//1237982
		int skipNum=1237982;
		int limitNum=10000;
		while (true) {
			//根据条件获取（速度慢）
//			List<FullMessage> li = msgDao.getNewRawMsg(10000);
			//通过指针获取
			List<FullMessage> li = msgDao.getNewRawMsg_skip(skipNum,limitNum);
			int size = li.size();
			if (size != 0) {
				msgDao.saveSQLMsg_Full(li);
				skipNum+=size;
			} else {
				break;
			}
			System.out.println("Transfer Done, SkipNum="+skipNum);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		List<FullMessage> li = msgDao.getNewRawMsg(10000);
//		msgDao.saveSQLMsg_Full(li);
		System.out.println("Transfer Done, SkipNum="+skipNum);
	}

}
