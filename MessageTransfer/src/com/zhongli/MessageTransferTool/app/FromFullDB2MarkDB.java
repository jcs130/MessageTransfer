package com.zhongli.MessageTransferTool.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.omg.PortableInterceptor.SUCCESSFUL;

import com.zhongli.MessageTransferTool.Dao.MsgDAO;
import com.zhongli.MessageTransferTool.Dao.impl.MsgDAOImpl;
import com.zhongli.MessageTransferTool.Model.MarkMessage;
import com.zhongli.MessageTransferTool.Model.TextAndURLs;
import com.zhongli.MessageTransferTool.utils.FileDownloadUtility;

/**
 * 将数据从完整的结构化存储的数据库中提取到标注数据库中
 * 
 * @author zhonglili
 *
 */
public class FromFullDB2MarkDB {
	public static void main(String[] args) {
		FromFullDB2MarkDB tm = new FromFullDB2MarkDB();
		tm.testDo();
	}

	/**
	 * 按照一定条件获取数据
	 */
	private void testDo() {
		MsgDAOImpl msgDao = new MsgDAOImpl();
		int limit = 10;
		ArrayList<String> hashtags = new ArrayList<String>();
		// hashtags.add("elxn");
		ArrayList<String> keywords = new ArrayList<String>();
		// keywords.add("elxn");
		String queryOption = " and lang='en' and media_types ='[photo, video]' "
				+ addKeyWordRule(keywords, true)
				+ addHashTagsRule(hashtags, true);
		String baseDir = "/Users/zhonglili/Documents/medias/";
		// 从完整数据库中按一定条件读取数据
		// 将获得的数据进行进一步的筛选，检测图片是否存在等等
		ArrayList<MarkMessage> filiteredMarkMessages = checkMarkMessages(
				msgDao.getNewMarkMessage(limit, queryOption), msgDao, baseDir);
		// 将筛选后的数据存入标记数据库
		msgDao.saveSQLMsg_Mark(filiteredMarkMessages);

	}

	/**
	 * 将获得的数据进行进一步的筛选，检测图片是否存在等等
	 * 
	 * @param newMarkMessage
	 * @return
	 */
	private ArrayList<MarkMessage> checkMarkMessages(
			ArrayList<MarkMessage> newMarkMessages, MsgDAO msgDao,
			String baseDir) {
		ArrayList<MarkMessage> res = new ArrayList<MarkMessage>();
		MarkMessage temp;
		for (int i = 0; i < newMarkMessages.size(); i++) {
			temp = newMarkMessages.get(i);
			// 解析文本消息
			TextAndURLs tau = getTextAndURLs(temp);
			System.out.println(tau);
			// 首先检测除去连接之后是否还有别的信息
			if ("".equals(tau.getText())) {
				// 如果只有链接没有别的消息则忽略
				System.out.println(tau.getText() + "No Text Messages, drop");
			} else {
				// 如果有媒体则检测最后一个链接是否存在
				if (tau.isHasMedia()) {
					if (isURLAvailable(tau.getUrls().get(
							tau.getUrls().size() - 1))) {
						// 修改消息，将最后的链接去掉
						String newText = tau.getText();
						for (int j = 0; j < tau.getUrls().size() - 1; j++) {
							newText += " " + tau.getUrls().get(j).toString();
						}
						temp.setText(newText);
						// 将媒体文件存放到本地
						if (saveMedia2Disk(temp, baseDir)) {
							res.add(temp);
						} else {
							// 更新数据库中的消息状态为del
							msgDao.updateState_Full(temp.getFull_msg_id(),
									"del");
						}

					} else {
						// 更新数据库中的消息状态为del
						msgDao.updateState_Full(temp.getFull_msg_id(), "del");
					}
				} else {
					// 若无媒体则不检测是否存在
					res.add(temp);
				}
			}
		}
		return res;
	}

	/**
	 * 分离文本消息与链接
	 * 
	 * @param temp
	 * @return
	 */
	private TextAndURLs getTextAndURLs(MarkMessage temp) {
		TextAndURLs res = new TextAndURLs();
		String text = temp.getText();
		String resText = "";
		ArrayList<URL> urls = new ArrayList<URL>();
		// 设置是否有媒体
		if (temp.getMedia_types().size() != 0) {
			res.setHasMedia(true);
		} else {
			res.setHasMedia(false);
		}
		// 将原文按照空格分成多个部分，再分别用URL类尝试转换，url中不允许有空格
		String[] subTexts = text.split("\\s+");
		for (int i = 0; i < subTexts.length; i++) {
			try {
				URL url = new URL(subTexts[i]);
				urls.add(url);
			} catch (MalformedURLException e) {
				// e.printStackTrace();
				resText += subTexts[i] + " ";
			}
		}
		res.setText(resText.trim());
		res.setUrls(urls);
		return res;
	}

	/**
	 * 将网络图片下载到本地并且添加本地存储路径（文件名）
	 * 
	 * @param temp
	 */
	private boolean saveMedia2Disk(MarkMessage temp, String basicDir) {
		List<String> media_types = temp.getMedia_types();
		List<String> media_urls = temp.getMedia_urls();
		String myFileName = "";
		boolean downloadSuccess = false;
		for (int i = 0; i < media_types.size(); i++) {
			String saveDir = "" + basicDir;
			// 根据不同的类型存储到不同的文件夹
			if ("photo".equals(media_types.get(i))) {
				saveDir += "/photos";
			} else if ("video".equals(media_types.get(i))) {
				saveDir += "/videos";
			} else if (" animated_gif".equals(media_types.get(i))) {
				saveDir += "/smallvideos";
			} else {
				saveDir += "/others";
			}

			String mediaURL = media_urls.get(i);
			System.out.println(mediaURL);
			myFileName = "" + temp.getFull_msg_id() + "_" + i;
			try {
				String savedFileName = FileDownloadUtility.downloadFile(
						mediaURL, saveDir, myFileName);
				if (!("Error").equals(savedFileName.split(" ")[0])) {
					temp.getMedia_urls_local().add(
							saveDir + "/" + savedFileName);
					downloadSuccess = true;
				} else {
					System.out.println(savedFileName);
					downloadSuccess = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return downloadSuccess;
	}

	/**
	 * 检测消息是否还存在
	 * 
	 * @param testURL
	 * @return
	 */
	private boolean isURLAvailable(URL testURL) {
		InputStream in;
		try {
			in = testURL.openStream();
			System.out.println(testURL + " 可以打开");
			in.close();
		} catch (Exception e1) {
			System.out.println(testURL + " 连接打不开!");
			return false;
		}

		return true;
	}

	/**
	 * 向查询语句添加关键词
	 * 
	 * @param keywords
	 * @param isAnd
	 * @return
	 */
	private String addKeyWordRule(List<String> keywords, boolean isAnd) {
		String res = "";
		for (int i = 0; i < keywords.size(); i++) {
			if (i == 0) {
				res += "and ( ";
			}
			res += "text LIKE '%" + keywords.get(i) + "%'";
			if (i != keywords.size() - 1) {
				if (isAnd) {
					res += " and ";
				} else {
					res += " or ";
				}
			}
			if (i == keywords.size() - 1) {
				res += ")";
			}
		}

		return res;
	}

	/**
	 * 向查询语句添加HashTags
	 * 
	 * @param hashtags
	 * @param isAnd
	 * @return
	 */
	private String addHashTagsRule(List<String> hashtags, boolean isAnd) {
		String res = "";
		for (int i = 0; i < hashtags.size(); i++) {
			if (i == 0) {
				res += "and ( ";
			}
			res += "hashtags LIKE '%" + hashtags.get(i) + "%'";
			if (i != hashtags.size() - 1) {
				if (isAnd) {
					res += " and ";
				} else {
					res += " or ";
				}
			}
			if (i == hashtags.size() - 1) {
				res += ")";
			}
		}

		return res;
	}

}
