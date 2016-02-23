package br.vbathke.helper;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jenkins.model.Jenkins;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;

import com.thoughtworks.xstream.io.json.JsonWriter;

public class SqliteHelper {
	private static Connection c = null;
	private static Connection cNewDb = null;
	public Statement stmt = null;
	public static boolean runReady = true;
	
	private void waitRunStart(){
		int semaforo = 0;
		for(int i=0; i<=60; i++){
			if(runReady){
				break;
			}
			try {
				Thread.sleep(500);
				semaforo+=500;
				System.out.println("semaforo ativo: "+semaforo);
			} catch (InterruptedException e) {} 
		}
	}
	
	private void setRunReady(boolean set){
		runReady = set;
	}

    public static JSONArray convertToJSON(ResultSet resultSet) throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int total_rows = resultSet.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {
                obj.put(""+resultSet.getMetaData().getColumnLabel(i + 1), ""+resultSet.getObject(i + 1));
            }
            jsonArray.add(obj);
        }
        return jsonArray;
    }	
	
	public JSONArray query(String query) throws Exception{
		ResultSet rs = null;
		//waitRunStart();
		JSONArray jsonArray = new JSONArray();
		setRunReady(false);
		try{
	        stmt = null;
	        stmt = getConn().createStatement();
			rs = stmt.executeQuery(query);
			jsonArray = convertToJSON(rs);
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(stmt!=null)stmt.close();
		setRunReady(true);		
		return jsonArray;
	}

	public boolean update(String query) throws Exception{
		boolean rs = false;
		//waitRunStart();
		setRunReady(false);
		try{
	        stmt = null;
	        stmt = getConn().createStatement();
	        stmt.executeUpdate(query);
	        rs = true;
		}catch(Exception e){
			System.out.println("falha: "+query);
			//e.printStackTrace();
		}
		if(stmt!=null)stmt.close();
		setRunReady(true);
		return rs;
	}
	
    public static Connection getConn()  throws Exception {
    	if(c == null){
        	Class.forName("org.sqlite.JDBC");
        	try{
    			c = DriverManager.getConnection("jdbc:sqlite:"+Jenkins.getInstance().getRootDir()+"/plugins/ui-test-capture/uitest.sqlite");
    			c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);        		
        	}catch(Exception e1){
    			c = DriverManager.getConnection("jdbc:sqlite:src/main/webapp/uitestdev.sqlite");
    			c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);        		
        	}
        	verifyDatabaseVersionMigration();
        }
        return c;
    }
    
    public static Connection getConnNewDb() throws Exception {
    	if(cNewDb == null){
        	Class.forName("org.sqlite.JDBC");
        	try{
        		cNewDb = DriverManager.getConnection("jdbc:sqlite:"+Jenkins.getInstance().getRootDir()+"/plugins/ui-test-capture/uitest-model.sqlite");
        		cNewDb.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);        		
        	}catch(Exception e1){
        		cNewDb = DriverManager.getConnection("jdbc:sqlite:src/main/webapp/uitest-model.sqlite");
        		cNewDb.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);        		
        	}
        }
        return cNewDb;
    }
    
    
    public static void verifyDatabaseVersionMigration() throws Exception{
    	File fprodModelDb = new File(Jenkins.getInstance().getRootDir()+"/plugins/ui-test-capture/uitest-model.sqlite");
    	
    	//se o arquivo modelo, realizar procedimento de exportação
    	if(fprodModelDb.exists() && !fprodModelDb.isDirectory()){ 
    		System.out.println("################################");
    		System.out.println("ATR-Database migration initiated");

    		System.out.println("ATR-Exporting data...");
    		StringBuffer data_tb_job =  new StringBuffer("");
    		StringBuffer data_tb_test =  new StringBuffer("");
    		StringBuffer data_tb_exec =  new StringBuffer("");
    		StringBuffer data_tb_result =  new StringBuffer("");
    		StringBuffer data_sqlite_sequence =  new StringBuffer("");
    		
            try{
            	try{
	    			ResultSet rs0 = getConn().createStatement().executeQuery( "SELECT * FROM sqlite_sequence");
	    			while (rs0.next()) {
	    			     String name = "";
	    			     int seq = 0;
	    			     try{name = rs0.getString("name");}catch(Exception e){}
	    			     try{seq = rs0.getInt("seq");}catch(Exception e){}
	    			     data_sqlite_sequence.append("insert into sqlite_sequence(name, seq) values('"+name+"','"+seq+"');\n");
	    			}
	    			rs0.close();
	    			getConn().createStatement().close();
	            	
	    	        ResultSet rs1 = getConn().createStatement().executeQuery( "SELECT * FROM tb_job");
	    			while (rs1.next()) {
	    			     int id = 0;
	    			     String name = "";
	    			     try{id = rs1.getInt("id");}catch(Exception e){}
	    			     try{name = rs1.getString("name");}catch(Exception e){}
	    			     data_tb_job.append("insert into tb_job(id,name) values('"+id+"','"+name+"');\n");
	    			}
	    			rs1.close();
	    			getConn().createStatement().close();
	
	    			ResultSet rs2 = getConn().createStatement().executeQuery( "SELECT * FROM tb_test");
	    			while (rs2.next()) {
	    			     int id = 0;
	    			     int id_job = 0;
	    			     String test = "";
	    			     String test_class = "";
	    			     String status = "";
	    			     String status_description = "";
	    			     String behavior = "";
	    			     try{id = rs2.getInt("id");}catch(Exception e){}
	    			     try{id_job = rs2.getInt("id_job");}catch(Exception e){}
	    			     try{test = rs2.getString("test");}catch(Exception e){}
	    			     try{test_class = rs2.getString("test_class");}catch(Exception e){}
	    			     try{status = rs2.getString("status");}catch(Exception e){}
	    			     try{status_description = rs2.getString("status_description");}catch(Exception e){}
	    			     try{behavior = rs2.getString("behavior");}catch(Exception e){}
	    			     data_tb_test.append("insert into tb_test(id,id_job, test, behavior, test_class, status, status_description) values('"+id+"','"+id_job+"','"+test+"','"+behavior+"','"+test_class+"','"+status+"','"+status_description+"');\n");
	    			}
	    			rs2.close();
	    			getConn().createStatement().close();
	
	    			ResultSet rs3 = getConn().createStatement().executeQuery( "SELECT * FROM tb_exec");
	    			while (rs3.next()) {
	    			     int id = 0;
	    			     int id_job = 0;
	    			     String version = "";
	    			     String date = "";
	    			     try{id = rs3.getInt("id");}catch(Exception e){}
	    			     try{id_job = rs3.getInt("id_job");}catch(Exception e){}
	    			     try{version = rs3.getString("version");}catch(Exception e){}
	    			     try{date = rs3.getString("date");}catch(Exception e){}
	    			     data_tb_exec.append("insert into tb_exec(id, id_job, version, date) values('"+id+"','"+id_job+"','"+version+"','"+date+"');\n");
	    			}
	    			rs3.close();
	    			getConn().createStatement().close();
	
	    			ResultSet rs4 = getConn().createStatement().executeQuery( "SELECT * FROM tb_result");
	    			while (rs4.next()) {
	    			     int id_exec = 0;
	    			     String test = "";
	    			     String status = "";
	    			     String description = "";
	    			     String stacktrace = "";
	    			     try{id_exec = rs4.getInt("id_exec");}catch(Exception e){}
	    			     try{test = rs4.getString("test");}catch(Exception e){}
	    			     try{status = rs4.getString("status");}catch(Exception e){}
	    			     try{description = rs4.getString("description");}catch(Exception e){}
	    			     try{stacktrace = rs4.getString("stacktrace");}catch(Exception e){}
	    			     data_tb_result.append("insert into tb_result(id_exec, test, status, description, stacktrace) values('"+id_exec+"','"+test+"','"+status+"','"+description+"','"+stacktrace+"');\n");
	    			}
	    			rs4.close();
	    			getConn().createStatement().close();

		    		try{
			            Statement stmt = getConnNewDb().createStatement();
			            stmt.executeUpdate(data_sqlite_sequence.toString());
			    	    stmt.executeUpdate(data_tb_job.toString());
			    	    stmt.executeUpdate(data_tb_test.toString());
			    	    stmt.executeUpdate(data_tb_exec.toString());
			    	    stmt.executeUpdate(data_tb_result.toString());
			    		stmt.close();
		    		}catch(Exception e){
			    		System.out.println("ATR-Fail executing migration queries!");	    			
		    		}
		    		c.close();
		    		cNewDb.close();
            	} catch (Exception e) {
		    		System.out.println("ATR-The previus db was not avaiable!");	    			
	    		}
	    		
	    		System.out.println("ATR-Backing-up existing db...");
	    		try{

	                SimpleDateFormat formatas = new SimpleDateFormat("yyyyMMdd-HHmmSS");
	                String data = formatas.format(new Date());
	    			File fprodCurrentDbBkp = new File(Jenkins.getInstance().getRootDir()+"/plugins/ui-test-capture/uitest-bkp-"+data+".sqlite");

	    			File fprodCurrentDb = new File(Jenkins.getInstance().getRootDir()+"/plugins/ui-test-capture/uitest.sqlite");
	    			FileUtils.copyFile(fprodCurrentDb, fprodCurrentDbBkp);
	    			fprodCurrentDb.delete();
	    		}catch(Exception e){
		    		System.out.println("ATR-Fail deleting DB!");	    			
	    		}
	    		
	    		System.out.println("ATR-Rename new db...");
	    		try{
		    		try {
		    			File fprodCurrentDb = new File(Jenkins.getInstance().getRootDir()+"/plugins/ui-test-capture/uitest.sqlite");
		    			FileUtils.copyFile(fprodModelDb, fprodCurrentDb);
		    		} catch (IOException e) {
		    		    e.printStackTrace();
		    		}		    		
	    		}catch(Exception e){
		    		System.out.println("ATR-Fail renaming DB!");	    			
	    		}
	    		
	    		try{
	    			fprodModelDb.delete();
	    		} catch (Exception e) {
	    		    e.printStackTrace();
	    		}		    		
	    		
	    		System.out.println("ATR-Migration successful!");
            }catch(Exception e){
	    		System.out.println("ATR-Migration failed!");	    			
            }

    		//Reinstanciando a base
        	try{
    			c = DriverManager.getConnection("jdbc:sqlite:"+Jenkins.getInstance().getRootDir()+"/plugins/ui-test-capture/uitest.sqlite");
    			c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);        		
        	}catch(Exception e1){
    			c = DriverManager.getConnection("jdbc:sqlite:src/main/webapp/uitestdev.sqlite");
    			c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);        		
        	}
    		System.out.println("################################");
    		
    	}else{
    		System.out.println("ATR-Database still updated");    		
    	}
    }

}
