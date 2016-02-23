//Init jQuery functions
(function($){

	
//### MODELS
	var bufferAtual=0;
	var novoBuffer=0;
	var versaoAtual=0;
	var buildInfo;
	var hash = "";
	var tests = [];


//### VIEW HELPERS
	function appendResult(obj, rawObj){
		if(obj.id_exec == descVersaoAtual || versaoAtual == 0){
			
			//Index
			tests.push(obj.metodo);
			atualizarTotais();
			
			//GC TestInfo
			var appendList = "#execucao";
			var rawObj = JSON.stringify(obj);
			if(obj.classificacao=='app_fail'){
				appendList = '#dlFailApp';
			}
			if(obj.classificacao=='test_fail'){
				appendList = '#dlFailTest';
			}
			
			//Título
			var comboAnaliseSuccess="<select id=\"analise "+obj.metodo+"\" name=\""+obj.metodo+"\" onChange=\"ajaxUpdateQuarantine(this);\" class=\"comboAnalise\"><option value=\"\">Move to quarantine</option><option value=\"app_fail\">App Failure</option><option value=\"test_fail\">Test Failure</option><option value=\"\">Remove from quarantine</option></select>";
			var comboAnaliseFailure="<select id=\"analise "+obj.metodo+"\" name=\""+obj.metodo+"\" onChange=\"ajaxUpdateQuarantine(this);\" class=\"comboAnalise\"><option value=\"\">Move to quarantine</option><option value=\"app_fail\">App Failure</option><option value=\"test_fail\">Test Failure</option><option value=\"\">Remove from quarantine</option></select>";
			var titulo = "<div class=\"containerTeste\" id=\"container "+obj.metodo+"\">";
			var label = "";
			if(obj.behavior != ""){
				label = obj.behavior;
			}else{
				label = obj.metodo;				
			}
			if(obj.status == "sucesso"){
				titulo+= "<dt class=\"containerPassou\"><img src=\"images/passed.gif\" class=\"itemUiTest itemPassou\" /> <a href=\"\" id=\"label "+obj.metodo+"\">"+label+" </a></dt>";
			}else{
				titulo+= "<dt class=\"containerFalhou\"><img src=\"images/failed.png\" class=\"itemUiTest itemFalhou\" /> <a href=\"\" id=\"label "+obj.metodo+"\">"+label+" </a></dt>";
			}
	
			var historicoStatus = "";
			var buildHistory = jQuery.parseJSON(obj.historico);
			for(var cont=0; cont<buildHistory.length; cont++){
				historicoStatus+=buildHistory[cont].id_exec+":";
				if(buildHistory[cont].status == "sucesso"){
					historicoStatus+="<img src=\"images/passed.gif\" class=\"imgstatus\" />";
				}else{
					historicoStatus+="<img src=\"images/failed.png\" class=\"imgstatus\" />";					
				}
				if(cont<buildHistory.length-1){
					historicoStatus+=", ";
				}
			}
			$(appendList).append(
				titulo  +
				"<dd style=\"display:none;\">" +
					"<input type=\"hidden\" id=\""+obj.metodo+"\" value='"+rawObj+"' />" +
					"<div class=\"infoUiTest\">" +
						"<div class=\"screenshot\"><h2>Screenshot:</h2>" +
							"<a href=\""+url+relativepath+"screenshots/"+obj.metodo+".png\" title=\""+obj.metodo+"\" class=\"linkScreenshot\">" +
								"<img src=\""+url+relativepath+"screenshots/"+obj.metodo+".png\" />" +
							"</a>" +
						"</div>" +
						"<div class=\"stackUiTest\"><h2>Stacktrace:</h2><pre class=\"stackUiTest\" id=\""+obj.metodo.replace(/\./g,"")+"\">"+obj.stacktrace+"</pre></div>" +
					"</div>" +
					"<div style=\"width: 100%; display: table; padding-right:90px; \">"+
						"<div class=\"observacoes\">" +
							"<h2>Result description:</h2>" +
							"<textarea  id=\"result "+obj.metodo+"\" class=\"todo\">"+obj.descricao+"</textarea>" +
							"<div class=\"description-commands\">" +
								"<span class=\"description-status\" id=\"result-status-"+obj.metodo.replace(/\./g,"")+"\"></span><input type=\"button\" value=\"Save\" onClick=\"javascript:ajaxUpdateQuarantineResult('"+obj.metodo+"');\" />" +
							"</div>" +
							"<div>" +
								"<h2>Result history:</h2> "+
								historicoStatus +		
							"</div>"+
						"</div>" +
						"<div class=\"observacoes\">" +
							"<h2>Test description:</h2>" +
							"<textarea  id=\"description "+obj.metodo+"\" class=\"todo\">"+obj.statusDescription+"</textarea>" +
							"<div class=\"description-commands\">" +
								"<span class=\"description-status\" id=\"description-status-"+obj.metodo.replace(/\./g,"")+"\"></span><input type=\"button\" value=\"Save\" onClick=\"javascript:ajaxUpdateQuarantineDescription('"+obj.metodo+"');\" />" +
							"</div>" +
							
							"<h2>Test behavior:</h2>" +
							"<input type=\"text\"  id=\"behavior "+obj.metodo+"\" class=\"behavior\" value=\""+obj.behavior+"\" />" +
							"<div class=\"description-commands\">" +
								"<span class=\"description-status\" id=\"behavior-status-"+obj.metodo.replace(/\./g,"")+"\"></span><input type=\"button\" value=\"Save\" onClick=\"javascript:ajaxUpdateQuarantineBehavior('"+obj.metodo+"');\" />" +
							"</div>" +
						"</div>"+
					"</div>"+
					"<br />"+
				
					((obj.status != "sucesso") ? 
						"<div class=\"comboAnalise\"><div><div class=\"analise\">"+comboAnaliseSuccess+"</div></div></div>":
						"<div class=\"comboAnalise\"><div><div class=\"analise\">"+comboAnaliseFailure+"</div></div></div>")+
				"</dd>" +
				"</div>"
			);
			
			$('.linkScreenshot').colorbox({retinaImage:true, retinaUrl:true});
			$('.accordion > div > dt > a').click(function() {
				$this = $(this);
				$target =  $this.parent().next();
				if(!$target.hasClass('active')){
					$('.active').removeClass('active').slideUp();
				     $target.addClass('active').slideDown();
				}
				return false;
			});
		}
	}

	function zerarTotais(){
		jQuery(".passou").html(0);
		jQuery(".falhou").html(0);
		jQuery(".working").html(0);
		jQuery(".flaky").html(0);
		jQuery(".total").html(0);
		jQuery("#resultado").show();
	}
  
	function ordenarNodos(){
		if(jQuery("#ordenar").is(":checked")){
			ordenar();
		}
	}

	function uiInitRun(){
		  if(buildInfo.id > versaoAtual){
			  jQuery("#execucao .containerTeste").children().remove();
			  jQuery('#dlFailApp .containerTeste').children().remove();
			  jQuery('#dlFailTest .containerTeste').children().remove();
			  jQuery("#buildInfoId").html(buildInfo.id);
			  descVersaoAtual = buildInfo.id;

			  bufferAtual=0;
		  }
		  jQuery(".reexec").fadeOut("slow");
		  jQuery(".loadingUiTest").show();
	}
	
	function uiStopRun(){
		  jQuery(".reexec").fadeIn("slow");
		  jQuery(".loadingUiTest").hide();
	}
	  
	function ordenar(){
		try{
			jQuery('#execucao .containerTeste').sortElements(function(a, b){return jQuery(a).attr('id') > jQuery(b).attr('id') ? 1 : -1;});
			jQuery('#dlFailApp .containerTeste').sortElements(function(a, b){return jQuery(a).attr('id') > jQuery(b).attr('id') ? 1 : -1;});
			jQuery('#dlFailTest .containerTeste').sortElements(function(a, b){return jQuery(a).attr('id') > jQuery(b).attr('id') ? 1 : -1;});
		}catch(err) {}
	}


//### SERVICE HELPERS
	function ajaxSubmit(obj){
		var stringAnterior = document.getElementById(obj.name).value;
		var jsonObj = {id: obj.name, status: obj.value}	
		jQuery.post(url+"ui-test-capture/ajaxProcess", jsonObj, function( data ) {
			atualizarQuadroFalhas();
		});
	}
		
	function ajaxReadResult(exec, stream){
		var streamsize = tests.length;
		var jsonObj = {job:job, exec:exec, stream:stream, streamsize:streamsize};
		var response = jQuery.ajax({type:"POST", url:url+"ui-test-capture/ajaxQueryHistorico", data:jsonObj, async:false, contentType: "application/x-www-form-urlencoded;charset=UTF-8" }).responseText;
		return jQuery.parseJSON(response);
	}

	function ajaxReadResultSize(exec){
		var jsonObj = {job:job, exec:exec};
		var response = jQuery.ajax({type:"POST", url:url+"ui-test-capture/consultarHistoricoExecSize", data:jsonObj, async:false, contentType: "application/x-www-form-urlencoded;charset=UTF-8" }).responseText;
		return jQuery.parseJSON(response);
	}
	
	function ajaxVerifyResults(){
		var jsonObj = {job:job, exec:descVersaoAtual};
		var response = jQuery.ajax({type:"GET", url:url+"ui-test-capture/ajaxVerifyResults", data:jsonObj, async:false, contentType: "application/x-www-form-urlencoded;charset=UTF-8" }).responseText;
	}
	
	function ajaxUpdateQuarantineDescription(test){
		var jsonObj = {job:job, test:test, statusDescription: jQuery("[id='description "+test+"']").val()}	
		var response = jQuery.ajax({type:"POST", url:url+"ui-test-capture/ajaxUpdateQuarantineDescription", data:jsonObj, async:false, contentType: "application/x-www-form-urlencoded;charset=UTF-8" }).responseText;
	}

	
	jQuery.fn.sortElements = (function(){
	var sort = [].sort;
	return function(comparator, getSortable) {
		getSortable = getSortable || function(){return this;};
	    var placements = this.map(function(){
	          var sortElement = getSortable.call(this),
	              parentNode = sortElement.parentNode,
	              nextSibling = parentNode.insertBefore(document.createTextNode(''), sortElement.nextSibling);
	          return function() {
	              if (parentNode === this) {
	                  throw new Error("You can't sort elements if any one is a descendant of another.");
	              }
	              parentNode.insertBefore(this, nextSibling);
	              parentNode.removeChild(nextSibling);
	          };
	      });
	      return sort.call(this, comparator).each(function(i){
	          placements[i].call(getSortable.call(this));
	      });
	    };
	})();


//###	CONTROLLER HELPERS
	function fetchBuffer(){
		jQuery.ajaxSetup({cache:false});

		if(historico){
			parseBuffer(ajaxReadResult(descVersaoAtual, false));  
		}else{
			var objLenght = ajaxReadResultSize(descVersaoAtual);
			ajaxVerifyResults();
			if(tests.length < objLenght){
				parseBuffer(ajaxReadResult(descVersaoAtual, true));
			}
		}
	}

	function parseBuffer(json){
		for(var j=tests.length; j<json.stack.length; j++){
			appendResult(json.stack[j]);
		}
		if(!historico){
			ordenarNodos();					
		}
		atualizarTotais();
	}


//###	CONTROLLER LISTENER
	jQuery(document).ready(function() {
		fetchBuffer();
		atualizarTotais();
		if(!historico){
			window.setInterval(function(){
				fetchBuffer();
				//Restart run
				try{
					buildInfo = jQuery.parseJSON(jQuery.ajax({ type:"GET", url:url+"/lastBuild/api/json", async:false, contentType: "application/x-www-form-urlencoded;charset=UTF-8"}).responseText);
					if(buildInfo.result === null){
						uiInitRun();
					}else{
						uiStopRun();
					}
					//TODO: refatorar reinício da run para ficar mais consistente
					if(versaoAtual > 0 && versaoAtual!=buildInfo.id){
						tests = [];	
						zerarTotais();
					}
					versaoAtual = buildInfo.id;
					descVersaoAtual = buildInfo.id;
		  	  	}catch(err){}
			},2000);
		}
	});
	
	
//End jQuery functions	
})(jQuery)


function ajaxRun(url){
	jQuery.post(url, '{}', function( data ) {});
}

function ajaxUpdateQuarantine(obj){
	var stringAnterior = document.getElementById(obj.name).value;
	var jsonObj = {job:job, test: obj.name, status: obj.value}	
	var response = jQuery.ajax({type:"POST", url:url+"ui-test-capture/ajaxUpdateQuarantine", data:jsonObj, async:false, contentType: "application/x-www-form-urlencoded;charset=UTF-8" }).responseText;

	if(obj.value == "app_fail"){
		jQuery("[id='container "+obj.name+"']").detach().appendTo("#dlFailApp");				
	}else{
		if(obj.value == "test_fail"){
			jQuery("[id='container "+obj.name+"']").detach().appendTo("#dlFailTest");		
		}else{			
			jQuery("[id='container "+obj.name+"']").detach().appendTo("#execucao");		
		}
	}
	jQuery("[id='container "+obj.name+"'] > dd").hide();		
	atualizarTotais();
}

function ajaxUpdateQuarantineDescription(test){
	var jsonObj = {job:job, test:test, statusDescription: jQuery("[id='description "+test+"']").val()}	
	var response = jQuery.ajax({type:"POST", url:url+"ui-test-capture/ajaxUpdateQuarantineDescription", data:jsonObj, async:false, contentType: "application/x-www-form-urlencoded;charset=UTF-8" }).responseText;
	document.getElementById("description-status-"+test.replace(/\./g,"")).innerHTML = "Saved";
}

function ajaxUpdateQuarantineResult(test){
	var jsonObj = {job:job, test:test, exec:descVersaoAtual, statusResult: jQuery("[id='result "+test+"']").val()}
	var response = jQuery.ajax({type:"POST", url:url+"ui-test-capture/ajaxUpdateQuarantineResult", data:jsonObj, async:false, contentType: "application/x-www-form-urlencoded;charset=UTF-8" }).responseText;
	document.getElementById("result-status-"+test.replace(/\./g,"")).innerHTML = "Saved";
}

function ajaxUpdateQuarantineBehavior(test){
	var jsonObj = {job:job, test:test, statusBehavior: jQuery("[id='behavior "+test+"']").val()}	
	var response = jQuery.ajax({type:"POST", url:url+"ui-test-capture/ajaxUpdateQuarantineBehavior", data:jsonObj, async:false, contentType: "application/x-www-form-urlencoded;charset=UTF-8" }).responseText;
	document.getElementById("behavior-status-"+test.replace(/\./g,"")).innerHTML = "Saved";
	if(jQuery("[id='behavior "+test+"']").val() != ""){
		document.getElementById("label "+test+"").innerHTML = jQuery("[id='behavior "+test+"']").val();		
	}else{
		document.getElementById("label "+test+"").innerHTML = test;
	}
}

function atualizarTotais(){
	var jsonObj = {job:job, exec:descVersaoAtual};
	var response = jQuery.ajax({type:"POST", url:url+"ui-test-capture/consultarQuadro", data:jsonObj, async:false, contentType: "application/x-www-form-urlencoded;charset=UTF-8" }).responseText;
	var retorno = jQuery.parseJSON(response);
	var success = 0;
	var fail = 0;
	var app_fail = 0;
	var test_fail = 0;
	var working = 0;
	var notQuarantined = 0;
	var total = 0;
	for(var i=0; i<retorno.length;i++){
		if(retorno[i].label == "success")success = parseInt(retorno[i].total);
		if(retorno[i].label == "fail")fail = parseInt(retorno[i].total);
		if(retorno[i].label == "app_fail")app_fail = parseInt(retorno[i].total);
		if(retorno[i].label == "test_fail")test_fail = parseInt(retorno[i].total);
		if(retorno[i].label == "working")notQuarantined = parseInt(retorno[i].total);
		working = notQuarantined+app_fail;
		total = app_fail+test_fail+notQuarantined;
	}
	jQuery(".passou").html(success);
	jQuery(".falhou").html(fail);
	jQuery(".working").html(working);
	jQuery(".flaky").html(test_fail);
	jQuery(".total").html(total);
	jQuery("#resultado").show();
}

