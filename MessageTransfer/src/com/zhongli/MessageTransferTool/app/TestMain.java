package com.zhongli.MessageTransferTool.app;

import com.zhongli.MessageTransferTool.Dao.impl.MsgDAOImpl;

public class TestMain {
	public static void main(String[] args) {
		TestMain tm = new TestMain();
		tm.testDo();
	}

	private void testDo() {
		MsgDAOImpl msgDao = new MsgDAOImpl();
		msgDao.getNewRawMsg(2);
	}

}
