package com.mlog.h2.common;


public class Constant {
	public final static int SUCCESS = +0;
	public final static int FAILURE = -1;

	public final static String DEFAULT_LOG_FILE_PATH = "./cfg/logback.xml";
	public final static String DEFAULT_CFG_FILE_PATH = "./cfg/server.properties";

	public static boolean run = false;
	
	public static final String DB_DRIVER = "h2.driver";
	public static final String DB_USER = "h2.username";
	public static final String DB_PASS = "h2.password";
	public static final String DB_PORT = "h2.port";
	public static final String DB_NAME = "h2.name";

	public static final String SCHEMA_PATH = "h2.schema.path";
	public static final String BACKUP_PATH = "h2.backup.path";
	public static final String BACKUP_INFO = "h2.backup.info";

	public static final String TAG_TABLE   = "table";
	public static final String TAG_TABLENM = "name";
	public static final String TAG_COLUMNS = "column";
	public static final String TAG_PERIOD  = "period";
	public static final String TAG_RESTORE = "restore";
}
