package com.dyy.njupt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;

public class BrowseTimes {

	public static void main(String[] args) throws IOException {
		
		BrowseTimes bt=new BrowseTimes();
		String response=bt.Browsetime("http://zhidao.baidu.com/question/1242101501038684099.html?fr=qlquick&entry=qb_list_default");
		System.out.println(response);
	}
	public static String Browsetime(String url)
	{    				
		String refererURL = url;
		String docId="";
		if(url != null && !"".equals(url)){
		for(int i=0;i<url.length();i++){
		if(url.charAt(i)>=48 && url.charAt(i)<=57){
		docId+=url.charAt(i);
		  }
	    }
	}
		MultiThreadedHttpConnectionManager connectionManager =new MultiThreadedHttpConnectionManager();
		 HttpClient client = new HttpClient(connectionManager);
		 GetMethod getMethod = new GetMethod("http://zhidao.baidu.com/api/qbpv?q="+docId);
		 getMethod.setRequestHeader("Host", "zhidao.baidu.com");
		 getMethod.setRequestHeader("Origin", "http://js.189.cn");
		 getMethod.setRequestHeader("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-ms-application, application/x-ms-xbap, application/vnd.ms-xpsdocument, application/xaml+xml, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
		 getMethod.setRequestHeader("Referer", refererURL);
		 getMethod.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
		 getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		 getMethod.setRequestHeader("Accept-Encoding", "gzip, deflate");
		 getMethod.setRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
		 getMethod.setRequestHeader("Connection", "Keep-Alive");
		 getMethod.setRequestHeader("Cache-Control", "no-cache");
		 getMethod.setRequestHeader("X-Requested-With", "XMLHttpRequest");		
		 getMethod.setRequestHeader("Cookie", "IK_CID_83=1; IK_CID_82=1; IK_CID_1031=1; IK_CID_1=1; IK_CID_74=5; IK_CID_81=1; BAIDUID=A7AFE5A963FF1FB0C66FB8DD29712542:FG=1; Hm_lvt_f4165db5a1ac36eadcfa02a10a6bd243=1468629928,1468676720,1468757732,1468762670; IK_A7AFE5A963FF1FB0C66FB8DD29712542=5; IK_CID_95=6; Hm_lvt_6859ce5aaf00fb00387e6434e4fcc925=1468676638,1468755197,1468762659,1468844540; Hm_lpvt_6859ce5aaf00fb00387e6434e4fcc925=1468849345");
		 try {
			client.executeMethod(getMethod);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		 	InputStream is = null;
	        try {
				is = getMethod.getResponseBodyAsStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        byte[] data = new byte[4096];  
	        int count = -1;  
	        try {
				while((count = is.read(data,0,4096)) != -1)  
				    outStream.write(data, 0, count);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	          
	        data = outStream.toByteArray();
	        ByteArrayInputStream bis = new ByteArrayInputStream(data);
	        GZIPInputStream gzip = null;
			try {
				gzip = new GZIPInputStream(bis);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return null;
//				e.printStackTrace();
			}
	        byte[] buf = new byte[1024];
	        int num = -1;
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        try {
				while ((num = gzip.read(buf, 0, buf.length)) != -1) {
			 	 baos.write(buf, 0, num);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
	        byte[] b = baos.toByteArray();
	        try {
				baos.flush();
		        baos.close();
		        gzip.close();
		        bis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
	        String response = null;
			try {
				response = new String(b,"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			getMethod.releaseConnection();
	        return response;
	}

}
