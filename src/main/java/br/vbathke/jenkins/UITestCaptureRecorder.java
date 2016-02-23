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

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.kohsuke.stapler.DataBoundConstructor;

public class UITestCaptureRecorder extends Recorder {

	@DataBoundConstructor
	public UITestCaptureRecorder() {
		super();
	}

	@Extension
	public static final class DescriptorImpl extends Descriptor{}	
		
	@Override
	public Descriptor getDescriptor() {
		return (Descriptor) super.getDescriptor();
	}

	/* Hide the plugin form main Jenkins menu
	 * */
	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	/* Set the actual run of the plugin visible in the icon list of the project
	 * */
	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		try {
			return new UITestCaptureProjectAction(project);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/* Set the plugin history visible after a run performed
	 * */
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
			build.getActions().add(new UITestCaptureAction(build));
		return true;
	}
}
