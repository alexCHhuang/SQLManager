package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class jdbcmysql {
		
	private Connection con = null; //Database objects 
	//連接object 
	private Statement stat = null; 
	//執行,傳入之sql為完整字串 
	private ResultSet rs = null; 
	//結果集 
	private PreparedStatement pst = null; 
	//執行,傳入之sql為預儲之字申,需要傳入變數之位置 
	//先利用?來做標示 
	Out jdbcmysqlout = null;
	
	// Define query type as a constant
	public static final int SELECT_TYPE = 1;
	public static final int INSERT_TYPE = 2;
	public static final int UPDATE_TYPE = 3;
	public static final int DELETE_TYPE = 4;
	public static final int UNKNOWN_TYPE = 9;
	
	public jdbcmysql(Out out){
		try{
			Class.forName("com.mysql.jdbc.Driver"); 
			//註冊driver 
			con = DriverManager.getConnection( 
			"jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=Big5", 
			"root","1234"); 
			//取得connection
			//jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=Big5
			//localhost是主機名,test是database名
			//useUnicode=true&characterEncoding=Big5使用的編碼
			jdbcmysqlout = out;
		}
		catch(ClassNotFoundException e) 
	    { 
			System.out.println("DriverClassNotFound :"+e.toString()); 
	    }//有可能會產生sqlexception 
	    catch(SQLException x) { 
			System.out.println("Exception :"+x.toString()); 
	    } 
	}
	// UPDATE table_name
	// SET column1=value1, column2=value2,...
	// WHERE some_column=some_value;
	public void UpdateRecord(String str)
	{
		try
		{
			stat = con.createStatement();
			stat.executeUpdate(str);
		}
		catch (SQLException e)
		{
			System.out.println("DeleteRecord Exception:" + e.toString());
		}
		finally{
			// Send the signal to client when query result are sent completely.
			jdbcmysqlout.println("EOL");
			Close();
		}
	}
	
	// INSERT INTO table_name (column1,column2,column3,...)
	// VALUES (value1,value2,value3,...);
	public void InsertRecord(String str)
	{
		String insertTableSQL = "INSERT INTO User"
				+ "(id, name, passwd) VALUES"
				+ "(?,?,?)";
		String[] token = str.split("VALUES");
		String value = token[1];
		value = value.replaceAll("[( )]", "");
		String[] values = value.split(",");
		try{
			pst = con.prepareStatement(insertTableSQL);
			// the first one element must be integer
			pst.setInt(1, Integer.parseInt(values[0]));
			// the remaining elements must be string
			for(int i = 1; i < values.length; i++)
			{
				pst.setString(i+1, values[i]);
			}
			// execute insert SQL stetement
			pst.executeUpdate();
		}
		catch(SQLException e) 
		{ 
			System.out.println("InsertDB Exception :" + e.toString()); 
		}
		catch(Exception e)
		{	//Handle errors for Class.forName
			System.out.println("Handle errors for Class.forName :" + e.toString()); 
		}
		finally{
			// Send the signal to client when query result are sent completely.
			jdbcmysqlout.println("EOL");
			Close();
		}
	}
	
	//	The generic SQL syntax of DELETE command:
	//	DELETE FROM table_name [WHERE Clause]
	//	DeleteRecord() function consists two steps,
	//	1. use select to query records which match the WHERE constraint
	//	2. use deleteRow() to delete the corresponding records
	public void DeleteRecord(String str)
	{
		try{
			stat = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			rs = stat.executeQuery(str.replaceFirst("DELETE", "SELECT *"));
			rs.last();
			rs.deleteRow();
			jdbcmysqlout.println("Delete operation is complete");
		}
		catch (SQLException e)
		{
			System.out.println("DeleteRecord Exception:" + e.toString());
		}
		finally{
			// Send the signal to client when query result are sent completely.
			jdbcmysqlout.println("EOL");
			Close();
		}
	}
	
	public void SelectTable(String str)
	{ 
		try 
		{ 
			stat = con.createStatement(); 
			rs = stat.executeQuery(str); 
			//System.out.println("ID\t\tName\t\tPASSWORD"); 
			jdbcmysqlout.println("ID\t\tName\t\tPASSWORD");
		while(rs.next()) 
		{ 
			
			//System.out.println(rs.getInt("id")+"\t\t"+ rs.getString("name")+"\t\t"+rs.getString("passwd")); 
			jdbcmysqlout.println(rs.getInt("id")+"\t\t"+ rs.getString("name")+"\t\t"+rs.getString("passwd"));
		} 
		} 
		catch(SQLException e) 
		{ 
			System.out.println("DropDB Exception :" + e.toString()); 
		} 
		finally 
		{ 
			// Send the signal to client when query result are sent completely.
			jdbcmysqlout.println("EOL");
			Close(); 
		} 
	}
	// Parse query type 
	public int CheckQueryType(String str){
		String[] token = str.split(" ");
		if(new String("Select").equalsIgnoreCase(token[0]))
			return SELECT_TYPE;
		else if(new String("Insert").equalsIgnoreCase(token[0]))
			return INSERT_TYPE;
		else if(new String("Update").equalsIgnoreCase(token[0]))
			return UPDATE_TYPE;
		else if(new String("Delete").equalsIgnoreCase(token[0]))
			return DELETE_TYPE;
		else
			return UNKNOWN_TYPE;
	}
	private void Close() 
	{ 
		try 
		{ 
			if(rs!=null) 
			{ 
				rs.close(); 
				rs = null; 
			} 
			if(stat!=null) 
			{ 
				stat.close(); 
				stat = null; 
			} 
			if(pst!=null) 
			{ 
				pst.close(); 
				pst = null; 
			} 
		} 
		catch(SQLException e) 
		{ 
			System.out.println("Close Exception :" + e.toString()); 
		} 
	} 
}
