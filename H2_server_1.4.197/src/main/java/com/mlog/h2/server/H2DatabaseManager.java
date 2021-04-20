package com.mlog.h2.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.h2.common.Constant;
import com.mlog.h2.common.Utils;
import com.mlog.h2.main.ProcessManager;


public class H2DatabaseManager implements ProcessManager {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static H2DatabaseManager instance = null;

	private H2Database h2Database = null;
	private String schemaPath = null;

	public synchronized static H2DatabaseManager getInstance() {
		if (instance == null) {
			instance = new H2DatabaseManager();
		}

		return instance;
	}

	public H2DatabaseManager() {
		super();
		schemaPath = Utils.getProperty(Constant.SCHEMA_PATH);
	}

	@Override
	public void start() {
		logger.info(this.getClass().getSimpleName() + " start");

		if (h2Database == null) {
			h2Database = new H2Database();
		}

		h2Database.start(schemaPath); // start H2 memory DB as server mode.

		logger.info(this.getClass().getSimpleName() + " started");
	}

	@Override
	public void close() {
		logger.info(this.getClass().getSimpleName() + " close");
		
		if (h2Database != null) {
			h2Database.stop();
		}

		logger.info(this.getClass().getSimpleName() + " closed");
	}

	@Override
	public void address(Object object) {
	}

}
