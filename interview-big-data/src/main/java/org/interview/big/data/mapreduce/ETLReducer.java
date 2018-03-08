package org.interview.big.data.mapreduce;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 简化
 * @author shersfy
 * @date 2018-03-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class ETLReducer extends Reducer<String, OutputFormatText, String, OutputFormatText> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ETLReducer.class);
	
	private OutputFormatText inputPath;
	private OutputFormatText outputFormat;
	
	public ETLReducer() {}
	public ETLReducer(OutputFormatText inputPath, OutputFormatText outputFormat) {
		super();
		this.inputPath = inputPath;
		this.outputFormat = outputFormat;
	}

	@Override
	protected void reduce(String inputKey, Iterable<OutputFormatText> inputPath,
			Reducer<String, OutputFormatText, String, OutputFormatText>.Context context)
			throws IOException, InterruptedException {
		
		LOGGER.info("executing reduce {} ...", inputKey);
		Iterator<OutputFormatText> iterator = inputPath.iterator();
		while(iterator.hasNext()) {
			OutputFormatText tmp = iterator.next();
			LOGGER.info("merging {} ...", tmp.getPath());
		}
		LOGGER.info("executed reduce {}", inputKey);
	}

	public OutputFormatText getOutputFormat() {
		return outputFormat;
	}


	public void setOutputFormat(OutputFormatText outputFormat) {
		this.outputFormat = outputFormat;
	}


	public OutputFormatText getInputPath() {
		return inputPath;
	}


	public void setInputPath(OutputFormatText inputPath) {
		this.inputPath = inputPath;
	}



}
