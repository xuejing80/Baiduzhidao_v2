package com.dyy.njupt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

public class UserDetails {

	public static final String LINE = System.getProperty("line.separator");
	static List<String> tableList = new ArrayList<String>();
	static HashSet<String> hs = new HashSet<String>(tableList);

	public static void main(String[] args) throws IOException {
		adduserinfo("wxz178", "C://Users//fulijiya//Desktop//output.txt");
		// 毕达哥拉斯wxz178
	}

	public static void userInfo(String userid, Writer writer) throws IOException {
			String url = "https://zhidao.baidu.com/ihome/usercard/index?un=" + userid;
			UserDetails userdetails = new UserDetails();
			String response = userdetails.getResponse(url);
			writer.write("<user>" + LINE);
			// 问题所在URL
			writer.write("<url>" + url + "</url>" + LINE);
			// 当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			writer.write("<time>" + df.format(new Date()) + "</time>" + LINE);// new
			// 输出用户id
			writer.write("<id>" + userid + "</id>" + LINE);
			// 经验值
			int start_experience = response.indexOf("total-score");
			start_experience = response.indexOf(">", start_experience);
			int end_experience = response.indexOf("<", start_experience);
			String experience = response.substring(start_experience + 1, end_experience);
			writer.write("<experience>" + experience + "</experience>" + LINE);

			// 财富值
			int start_treasure = response.indexOf("total-wealth");
			start_treasure = response.indexOf(">", start_treasure);
			int end_treasure = response.indexOf("<", start_treasure);
			String treasure = response.substring(start_treasure + 1, end_treasure);
			writer.write("<treasure>" + treasure + "</treasure>" + LINE);
			// // 采纳率
			int start_accept = response.indexOf("total-wealth");
			start_accept = response.indexOf("</span>", start_accept);
			start_accept = response.indexOf("</td><td>", start_accept);
			int end_accept = response.indexOf("tr", start_treasure);
			String details = response.substring(start_accept, end_accept);
			String detail = details.substring(0, details.length() - 2).replaceAll("</td>", "");
			// a数组存放下面所有数据
			String a[] = detail.split("<td>");
			// 采纳率
			if (!a[1].equals("")) {
				writer.write("<accept>" + a[1] + "</accept>" + LINE);
			} else {
				writer.write("<accept>null</accept>" + LINE);
			}
			// 提问数
			writer.write("<question_num>" + a[2] + "</question_num>" + LINE);
			// 回答数
			writer.write("<answer_num>" + a[3] + "</answer_num>" + LINE);
			// 精彩回答
			writer.write("<excellent>" + a[4] + "</excellent>" + LINE);
			// // 赞同数
			writer.write("<goods>" + a[5] + "</goods>" + LINE);
			// 关注的分类
			int start_attention_cates = response.indexOf("category");
			start_attention_cates = response.indexOf("f-gray", start_attention_cates);
			start_attention_cates = response.indexOf(">", start_attention_cates);
			int end_attention_cates = response.indexOf("<", start_attention_cates);
			String attention_cates = response.substring(start_attention_cates + 1, end_attention_cates);
			writer.write("<attention_cates>" + attention_cates + "</attention_cates>" + LINE);
			// 关注的关键词
			int start_attention_words = response.indexOf("keyword-list");
			start_attention_words = response.indexOf("f-gray", start_attention_words);
			start_attention_words = response.indexOf(">", start_attention_words);
			int end_attention_words = response.indexOf("<", start_attention_words);
			String attention_words = response.substring(start_attention_words + 1, end_attention_words);
			writer.write("<attention_words>" + attention_words + "</attention_words>" + LINE);
			// // 参加的活动
			int start_activities = response.indexOf("activity");
			start_activities = response.indexOf("f-gray", start_activities);
			start_activities = response.indexOf(">", start_activities);
			int end_activities = response.indexOf("<", start_activities);
			String activities = response.substring(start_activities + 1, end_activities);
			writer.write("<activities>" + activities + "</activities>" + LINE);
			// 输出用户所在团队
			int start_team = response.indexOf("team-info");
			start_team = response.indexOf("f-gray", start_team);
			start_team = response.indexOf(">", start_team);
			int end_team = response.indexOf("<", start_team);
			String team = response.substring(start_team + 1, end_team);
			writer.write("<teams>" + team + "</teams>" + LINE);
			writer.write("</user>" + LINE);
		}

	public String getResponse(String url) {
		HttpURLConnection httpUrlConnection = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String html = "";
		try {
			httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			httpUrlConnection.setRequestMethod("GET");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpUrlConnection.setUseCaches(true);
		try {
			httpUrlConnection.connect();
			inputStream = httpUrlConnection.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "gbk")); // 选择合�?�的编码方式，如"gbk","gb2312"
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String string;
		try {
			while ((string = bufferedReader.readLine()) != null) {
				html += string;
			}
			bufferedReader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpUrlConnection.disconnect();
		return html;
	}

	public static void adduserinfo(String userid, String outPath) {
		try {
			File f = new File(outPath/* "C://Users//fulijiya//Desktop//userinfo.txt" */);
			if (!f.exists())
				f.createNewFile();
			FileWriter fw = new FileWriter(f, true);
			BufferedWriter bw = new BufferedWriter(fw);
			userInfo(userid, bw);
			bw.flush();
			bw.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

}
