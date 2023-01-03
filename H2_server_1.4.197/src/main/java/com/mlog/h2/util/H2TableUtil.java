package com.mlog.h2.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.h2.common.Constant;
import com.mlog.h2.common.Utils;
import com.mlog.h2.connector.H2Connector;

public class H2TableUtil {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String BACKUP_PATH = Utils.getProperty(Constant.BACKUP_PATH);

	private BigInteger lastId = BigInteger.ZERO;

	public int deleteTable(String name, String column) {
		int retv = Constant.FAILURE;

		if (name == null || column == null) {
			return retv;
		}

		try (Connection connection = H2Connector.getConnection()) {
			if (connection == null) {
				return retv;
			}

			String sql = String.format("DELETE FROM %s WHERE AUTO_ID <= '%s'", name, lastId);
			Statement stmt = connection.createStatement();

			stmt.execute(sql);
			connection.commit();

			retv = Constant.SUCCESS;
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	public int backupTable(String name, String column, String separator) {
		int retv = Constant.FAILURE;

		if (name == null || column == null) {
			return retv;
		}
		
		if (separator == null) {
			separator = ",";
		}

		try (Connection connection = H2Connector.getConnection()) {
			BigInteger currId = selectRowCount(name);

			if (currId == BigInteger.ZERO || currId.compareTo(lastId) == 0) {
				return retv;
			}

			Statement stmt = connection.createStatement();

			String sourcePath = BACKUP_PATH + "/" + name + "/" + "temp.CSV";
			String targetPath = BACKUP_PATH + "/" + name + "/" + name + "_" + Utils.getTime(3) + ".CSV";

			String sql = String.format(
					"CALL CSVWRITE('%s', 'SELECT %s FROM %s WHERE AUTO_ID BETWEEN %s AND %s', 'UTF-8', '%s')", sourcePath,
					column, name, lastId.add(BigInteger.ONE), currId, separator);

			stmt.execute(sql);

			lastId = currId;

			renameOrAppendFile(sourcePath, targetPath, name);

			retv = Constant.SUCCESS;
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	public int restoreTable(String name, String column, String separator) {
		int retv = Constant.FAILURE;

		if (name == null || column == null) {
			return retv;
		}
		
		if (separator == null) {
			separator = ",";
		}

		try (Connection connection = H2Connector.getConnection()) {
			String dataPath = BACKUP_PATH + "/" + name;

			File[] fileList = new File(dataPath).listFiles();
			if (fileList == null) {
				return retv;
			}

			for (File file : fileList) {
				if (file == null || !file.isFile()) {
					continue;
				}

				String path = file.getAbsolutePath();
				if (path == null) {
					continue;
				}

				String sql = String.format(
						"INSERT INTO %s (%s) SELECT %s FROM CSVREAD('%s', null, 'charset=UTF-8 fieldSeparator=%s')",
						name, column, column, path, separator);				

				Statement stmt = connection.createStatement();
				stmt.execute(sql);
			}

			lastId = selectRowCount(name);

			retv = Constant.SUCCESS;
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	public BigInteger selectRowCount(String name) {
		BigInteger count = BigInteger.ZERO;

		try (Connection connection = H2Connector.getConnection()) {
			String sql = String.format("SELECT NVL2(MAX(AUTO_ID), MAX(AUTO_ID), 0) FROM %s", name);

			Statement stmt = connection.createStatement();
			ResultSet rest = stmt.executeQuery(sql);

			if (rest != null) {
				rest.first();
				count = rest.getBigDecimal(1).toBigInteger();
			}
			
			// logger.debug("{}", count);

		}catch (Exception e) {
			logger.error("", e);
		}

		return count;
	}

	public void renameOrAppendFile(String sourcePath, String targetPath, String name) {
		if (sourcePath == null || targetPath == null) {
			return;
		}
		
		File sourceFile = new File(sourcePath);
		File targetFile = new File(targetPath);

		try {
			String line = "";
			
			if (targetFile.exists() == true) {
				FileWriter fileWriter = new FileWriter(targetFile, true);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

				int count = 0;
				while ((line = bufferedReader.readLine()) != null) {
					if (count == 0) {
						count++;
						continue;
					}

					bufferedWriter.write(line);
					bufferedWriter.write("\n");
				}

				bufferedWriter.flush();
				bufferedReader.close();
				bufferedWriter.close();
			} else {
				sourceFile.renameTo(targetFile);

				// delete stored backup data file.
//				File oldFile = new File(BACKUP_PATH + "/" + name + "/" + name + "_" + Utils.diffDate("HH", Utils.getTime(3), -2) + ".CSV");
//				if (oldFile.exists()) {
//					oldFile.delete();
//				}
			}

			sourceFile.delete();
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
}
