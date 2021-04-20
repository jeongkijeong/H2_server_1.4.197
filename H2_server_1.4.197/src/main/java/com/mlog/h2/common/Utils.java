package com.mlog.h2.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class Utils {
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	private static Properties props = null;

	/**
	 * 로그설정 파일을 초기화 한다.
	 * @param path
	 */
	public static int LoadLogConfigs(String path) {
		int retv = Constant.SUCCESS;

		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator joranConfigurator = new JoranConfigurator();
		joranConfigurator.setContext(loggerContext);
		loggerContext.reset();

		try {
			joranConfigurator.doConfigure(path);
		} catch (JoranException e) {
			retv = Constant.FAILURE;
			e.printStackTrace();
		}

		return retv;
	}
    
	/**
	 * 프로퍼티 파일을 초기화 한다.
	 * @param path
	 */
	public static int loadProperties(String path) {
		int retv = Constant.SUCCESS;

		try {
			if (props == null) {
				props = new Properties();
			}

			props.load(new BufferedInputStream(new FileInputStream(path)));
		} catch (Exception e) {
			retv = Constant.FAILURE;
			logger.error("", e);
		}

		return retv;
	}

	/**
	 * 프로퍼티 파일로 부터 키 값에 해당하는 내용을 찾아서 반환한다.
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		String property = null;

		if (props != null) {
			property = props.getProperty(key);
		}

		return property;
	}
	
    //다른 포맷이 필요하다면 case에 추가하세요
    /**
	* <pre>
	* 현재 날짜(또는 일자,시간)를 다양한 포멧으로 리턴한다.
	* iCase
	*  1 : yyyyMMddHHmmss
	*  2 : yyyyMMddHHmm
	*  3 : dd
	*  4 : yyyyMMdd
	*  5 : yyyy/MM/dd HH:mm:ss
	*  6 : MM/dd HH:mm:ss
	*  7 : HH
	*  8 : mm
	*  9 : HHmm
	* </pre>
	* @param iCase 숫자에 따라서 시간 포맷이 달라진다.
	* */
    static public String getTime(int iCase) {
		Calendar cal = Calendar.getInstance(new Locale("Korean", "Korea"));
		SimpleDateFormat df = null;
		
		switch (iCase) {
		case 1: df = new SimpleDateFormat("yyyyMMddHHmmss");	  break;
		case 2: df = new SimpleDateFormat("yyyyMMddHHmm");		  break;
		case 3: df = new SimpleDateFormat("yyyyMMddHH");		  break;
		case 4: df = new SimpleDateFormat("yyyyMMdd");			  break;
		case 5: df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); break;
		case 6: df = new SimpleDateFormat("MM/dd HH:mm:ss"); 	  break;
		case 7: df = new SimpleDateFormat("HH");             	  break;
		case 8: df = new SimpleDateFormat("mm");             	  break;
		case 9: df = new SimpleDateFormat("mmss");           	  break;
		case 10: df = new SimpleDateFormat("yyyyMM");         	  break;
		default: break;
		}
		
		return df.format(cal.getTime());
    }

	public static String diffDate(String type, String inputDate, int interval) throws Exception {
		int year = 0;
		int month = 0;
		int day = 0;
		int hour = 0;
		int minute = 0;
		String format = "yyyyMMddHHmm";
            
		Calendar cal = Calendar.getInstance(Locale.KOREA);
            
		if (type.toUpperCase().equals("YYYY")) {
			format = "yyyy";

			year = Integer.parseInt(inputDate.substring(0, 4));
			cal.set(year + interval + 1, month, day, hour, minute, 0);
		} else if (type.toUpperCase().equals("MM")) {
			format = "yyyyMM";

			year = Integer.parseInt(inputDate.substring(0, 4));
			month = Integer.parseInt(inputDate.substring(4, 6));
			cal.set(year, month + interval, day, hour, minute, 0);
		} else if (type.toUpperCase().equals("DD")) {
			format = "yyyyMMdd";

			year = Integer.parseInt(inputDate.substring(0, 4));
			month = Integer.parseInt(inputDate.substring(4, 6));
			day = Integer.parseInt(inputDate.substring(6, 8));
			cal.set(year, month - 1, day + interval, hour, minute, 0);
		} else if (type.toUpperCase().equals("HH")) {
			format = "yyyyMMddHH";

			year = Integer.parseInt(inputDate.substring(0, 4));
			month = Integer.parseInt(inputDate.substring(4, 6));
			day = Integer.parseInt(inputDate.substring(6, 8));
			hour = Integer.parseInt(inputDate.substring(8, 10));
			cal.set(year, month - 1, day, hour + interval, minute, 0);
		} else if (type.toUpperCase().equals("MI")) {
			format = "yyyyMMddHHmm";

			year = Integer.parseInt(inputDate.substring(0, 4));
			month = Integer.parseInt(inputDate.substring(4, 6));
			day = Integer.parseInt(inputDate.substring(6, 8));
			hour = Integer.parseInt(inputDate.substring(8, 10));
			minute = Integer.parseInt(inputDate.substring(10, 12));

			cal.set(year, month - 1, day, hour, minute + interval, 0);
		}

		Date currentTime = cal.getTime();
		SimpleDateFormat fomatter = new SimpleDateFormat(format, Locale.KOREA);
		String time_str = fomatter.format(currentTime);
		return time_str;
	}
	
	public static List<Map<String, String>> tableXmlParse(String xmlFile) {
		List<Map<String, String>> tableList = new ArrayList<Map<String, String>>();
	
		try {
			InputSource inputSource = new InputSource(new FileReader(xmlFile));
	
			Document docu = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
			NodeList list = docu.getElementsByTagName(Constant.TAG_TABLE);

			for (int temp = 0; temp < list.getLength(); temp++) {
				Node node = list.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;

					Map<String, String> table = new HashMap<String, String>();
					table.put(Constant.TAG_TABLENM, getTagValue(Constant.TAG_TABLENM, element));
					table.put(Constant.TAG_COLUMNS, getTagValue(Constant.TAG_COLUMNS, element));
					table.put(Constant.TAG_PERIOD , getTagValue(Constant.TAG_PERIOD , element));
					table.put(Constant.TAG_RESTORE, getTagValue(Constant.TAG_RESTORE, element));

					tableList.add(table);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return tableList;
	}

	public static String getTagValue(String sTag, Element eElement) {
		String val = null;

		try {
			NodeList list = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
			Node node = (Node) list.item(0);
			val = node.getNodeValue();
		} catch (Exception e) {
			logger.error("", e);
		}

		return val;
	}	
	
	

	
	
}
