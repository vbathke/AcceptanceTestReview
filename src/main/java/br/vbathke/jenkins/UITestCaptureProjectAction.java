/*
 * The MIT License
 *
 * Copyright 2013 Praqma.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.vbathke.jenkins;

import hudson.model.ProminentProjectAction;
import hudson.model.AbstractProject;

public class UITestCaptureProjectAction extends UITestCaptureBase implements ProminentProjectAction{

    public final AbstractProject<?,?> project;
    
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

    public UITestCaptureProjectAction(AbstractProject<?,?> project) {
        this.project = project;
    }
    
    public AbstractProject<?,?> getProject(){
    	return project;
    }
    
    public String getName(){
    	return project.getName();
    }
    
    public String getVersaoAtual(){
    	return ""+project.getLastBuild().getNumber();
    }
    
    public String getBuildArtifacts(){
    	return "ws/target/";
    }
    
    public String getProjectUrl(){
    	return getProject().getUrl();
    }    
}