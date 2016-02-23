package br.vbathke.jenkins;

import hudson.model.AbstractProject;
import hudson.model.Descriptor.FormException;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

public class Descriptor extends BuildStepDescriptor<Publisher> {

	public Descriptor() {
		load();
	}

	@Override
	public boolean isApplicable(Class<? extends AbstractProject> project) {
		return true;
	}

	@Override
	public String getDisplayName() {
		return "UI Test Capture";
	}

	@Override
	public boolean configure(StaplerRequest staplerRequest, JSONObject json){
		save();
		return true;
	}
}