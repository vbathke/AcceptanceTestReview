package br.vbathke.jenkins;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class UiTestCaptureBase {
    public String getRootUrl(){
    	return ""+Jenkins.getInstance().getRootUrl();
    }
    
    public void doAjaxProcess(StaplerRequest request, StaplerResponse response) {
      	try {
    	  response.getOutputStream().println("teste de print"+request.getParameter("name"));
		} catch (IOException e) {}
    }

}
