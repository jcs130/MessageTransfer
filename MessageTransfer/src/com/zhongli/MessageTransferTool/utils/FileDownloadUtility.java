package com.zhongli.MessageTransferTool.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author zhonglili
 *
 */
public class FileDownloadUtility {
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Download the file from http url
	 * 
	 * @param fileURL
	 * @param saveDir
	 * @param myFilename
	 * @return
	 * @throws IOException
	 */
	public static String downloadFile(String fileURL, String saveDir,
			String myFilename) throws IOException {
		URL url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String fileName = "";
			String disposition = httpConn.getHeaderField("Content-Disposition");
			String contentType = httpConn.getContentType();
			int contentLength = httpConn.getContentLength();

			if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10,
							disposition.length() - 1);
				}
			} else {
				// extracts file name from URL
				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
						fileURL.length());
			}
			// If given a filename
			if (!"".equals(myFilename)) {
				String[] filenamesubs = fileName.split(".");
				String fileType = filenamesubs[filenamesubs.length - 1];
				fileName = myFilename + "." + fileType;
			}

			System.out.println("Content-Type = " + contentType);
			System.out.println("Content-Disposition = " + disposition);
			System.out.println("Content-Length = " + contentLength);
			System.out.println("fileName = " + fileName);

			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			String saveFilePath = saveDir + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File downloaded");
			httpConn.disconnect();
			return fileName;
		} else {
			System.out
					.println("No file to download. Server replied HTTP code: "
							+ responseCode);
			httpConn.disconnect();
			return "Error "+responseCode;
		}

	}
}