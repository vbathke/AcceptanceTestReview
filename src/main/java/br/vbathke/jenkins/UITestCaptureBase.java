package br.vbathke.jenkins;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import br.vbathke.helper.SqliteHelper;
import br.vbathke.model.Execution;
import br.vbathke.model.Result;
import br.vbathke.model.Job;
import br.vbathke.model.Test;
import jenkins.model.Jenkins;

public class UITestCaptureBase {

	
    public String getRootUrl(){
    	return ""+Jenkins.getInstance().getRootUrl();
    }
    
    public Object getUITestCaptureProjectAction(){
    	return UITestCaptureBase.class;
    }
    
    public void doAjaxQueryHistorico(StaplerRequest request, StaplerResponse response) throws Exception {
    	int streamsize = 0;
    	if(request.getParameter("streamsize") != null){
    		streamsize = Integer.parseInt(request.getParameter("streamsize"));
    	}
    	Job job = new Job(request.getParameter("job"));
    	Execution exec = new Execution(request.getParameter("exec"), job.getId());
    	String historico = exec.consultarHistoricoExec(request.getParameter("stream"), streamsize);
      	try {
			ServletOutputStream out = response.getOutputStream();
			out.write((historico).getBytes("UTF-8")); 
      	} catch (IOException e) {
			System.out.println(e);
		}
    }
    
    public void doAjaxUpdateQuarantine(StaplerRequest request, StaplerResponse response) {
    	Test test = new Test(request.getParameter("job"), request.getParameter("test"));
    	test.setStatus(request.getParameter("status"));
    	test.save();
      	try {
			response.getOutputStream().println("{\"message\":\"sucesso\"}");
      	} catch (IOException e) {
			System.out.println(e);
		}    	
    }

    public void doAjaxUpdateQuarantineDescription(StaplerRequest request, StaplerResponse response) {
    	Test test = new Test(request.getParameter("job"), request.getParameter("test"));
    	test.setStatusDescription(request.getParameter("statusDescription"));
    	test.save();
      	try {
			response.getOutputStream().println("{\"message\":\"sucesso\"}");
      	} catch (IOException e) {}    	
    }
    
    public void doAjaxUpdateQuarantineResult(StaplerRequest request, StaplerResponse response) {
    	Result result = new Result(Integer.parseInt(request.getParameter("exec")), request.getParameter("test"));
    	result.setDescription(request.getParameter("statusResult"));
    	result.save();
      	try {
			response.getOutputStream().println("{\"message\":\"sucesso\"}");
      	} catch (IOException e) {}    	
    }
    
    public void doAjaxUpdateQuarantineBehavior(StaplerRequest request, StaplerResponse response) {
    	Test test = new Test(request.getParameter("job"), request.getParameter("test"));
    	test.setBehavior(request.getParameter("statusBehavior"));
    	test.save();
      	try {
			response.getOutputStream().println("{\"message\":\"sucesso\"}");
      	} catch (IOException e) {}    	
    }    
    
    public void doConsultarQuadro(StaplerRequest request, StaplerResponse response) throws Exception{
    	Job job = new Job(request.getParameter("job"));
    	Execution exec = new Execution(request.getParameter("exec"), job.getId());
      	try {
    		String retorno = exec.consultarQuadro();
        	if(retorno.equals("")){
        		retorno="[{}]";
    		}
			response.getOutputStream().println(retorno);
      	} catch (IOException e) {}
    }
    
    public void doConsultarHistoricoExecSize(StaplerRequest request, StaplerResponse response) throws Exception{
    	Job job = new Job(request.getParameter("job"));
    	int idJob = 1;
    	if(job.getId() > 0){
    		idJob = job.getId();
    	}
    	Execution exec = new Execution(request.getParameter("exec"), idJob);
      	try {
    		String retorno = Integer.toString(exec.consultarHistoricoExecSize());
        	if(retorno.equals("")){
        		retorno="[{}]";
    		}
			response.getOutputStream().println(retorno);
      	} catch (IOException e) {}
    }    
}
