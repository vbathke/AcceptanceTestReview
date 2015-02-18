package br.vbathke.jenkins;

import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.Project;


public class UITestCaptureAction implements Action {
    AbstractBuild<?,?> build;
    
    public UITestCaptureAction(AbstractBuild<?,?> build) {
        this.build = build;
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
    
    
}