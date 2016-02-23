package br.vbathke.model;

import java.sql.ResultSet;
import java.sql.Statement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import br.vbathke.helper.SqliteHelper;

public class Job {

	private int id = 0;
	private String name = "";
	
	public Job(){		
	}
		
	public Job(int id){
		try {
	    	SqliteHelper conn = new SqliteHelper();
	    	JSONArray rs;
	    	setName(name);
			rs = conn.query( "SELECT * FROM tb_job where id='"+id+"';" );
			if(rs.getJSONObject(0).getInt("id") > 0){
				setId(rs.getJSONObject(0).getInt("id"));
				setName(rs.getJSONObject(0).getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public Job(String job){
		try {
	    	SqliteHelper conn = new SqliteHelper();
	    	JSONArray rs;
	    	setName(job);
			rs = conn.query( "SELECT * FROM tb_job where name='"+getName()+"';" );
			if(rs.size() > 0){
				setId(rs.getJSONObject(0).getInt("id"));
				setName(rs.getJSONObject(0).getString("name"));
			}else{
				save();
				rs = conn.query( "SELECT * FROM tb_job where name='"+getName()+"';" );
				setId(rs.getJSONObject(0).getInt("id"));
				setName(rs.getJSONObject(0).getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(){
		try {
	    	SqliteHelper conn = new SqliteHelper();
	    	JSONArray rs;
			rs = conn.query( "SELECT * FROM tb_job where name='"+getName()+"';" );
			int job=0;
			for (int i=0; i<rs.size(); i++) {
				JSONObject item = rs.getJSONObject(i);
			    if(item.getString("name").equals(name)){
					job = item.getInt("id");
					break;
			    }
			}
			if(job == 0){
	            conn.update("INSERT INTO tb_job(name) VALUES('"+getName()+"');");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

}
