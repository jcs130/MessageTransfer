package com.zhongli.MessageTransferTool.app;

import java.util.List;

import com.zhongli.MessageTransferTool.Dao.impl.MsgDAOImpl;
import com.zhongli.MessageTransferTool.Model.SQLmessage;

public class TestMain {
	public static void main(String[] args) {
		TestMain tm = new TestMain();
		tm.testDo();
	}

	private void testDo() {
		MsgDAOImpl msgDao = new MsgDAOImpl();
		List<SQLmessage> li=msgDao.getNewRawMsg(10000);
		msgDao.saveSQLMsg(li);
	}

}
