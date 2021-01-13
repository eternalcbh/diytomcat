package cn.gdoucbh.diytomcat;

import cn.hutool.core.util.NetUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author cbh
 * @PackageName:cn.gdoucbh.diytomcat
 * @ClassName:Bootstrap
 * @Description: jvm类加载（bootstrap,extclass,appclass）
 * @date 2021-01-13 9:54
 */
public class Bootstrap {
	public static void main(String[] args) {
		try {
			int port = 18080;

			if (!NetUtil.isUsableLocalPort(port)){
				System.out.println(port + "端口被占用");
				return;
			}

			ServerSocket serverSocket = new ServerSocket(port);

			while (true){
				Socket socket = serverSocket.accept();
				InputStream inputStream = socket.getInputStream();
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				inputStream.read(buffer);
				String requestString = new String(buffer, "utf-8");
				System.out.println("浏览器输入的信息:\r\n" + requestString);

				OutputStream outputStream = socket.getOutputStream();

				//响应头
				String responseHead = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n";

				//响应体
				String responseString = "Hello DIY Tomcat from gdoucbh";
				responseString = responseHead + responseString;

				//输出返回
				outputStream.write(responseString.getBytes());
				outputStream.close();
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
