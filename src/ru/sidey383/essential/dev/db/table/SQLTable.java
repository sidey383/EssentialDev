package ru.sidey383.essential.dev.db.table;

import java.util.Collections;
import java.util.List;


public abstract class SQLTable {

	private final List<SQLField> fields;
	
	private final String name;
	
	public SQLTable(String name) 
	{
		this.name = name;
		this.fields = Collections.unmodifiableList(tableFields());
	}
	
	public List<SQLField> getFields()
	{
		return fields;
	}
	
	public SQLField getField(String name) 
	{
		name = name.toLowerCase();
		for(SQLField field: fields)
			if(field.getName().toLowerCase().equals(name))
				return field;
		return null;
	}
	
	/**
	 * return table name
	 * **/
	public String getName() 
	{
		return name;
	}
	
	/**
	 * get SQL request for create this table
	 * **/
	public String getCreateString() 
	{
		if(fields == null || fields.size() == 0) return "CREATE TABLE IF NOT EXISTS "+getName()+";";
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE IF NOT EXISTS ").append(getName()).append(" (");
		for(SQLField field : fields) 
		{
			builder.append(field.getFullName()).append(", ");
		}
		int last = builder.length()-1;
		builder.delete(last-1, last);
		builder.append(");");
		return builder.toString();
	}
	
	/**
	 * @return fields of created table
	 * must be implemented
	 * **/
	protected abstract List<SQLField> tableFields();
	
}
