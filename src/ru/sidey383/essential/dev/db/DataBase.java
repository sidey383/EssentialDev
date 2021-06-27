package ru.sidey383.essential.dev.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface DataBase {

	public Connection getConnection() throws SQLException;
	
	public Statement getStatement() throws SQLException;
	
	
}
