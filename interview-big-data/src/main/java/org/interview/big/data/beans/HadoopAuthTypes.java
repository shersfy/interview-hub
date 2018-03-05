package org.interview.big.data.beans;

import com.alibaba.fastjson.JSONObject;

public enum HadoopAuthTypes {
	SIMPLE("simple"),
	KERBEROS("kerberos"),
	SENTRY("sentry");

	private JSONObject json;
	private String alias;

	HadoopAuthTypes(String alias){
		this.alias = alias;
		this.json = new JSONObject();
	}

	public static HadoopAuthTypes indexOf(int index){
		switch (index) {
		case 1:
			return SIMPLE;
		case 2:
			return KERBEROS;
		case 3:
			return SENTRY;
		default:
			return SIMPLE;
		}
	}

	public String alias() {
		return alias;
	}

	public int index(){
		return this.ordinal()+1;
	}

	@Override
	public String toString() {
		json.put("index", this.index());
		json.put("alias", this.alias);
		json.put("name", this.name());
		return json.toJSONString();
	}
}
