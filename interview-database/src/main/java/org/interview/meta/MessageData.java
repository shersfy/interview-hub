package org.interview.meta;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 消息推送数据
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class MessageData extends Data{

	private String cmd;
	private JSONObject data;
	
	public MessageData() {
		data = new JSONObject();
	}
	
	public MessageData(String cmd) {
		this();
		this.cmd = cmd;
	}
	
	public String getCmd() {
		return cmd;
	}
	
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	
	public void putData(String key, Object data) {
		this.data.put(key, data);
	}
	
	public Object getData(String key) {
		Object obj = null;
		obj = this.data.get(key);
		if(obj == null){
			obj = "";
		}
		return obj;
	}
	
	public JSONObject getData() {
		return this.data;
	}
	
	public void putAll(Map<? extends String, ? extends Object> data) {
		this.data.putAll(data);
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
}
