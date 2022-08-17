package com.p3.tmb.core.sip;

import com.opentext.ia.sdk.sip.*;
import com.opentext.ia.sdk.support.io.FileSupplier;
import com.opentext.ia.sip.assembly.stringtemplate.StringTemplate;
import com.p3.tmb.beans.propertyBean;

import java.io.File;

public class SipCreator {

	private String appName;
	private String holding;
	private String producer;
	private String namespace;
	private String entity;
	private long rpx;
	protected String outputPath;
	protected BatchSipAssembler<RecordData> batchAssembler;
	private String sipFileName;
	private int tableFileCount;
	private propertyBean propBean;

	public SipCreator(propertyBean propBean, String schemaName, String tableName, String holdingName, int tableFileCount)
			throws Exception {
		this.propBean = propBean;
		this.outputPath = propBean.getOutputLocation() + File.separator + schemaName;
		this.rpx = 100 * 1024 * 1024;
		this.appName = propBean.getApplicationName();
		this.holding = holdingName + "_" + tableName;
		this.producer = propBean.getProducer();
		this.entity = propBean.getEntity();
		this.namespace = "urn:x-emc:eas:schema:" + holding + ":1.0";
		this.sipFileName = tableName;
		this.tableFileCount = tableFileCount;
		create();
	}

	private void create() {

		PackagingInformation prototype = PackagingInformation.builder().dss().application(appName).holding(holding)
				.producer(producer).entity(entity).schema(namespace).end().build();

		PackagingInformationFactory factory = new OneSipPerDssPackagingInformationFactory(
				new DefaultPackagingInformationFactory(prototype), new SequentialDssIdSupplier("ex6dss", 1));

		String sipHeader = "<TABLE_" + sipFileName.toUpperCase() + " xmlns=\"" + namespace + "\">\n";
		String sipFooter = "</TABLE_" + sipFileName.toUpperCase() + ">\n";

		PdiAssembler<RecordData> pdiAssembler = new TemplatePdiAssembler<>(
				new StringTemplate<>(sipHeader, sipFooter, "$model.data$\n"));

		SipAssembler<RecordData> sipAssembler = SipAssembler.forPdiAndContent(factory, pdiAssembler,
				new FilesToDigitalObjects());

		batchAssembler = new BatchSipAssembler<>(sipAssembler, SipSegmentationStrategy.byMaxSipSize(rpx), FileSupplier
				.fromDirectory(new File(outputPath), "sip-" + sipFileName + "-" + tableFileCount + "-", ".zip"));
	}

	public BatchSipAssembler<RecordData> getBatchAssembler() {
		return batchAssembler;
	}

}
