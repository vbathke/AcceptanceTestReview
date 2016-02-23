package br.vbathke.model;

import java.sql.ResultSet;
import java.sql.Statement;

import net.sf.json.JSONArray;
import br.vbathke.helper.SqliteHelper;

public class Test {

	private int id = 0;
	private int idJob = 0;
	private String test = "";
	private String behavior = "";
	private String testClass = "";
	private String status = "";
	private String statusDescription = "";
	private String stackTrace = "";
	
	public Test(){		
	}
		
	public Test(String job, String test){
        Statement stmt = null;
        try{
	        stmt = SqliteHelper.getConn().createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT tt.* FROM tb_test as tt inner join tb_job as tj on tt.id_job=tj.id  and tj.name='"+job+"' where test='"+test+"'");
			while (rs.next()) {
			     this.setId(rs.getInt("id"));
			     this.setIdJob(rs.getInt("id_job"));
			     this.setTest(rs.getString("test"));
			     this.setBehavior(rs.getString("behavior"));
			     this.setTestClass(rs.getString("test_class"));
			     this.setStatus(rs.getString("status"));
			     this.setStatusDescription(rs.getString("status_description"));
			}
			
			rs.close();
			stmt.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	public void save(){
        Statement stmt = null;
        try {
            stmt = SqliteHelper.getConn().createStatement();
			if(getId() <= 0){
				if(!getTest().equals("") || getIdJob() <= 0){
		            stmt.executeUpdate("INSERT INTO tb_test(id_job, test, behavior, test_class, status, status_description) "
		            		+ "values('"+this.getIdJob()+"','"+this.getTest()+"','"+this.getBehavior()+"','"+this.getTestClass()+"','"+this.getStatus()+"','"+this.getStatusDescription()+"');");
				}
			}else{
	            stmt.executeUpdate("update tb_test set "
				            		+ "status='"+this.getStatus()+"',"
				            		+ "behavior='"+this.getBehavior()+"',"				            		
				            		+ "status_description='"+this.getStatusDescription()+"' "
				            		+ "where test='"+this.getTest()+"' " 
				            		+ "and id='"+this.getId()+"'");				
			}
			stmt.close();
		} catch ( Exception e ) {
		  	e.printStackTrace();
		}		
	}
	
    public String consultarHistorico() throws Exception{
    	SqliteHelper conn = new SqliteHelper();
    	JSONArray rs = 
    			conn.query( "select te.id as id_exec,tr.status from tb_result tr "
					+ "inner join tb_exec te on tr.id_exec=te.id "
					+ "inner join tb_job tj on te.id_job=tj.id "
					+ "where test='"+this.getTest()+"' "
					+ "and tj.id='"+this.getIdJob()+"' "
					+ "order by id_exec desc "
					+ "LIMIT 0, 10;" );
    	String retorno = rs.toString();
		if(!retorno.equals("")){
			return retorno; 
		}else{
			return "[{}]";
		}
    }   
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdJob() {
		return idJob;
	}
	public void setIdJob(int idJob) {
		this.idJob = idJob;
	}
	public String getTest() {
		return test;
	}
	public String getBehavior() {
		return behavior;
	}
	public void setTest(String test) {
		this.test = test;
	}
	public void setBehavior(String behavior) {
		this.behavior = behavior;
	}	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public String getTestClass() {
		return testClass;
	}

	public void setTestClass(String testClass) {
		this.testClass = testClass;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
}
