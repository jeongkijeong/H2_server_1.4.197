package com.mlog.h2.context;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.h2.common.Constant;
import com.mlog.h2.main.ProcessManager;
import com.mlog.h2.server.H2DatabaseManager;
import com.mlog.h2.util.H2TableManager;

public class ContextManager {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private List<ProcessManager> managerList = null;

	private static ContextManager instance;
	
	public static ContextManager getInstance() {
		if (instance == null) {
			instance = new ContextManager();
		}

		return instance;
	}
	
	public ContextManager() {
		managerList = new ArrayList<ProcessManager>();

		managerList.add(H2DatabaseManager.getInstance());
		managerList.add(H2TableManager.getInstance());
	}

	public void start() {
		logger.info(this.getClass().getSimpleName() + " start");
		
		Constant.run = true;
		try {
			for (ProcessManager manager : managerList) {
				manager.start();
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		logger.info(this.getClass().getSimpleName() + " start completed");
	}

	public void close() {
		logger.info(this.getClass().getSimpleName() + " close");

		Constant.run = false;
		try {
			for (ProcessManager manager : managerList) {
				manager.close();
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		logger.info(this.getClass().getSimpleName() + " close completed");
		
		System.exit(0);
	}
}
