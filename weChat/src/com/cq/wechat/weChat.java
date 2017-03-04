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

	private String jsonString; // ���ڴ��json����

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
        //��֤��Ϣ�Ƿ�����΢�ţ��ǵĻ����Ѹ��ַ������ظ�΢��
		if (echostr!=null) {
			valid(request);
			PrintWriter out = response.getWriter();
			out.println(jsonString);
			out.close();
			System.out.print(jsonString);
		}
        
	   //����΢�ŵ����ͣ���ע��ȡ����ע�ȣ�
		try {
		    //����΢�ŷ��ص�xml
			Map<String, String> map = parseXml(request);
			//���Բ鿴���ص���Ϣ
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
     * ��֤΢����Ϣ
     * @param request
     */
	public void valid(HttpServletRequest request) {
		String encryptStr = "";
		List<String> list = new ArrayList<String>();
		list.add(request.getParameter("nonce"));
		list.add(request.getParameter("timestamp"));
		list.add("cqtoken");//�Լ���token
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
	 * ����ƴ������Ƚ���
	 */
	@SuppressWarnings("rawtypes")
	class SpellComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			try {
				// ȡ�ñȽ϶���ĺ��ֱ��룬������ת�����ַ���
				String s1 = new String(o1.toString().getBytes("GB2312"), "UTF-8");
				String s2 = new String(o2.toString().getBytes("GB2312"), "UTF-8");
				// ����String��� compareTo������������������бȽ�
				return s1.compareTo(s2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}

	/**
	 * 
	 * ����΢�ŷ���������XML��
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

		// ����������洢��HashMap��

		Map<String, String> map = new HashMap<String, String>();

		// ��request��ȡ��������

		InputStream inputStream = request.getInputStream();

		// ��ȡ������

		SAXReader reader = new SAXReader();

		Document document = reader.read(inputStream);

		String requestXml = document.asXML();

		String subXml = requestXml.split(">")[0] + ">";

		requestXml = requestXml.substring(subXml.length());

		// �õ�xml��Ԫ��

		Element root = document.getRootElement();

		// �õ���Ԫ�ص�ȫ���ӽڵ�

		List<Element> elementList = root.elements();

		// ����ȫ���ӽڵ�

		for (Element e : elementList) {

			map.put(e.getName(), e.getText());

		}

		map.put("requestXml", requestXml);

		// �ͷ���Դ

		inputStream.close();

		inputStream = null;

		return map;

	}

}
