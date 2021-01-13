package cn.gdoucbh.diytomcat.utiil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author cbh
 * @PackageName:cn.gdoucbh.diytomcat.utiil
 * @ClassName:MiniBrowser
 * @Description:模拟浏览器
 * @date 2021-01-13 14:42
 */
public class MiniBrowser {
	public static void main(String[] args) {
		//静态页面网站
		String url = "http://115.159.193.54:9888/trend/";
		String contentString = getContentString(url, false);
		System.out.println(contentString);
		String httpString = getHttpString(url, false);
		System.out.println(httpString);
	}

	/**
	 * 返回字符串的http响应内容，即去掉头的html部分
	 *
	 * @param url
	 * @param gzip
	 * @return
	 */
	public static String getContentString(String url, boolean gzip) {
		byte[] result = getContentBytes(url, gzip);
		if (null == result) {
			return null;
		}
		try {
			return new String(result, "utf-8").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * getContentString重载方法
	 *
	 * @param url
	 * @return
	 */
	public static String getContentString(String url) {
		return getContentString(url, false);
	}

	/**
	 * 获取二进制的http响应内容
	 *
	 * @param url
	 * @param gzip
	 * @return
	 */
	public static byte[] getContentBytes(String url, boolean gzip) {
		byte[] response = getHttpBytes(url, gzip);
		byte[] doubleReturn = "\r\n\r\n".getBytes();

		int pos = -1;
		for (int i = 0; i < response.length - doubleReturn.length; i++) {
			byte[] temp = Arrays.copyOfRange(response, i, i + doubleReturn.length);

			if (Arrays.equals(temp, doubleReturn)) {
				pos = i;
				break;
			}
		}

		if (-1 == pos) {
			return null;
		}

		pos += doubleReturn.length;

		byte[] result = Arrays.copyOfRange(response, pos, response.length);
		return result;
	}

	/**
	 * 返回字符串的http响应
	 *
	 * @param url
	 * @param gzip
	 * @return
	 */
	public static String getHttpString(String url, boolean gzip) {
		byte[] bytes = getHttpBytes(url, gzip);
		return new String(bytes).trim();
	}

	public static String getHttpString(String url) {
		return getHttpString(url, false);
	}

	/**
	 * 返回二进制的http响应
	 *
	 * @param url
	 * @param gzip
	 * @return
	 */
	public static byte[] getHttpBytes(String url, boolean gzip) {
		byte[] result = null;
		try {
			URL u = new URL(url);
			Socket client = new Socket();
			int port = u.getPort();
			if (-1 == port) {
				port = 80;
			}
			InetSocketAddress inetSocketAddress = new InetSocketAddress(u.getHost(), port);
			client.connect(inetSocketAddress, 1000);
			Map<String, String> requestHeaders = new HashMap<>();

			requestHeaders.put("Host", u.getHost() + ":" + port);
			requestHeaders.put("Accept", "text/html");
			requestHeaders.put("Connection", "close");
			requestHeaders.put("User-Agent", "gdoucbh mini brower / java1.8");

			if (gzip) {
				requestHeaders.put("Accept-Encoding", "gzip");
			}

			String path = u.getPath();
			if (path.length() == 0){
				path = "/";
			}

			String firstLine = "GET " + path + " HTTP/1.1\r\n";

			StringBuffer httpRequestString = new StringBuffer();
			httpRequestString.append(firstLine);
			Set<String> headers = requestHeaders.keySet();
			for (String header : headers) {
				String headerLine = header + ":" + requestHeaders.get(header) + "\r\n";
				httpRequestString.append(headerLine);
			}

			PrintWriter printWriter = new PrintWriter(client.getOutputStream(), true);
			printWriter.println(httpRequestString);
			InputStream inputStream = client.getInputStream();

			int buffer_size = 1024;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[buffer_size];
			while (true){
				int length = inputStream.read(buffer);
				if (-1 == length){
					break;
				}
				byteArrayOutputStream.write(buffer, 0, length);
				if (length != buffer_size){
					break;
				}
			}
			result = byteArrayOutputStream.toByteArray();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				result = e.toString().getBytes("utf-8");
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

}
