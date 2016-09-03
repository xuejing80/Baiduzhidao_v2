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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Getbaiduzhidao {
	public static final String LINE = System.getProperty("line.separator");
	public static int id = 282520;
	static String accept = "";

	public static void main(String[] args) throws IOException {
		 for (int n = 1121032; n < Long.parseLong("100000000000000000"); n++) {
		 addQA("http://zhidao.baidu.com/question/" + n + ".html");
//		  addQA(" http://zhidao.baidu.com/question/1107908.html");
		 }
	}

	// 类别map
	static Map<Integer, String> classesmap = new HashMap<Integer, String>();

	public static void classesmap(String response) {
		int start_pos_classes = response.indexOf("question-tag-link");
		start_pos_classes = response.indexOf(">", start_pos_classes);
		int end_pos_classes = response.indexOf("<", start_pos_classes);

		for (int i = 0;; i++) {
			String classes = response.substring(start_pos_classes, end_pos_classes).trim();
			classes = classes.substring(1, classes.length()).trim();
			classesmap.put(i, classes);
			start_pos_classes = response.indexOf("question-tag-link", end_pos_classes);
			if (start_pos_classes == -1)
				break;
			start_pos_classes = response.indexOf(">", start_pos_classes);
			end_pos_classes = response.indexOf("<", start_pos_classes);
		}
	}

	// 用户map,放入回答所有信息，放入map
	static Map<Integer, String> answermap = new HashMap<Integer, String>();

	public static void answermap(String response) {
		int start_pos_answerinfo = response.indexOf("qb-accuse-link");
		start_pos_answerinfo = response.indexOf(">", start_pos_answerinfo);
		int end_pos_answerinfo = response.indexOf("分享", start_pos_answerinfo);

		for (int i = 0;; i++) {
			String answerinfo = response.substring(start_pos_answerinfo, end_pos_answerinfo).trim();
			answerinfo = answerinfo.substring(1, answerinfo.length()).trim();
			answermap.put(i, answerinfo);
			start_pos_answerinfo = response.indexOf("qb-accuse-link", end_pos_answerinfo);
			if (start_pos_answerinfo == -1)
				break;
			start_pos_answerinfo = response.indexOf(">", start_pos_answerinfo);
			end_pos_answerinfo = response.indexOf("分享", start_pos_answerinfo);
		}

	}

	public static void getFromBaidu(String url, Writer writer, String outPath) throws IOException {

		Getbaiduzhidao getbaiduzhidao = new Getbaiduzhidao();
		String response = getbaiduzhidao.getResponse(url);
		answermap(response);
		if (response.contains("aContent")) {
			writer.write("<QA>" + LINE);
			// 问题所在URL
			writer.write("<url>" + url + "</url>" + LINE);
			System.out.println(url);
			// 爬虫access当前页面的时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//
			// 设置日期格式
			writer.write("<time>" + df.format(new Date()) + "</time>" + LINE);//
			// 问题的类目
			writer.write("<classes>");
			classesmap(response);
			Set<Integer> keys = classesmap.keySet();
			Iterator<Integer> it = keys.iterator();
			while (it.hasNext()) {
				int key = it.next();
				if (it.hasNext()) {
					writer.write(classesmap.get(key));
					writer.write("/");
				} else {
					writer.write(classesmap.get(key));
				}
			}
			classesmap.clear();
			writer.write("</classes>" + LINE);
			// 问题内容
			writer.write("<question>" + LINE);
			// 爬虫生成的唯一id号，可以使用count函数逐渐累加，便于后面做索引
			id = id + 1;
			System.out.println(id);
			writer.write("<id>" + id + "</id>" + LINE);
			// 问题的标题
			int start_title = response.indexOf("ask-title");
			start_title = response.indexOf(">", start_title);
			int end_title = response.indexOf("<", start_title);
			String title = response.substring(start_title + 1, end_title);
			writer.write("<title>" + title + "</title>" + LINE);
			// 提问人的id
			int start_asker = response.indexOf("qb-ask-uname");
			start_asker = response.indexOf(">", start_asker);
			int end_asker = response.indexOf("<", start_asker);
			String asker = response.substring(start_asker + 1, end_asker);
			if (!"".equals(asker)) {
				writer.write("<user>" + asker + "</user>" + LINE);
				// 收集提问人的信息，输出到文件
				UserDetails.adduserinfo(asker, outPath);
			} else {
				writer.write("<user>null</user>" + LINE);
			}

			// 被浏览次数
			String browsetimes = BrowseTimes.Browsetime(url);
			writer.write("<view>" + browsetimes + "</veiw>" + LINE);
			// 提问发布时间ask-time
			int start_asktime = response.indexOf("ask-time");
			start_asktime = response.indexOf("f-pipe", start_asktime);
			start_asktime = response.indexOf("<", start_asktime);
			start_asktime = response.indexOf(">", start_asktime);
			start_asktime = response.indexOf("<", start_asktime);
			start_asktime = response.indexOf(">", start_asktime);
			int end_asktime = response.indexOf("<", start_asktime);
			String asktime = response.substring(start_asktime + 1, end_asktime);
			writer.write("<ask-time>" + asktime + "</ask-time>" + LINE);
			// 问题内容
			try {
				int start_question = response.indexOf("qContent");
				start_question = response.indexOf(">", start_question);
				int end_question = response.indexOf("</pre>", start_question);
				String question = response.substring(start_question + 1, end_question);
				question = question.replaceAll("<img.*?>", "");
				if (!"".equals(question) && !question.startsWith("<!--STATUS OK-->")) {
					writer.write("<content>" + question + "</content>" + LINE);
				} else {
					writer.write("<content>null</content>" + LINE);
				}
			} catch (Exception e) {
				writer.write("<content>null</content>" + LINE);
			}

			writer.write("</question>" + LINE);

			// ===========================================================================
			// 针对没有网友采纳的页面
			if (!response.contains("网友采纳") && !response.contains("提问者采纳")) {
				// 输出答案
				writer.write("<answers>" + LINE);

				Set<Integer> answerkey = answermap.keySet();
				Iterator<Integer> itanswer = answerkey.iterator();
				while (itanswer.hasNext()) {
					writer.write("<answer>" + LINE);
					int key = itanswer.next();
					String answerdetails = answermap.get(key);
					// 回答人id或者昵称
					int start = answerdetails.indexOf("span");// 判断是否是热心网友
					start = answerdetails.indexOf(">", start);
					int end = answerdetails.indexOf("<", start);
					String rexinwangyou = answerdetails.substring(start + 1, end);
					if (!rexinwangyou.equals("热心网友")) {
						int start_user = answerdetails.indexOf("user-name");
						start_user = answerdetails.indexOf("target=\"_blank\"", start_user);
						start_user = answerdetails.indexOf(">", start_user);
						int end_user = answerdetails.indexOf("<", start_user);
						String user = answerdetails.substring(start_user + 1, end_user);
						writer.write("<user>" + user + "</user>" + LINE);
						// 收集回答者的信息
						UserDetails.adduserinfo(user, outPath);
					} else {
						writer.write("<user>热心网友</user>" + LINE);
					}
					// <time>回答时间</time>
					int start_time = answerdetails.indexOf("ins");
					start_time = answerdetails.indexOf(">", start_time);
					int end_time = answerdetails.indexOf("<", start_time);
					String time = answerdetails.substring(start_time + 1, end_time);
					writer.write("<time>" + time + "</time>" + LINE);
					// <content>回答内容</content>
					int start_content = answerdetails.indexOf("aContent");
					start_content = answerdetails.indexOf("class=\"con\"");
					start_content = answerdetails.indexOf(">", start_content);
					int end_content = answerdetails.indexOf("<", start_content);
					String content = answerdetails.substring(start_content + 1, end_content);
					writer.write("<content>" + content + "</content>" + LINE);
					// <good>好评数</good>
					int start_good = answerdetails.indexOf("data-evaluate");
					start_good = answerdetails.indexOf("=", start_good);
					int end_good = answerdetails.indexOf(">", start_good);
					String good = answerdetails.substring(start_good + 2, end_good).trim();
					good = good.replaceAll("\"", "");
					writer.write("<good>" + good + "</good>" + LINE);
					// <bad>差评数</bad>
					int start_bad = answerdetails.indexOf("data-evaluate", end_good);
					start_bad = answerdetails.indexOf("=", start_bad);
					int end_bad = answerdetails.indexOf(">", start_bad);
					String bad = answerdetails.substring(start_bad + 2, end_bad).trim();
					bad = bad.replaceAll("\"", "");
					writer.write("<bad>" + bad + "</bad>" + LINE);
					writer.write("</answer>" + LINE);
				}
				writer.write("</answers>" + LINE);
				writer.write("</QA>" + LINE);

				// ==========================================================================
				// 针对有网友采纳的页面
			} else {
				// 输出答案
				writer.write("<answers>" + LINE);
				// 第一个回答
				writer.write("<recommend>" + LINE);
				writer.write("<answer>" + LINE);

				// 网友采纳
				if (response.contains("网友采纳")) {
					int start_accept = response.indexOf("qb-accuse-link-recom");// 被采纳者回答信息
					int end_accept = response.indexOf("其他类似问题", start_accept);
					accept = response.substring(start_accept + 1, end_accept);
				} else {
					// 提问者采纳
					int start_accept = response.indexOf("qb-accuse-link-best");// 被采纳者回答信息
					int end_accept = response.indexOf("其他类似问题", start_accept);
					accept = response.substring(start_accept + 1, end_accept);
				}
				int start_acceptid = accept.indexOf("user-name");// 被采纳者的id
				start_acceptid = accept.indexOf(">", start_acceptid);
				int end_acceptid = accept.indexOf("<", start_acceptid);
				String acceptid = accept.substring(start_acceptid + 1, end_acceptid);
				// 回答者的id
				if (!acceptid.equals("")) {
					writer.write("<user>" + acceptid + "</user>" + LINE);
					UserDetails.adduserinfo(acceptid, outPath);
				} else {
					writer.write("<user>热心网友</user>" + LINE);
				}
				// 回答时间
				int start_accepttime = accept.indexOf("ins");// 被采纳者回答时间
				start_accepttime = accept.indexOf(">", start_accepttime);
				int end_accepttime = accept.indexOf("<", start_accepttime);
				String accepttime = accept.substring(start_accepttime + 1, end_accepttime);
				writer.write("<time>" + accepttime + "</time>" + LINE);
				// 回答内容
				int start_acceptcontent = response.indexOf("aContent");
				start_acceptcontent = response.indexOf(">", start_acceptcontent);
				int end_acceptcontent = response.indexOf("</pre>", start_acceptcontent);
				String acceptcontent = response.substring(start_acceptcontent + 1, end_acceptcontent);
				acceptcontent = acceptcontent.replaceAll("<img.*?>", "").replaceAll("<.*?>", "");
				writer.write("<content>" + acceptcontent + "</content>" + LINE);
				// 好评数
				int start_acceptgood = accept.indexOf("data-evaluate");
				start_acceptgood = accept.indexOf("=", start_acceptgood);
				int end_acceptgood = accept.indexOf(">", start_acceptgood);
				String acceptgood = accept.substring(start_acceptgood + 2, end_acceptgood).trim();
				acceptgood = acceptgood.replaceAll("\"", "");
				writer.write("<good>" + acceptgood + "</good>" + LINE);
				// <bad>差评数</bad>
				int start_acceptbad = accept.indexOf("data-evaluate", end_acceptgood);
				start_acceptbad = accept.indexOf("=", start_acceptbad);
				int end_acceptbad = accept.indexOf(">", start_acceptbad);
				String acceptbad = accept.substring(start_acceptbad + 2, end_acceptbad).trim();
				acceptbad = acceptbad.replaceAll("\"", "");
				writer.write("<bad>" + acceptbad + "</bad>" + LINE);
				writer.write("</answer>" + LINE);
				writer.write("</recommend>" + LINE);

				// 第二个回答
				writer.write("<answer>" + LINE);
				String secondanswer = answermap.get(1);
				// 回答人id或者昵称
				int start = secondanswer.indexOf("span");// 判断是否是热心网友
				start = secondanswer.indexOf(">", start);
				int end = secondanswer.indexOf("<", start);
				String rexinwangyou = secondanswer.substring(start + 1, end);
				if (!rexinwangyou.equals("热心网友")) {
					int start_user = secondanswer.indexOf("user-name");
					start_user = secondanswer.indexOf("target=\"_blank\"", start_user);
					start_user = secondanswer.indexOf(">", start_user);
					int end_user = secondanswer.indexOf("<", start_user);
					String user = secondanswer.substring(start_user + 1, end_user);
					writer.write("<user>" + user + "</user>" + LINE);
					// 收集回答者的信息
					UserDetails.adduserinfo(user, outPath);
				} else {
					writer.write("<user>热心网友</user>" + LINE);
				}
				// <time>回答时间</time>
				int start_time = secondanswer.indexOf("ins");
				start_time = secondanswer.indexOf(">", start_time);
				int end_time = secondanswer.indexOf("<", start_time);
				String time = secondanswer.substring(start_time + 1, end_time);
				writer.write("<time>" + time + "</time>" + LINE);
				// <content>回答内容</content>
				int start_content = secondanswer.indexOf("aContent");
				start_content = secondanswer.indexOf("con", start_content);
				start_content = secondanswer.indexOf(">", start_content);
				int end_content = secondanswer.indexOf("</span>", start_content);
				String content = secondanswer.substring(start_content + 1, end_content);
				content = content.replaceAll("<img.*?>", "").replaceAll("<.*?>", "");
				writer.write("<content>" + content + "</content>" + LINE);
				// <good>好评数</good>
				int start_good = secondanswer.indexOf("data-evaluate");
				start_good = secondanswer.indexOf("=", start_good);
				int end_good = secondanswer.indexOf(">", start_good);
				String good = secondanswer.substring(start_good + 2, end_good).trim();
				good = good.replaceAll("\"", "");
				writer.write("<good>" + good + "</good>" + LINE);
				// <bad>差评数</bad>
				int start_bad = secondanswer.indexOf("data-evaluate", end_good);
				start_bad = secondanswer.indexOf("=", start_bad);
				int end_bad = secondanswer.indexOf(">", start_bad);
				String bad = secondanswer.substring(start_bad + 2, end_bad).trim();
				bad = bad.replaceAll("\"", "");
				writer.write("<bad>" + bad + "</bad>" + LINE);
				writer.write("</answer>" + LINE);

				// 下面其他回答
				for (int i = 2; i < answermap.size(); i++) {
					writer.write("<answer>" + LINE);
					// 回答人id或者昵称
					String answerdetails = answermap.get(i);
					start = answerdetails.indexOf("span");// 判断是否是热心网友
					start = answerdetails.indexOf(">", start);
					end = answerdetails.indexOf("<", start);
					rexinwangyou = answerdetails.substring(start + 1, end);
					if (!rexinwangyou.equals("热心网友")) {
						int start_user = answerdetails.indexOf("user-name");
						start_user = answerdetails.indexOf("target=\"_blank\"", start_user);
						start_user = answerdetails.indexOf(">", start_user);
						int end_user = answerdetails.indexOf("<", start_user);
						String user = answerdetails.substring(start_user + 1, end_user);
						writer.write("<user>" + user + "</user>" + LINE);
						// 收集回答者的信息
						UserDetails.adduserinfo(user, outPath);
					} else {
						writer.write("<user>热心网友</user>" + LINE);
					}
					// <time>回答时间</time>
					start_time = answerdetails.indexOf("ins");
					start_time = answerdetails.indexOf(">", start_time);
					end_time = answerdetails.indexOf("<", start_time);
					time = answerdetails.substring(start_time + 1, end_time);
					writer.write("<time>" + time + "</time>" + LINE);
					// <content>回答内容</content>
					start_content = answerdetails.indexOf("aContent");
					start_content = answerdetails.indexOf("class=\"con\"");
					start_content = answerdetails.indexOf(">", start_content);
					end_content = answerdetails.indexOf("<", start_content);
					content = answerdetails.substring(start_content + 1, end_content);
					writer.write("<content>" + content + "</content>" + LINE);
					// <good>好评数</good>
					start_good = answerdetails.indexOf("data-evaluate");
					start_good = answerdetails.indexOf("=", start_good);
					end_good = answerdetails.indexOf(">", start_good);
					good = answerdetails.substring(start_good + 2, end_good).trim();
					good = good.replaceAll("\"", "");
					writer.write("<good>" + good + "</good>" + LINE);
					// <bad>差评数</bad>
					start_bad = answerdetails.indexOf("data-evaluate", end_good);
					start_bad = answerdetails.indexOf("=", start_bad);
					end_bad = answerdetails.indexOf(">", start_bad);
					bad = answerdetails.substring(start_bad + 2, end_bad).trim();
					bad = bad.replaceAll("\"", "");
					writer.write("<bad>" + bad + "</bad>" + LINE);
					writer.write("</answer>" + LINE);
				}
				writer.write("</answers>" + LINE);
				writer.write("</QA>" + LINE);
				
			}
		}
		answermap.clear();
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

	public static void addQA(String url) {
		try {
			// File f = new File("//home//yangyang//QA.txt");
			File f = new File("C://Users//fulijiya//Desktop//QA.txt");

			if (!f.exists())
				f.createNewFile();
			FileWriter fw = new FileWriter(f, true);
			BufferedWriter bw = new BufferedWriter(fw);
			getFromBaidu(url, bw, "C://Users//fulijiya//Desktop//userinfo.txt");
			// getFromBaidu(url, bw, "//home//yangyang//userinfo.txt");
			bw.flush();
			bw.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
