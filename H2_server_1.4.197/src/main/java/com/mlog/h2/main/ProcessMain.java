package com.mlog.h2.main;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.h2.common.Constant;
import com.mlog.h2.common.Utils;
import com.mlog.h2.context.ContextManager;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class ProcessMain implements SignalHandler {
	private static final Logger logger = LoggerFactory.getLogger(ProcessMain.class);

	public static String NAME = "H2DB_1.4.197";

	public static void main(String args[]) {
		int retv = -1;

		String cfgFilePath = Constant.DEFAULT_CFG_FILE_PATH;
		String logFilePath = Constant.DEFAULT_LOG_FILE_PATH;

		if (args != null && args.length > 1) {
			cfgFilePath = args[0];
			logFilePath = args[1];
		}

		retv = Utils.loadProperties(cfgFilePath);
		if (retv < 0) {
			return;
		}

		retv = Utils.LoadLogConfigs(logFilePath);
		if (retv < 0) {
			return;
		}

		ProcessMain proccesMain = new ProcessMain();
		delegateHandler("INT" , proccesMain);
		delegateHandler("TERM", proccesMain);
		delegateHandler("ABRT", proccesMain);

		proccesMain.startProcess();

		while (Constant.run == true) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				logger.info(NAME + " process finish");
			}
		}

		logger.info(NAME + " process finish completed");
	}

	public static void delegateHandler(String signalName, SignalHandler signalHandler) {
		Signal signal = null;
		try {
			signal = new Signal(signalName);
			signalHandler = Signal.handle(signal, signalHandler);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	@Override
	public void handle(Signal signal) {
		logger.info("Receive SIG NAME[" + signal.getName() + "] / NUMB[" + signal.getNumber() + "]");
		closeProcess();
	}

	public void closeProcess() {
		logger.info(NAME + " process close");
		ContextManager contextManager = ContextManager.getInstance();
		contextManager.close();
	}

	public void startProcess() {
		logger.info(NAME + " process start");
		ContextManager contextManager = ContextManager.getInstance();
		contextManager.start();
	}
	
}
