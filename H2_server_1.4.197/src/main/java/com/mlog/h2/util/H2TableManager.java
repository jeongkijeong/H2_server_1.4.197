package com.mlog.h2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.h2.common.Constant;
import com.mlog.h2.common.Utils;
import com.mlog.h2.context.TimeHandler;
import com.mlog.h2.main.ProcessManager;


public class H2TableManager implements ProcessManager {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private int worker_count = 1;
	private int active_index = 0;

	private String BACKUP_INFO = Utils.getProperty(Constant.BACKUP_INFO);

	private List<TimeHandler> handlerList = null;

	private static H2TableManager instance = null;

	public synchronized static H2TableManager getInstance() {
		if (instance == null) {
			instance = new H2TableManager();
		}

		return instance;
	}

	@Override
	public void start() {
		try {
			handlerList = new ArrayList<TimeHandler>();
			TimeHandler handler = null;

			List<Map<String, String>> tableList = Utils.tableXmlParse(BACKUP_INFO);
			
			if (tableList != null) {
				for (Map<String, String> table : tableList) {
					String columns = table.get(Constant.TAG_COLUMNS);
					String tableNm = table.get(Constant.TAG_TABLENM);
					String restore = table.get(Constant.TAG_RESTORE);
					String period  = table.get(Constant.TAG_PERIOD);

					if (tableNm == null || columns == null || period == null || restore == null) {
						continue;
					}

					logger.debug("{} / {} / {} / {}", tableNm, columns, period, restore);

					Thread thread = new Thread(handler = new H2TableHandler(tableNm, columns, Integer.valueOf(period),
							Boolean.valueOf(restore), false));
					thread.start();

					handlerList.add(handler);
				}

				worker_count = tableList.size();
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}

		logger.info("start [{}] / [{}]", getClass().getSimpleName(), worker_count);
	}

	@Override
	public void close() {
		Constant.run = false;
	}

	@Override
	public void address(Object object) {
		if (handlerList != null && handlerList.size() >= active_index) {
			handlerList.get(active_index++).put(object);
		} else {
			return;
		}

		if (active_index % worker_count == 0) {
			active_index = 0;
		}
	}
	
}
