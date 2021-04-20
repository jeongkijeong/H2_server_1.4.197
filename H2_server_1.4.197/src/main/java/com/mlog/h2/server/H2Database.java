package com.mlog.h2.server;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;

import org.h2.tools.RunScript;
import org.h2.tools.Server;

import com.mlog.h2.common.Constant;
import com.mlog.h2.common.Utils;

public class H2Database {
	private Server server = null;

	public String driver = null;
	public String dbName = null;
	public String dbUser = null;
	public String dbPass = null;
	public String dbPort = null;

	public H2Database() {
		super();

		setProperties();
	}

	private void setProperties() {
		driver = Utils.getProperty(Constant.DB_DRIVER);
		dbName = Utils.getProperty(Constant.DB_NAME);
		dbUser = Utils.getProperty(Constant.DB_USER);
		dbPass = Utils.getProperty(Constant.DB_PASS);
		dbPort = Utils.getProperty(Constant.DB_PORT);
	}

	public void start(String path) {
		String port = dbPort;

		try {
			if (server != null) {
				return;
			}

			if (port != null && port.length() > 0) {
				server = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", port).start();
			} else {
				server = Server.createTcpServer("-tcpAllowOthers").start();
			}

			Class.forName(driver);
			Connection conn = DriverManager
					.getConnection("jdbc:h2:mem:" + dbName + ";MULTI_THREADED=TRUE;DB_CLOSE_DELAY=-1", dbUser, dbPass);

//			Connection conn = DriverManager.getConnection("jdbc:h2:mem:DB_CLOSE_DELAY=-1" + dbName, dbUser, dbPass);

			if (path != null && path.length() > 0) {
				File schema = new File(path);
				if (schema.exists() == true) {
					RunScript.execute(conn, new FileReader(schema));
				} else {
					System.out.println("could not find script file in " + path);
				}
			}

			conn.close();

			System.out.println("Server started and connection is open.");
			System.out.println("URL: jdbc:h2:" + server.getURL() + "/mem:" + dbName);
		} catch (Exception e) {
			System.out.println("error " + e.getMessage());
		}
	}

	public void stop() {
		try {
			if (server != null) {
				server.stop();
				server.shutdown();
			}

		} catch (Exception e) {
			System.out.println("error " + e.getMessage());
		}
	}

//	public void restore() {
//		H2TableManager handler = new H2TableManager();
//		handler.resotreTable();
//	}
}
