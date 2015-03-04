package br.vbathke.jenkins;

import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.Project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;


public class UITestCaptureAction extends UITestCaptureBase implements Action {
    AbstractBuild<?,?> build;
    Project<?,?> project;
    
    public UITestCaptureAction(AbstractBuild<?,?> build) {
        this.build = build;
        this.project = (Project<?,?>) build.getProject();
    }
    
    @Override
    public String getIconFileName() {
        return "/plugin/uitestcapture/images/uitestcapture.png";
    }

    @Override
    public String getDisplayName() {
        return "UI Test Capture";
    }

    @Override
    public String getUrlName() {
        return "uitestcapture";
    }
    
    public AbstractBuild<?,?> getBuild() {
        return this.build;
    }
    
    public String getVersaoAtual(){
    	return ""+build.getNumber();
    }

    public String getName(){
    	return build.getProject().getName();
    }
    
    public String getBuildArtifacts(){
    	return "artifact/target/";
    }
        
    public String getProjectUrl(){
    	return getBuild().getUrl();
    }
    
    public void doAjaxProcess(StaplerRequest request, StaplerResponse response) {
      	try {
			String jsoncontent = "{\"id\":\""+request.getParameter("id")+"\", \"status\":\""+request.getParameter("status")+"\"}";
			BufferedWriter out = new BufferedWriter(new FileWriter(getBuild().getRootDir().getAbsolutePath()+"/archive/target/surefire-reports/"+request.getParameter("id")+".txt"));
			out.write(jsoncontent);
			out.close();
			
			response.getOutputStream().println("escrita "+request.getParameter("id")+": "+jsoncontent);
			System.out.println("escrita "+request.getParameter("id")+": "+jsoncontent);

      	} catch (IOException e) {
			System.out.println(e);
		}
    }
}