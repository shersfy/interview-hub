package org.interview.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;

/**
 * 统一异常类
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class StandardException extends Exception{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	   * Constructs a new throwable with null as its detail message.
	   */
	  public StandardException() {
	    super();
	  }

	  /**
	   * Constructs a new throwable with the specified detail message.
	   *
	   * @param message
	   *          - the detail message. The detail message is saved for later retrieval by the getMessage() method.
	   */
	  public StandardException( String message ) {
	    super( message );
	  }

	  /**
	   * Constructs a new throwable with the specified cause and a detail message of (cause==null ? null : cause.toString())
	   * (which typically contains the class and detail message of cause).
	   *
	   * @param cause
	   *          the cause (which is saved for later retrieval by the getCause() method). (A null value is permitted, and
	   *          indicates that the cause is nonexistent or unknown.)
	   */
	  public StandardException( Throwable cause ) {
	    super( cause );
	  }

	  /**
	   * Constructs a new throwable with the specified detail message and cause.
	   *
	   * @param message
	   *          the detail message (which is saved for later retrieval by the getMessage() method).
	   * @param cause
	   *          the cause (which is saved for later retrieval by the getCause() method). (A null value is permitted, and
	   *          indicates that the cause is nonexistent or unknown.)
	   */
	  public StandardException( String message, Throwable cause ) {
	    super(message, cause );
	  }
	  
	  /**
	   * 带参构造器
	   * @param message 可带参数, 参数占位符"%s", String.format(msg, args)实现
	   * @param cause 异常
	   * @param msgArgs 参数列表
	   */
	  public StandardException(Throwable cause, String message, Object...msgArgs) {
		  super(msgArgs!=null?String.format(message, msgArgs):message.replace("%s", ""), cause);
	  }

	  /**
	   * get the messages back to it's origin cause.
	   */
	  @Override
	  public String getMessage() {
	    String cr = System.getProperty("line.separator");
	    String retval = "";
	    retval += super.getMessage() + cr;

	    Throwable cause = getCause();
	    if ( cause != null ) {
	      String message = cause.getMessage();
	      if ( message != null ) {
	        retval += message + cr;
	      } else {
	        // Add with stack trace elements of cause...
	        StackTraceElement[] ste = cause.getStackTrace();
	        for ( int i = ste.length - 1; i >= 0; i-- ) {
	          retval +=
	            " at "
	              + ste[i].getClassName() + "." + ste[i].getMethodName() + " (" + ste[i].getFileName() + ":"
	              + ste[i].getLineNumber() + ")" + cr;
	        }
	      }
	    }

	    return retval;
	  }

	  public String getSuperMessage() {
	    return getSuperMessage(3);
	  }
	  
	  /**
	   * 限制异常信息行数
	   * 
	   * @author PengYang
	   * @date 2017-05-03
	   * 
	   * @param limit 行数限制
	   * @return String
	   */
	  public String getSuperMessage(int limit) {
		  
		  String err   = getMessage()==null?"":getMessage();
		  String cause = getCause()==null?"":getCause().getMessage();
		  cause = StringUtils.isBlank(cause)?"":"Caused by:\n"+cause;
		  
		  List<String> rows = new ArrayList<>();
		  rows.addAll(Arrays.asList(err.split("\n")));
		  rows.addAll(Arrays.asList(cause.split("\n")));
		  rows.removeIf(new Predicate<String>() {

			@Override
			public boolean test(String msg) {
				return StringUtils.isBlank(msg);
			}
		});

		  StringBuffer msg = new StringBuffer(0);
		  for(String row : rows){
			  msg.append(row.trim()).append("\n");
		  }
		  
		  return msg.toString();
	  }
	  /**
	   * 获取异常原因
	   * 
	   * @param ex 异常
	   * @return 异常原因, 无字符串返回空字符串
	   */
	  public static String getCauseMsg(Throwable ex){
			String error = ex.getMessage();
			if(ex instanceof StandardException){
				StandardException de = (StandardException) ex;
				error = de.getSuperMessage();
			}
			else if(ex.getCause()!=null){
				error = ex.getCause().getMessage();
			}
			error = error==null?"":error;
			return error;
		}

}
