package com.zhongli.MessageTransferTool.Model;

import java.util.List;

/**
 * 用于标注的数据的数据结构
 * 
 * @author zhonglili
 *
 */
public class MarkMessage {
	// 标注数据库中的编号
	private long msg_id;
	// 完整信息的编号
	private long full_msg_id;
	// 文字消息
	private String text;
	// 媒体类型
	private List<String> media_types;
	// 媒体url
	private List<String> media_urls;
	// 媒体本地url
	private List<String> media_urls_local;
	// 标注次数
	private int mark_times;
	// 文字情感
	private String emotion_text;
	// 文字情感正确率
	private double emotion_text_confidence;
	// 图片情感
	private List<String> emotion_medias;
	// 图片情感的正确率
	private List<Double> emotion_medias_confidence;
	//语言
	private String lang;
	public long getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(long msg_id) {
		this.msg_id = msg_id;
	}
	public long getFull_msg_id() {
		return full_msg_id;
	}
	public void setFull_msg_id(long full_msg_id) {
		this.full_msg_id = full_msg_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<String> getMedia_types() {
		return media_types;
	}
	public void setMedia_types(List<String> media_types) {
		this.media_types = media_types;
	}
	public List<String> getMedia_urls() {
		return media_urls;
	}
	public void setMedia_urls(List<String> media_urls) {
		this.media_urls = media_urls;
	}
	public List<String> getMedia_urls_local() {
		return media_urls_local;
	}
	public void setMedia_urls_local(List<String> media_urls_local) {
		this.media_urls_local = media_urls_local;
	}
	public int getMark_times() {
		return mark_times;
	}
	public void setMark_times(int mark_times) {
		this.mark_times = mark_times;
	}
	public String getEmotion_text() {
		return emotion_text;
	}
	public void setEmotion_text(String emotion_text) {
		this.emotion_text = emotion_text;
	}
	public double getEmotion_text_confidence() {
		return emotion_text_confidence;
	}
	public void setEmotion_text_confidence(double emotion_text_confidence) {
		this.emotion_text_confidence = emotion_text_confidence;
	}
	public List<String> getEmotion_medias() {
		return emotion_medias;
	}
	public void setEmotion_medias(List<String> emotion_medias) {
		this.emotion_medias = emotion_medias;
	}
	public List<Double> getEmotion_medias_confidence() {
		return emotion_medias_confidence;
	}
	public void setEmotion_medias_confidence(List<Double> emotion_medias_confidence) {
		this.emotion_medias_confidence = emotion_medias_confidence;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	@Override
	public String toString() {
		return "MarkMessage [msg_id=" + msg_id + ", full_msg_id=" + full_msg_id
				+ ", text=" + text + ", media_types=" + media_types
				+ ", media_urls=" + media_urls + ", media_urls_local="
				+ media_urls_local + ", mark_times=" + mark_times
				+ ", emotion_text=" + emotion_text
				+ ", emotion_text_confidence=" + emotion_text_confidence
				+ ", emotion_medias=" + emotion_medias
				+ ", emotion_medias_confidence=" + emotion_medias_confidence
				+ ", lang=" + lang + "]";
	}
	
	

}
