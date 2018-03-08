package org.interview.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.interview.connector.relationship.DbConnectorInterface;
import org.interview.connector.relationship.OracleConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class SqlUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(SqlUtil.class);

	private SqlUtil(){}
	
	/***
	 *  替换SQL中的数据库DB系统时间,  按照指定格式参数替换<br/>
	 * 
	 * @author PengYang
	 * @date 2017-09-11
	 * 
	 * @param sql 替换前的SQL
	 * @param fun 指定函数名{@link SystemFunctions}{"DB_SYSTEM_HH_0","DB_SYSTEM_HH_59","DB_SYSTEM_TIME"}
	 * @param connector 数据库连接器
	 * @return 替换完的SQL
	 */
	public static String replaceDBSystemTime(String sql, SystemFunctions fun, DbConnectorInterface connector){
		if(sql == null || fun==null || connector==null){
			return sql;
		}
		
		return sql;
	}
	/***
	 *  替换SQL中的数据库DB系统时间,  按照指定格式参数替换<br/>
	 *  {@link SystemFunctions}{"DB_SYSTEM_HH_0","DB_SYSTEM_HH_59","DB_SYSTEM_TIME"}
	 * 
	 * @author PengYang
	 * @date 2017-09-11
	 * 
	 * @param sql 替换前的SQL
	 * @param connector 数据库连接器
	 * @return 替换完的SQL
	 */
	public static String replaceDBSystemTime(String sql, DbConnectorInterface connector){
		if(sql == null || connector==null){
			return sql;
		}
		
		sql = replaceDBSystemTime(sql, SystemFunctions.DB_SYSTEM_HH_0, 	connector);
		sql = replaceDBSystemTime(sql, SystemFunctions.DB_SYSTEM_HH_59, connector);
		sql = replaceDBSystemTime(sql, SystemFunctions.DB_SYSTEM_TIME, 	connector);
		
		return sql;
	}

	/**
	 * 替换SQL中的当前日期, 按照指定格式参数替换<br/>
	 * 返回替换后的SQL, 如被替换SQL中不包含任何当前日期的字符串, 返回原SQL<br/>
	 * 
	 * @author PengYang
	 * @date 2017-03-27
	 * 
	 * @param sql 被替换的SQL
	 * @param fun 指定函数名{@link SystemFunctions}{"current_date","current_time0","current_time23"}
	 * @param clazz 数据库连接器类, 为null不支持数据库日期时间函数
	 * @return String
	 */
	public static String replaceSqlCurrentTime(String sql, SystemFunctions fun, Class<? extends DbConnectorInterface> clazz){

		String newSql = sql;
		try {
			if(StringUtils.isNotBlank(sql) && fun!=null){

				// 查找fun的位置
				String indexes = getSysFunIndex(newSql, fun.getName());
				if(StringUtils.isBlank(indexes)){
					return sql;
				}
				
				String[] arr = indexes.split(",");
				// 开始替换位置
				int start 	= Integer.valueOf(arr[0]);
				int end 	= Integer.valueOf(arr[1]);

				// 被替换函数
				String funSql = sql.substring(start, end+1);
				int len = funSql.length();

				// 被替换函数参数
				String param = funSql.substring(funSql.indexOf("(")+1, funSql.length()-1).trim();
				param = param.replace("\"", "");
				param = param.replace("'", "");

				// 兼容Oracle
				String oraclToDateFrmt = "";
				// 当前日期
				Date timeTmp = null;
				if(SystemFunctions.CURRENT_YEAR == fun
						|| SystemFunctions.CURRENT_MONTH == fun
						|| SystemFunctions.CURRENT_DATE == fun){
					timeTmp = new Date();
					oraclToDateFrmt = param.replace("MM", "mm").replace("M", "m");
				}
				// 当前时间0点0分0秒
				else if(SystemFunctions.CURRENT_TIME0 == fun){
					String dateTmp = DateUtil.format("yyyy/MM/dd");
					dateTmp = dateTmp + " 00:00:00";
					timeTmp = DateUtil.getDateByStr(dateTmp, "yyyy/MM/dd HH:mm:ss");
					oraclToDateFrmt = param.replace("hh", "hh12")
							.replace("HH", "hh24")
							.replace("H", "hh24")
							.replace("mm", "mi")
							.replace("MM", "mm");
				}
				// 当前时间23点59分59秒
				else if(SystemFunctions.CURRENT_TIME23 == fun){
					String dateTmp = DateUtil.format("yyyy/MM/dd");
					dateTmp = dateTmp + " 23:59:59";
					timeTmp = DateUtil.getDateByStr(dateTmp, "yyyy/MM/dd HH:mm:ss");
					oraclToDateFrmt = param.replace("hh", "hh12")
							.replace("HH", "hh24")
							.replace("H", "hh24")
							.replace("mm", "mi")
							.replace("MM", "mm");
				}
				// 非系统内置
				else{
					return sql;
				}

				String dateStr = DateUtil.format(timeTmp, param);
				StringBuffer replaced = new StringBuffer(sql);
				
				// 兼容oracle日期函数
				if(clazz!=null && clazz.getName().equals(OracleConnector.class.getName())){
					String rep = String.format("TO_DATE('%s', '%s')", dateStr, oraclToDateFrmt);
					// -1、+1替换掉单引号
					newSql = replaced.replace(start-1, start+len+1, rep).toString();
				} 
				// 其它
				else {
					newSql = replaced.replace(start, start+len, dateStr).toString();
				}
			}

			if(newSql.equals(sql)){
				return newSql;
			}
			// 递归调用
			else{
				return replaceSqlCurrentTime(newSql, fun, clazz);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return newSql;
	}

	/**
	 * 替换SQL中的当前日期, 按照指定格式参数替换<br/>
	 * 返回替换后的SQL, 如被替换SQL中不包含任何当前日期的字符串, 返回原SQL<br/>
	 * 
	 * @author PengYang
	 * @date 2017-03-27
	 * 
	 * @param sql 被替换的SQL
	 * @param funs 指定函数名{@link SystemFunctions}{"current_year","current_month","current_date","current_time0","current_time23"}
	 * @return String
	 */
	public static String replaceSqlCurrentTime(String sql, SystemFunctions[] funs){
		if(funs == null){
			return sql;
		}

		for(SystemFunctions fun : funs){
			sql = replaceSqlCurrentTime(sql, fun, null);
		}

		return sql;
	}
	/**
	 * 替换SQL中的当前日期, 按照指定格式参数替换<br/>
	 * 返回替换后的SQL, 如被替换SQL中不包含任何当前日期的字符串, 返回原SQL<br/>
	 * 默认替换函数名{"current_year","current_month","current_date","current_time0","current_time23"}
	 * 
	 * @author PengYang
	 * @date 2017-03-27
	 * 
	 * @param sql 被替换的SQL
	 * @param clazz 数据库连接器类, 为null不支持数据库日期时间函数
	 * @return String
	 */
	public static String replaceSqlCurrentTime(String sql, Class<? extends DbConnectorInterface> clazz){
		if(StringUtils.isBlank(sql)){
			return StringUtils.EMPTY;
		}
		SystemFunctions[] funs = new SystemFunctions[]{SystemFunctions.CURRENT_YEAR, SystemFunctions.CURRENT_MONTH,
				SystemFunctions.CURRENT_DATE, SystemFunctions.CURRENT_TIME0, SystemFunctions.CURRENT_TIME23};

		for(SystemFunctions fun : funs){
			sql = replaceSqlCurrentTime(sql, fun, clazz);
		}
		
		return sql;
	}
	
	/**
	 * 拆分数据块条件
	 * 
	 * @param partColumnName 分块字段名
	 * @param partSize 分块大小
	 * @param partitionMinValue 分块字段最大值
	 * @param partitionMaxValue 分块字段最小值
	 * @return 返回分块条件
	 */
	public static List<String> splitPartitions(String partColumnName, int partSize, String partitionMinValue, String partitionMaxValue) {

		List<String> partitions = new ArrayList<>();

		String minStringValue = null;
		String maxStringValue = null;

		// Remove common prefix if any as it does not affect outcome.
		int maxPrefixLen = Math.min(partitionMinValue.length(),
				partitionMaxValue.length());
		// Calculate common prefix length
		int cpLen = 0;

		for (cpLen = 0; cpLen < maxPrefixLen; cpLen++) {
			char c1 = partitionMinValue.charAt(cpLen);
			char c2 = partitionMaxValue.charAt(cpLen);
			if (c1 != c2) {
				break;
			}
		}

		// The common prefix has length 'sharedLen'. Extract it from both.
		String prefix = partitionMinValue.substring(0, cpLen);
		minStringValue = partitionMinValue.substring(cpLen);
		maxStringValue = partitionMaxValue.substring(cpLen);

		BigDecimal minStringBD = textToBigDecimal(minStringValue);
		BigDecimal maxStringBD = textToBigDecimal(maxStringValue);

		// Having one single value means that we can create only one single split
		if(minStringBD.equals(maxStringBD)) {

			String where = constructWheres(partColumnName, prefix,
					partitionMinValue, partitionMaxValue, 
					new BigDecimal(0), new BigDecimal(0), 
					true, true);
			partitions.add(where);

			return partitions;
		}

		// Get all the split points together.
		List<BigDecimal> splitPoints = new LinkedList<BigDecimal>();

		BigDecimal numMin = new BigDecimal(10000 * Double.MIN_VALUE);
		BigDecimal splitSize = divide(maxStringBD.subtract(minStringBD), new BigDecimal(partSize));
		if (splitSize.compareTo(numMin) < 0) {
			splitSize = numMin;
		}

		BigDecimal curVal = minStringBD;

		int parts = 0;

		while (curVal.compareTo(maxStringBD) <= 0 && parts < partSize) {
			splitPoints.add(curVal);
			curVal = curVal.add(splitSize);
			// bigDecimalToText approximates to next comparison location.
			// Make sure we are still in range
			String text = bigDecimalToText(curVal);
			curVal = textToBigDecimal(text);
			++parts;
		}

		if (splitPoints.size() == 0
				|| splitPoints.get(0).compareTo(minStringBD) != 0) {
			splitPoints.add(0, minStringBD);
		}

		if (splitPoints.get(splitPoints.size() - 1).compareTo(maxStringBD) != 0
				|| splitPoints.size() == 1) {
			splitPoints.add(maxStringBD);
		}

		// Turn the split points into a set of string intervals.
		BigDecimal start = splitPoints.get(0);
		for (int i = 1; i < splitPoints.size(); i++) {
			BigDecimal end = splitPoints.get(i);
			String where = constructWheres(partColumnName, prefix, 
					partitionMinValue, partitionMaxValue, 
					start, end, 
					i == 1, i == splitPoints.size() - 1);
			partitions.add(where);
			start = end;
		}

		return partitions;
	}

	private static final BigDecimal UNITS_BASE = new BigDecimal(0x200000);
	private static final int MAX_CHARS_TO_CONVERT = 4;

	/**string类型转换为BigDecimal**/
	private static BigDecimal textToBigDecimal(String str) {
		BigDecimal result  = BigDecimal.ZERO;
		BigDecimal divisor = UNITS_BASE;

		int len = Math.min(str.length(), MAX_CHARS_TO_CONVERT);

		for (int n = 0; n < len; ) {
			int codePoint = str.codePointAt(n);
			n += Character.charCount(codePoint);
			BigDecimal val = divide(new BigDecimal(codePoint), divisor);
			result = result.add(val);
			divisor = divisor.multiply(UNITS_BASE);
		}

		return result;
	}

	/**BigDecimal转换为string类型**/
	private static String bigDecimalToText(BigDecimal bd) {

		BigDecimal curVal = bd.stripTrailingZeros();
		StringBuilder sb = new StringBuilder();

		for (int n = 0; n < MAX_CHARS_TO_CONVERT; ++n) {
			curVal = curVal.multiply(UNITS_BASE);
			int cp = curVal.intValue();
			if (0 >= cp) {
				break;
			}

			if (!Character.isDefined(cp)) {
				int t_cp = Character.MAX_CODE_POINT < cp ? 1 : cp;
				// We are guaranteed to find at least one character
				while(!Character.isDefined(t_cp)) {
					++t_cp;
					if (t_cp == cp) {
						break;
					}
					if (t_cp >= Character.MAX_CODE_POINT || t_cp <= 0)  {
						t_cp = 1;
					}
				}
				cp = t_cp;
			}
			curVal = curVal.subtract(new BigDecimal(cp));
			sb.append(Character.toChars(cp));
		}

		return sb.toString();
	}

	/**构造条件**/
	private  static String constructWheres(String partColumnName, String prefix, String partMinValue, String partMaxValue, 
			BigDecimal start, BigDecimal end, boolean firstOne, boolean lastOne) {

		StringBuilder conditions = new StringBuilder(0);

		String lbString = prefix + bigDecimalToText(start);
		String ubString = prefix + bigDecimalToText(end);

		conditions.append(partColumnName);
		conditions.append(" >= ");
		conditions.append("N\'").append(firstOne ? partMinValue : lbString).append('\'');

		conditions.append(" AND ");
		conditions.append(partColumnName);
		conditions.append(lastOne ? " <= " : " < ");
		conditions.append("N\'").append(lastOne ? partMaxValue : ubString).append('\'');

		return conditions.toString();
	}

	/**除法计算**/
	private static BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
		try {
			return numerator.divide(denominator);
		} catch (ArithmeticException ae) {
			return numerator.divide(denominator, BigDecimal.ROUND_HALF_UP);
		}
	}
	
	/**
	 * 获取系统函数的位置
	 * 
	 * @param sql SQL
	 * @param fun 系统函数
	 * @return 位置"start,end", start开始位置, end结束位置,如果函数不存在， 返回null
	 */
	public static String getSysFunIndex(String sql, String funName){
		String indexes = null;
		if(StringUtils.isBlank(sql) 
				|| StringUtils.isBlank(funName)){
			return indexes;
		}
		
		// 开始替换位置
		int start = sql.indexOf(funName);
		// 找不到系统函数
		if(start == -1){
			return indexes;
		}

		// 替换长度
		int len = funName.length();
		String tmp = sql.substring(start+len);
		// current_date后面不是紧跟着括号"("
		if(!tmp.trim().startsWith("(")){
			return indexes;
		}
		
		// 结束括号的位置
		List<String> these = FunUtil.indexOfParenthese(tmp);
		// 找不到结束的括号")"
		if(these.isEmpty()){
			return indexes;
		}

		int end = start+len+Integer.valueOf(these.get(0).split(",")[1]);
		LOGGER.debug("fun={}", sql.substring(start, end+1));
		indexes = String.format("%s,%s", start, end);
		return indexes;
	}
	
	/**
	 * sql是否支持拆分数据块
	 * 
	 * @param where 查询条件
	 * @return true支持, false不支持
	 */
	public static boolean supportSplitBlocks(String where){
		if(StringUtils.isBlank(where)){
			return true;
		}
		
		StringBuffer sql = new StringBuffer("SELECT * FROM T ");
		if(!StringUtils.startsWithIgnoreCase(where.trim(), "where")){
			sql.append(" WHERE ");
		}
		sql.append(where);
		try {
			net.sf.jsqlparser.statement.Statement st = CCJSqlParserUtil.parse(sql.toString());
			if(st instanceof Select){
				Select sel = (Select) st;
				if(sel.getSelectBody() instanceof PlainSelect){
					PlainSelect psel = (PlainSelect) sel.getSelectBody();
					// 包含hive mysql psql分页关键字limit
					if(psel.getLimit()!=null){
						return false;
					}
					// 包含order by
					if(psel.getOrderByElements()!=null){
						return false;
					}
					// 包含sql server 分页关键字top
					if(psel.getTop()!=null){
						return false;
					}
				}
			}
			
			
		} catch (Exception e) {
			LOGGER.error("");
		}
		
		return true;
	}
	/**
	 * 处理SQL查询字段别名, 最终每个查询字段别名唯一标识
	 * 
	 * @author PengYang
	 * @date 2017-11-08
	 * 
	 * @param sql 待处理的SQL
	 * @return 已处理的SQL
	 */
	public static String parseSqlAlias(String sql){
		
		if(StringUtils.isBlank(sql)){
			return sql;
		}
		
		try {
			net.sf.jsqlparser.statement.Statement st = CCJSqlParserUtil.parse(sql);
			if(st instanceof Select){
				Select sel = (Select) st;
				if(sel.getSelectBody() instanceof PlainSelect){
					PlainSelect psel = (PlainSelect) sel.getSelectBody();
					List<SelectItem> selectItems = psel.getSelectItems();
					
					Set<String> aliasSet = new HashSet<>();
					for(SelectItem item :selectItems){
						if(item instanceof SelectExpressionItem){
							SelectExpressionItem seitem = (SelectExpressionItem) item;
							Alias alias = seitem.getAlias();
							// 别名存在
							if(alias!=null){
								for(int i=1 ; i<=selectItems.size(); i++){
									if(!aliasSet.contains(alias.getName())){
										aliasSet.add(alias.getName());
										break;
									}
									alias.setName(alias.getName()+i);
								}
								continue;
							}
							// 别名不存在
							else if(seitem.getExpression() instanceof Column){
								Column col = (Column) seitem.getExpression();
								String asName = col.getColumnName();
								for(int i=1 ; i<=selectItems.size(); i++){
									if(!aliasSet.contains(asName)){
										aliasSet.add(asName);
										break;
									}
									asName = col.getColumnName()+i;
								}
								seitem.setAlias(new Alias(asName, true));
							}
						}
					}
				}
			}
			sql = st.toString();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		return sql;
	}
}
