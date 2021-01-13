package cn.gdoucbh.diytomcat.test;

import cn.gdoucbh.diytomcat.utiil.MiniBrowser;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author cbh
 * @PackageName:cn.gdoucbh.diytomcat.test
 * @ClassName:TestTomcat
 * @Description:
 * @date 2021-01-13 16:13
 */
public class TestTomcat {
	private static int port = 18080;
	private static String ip = "127.0.0.1";

	public static void beforeClass(){
		if (NetUtil.isUsableLocalPort(port)){
			System.err.println("请先启动位于端口：" + port + "的diy timcat，否则无法进行单元测试");
			System.exit(1);
		}else {
			System.out.println("检测到 diy tomcat已经启动，开始进行单元测试");
		}
	}

	@Test
	public void testHelloTomcat(){
		String html = getContentString("/");
		Assert.assertEquals(html,"Hello DIY Tomcat gdoucbh");
	}

	private String getContentString(String uri){
		String url = StrUtil.format("http://{}:{}{}",ip,port,uri);
		String content = MiniBrowser.getContentString(url);
		return content;
	}
}
