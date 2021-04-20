package com.mlog.h2.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mlog.h2.common.Constant;
import com.mlog.h2.common.Utils;

public class H2Connector {
	public static Connection connection = null;
	
	private static String driver = Utils.getProperty(Constant.DB_DRIVER);
	private static String dbUser = Utils.getProperty(Constant.DB_USER);
	private static String dbPass = Utils.getProperty(Constant.DB_PASS);
	private static String dbName = Utils.getProperty(Constant.DB_NAME);

	public static Connection getConnection() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			Connection connection = DriverManager
					.getConnection("jdbc:h2:mem:" + dbName + ";MULTI_THREADED=TRUE;DB_CLOSE_DELAY=-1", dbUser, dbPass);

			return connection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return connection;
	}
}
