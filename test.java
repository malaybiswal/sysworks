import java.sql.*;
 
public class test {
 
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		int i=0;
		String myDriver = "";
	      String myUrl = "";
	      java.sql.Connection conn1 = null;
	      String query=""; String sql ="";
	      PreparedStatement preparedStmt = null;
	      
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
		//Connection conn = DriverManager.getConnection("jdbc:sqlserver://rbadb-stage.dfw.intensive.int;user=malay.biswal;password=Z5I2Hg3N2VL5;database=Working");
		Connection conn = DriverManager.getConnection("jdbc:sqlserver://rbadb-live.dfw.intensive.int;user=malay.biswal;password=59ocDvZpQJix;database=Working");

		System.out.println("test");
		Statement sta = conn.createStatement();
		String Sql = "select total_elapsed_time/1000 AS [total elapsed time in ms], (total_elapsed_time/execution_count)/1000 AS [Avg Exec Time in ms] , max_elapsed_time/1000000 AS [MaxExecTime in sec], min_elapsed_time/1000 AS [MinExecTime in ms], (total_worker_time/execution_count)/1000 AS [Avg CPU Time in ms], qs.execution_count AS NumberOfExecs, (total_logical_writes+total_logical_Reads)/execution_count AS [Avg Logical IOs], max_logical_reads AS MaxLogicalReads, min_logical_reads AS MinLogicalReads, max_logical_writes AS MaxLogicalWrites"
				+", min_logical_writes AS MinLogicalWrites,(SELECT SUBSTRING(text,statement_start_offset/2,(CASE WHEN statement_end_offset = -1"
				+"then LEN(CONVERT(nvarchar(max), text)) * 2 ELSE statement_end_offset end -statement_start_offset)/2 ) FROM sys.dm_exec_sql_text(sql_handle)"
				+") AS query_text , total_rows AS total_rows , query_plan_hash AS query_plan_hash , sql_handle AS sql_handle FROM sys.dm_exec_query_stats qs where [max_elapsed_time]/1000000>30 ORDER BY [Avg Exec Time in ms] DESC;";
		ResultSet rs = sta.executeQuery(Sql);
		
		myDriver = "org.gjt.mm.mysql.Driver";
	       myUrl = "jdbc:mysql://localhost/test";
	      Class.forName(myDriver);
	      conn1 = DriverManager.getConnection(myUrl, "malay", "malay123");
	      query = " insert into stats ( totaltime_ms, avg_exec_time_ms, max_time_sec, min_time_ms, avg_cpu_time_ms,  exec_count, avg_logicalio, max_logical_reads, min_logical_reads, max_logical_writes, min_logical_writes, query_text, total_rows, query_plan_hash, sql_handle)"
	  	        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	      int len=1;
	      
		while (rs.next()) {
			preparedStmt = conn1.prepareStatement(query);
			preparedStmt.setInt (1, rs.getInt(1));
			preparedStmt.setInt (2, rs.getInt(2));
			preparedStmt.setInt (3, rs.getInt(3));
			preparedStmt.setInt (4, rs.getInt(4));
			preparedStmt.setInt (5, rs.getInt(5));
			preparedStmt.setInt (6, rs.getInt(6));
			preparedStmt.setInt (7, rs.getInt(7));
			preparedStmt.setInt (8, rs.getInt(8));
			preparedStmt.setInt (9, rs.getInt(9));
			preparedStmt.setInt (10, rs.getInt(10));
			preparedStmt.setInt (11, rs.getInt(11));
			if(rs.getString(12).length()>=3998){
				len=3998;
			}
			else
				len=rs.getString(12).length();
			preparedStmt.setString (12, rs.getString(12).substring(0,len));
			preparedStmt.setInt (13, rs.getInt(13));
			preparedStmt.setString (14, "0x"+rs.getString(14));
			preparedStmt.setString (15, "0x"+rs.getString(15));
			preparedStmt.execute();
			System.out.println(rs.getInt(1)+" "+rs.getString(2)+" "+rs.getString(13)+" "+rs.getString(14));
			i++;
		}
		conn.close();
		conn1.close();
		preparedStmt.close();
		rs.close();
		System.out.println(i);
	}
}
