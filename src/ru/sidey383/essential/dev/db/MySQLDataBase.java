package ru.sidey383.essential.dev.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLDataBase implements DataBase {

	private String url;
	private String username;
	private String password;
	
	public MySQLDataBase(String url, String username, String password) throws SQLException 
	{
		this.url = url;
		this.username = username;
		this.password = password;
		DriverManager.getConnection(url, username, password);
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username,password);
	}

	@Override
	public Statement getStatement() throws SQLException {
		return getConnection().createStatement();
	}
	
}
