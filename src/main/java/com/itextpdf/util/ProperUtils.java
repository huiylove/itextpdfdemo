package com.itextpdf.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 */
public class ProperUtils {

	private static Properties properties = new Properties();

	static {
		try {
			InputStream io = ProperUtils.class.getResourceAsStream("/init.properties");
			properties.load(io);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取init.properties文件中的配置参数
	 * 
	 * @param key
	 *            参数名
	 * @return String 参数值
	 */
	public static String getInitParam(String key) {

		return properties.get(key) == null ? key : (String) properties.get(key);
	}
	
	
}
