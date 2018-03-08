package org.interview.big.data.sqoop;

import java.util.List;

import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.client.SubmissionCallback;
import org.apache.sqoop.model.MConfig;
import org.apache.sqoop.model.MDriverConfig;
import org.apache.sqoop.model.MFromConfig;
import org.apache.sqoop.model.MInput;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.model.MLink;
import org.apache.sqoop.model.MLinkConfig;
import org.apache.sqoop.model.MSubmission;
import org.apache.sqoop.model.MToConfig;
import org.apache.sqoop.submission.counter.Counter;
import org.apache.sqoop.submission.counter.CounterGroup;
import org.apache.sqoop.submission.counter.Counters;
import org.apache.sqoop.validation.Message;
import org.apache.sqoop.validation.Status;

import org.interview.beans.DBAccessType;
import org.interview.connector.relationship.DbConnectorInterface;
import org.interview.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqoopClientUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SqoopClientUtil.class);

	private SqoopClientUtil() {}

	public static void submitJobForDb2Hdfs(InputParams inputParam, OutputParam outputParam) {

		if(inputParam == null 
				|| inputParam.getSrcDBInfo()==null
				|| outputParam==null 
				|| outputParam.getHdfsInfo()==null) {
			return ;
		}
		String userName = outputParam.getHdfsInfo().getUserName();
		//初始化
		String url = inputParam.getSqoopServerUrl();
		SqoopClient client = new SqoopClient(url);

		System.setProperty("HADOOP_USER_NAME", userName);
		System.setProperty("user.name", userName);

		try {
			DbConnectorInterface connector = DbConnectorInterface.getInstance(inputParam.getSrcDBInfo());

			//创建一个源链接 JDBC
			//	      String fromConnectorId = "2";

			MLink fromLink = client.createLink("generic-jdbc-connector");
			//			MLink fromLink = client.getLink(1);
			fromLink.setName("JDBCconnector"+System.nanoTime());
			fromLink.setCreationUser(userName);
			fromLink.setLastUpdateUser(userName);

			MLinkConfig fromLinkConfig = fromLink.getConnectorLinkConfig();
			fromLinkConfig.getStringInput("linkConfig.connectionString").setValue(connector.getDbMeta().getUrl());
			fromLinkConfig.getStringInput("linkConfig.jdbcDriver").setValue(connector.loadDriverClass(DBAccessType.JDBC));
			fromLinkConfig.getStringInput("linkConfig.username").setValue(connector.getDbMeta().getUserName());
			fromLinkConfig.getStringInput("linkConfig.password").setValue(connector.getDbMeta().getPassword());
			//			fromLinkConfig.getStringInput("dialect.identifierEnclose").setValue(" ");

			Status fromStatus = client.saveLink(fromLink);
			if(fromStatus.canProceed()) {
				LOGGER.info("create jdbc link successful, link id={}", fromLink.getPersistenceId());
			} else {
				LOGGER.info("create jdbc link failed");
			}
			//	        MLink fromLink = client.getLink(fromConnectorId);
			//创建一个目的地链接HDFS
			//	        String toConnectorId = "1";

			MLink toLink = client.createLink("hdfs-connector");
			//			MLink toLink = client.getLink(2);
			toLink.setName("HDFSconnector"+System.nanoTime());
			toLink.setCreationUser(userName);
			toLink.setLastUpdateUser(userName);

			MLinkConfig toLinkConfig = toLink.getConnectorLinkConfig();
			toLinkConfig.getStringInput("linkConfig.uri").setValue(outputParam.getHdfsInfo().getUrl());
			Status toStatus = client.saveLink(toLink);
			if(toStatus.canProceed()) {
				LOGGER.info("create hdfs link successful, link id={}", toLink.getPersistenceId());
			} else {
				LOGGER.info("create hdfs link failed");
			}
			//			MLink toLink = client.getLink(1);

			//创建一个任务
			long fromLinkId = fromLink.getPersistenceId();
			long toLinkId   = toLink.getPersistenceId();

			MJob job = client.createJob(fromLinkId, toLinkId);
			// job不能重名
			job.setName(String.format("job_mysql2hdfs_%s", DateUtil.format("yyyyMMdd_HHmmssSSS")));
			job.setCreationUser(userName);
			job.setLastUpdateUser(userName);

			//设置源链接任务配置信息
			MFromConfig fromJobConfig = job.getFromJobConfig();
			fromJobConfig.getStringInput("fromJobConfig.schemaName").setValue(inputParam.getSrcTable().getCatalog());
			fromJobConfig.getStringInput("fromJobConfig.tableName").setValue(inputParam.getSrcTable().getName());

			//			fromJobConfig.getStringInput("fromJobConfig.partitionColumn").setValue("id");
			MToConfig toJobConfig = job.getToJobConfig();
			toJobConfig.getStringInput("toJobConfig.outputDirectory").setValue(outputParam.getTarHdfsPath());
			MDriverConfig driverConfig = job.getDriverConfig();
			driverConfig.getIntegerInput("throttlingConfig.numExtractors").setValue(outputParam.getNumExtractors());

			Status status = client.saveJob(job);
			if(status.canProceed()) {
				LOGGER.info("create job successful, link id={}", job.getPersistenceId());
			} else {
				LOGGER.info("create job failed");
			}

			//启动任务
			long jobId = job.getPersistenceId();
			SubmissionCallback callback = new SubmissionCallback() {

				@Override
				public void updated(MSubmission submission) {
					LOGGER.info("status= {}, progress= {}%", submission.getStatus(), submission.getProgress()*100);
				}

				@Override
				public void submitted(MSubmission submission) {
					LOGGER.info("submitted :{}", submission.getStatus());

				}

				@Override
				public void finished(MSubmission submission) {
					LOGGER.info("finished :{}", submission.getStatus());

				}
			};

			MSubmission submission = client.startJob(jobId, callback, 3*1000);
			LOGGER.info("job {} submission status {}", jobId, submission.getStatus());
			if(submission.getStatus().isFailure()){
				List<Message> msgs = submission.getValidationMessages();
				Message error = null;
				for(Message msg :msgs){
					if(Status.ERROR == msg.getStatus()){
						error = error==null?msg:error;
						LOGGER.error(msg.getMessage());
					}
					else {
						LOGGER.info(msg.getMessage());
					}
				}
				if(error!=null){
					LOGGER.error(error.getMessage());
				}
			}

			LOGGER.info("hadoop job id={}", submission.getExternalJobId());
			Counters counters = submission.getCounters();
			if(counters != null) {
				LOGGER.info("=====Counter start=====");
				for(CounterGroup group : counters) {
					LOGGER.info("\t {}", group.getName());
					for(Counter counter : group) {
						LOGGER.info("\t\t {}={}", counter.getName(), counter.getValue());
					}
				}
				LOGGER.info("=====Counter end=====");
			}

			List<MLink> links = client.getLinks();
			for(MLink link :links){
				printMessage(link.getConnectorLinkConfig().getConfigs());
			}
			LOGGER.info("job {} successful executed");
		} catch (Exception e) {
			LOGGER.error("", e);
		}

	}
	
	private static void printMessage(List<MConfig> configs) {
		for(MConfig config : configs) {
			List<MInput<?>> inputlist = config.getInputs();
			if (config.getValidationMessages() != null) {
				for(Message message : config.getValidationMessages()) {
					LOGGER.info("config validation message: {}", message.getMessage());
				}
			}
			for (MInput<?> minput : inputlist) {
				if (minput.getValidationStatus() == Status.OK) {
					continue;
				}
				
				for(Message message : minput.getValidationMessages()) {
					if(minput.getValidationStatus() == Status.WARNING) {
						LOGGER.warn("config validation warning: {}", message.getMessage());
					}
					else if(minput.getValidationStatus() == Status.ERROR) {
						LOGGER.error("config validation error: {}", message.getMessage());
					}
				}
			}
		}
	}

}
