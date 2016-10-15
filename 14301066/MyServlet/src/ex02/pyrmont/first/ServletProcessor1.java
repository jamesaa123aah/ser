package ex02.pyrmont.first;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ex02.pyrmont.Constants;
import ex02.pyrmont.Request;
import ex02.pyrmont.Response;

public class ServletProcessor1 {

    public void process(Request request, Response response) {

        String uri = request.getUri();
        int index = -1;
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);

        if (servletName.contains("?")) {
			index = servletName.indexOf("?");
			servletName = servletName.substring(0, index);
		} else {

		}
        
        
//        解析XML
        File file = new File(System.getProperty("user.dir")+"/lib/web.xml");
        String xml = "";
        try {
			FileReader fileReader = new FileReader(file);
			int len = (int)file.length();
			char[] charArr = new char[len];
			fileReader.read(charArr);
			xml=String.valueOf(charArr);
			
 		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println(servletName);
        
        Document document = Jsoup.parse(xml);
        Elements servlets = document.getElementsByTag("servlet-mapping");
        
        for (Element element : servlets) {
			
        	if(servletName.equals(element.getElementsByTag("url-pattern").get(0).text().substring(1))){
        		
        		String nameOfServlet = element.getElementsByTag("servlet-name").get(0).text();
        		
        		//类加载器，用于从指定JAR文件或目录加载类
                URLClassLoader loader = null;
                try {
                    URLStreamHandler streamHandler = null;
                    //创建类加载器
                    loader = new URLClassLoader(new URL[]{new URL(null, "file:" + Constants.WEB_SERVLET_ROOT, streamHandler)});
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
                
                Class<?> myClass = null;
                try {
                    //加载对应的servlet类
                    myClass = loader.loadClass(nameOfServlet);
                } catch (ClassNotFoundException e) {
                    System.out.println(e.toString());
                }

                Servlet servlet = null;

                try {
                    //生产servlet实例
                    servlet = (Servlet) myClass.newInstance();
                    //执行ervlet的service方法
                    servlet.service((ServletRequest) request,(ServletResponse) response);
                } catch (Exception e) {
                    System.out.println(e.toString());
                } catch (Throwable e) {
                    System.out.println(e.toString());
                }
        	}
        	
		}
        
        
        
        
        

    }
}