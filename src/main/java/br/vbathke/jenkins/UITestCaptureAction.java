package br.vbathke.jenkins;

import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.Project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import br.vbathke.model.Execution;
import br.vbathke.model.Job;
import br.vbathke.model.Result;
import br.vbathke.model.Test;


public class UITestCaptureAction extends UITestCaptureBase implements Action {
    AbstractBuild<?,?> build;
    Project<?,?> project;
    
    public UITestCaptureAction(AbstractBuild<?,?> build) {
        this.build = build;
        this.project = (Project<?,?>) build.getProject();
    }
    
    @Override
    public String getIconFileName() {
        return "/plugin/ui-test-capture/images/uitestcapture.png";
    }

    @Override
    public String getDisplayName() {
        return "UI Test Capture";
    }

    @Override
    public String getUrlName() {
        return "ui-test-capture";
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
    	return "artifact/";
    }
        
    public String getProjectUrl(){
    	return getBuild().getUrl();
    }
}