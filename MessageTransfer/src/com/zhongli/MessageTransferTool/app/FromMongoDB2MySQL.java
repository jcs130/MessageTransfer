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
		while (true) {
			List<FullMessage> li = msgDao.getNewRawMsg(10000);
			int size = li.size();
			if (size != 0) {
				msgDao.saveSQLMsg(li);
			} else {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Transfer Done");
	}

}
