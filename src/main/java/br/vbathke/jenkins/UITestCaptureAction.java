package br.vbathke.jenkins;

import jenkins.model.Jenkins;
import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.Project;


public class UITestCaptureAction extends UiTestCaptureBase implements Action {
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
}