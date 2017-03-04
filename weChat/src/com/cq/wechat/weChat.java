package com.cq.wechat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.io.SAXReader;

import java.io.InputStream;

import java.util.HashMap;

import java.util.Map;

import org.dom4j.Document;

import org.dom4j.Element;

/**
 * Servlet implementation class weChat
 */
@WebServlet("/weChat")
public class weChat extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String jsonString; // 用于存放json数据

	/**
	 * Default constructor.
	 */
	public weChat() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.print("------------------------------------");
		String echostr=""; 
		echostr = request.getParameter("echostr");
        //验证消息是否来自微信，是的话，把该字符串返回给微信
		if (echostr!=null) {
			valid(request);
			PrintWriter out = response.getWriter();
			out.println(jsonString);
			out.close();
			System.out.print(jsonString);
		}
        
	   //接受微信的推送（关注和取消关注等）
		try {
		    //解析微信返回的xml
			Map<String, String> map = parseXml(request);
			//测试查看返回的消息
			System.out.println(map.size());
			 for (String key : map.keySet()) {
				   System.out.println("key= "+ key + " and value= " + map.get(key));
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);

	}
    /**
     * 验证微信消息
     * @param request
     */
	public void valid(HttpServletRequest request) {
		String encryptStr = "";
		List<String> list = new ArrayList<String>();
		list.add(request.getParameter("nonce"));
		list.add(request.getParameter("timestamp"));
		list.add("cqtoken");//自己的token
		Collections.sort(list, new SpellComparator());
		for (String s : list) {
			encryptStr += s;
		}
		String encrypt = SHA1.hex_sha1(encryptStr);
		if (encrypt.equals(request.getParameter("signature"))) {
			jsonString = request.getParameter("echostr");
		}
	}

	/**
	 * 汉字拼音排序比较器
	 */
	@SuppressWarnings("rawtypes")
	class SpellComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			try {
				// 取得比较对象的汉字编码，并将其转换成字符串
				String s1 = new String(o1.toString().getBytes("GB2312"), "UTF-8");
				String s2 = new String(o2.toString().getBytes("GB2312"), "UTF-8");
				// 运用String类的 compareTo（）方法对两对象进行比较
				return s1.compareTo(s2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}

	/**
	 * 
	 * 解析微信发来的请求（XML）
	 * 
	 * 
	 * 
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws Exception
	 * 
	 */

	public Map<String, String> parseXml(HttpServletRequest request) throws Exception {

		// 将解析结果存储在HashMap中

		Map<String, String> map = new HashMap<String, String>();

		// 从request中取得输入流

		InputStream inputStream = request.getInputStream();

		// 读取输入流

		SAXReader reader = new SAXReader();

		Document document = reader.read(inputStream);

		String requestXml = document.asXML();

		String subXml = requestXml.split(">")[0] + ">";

		requestXml = requestXml.substring(subXml.length());

		// 得到xml根元素

		Element root = document.getRootElement();

		// 得到根元素的全部子节点

		List<Element> elementList = root.elements();

		// 遍历全部子节点

		for (Element e : elementList) {

			map.put(e.getName(), e.getText());

		}

		map.put("requestXml", requestXml);

		// 释放资源

		inputStream.close();

		inputStream = null;

		return map;

	}

}
