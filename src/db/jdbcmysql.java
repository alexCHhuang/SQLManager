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
			"jdbc:mysql://localhost/employees?useUnicode=true&characterEncoding=Big5", 
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
		String insertTableSQL = "INSERT INTO employees"
				+ "(emp_no, birth_date, first_name, last_name, gender, hire_date) VALUES"
				+ "(?,?,?,?,?,?)";
		String[] token = str.split("VALUES");
		String value = token[1];
		value = value.replaceAll("[( )]", "");
		String[] values = value.split(",");
		try{
			pst = con.prepareStatement(insertTableSQL);
			// the first one element must be integer
			pst.setInt(1, Integer.parseInt(values[0]));
			// the second column type is date YYYY-MM-DD
			pst.setDate(2, java.sql.Date.valueOf(values[1]));
			// the third column type is varchar
			pst.setString(3, values[2]);
			// the fourth column type is varchar
			pst.setString(4, values[3]);
			// the fifth column type is enum
			pst.setString(5, values[4]);
			// the sixth column type is date
			pst.setDate(6, java.sql.Date.valueOf(values[5]));
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
			jdbcmysqlout.println("emp_no\tbirth_date\tfirst_name\tlast_name\tgender\thire_date");
		while(rs.next()) 
		{ 
			
			//System.out.println(rs.getInt("id")+"\t\t"+ rs.getString("name")+"\t\t"+rs.getString("passwd")); 
			jdbcmysqlout.println(rs.getInt("emp_no")+"\t"+ rs.getDate("birth_date")+"\t"+
					rs.getString("first_name")+"\t"+rs.getString("last_name")+"\t"+
					rs.getString("gender")+"\t"+rs.getDate("hire_date"));
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
