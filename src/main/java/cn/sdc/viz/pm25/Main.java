package cn.sdc.viz.pm25;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONArray;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.omg.CORBA.Request;

/**
 * 
 * @author duyi
 * 
 *20140110 duyi 添加插入数据库失败后保存临时文件
 *20140408 duyi 去掉mongodb，只作为slave取数据
 */
public class Main {
	
	private String key = null;
	
	public Main(String key) {
		this.key = key;
	}

	/**
	 * 执行任务
	 */
	public JSONArray task() {
		String url = "http://www.pm25.in/api/querys/all_cities.json?token="
				+ key;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response1 = httpclient.execute(httpGet);

            try {
            	int code = response1.getStatusLine().getStatusCode();
            	if(code >= 200 && code <300){
            		HttpEntity entity1 = response1.getEntity();
            		JSONArray array = new JSONArray();
            		array = JSONArray.fromObject(readStream(entity1.getContent()));
                    EntityUtils.consume(entity1);
                    return array;
            	}       
            } catch (IOException e) {
				e.printStackTrace();
			} finally {
                try {
					response1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }

        } catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }		
        return new JSONArray();
	}
	
	/**
	 * 解析数据流成字符串
	 * 
	 * @param is
	 * @return
	 */
	public String readStream(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String lastStr = "";
		String tmp = "";
		try {
			while ((tmp = reader.readLine()) != null) {
				lastStr += tmp;
				if(tmp.equals("{\"error\":\"Sorry，您这个小时内的API请求次数用完了，休息一下吧！\"}"))
					return "[]";
			}
		} catch (IOException e) {
			processError("", e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				processError("", e);
			}
		}
		return lastStr;
	}


	/**
	 * 错误处理，记录日志+发邮件
	 * 
	 * @param s
	 * @param e
	 */
	private void processError(String s, Exception e) {
//		try {
		System.err.println(e);
//			logger.error(e);
//		} catch (Exception e3) {
//			processError("error process error!", e3);
//		}
	}

}
