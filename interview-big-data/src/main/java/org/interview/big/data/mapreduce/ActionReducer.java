package org.interview.big.data.mapreduce;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.interview.action.Action;
import org.interview.utils.HostUtil;

public class ActionReducer extends Action {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ETLReducer reducer;
	
	public ActionReducer(ETLReducer reducer){
		this.reducer = reducer;
	}

	@Override
	public void doAction() {
		String key = reducer.getInputPath().getKey();
		try {
			
			if(!StringUtils.containsIgnoreCase(key, HostUtil.HOSTNAME) && !StringUtils.containsIgnoreCase(key, HostUtil.IP)) {
				return;
			}
			
			List<OutputFormatText> tmpFiles = new ArrayList<>();
			for(String tmp :reducer.getInputPath().getTmpFiles()) {
				tmpFiles.add(new OutputFormatText(tmp));
			}
			
			Iterable<OutputFormatText> it = new Iterable<OutputFormatText>() {
				
				@Override
				public Iterator<OutputFormatText> iterator() {
					return tmpFiles.iterator();
				}
			};
			reducer.reduce(key, it, null);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public ETLReducer getReducer() {
		return reducer;
	}

	public void setReducer(ETLReducer reducer) {
		this.reducer = reducer;
	}


}
