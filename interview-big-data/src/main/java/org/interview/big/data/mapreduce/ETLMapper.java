package org.interview.big.data.mapreduce;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.mapreduce.Mapper;
import org.interview.connector.relationship.DbConnectorInterface;
import org.interview.jgroups.cluster.ClusterMessage;
import org.interview.jgroups.cluster.ClusterServer;
import org.interview.utils.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * 映射
 * Mapper<K1, V1, K2, V2><br/>
 * K1V1: 输入键值对<br/>
 * V2V2: 输出键值对<br/>
 * 
 * @author shersfy
 * @date 2018-03-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class ETLMapper extends Mapper<String, InputFormatDB, String, OutputFormatText> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ETLMapper.class);
	
	private InputFormatDB inputFormat;
	private OutputFormatText outputFormat;
	
	public ETLMapper() {}
	
	public ETLMapper(InputFormatDB inputFormat, OutputFormatText outputFormat) {
		super();
		this.inputFormat = inputFormat;
		this.outputFormat = outputFormat;
	}

	@Override
	protected void map(String key, InputFormatDB value, Mapper<String, InputFormatDB, String, OutputFormatText>.Context context)
			throws IOException, InterruptedException {
		LOGGER.info("start running mapper {}", key);
		DbConnectorInterface connector = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		OutputStream output = null;
		try {
			connector = DbConnectorInterface.getInstance(value.getDbinfo());
			conn = connector.connection();
			File part = new File(outputFormat.getPath(), key+".txt");
			pstmt = conn.prepareStatement(value.getSplitSql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			connector.prepareStatementByCursor(pstmt);
			rs = pstmt.executeQuery();
			
			output = new FileOutputStream(part, true);
			// 输出数据
			StringBuilder line = new StringBuilder();
			int cnt = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				line.setLength(0);
				for(int i=1; i<=cnt; i++) {
					line.append(rs.getString(i));
					if(i!=cnt) {
						line.append(outputFormat.getColumnSep());
					}
				}
				line.append("\n");
				
				IOUtils.write(line.toString(), output, CharsetUtil.getUTF8().name());
				output.flush();
			}
			
			outputFormat.setPath(part.getAbsolutePath());
			ClusterMessage msg = new ClusterMessage(null, JSON.toJSONString(new TmpMessage(key, outputFormat)));
			ClusterServer.getInstance(APP.clusterConf).sendMessage(msg);
			
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if(connector!=null) {
				connector.close(rs, pstmt, conn);
			}
			IOUtils.closeQuietly(output);
		}
		LOGGER.info("end running mapper {}", key);
	}



	public InputFormatDB getInputFormat() {
		return inputFormat;
	}


	public void setInputFormat(InputFormatDB inputFormat) {
		this.inputFormat = inputFormat;
	}



	public OutputFormatText getOutputFormat() {
		return outputFormat;
	}



	public void setOutputFormat(OutputFormatText outputFormat) {
		this.outputFormat = outputFormat;
	}

}
