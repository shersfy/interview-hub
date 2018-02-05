package org.interview.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunUtil {

	private static final Logger logger = LoggerFactory.getLogger(FunUtil.class);

	/**
	 * 计算表达式的值
	 * 
	 * @author PengYang
	 * @date 2016-09-01
	 * 
	 * @param exp 字符串表达式
	 * @return Object
	 */
	public static String computeExpression(String exp){
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
		Object var = null;

		try {
			var = engine.eval(exp);

		} catch (ScriptException e) {
			logger.error("computeExpression()", e);
		}
		return var.toString();
	}


	/**
	 * 将“,”分隔的string转换为List
	 * 
	 * @author PengYang
	 * @date 2016-09-04
	 * 
	 * @param ids
	 * @return List<Long>
	 */
	public static List<Long> frmtIds(String ids){

		String arr[] = ids.split(",");
		List<Long> idList = new ArrayList<Long>();

		for(String id : arr){
			idList.add(Long.valueOf(id.trim()));
		}

		return idList;
	}


	/**
	 * 26进制转十进制
	 * 
	 * @author LinXibiao
	 * @date 2017年6月29日
	 *
	 */
	public static long convetChar2Num(String s){
		if (s == null){
			return 0; 
		}
		long n = 0;
		char[] chars = s.toCharArray();

		for (int i = chars.length - 1, j = 1; i >= 0; i--, j *= 26){
			char c = chars[i];
			if (c < 'A' || c > 'Z') return 0;
			n += ((long)c - 64) * j;
		}
		return n;
	}
	
	/**
	 * 获取括号配对位置, 仅考虑以左括号开始且配对的情况<br/>
	 * 返回：配对成功返回每一对的位置, 如"3,6", 表示该左括号位置3, 右括号位置6, 使用逗号","分隔<br/>
	 * 返回：配对失败返回0对<br/>
	 * 
	 * @author PengYang
	 * @date 2017-08-24
	 * 
	 * @param word
	 * @return
	 */
	public static List<String> indexOfParenthese(String word){
		List<String> matches = new ArrayList<>();
		if(word == null){
			return matches;
		}
		
		// 先找结束括号，找到后往前找配对的开始括号
		// 记录已经找到的开始括号的位置
		List<Integer> left 	= new ArrayList<>();
		
		// 寻找结束括号)
		int rindex = 0;
		while(true){
			if(rindex == word.length()){
				break;
			}
			rindex = word.indexOf(")", rindex+1);
			if(rindex==-1){
				break;
			}
			// 寻找开始括号(
			String sub = word.substring(0, rindex);
			int lindex = sub.length()-1;
			while(true){
				
				if(lindex<0){
					break;
				}
				if('(' == sub.charAt(lindex)){
					if(left.contains(lindex)){
						lindex--;
						continue;
					}
					left.add(lindex);
					String m = String.format("%s,%s", lindex, rindex);
					matches.add(m);
					break;
				}
				lindex--;
			}
		}
		
		matches.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int a = Integer.valueOf(o1.split(",")[0]);
				int b = Integer.valueOf(o2.split(",")[0]);
				return a-b;
			}
		});
		
		return matches;
	}
	
	/**
	 * 参数替换, 格式{n}, 参数位置
	 * @param str 字符串
	 * @param args 参数
	 * @return 
	 */
	public static String formatArgs(String str, Object ...args){
		if(str!=null){
			for(int i=0; i<args.length; i++){
				String sep = String.format("{%s}", i);
				str = str.replace(sep, String.valueOf(args[i]));
			}
		}
		return str;
	}
	
}
