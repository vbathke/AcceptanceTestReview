package br.vbathke.jenkins;

import jenkins.model.Jenkins;

public class UITestCaptureBase {
    public String getRootUrl(){
    	return ""+Jenkins.getInstance().getRootUrl();
    }
    
    public Object getUITestCaptureProjectAction(){
    	return UITestCaptureBase.class;
    }    
}
