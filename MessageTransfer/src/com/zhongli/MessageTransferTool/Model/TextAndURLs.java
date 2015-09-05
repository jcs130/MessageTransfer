package com.zhongli.MessageTransferTool.Model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TextAndURLs {
	private String text = "";
	private List<URL> urls = new ArrayList<URL>();
	private boolean hasMedia = false;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<URL> getUrls() {
		return urls;
	}

	public void setUrls(List<URL> urls) {
		this.urls = urls;
	}

	public boolean isHasMedia() {
		return hasMedia;
	}

	public void setHasMedia(boolean hasMedia) {
		this.hasMedia = hasMedia;
	}

	@Override
	public String toString() {
		return "TextAndURLs [text=" + text + ", urls=" + urls + ", hasMedia="
				+ hasMedia + "]";
	}

}
