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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import br.vbathke.helper.JsonParseSingleQuote;
import br.vbathke.model.Execution;
import br.vbathke.model.Job;
import br.vbathke.model.Result;
import br.vbathke.model.Test;

public class UITestCaptureProjectAction extends UITestCaptureBase implements ProminentProjectAction{

    public final AbstractProject<?,?> project;
    
    private String hash = "";
    private String fileString = "";

    
    public UITestCaptureProjectAction(AbstractProject<?,?> project) throws IOException, NoSuchAlgorithmException {
    	this.project = project;
    	try{
    	fileString = project.getLastBuild().getWorkspace().toString()+"/target/teststream.txt";
    	}catch(Exception e){
    		System.out.println("/target/teststream.txt não encontrado");
    	}
    	hash = md5Hash(hash);
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
    	return "ws/";
    }
    
    public String getProjectUrl(){
    	return getProject().getUrl();
    }
        
    public void doAjaxVerifyResults(StaplerRequest request, StaplerResponse response){
    	//System.out.println("getLastBuild().getRootDir(): 			"+project.getLastBuild().getRootDir().toString());
    	//System.out.println("project.getLastBuild().getWorkspace():	"+project.getLastBuild().getWorkspace().toString());
    	
		try {
			String testStreamLocal = getTestStream();
	    	String tmpHash = md5Hash(testStreamLocal);
	    	String[] testStreamSplit;
	    	String outResponse = "";
	    	
			//Se arquivo possuí mudanças
			if(testStreamPossuiDiferenca()){
				Job job = new Job(getName());
		    	Execution exec = new Execution(request.getParameter("exec"), job.getId());

		    	//unstack from file and record on db
				testStreamSplit = testStreamLocal.split("\\n");
				for(int i=0; i<testStreamSplit.length; i++){
					if(!testStreamSplit[i].equals("")){
						JsonParseSingleQuote jsonLinha = new JsonParseSingleQuote(testStreamSplit[i]);
				    	
				    	Test test = new Test(getName(), jsonLinha.get("metodo"));
				    	test.setIdJob(job.getId());
				    	test.setTest(jsonLinha.get("metodo"));
				    	test.setTestClass(jsonLinha.get("classe"));
				    	test.save();

				    	//record the result
				    	Result result = new Result(exec.getId(), test.getTest());
				    	result.setStatus(jsonLinha.get("status"));
				    	result.setStacktrace(FileUtils.readFileToString(new File(project.getRootDir().getCanonicalPath()+"/workspace/target/surefire-reports/"+jsonLinha.get("classe").trim()+".txt"), "UTF-8"));
				    	result.save();						

				    	testStreamLocal = testStreamLocal.replace(testStreamSplit[i]+"\n", "");
					}
				}
				hash = tmpHash;

				//se após o processamento o arquivo NÃO foi alterado, desempilhe
				if(!testStreamPossuiDiferenca()){
					try{
						Files.write(Paths.get(fileString), testStreamLocal.getBytes(StandardCharsets.UTF_8));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				outResponse = "hash: "+hash+" lines discovered: "+testStreamSplit.length;
			}else{
				outResponse = "hash: "+hash+" lines: 0";
			}
			ServletOutputStream out = response.getOutputStream();
			out.write(outResponse.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public boolean testStreamPossuiDiferenca() throws NoSuchAlgorithmException{
    	String tmpHash = md5Hash(getTestStream());
		if(!hash.equals(tmpHash)){
			return true;
		}else{
			return false;
		}
    }
    
    public String getTestStream(){
		try{
			return FileUtils.readFileToString(new File(fileString), "UTF-8");
		}catch(Exception e){
			return "";
		}
    }
    
    public String md5Hash(String data) throws NoSuchAlgorithmException{
		MessageDigest messageDigest;
		messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.reset();
		messageDigest.update(data.getBytes(Charset.forName("UTF8")));
		byte[] resultByte = messageDigest.digest();
		String result = new String(Hex.encodeHex(resultByte));
		return result;
    }
}