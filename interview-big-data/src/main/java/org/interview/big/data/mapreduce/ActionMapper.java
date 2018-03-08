package org.interview.big.data.mapreduce;

import org.apache.commons.lang.StringUtils;
import org.interview.action.Action;
import org.interview.utils.HostUtil;

public class ActionMapper extends Action {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ETLMapper mapper;
	
	public ActionMapper(ETLMapper mapper){
		this.mapper = mapper;
	}

	@Override
	public void doAction() {
		String key = mapper.getInputFormat().getKey();
		try {
			if(!StringUtils.containsIgnoreCase(key, HostUtil.HOSTNAME) && !StringUtils.containsIgnoreCase(key, HostUtil.IP)) {
				return;
			}
			mapper.map(mapper.getInputFormat().getKey(), mapper.getInputFormat(), null);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public ETLMapper getMapper() {
		return mapper;
	}

	public void setMapper(ETLMapper mapper) {
		this.mapper = mapper;
	}

}
