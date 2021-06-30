package ru.sidey383.essential.dev.db.table;

import java.util.Arrays;

public class SQLField {
	
	private final String name;
	private final String type;
	private final String params[];
	
	/**
	 * @param name - name of field
	 * @param type - type of field
	 * 	for example INT, BOOL, varchar(255)
	 * @param params - params as NOT NULL, DEFAULT, PRIMARY KEY and etc.
	 * **/
	public SQLField(String name, String type, String... params) 
	{
		this.name = name;
		this.type = type;
		this.params = params;
	}
	
	/***
	 * use for create tables
	 * for example, "id INT PRIMARY KEY NOT NULL"
	 * **/
	public String getFullName() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getName()).append(" ").append(getType());
		for(String param: params) 
		{
			builder.append(" ").append(param);
		}
		return builder.toString();
	}

	public String getName() 
	{
		return name;
	}
	
	public String getType() 
	{
		return type;
	}
	
	public String[] getParams() 
	{
		return Arrays.copyOf(params, params.length);
	}
	
	public String toString() 
	{
		return getName();
	}

}
