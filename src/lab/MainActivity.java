package lab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class MainActivity {
	
	final static String FILE_NAME1 = "/Users/Yanhan/Downloads/Study/Database/lab/sorted10000";
	final static String FILE_NAME2 = "/Users/Yanhan/Downloads/Study/Database/lab/unsorted10000";	
	final static String DATABASE_NAME = "sample_db";

	static class BenchMark{
		int theKey;
		int columnA;
		int columnB;
		String filler;
		BenchMark(int theKey, int columnA, int columnB, String filler){
			this.theKey = theKey;
			this.columnA = columnA;
			this.columnB = columnB;
			this.filler = filler;
		}
		public String toString(){
			return "" + theKey + " " + columnA + " " + columnB + " " + filler;
		}
	}
	
	private static void insert(Statement st, String fileName, String tableName, Connection conn){
		FileReader fr;
		double progress = 0;
		try {
			fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String str = "";	
			int counter = 0;   
			String insertTableSQL = "INSERT INTO " + tableName + "(theKey, columnA, columnB, filler) VALUES" + "(?,?,?,?)";	
			PreparedStatement ps = conn.prepareStatement(insertTableSQL);
			
			while( (str = br.readLine()) != null){
				counter++;
				while(counter == 1000000){
					counter -= 1000000;
					progress += 0.2;
					System.out.println(tableName + " progrss: " + progress);
				}
				String[] strs = str.split(" ");
				int theKey = Integer.valueOf(strs[0]);
				int columnA = Integer.valueOf(strs[1]);
				int columnB = Integer.valueOf(strs[2]);
				String filler = strs[3];
				ps.setInt(1, theKey);
				ps.setInt(2, columnA);
				ps.setInt(3, columnB);
				ps.setString(4, filler);
				ps.addBatch();
			}
			long startTime = System.nanoTime();
			ps.executeBatch();
			conn.commit();
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			br.close();
			long milisec = duration/1000000;
			long sec = milisec/1000;
			
			System.out.println("insertion: " + tableName + ": " + sec + "sec");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void show(Statement st, String tableName){
		ResultSet rs;
		try {
			String query = "SELECT * FROM " +  tableName;
			rs = st.executeQuery(query);
			while (rs.next())
			{
			   System.out.println(rs.getString(1) + ", " + rs.getString(2) + ", " + 
					   rs.getString(3) + ", " + rs.getString(4));
			} 
			rs.close();
			st.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		String url = "jdbc:postgresql://localhost/" + DATABASE_NAME;		
		Properties props = new Properties();
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, props);
			conn.setAutoCommit(false);
			Statement st = conn.createStatement();
			lab2(conn, st);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static void lab2(Connection conn, Statement st){		
		try {
			
			Random r = new Random();
			String prefix = "oo" + r.nextInt(1000);
			String table0 = prefix + "sortedNoIndex";
			String table1 = prefix + "sortedIndex1";
			String table2 = prefix + "sortedIndex2";
			String table3 = prefix + "sortedIndex3";
			String table4 = prefix + "unSortedNoIndex";
			String table5 = prefix + "unSortedIndex1";
			String table6 = prefix + "unSortedIndex2";
			String table7 = prefix + "unSortedIndex3";
			
			List<String> tables = new ArrayList<String>();
			
			tables.add(table0);
			tables.add(table1);
			tables.add(table2);
			tables.add(table3);
			tables.add(table4);
			tables.add(table5);
			tables.add(table6);
			tables.add(table7);
			
			int option = 0;
			for(int i = 0; i < tables.size(); i++){
				if(option == 4){
					option = 0;
				}
				createTable(st, tables.get(i), option);
			}
			
			
			for(int i = 0; i< tables.size(); i++){
				if(i < tables.size() / 2){
					insert(st, FILE_NAME1, tables.get(i), conn);
				}
				else{
					insert(st, FILE_NAME2, tables.get(i), conn);
				}	
			}
			
			for(String table: tables){
				query(st, table, 1);
				query(st, table, 2);
				query(st, table, 3);
				System.out.println();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	private static void createTable(Statement st, String tableName, int index) throws Exception{
		String query = "create table " + tableName + " ( theKey INT PRIMARY KEY, columnA INT, columnB INT, filler CHAR(247));";
		st.executeUpdate(query);
		System.out.println(tableName + ": create successfully");
		if(index == 0){
		}
		else if(index == 1){
			String command = "CREATE INDEX ON " + tableName + " (columnA)";
			st.executeUpdate(command);
			System.out.println("index: " + index + " successfully");
		}
		else if(index == 2){
			String command = "CREATE INDEX ON " + tableName + " (columnB)";
			st.executeUpdate(command);
			System.out.println("index: " + index + " successfully");
		}else if(index == 3){
			String command1 = "CREATE INDEX ON " + tableName + " (columnA)";
			st.executeUpdate(command1);
			String command2 = "CREATE INDEX ON " + tableName + " (columnB)";
			st.executeUpdate(command2);
			System.out.println("index: " + index + " successfully");
		}else{
			System.out.println("wrong index");
		}
	}
	
	
	private static void query(Statement st, String tableName, int option) throws Exception{
		String query;
		if(option == 1){
			query = "SELECT * FROM " + tableName + " WHERE " + tableName + ".columnA = 25000;";
		}
		else if(option == 2){
			query = "SELECT * FROM " + tableName + " WHERE " + tableName + ".columnB = 25000;";
		}else if(option == 3){
			query = "SELECT * FROM " + tableName + " WHERE " + tableName + ".columnA = 25000" + " AND " + 
					tableName + ".columnB = 25000;";
		}else{
			System.out.println("wrong result, no option available");
			return;
		}
		long startTime = System.nanoTime();
		st.executeQuery(query);
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		long micro = duration/1000;
		long milisec = duration/1000000;
		System.out.println(tableName + ": query " + option + ": (micro): " + micro);
	}
}
	

