package com.mlog.h2.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.h2.common.Constant;
import com.mlog.h2.context.TimeHandler;

public class H2TableHandler extends TimeHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private H2TableUtil h2TableUtil = null;

	private String tableNm;
	private String columns;

	private boolean afterDelete = false;

	public H2TableHandler() {
		super();

		h2TableUtil = new H2TableUtil();
	}
	
	public H2TableHandler(String tableNm, String columns, int period, boolean restore, boolean afterDelete) {
		this();

		setTimeout(period);

		if (restore) {
			h2TableUtil.restoreTable(tableNm, columns, "||");
		}

		this.tableNm = tableNm;
		this.columns = columns;

		this.afterDelete = afterDelete;
	}

	@Override
	public void handler(Object object) {
		int retv = 0;

		try {
			retv = h2TableUtil.backupTable(this.tableNm, this.columns, "||");
			if (retv != Constant.SUCCESS) {
				return;
			}

			if (!afterDelete) {
				return;
			}

			retv = h2TableUtil.deleteTable(this.tableNm, this.columns);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
