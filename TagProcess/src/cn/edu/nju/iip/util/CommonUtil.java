package cn.edu.nju.iip.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import cn.edu.nju.iip.redis.JedisPoolUtils;


public class CommonUtil {

	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);



	/**
	 * 公路建设从业企业数据导入
	 * @return
	 */
	public static List<String> importConsRoadUnitName() {
		Workbook workbook = null;
		List<String> list = new ArrayList<String>();
		try {
			workbook = Workbook.getWorkbook(new File(System
					.getProperty("user.dir") + "/resources/公路企业基本信息.xls"));
		} catch (Exception e) {
			logger.error("importFromXls error!", e);
		}
		Sheet sheet = workbook.getSheet(0);
		int rowCount = sheet.getRows();
		for (int i = 1; i < rowCount; i++) {
			String UnitName = sheet.getCell(2, i).getContents().trim();
			if (UnitName.contains("公司")) {
				list.add(UnitName);
			}
		}
		return list;
	}
	
	/**
	 * 水运建设从业企业数据导入
	 * @return
	 */
	public static List<String> importConsShipUnitName() {
		Workbook workbook = null;
		List<String> list = new ArrayList<String>();
		try {
			workbook = Workbook.getWorkbook(new File(System
					.getProperty("user.dir") + "/resources/水运企业基本信息.xls"));
		} catch (Exception e) {
			logger.error("importConsShipUnitName error!", e);
		}
		Sheet sheet = workbook.getSheet(0);
		int rowCount = sheet.getRows();
		for (int i = 1; i < rowCount; i++) {
			String UnitName = sheet.getCell(1, i).getContents().trim();
			list.add(UnitName);
		}
		return list;
	}
	
	/**
	 * 道路运输从业企业数据导入
	 * @return
	 */
	public static List<String> importTransRoadUnitName() {
		Workbook workbook = null;
		List<String> list = new ArrayList<String>();
		try {
			workbook = Workbook.getWorkbook(new File(System
					.getProperty("user.dir") + "/resources/道路运输从业企业.xls"));
		} catch (Exception e) {
			logger.error("importConsShipUnitName error!", e);
		}
		Sheet sheet = workbook.getSheet(0);
		int rowCount = sheet.getRows();
		for (int i = 1; i < rowCount; i++) {
			String UnitName = sheet.getCell(2, i).getContents().trim();
			if (UnitName.contains("公司")) {
				list.add(UnitName);
			}
		}
		return list;
	}
	
	/**
	 * 将相关企业实体标签存入redis标签库
	 */
	public static void saveUnitNameToRedis() {
		Jedis jedis = JedisPoolUtils.getInstance().getJedis();
		List<String> ConsShipUnitNames = importConsShipUnitName();
		logger.info("ConsShipUnitNames size="+ConsShipUnitNames.size());
		for(String unitName:ConsShipUnitNames) {
			jedis.sadd("水运建设企业", unitName);
		}
		List<String> ConsRoadUnitName = importConsRoadUnitName();
		logger.info("ConsRoadUnitName size="+ConsRoadUnitName.size());
		for(String unitName:ConsRoadUnitName) {
			jedis.sadd("公路建设企业", unitName);
		}
		List<String> TransRoadUnitName = importTransRoadUnitName();
		logger.info("TransRoadUnitName size="+TransRoadUnitName.size());
		for(String unitName:TransRoadUnitName) {
			jedis.sadd("道路运输企业", unitName);
		}
		saveCreditTag(jedis);
		JedisPoolUtils.getInstance().returnRes(jedis);
	}
	
	public static void saveCreditTag(Jedis jedis) {
		jedis.sadd("信用相关标签", "表彰");
		jedis.sadd("信用相关标签", "批评");
		jedis.sadd("信用相关标签", "获奖");
	}
	
	public static Set<String> getUnitNameSet(String unitType) {
		Jedis jedis = JedisPoolUtils.getInstance().getJedis();
		Set<String> set = jedis.smembers(unitType);
		JedisPoolUtils.getInstance().returnRes(jedis);
		return set;
	}
	
	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getTime() {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期格式
		String time = dateFormat.format(now);
		return time;
	}
	
	public static Date parseDate(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException e) {
			logger.error("parseDate error!", e);
		}  
		return date;
	}

	public static void main(String[] args) {
		//logger.info(getUnitNameSet("水运建设企业").toString());
		saveUnitNameToRedis();
	}

}