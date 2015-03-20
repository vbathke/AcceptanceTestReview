(function($){

var allPanels = $('.accordion > dd').hide();
var bufferAtual=0;
var novoBuffer=0;
var buildInfo;
var versaoAtual=0;
var historico=false;
  
	function parseBuffer(text){
		var lines = text.split(/\n/);
		novoBuffer = lines.length -1;
		if(bufferAtual < novoBuffer){
			for(var i=bufferAtual; i<novoBuffer; i++){
				var obj = jQuery.parseJSON(lines[i]);
				appendResult(obj, lines[i]);
			}
			bufferAtual=novoBuffer;
		}
	}

	function gravarComentario(metodo){
		console.log("gravar: "+metodo)
	}
	
	function carregarComentario(metodo){
		console.log("recuperar: "+metodo)		
	}
	
	
	function appendResult(obj, rawObj){
		var comboAnalise="<select id=\"analise "+obj.metodo+"\" name=\""+obj.metodo+"\" onChange=\"ajaxSubmit(this);\" class=\"comboAnalise\"><option value=\"\"></option><option value=\"failapp\">App broken</option><option value=\"failtest\">Test broken</option><option value=\"brittle\">Intermittent</option></select>";
		var titulo = "<div class=\"containerTeste\" id=\"container "+obj.metodo+"\">";
		if(obj.status == "sucesso"){
			titulo+= "<dt class=\"containerPassou\"><img src=\"images/passed.gif\" class=\"itemUiTest itemPassou\" /> <a href=\"\">"+obj.metodo+" </a></dt>";
			comboAnalise="";
		}else{
			titulo+= "<dt class=\"containerFalhou\"><img src=\"images/failed.png\" class=\"itemUiTest itemFalhou\" /> <a href=\"\">"+obj.metodo+" </a></dt>";
		}
		$("#execucao").append(
			titulo  +
			"<dd>" +
				"<input type=\"hidden\" id=\""+obj.metodo+"\" value='"+rawObj+"' />" +
				"<div class=\"infoUiTest\">" +
					"<div class=\"screenshot\"><h2>Screenshot:</h2>" +
						"<a href=\""+url+relativepath+"screenshots/"+obj.metodo+".png\" title=\""+obj.metodo+"\" class=\"linkScreenshot\">" +
							"<img src=\""+url+relativepath+"screenshots/"+obj.metodo+".png\" />" +
						"</a>" +
					"</div>" +
					((obj.status != "sucesso") ? "<div class=\"stackUiTest\"><h2>Stacktrace:</h2><pre class=\"stackUiTest\" id=\""+obj.metodo.replace(/\./g,"")+"\"></pre></div>" : "") +
				"</div>" +
				((obj.status != "sucesso") ? "<div class=\"comboAnalise\"><div><h2>Failure analysis:</h2><div class=\"analise\">"+comboAnalise+"</div>"  : "") +
				//"<div>" +
					//"<h2>Comments:</h2>" +
					//"<textarea  name=\"comments_"+obj.metodo+"\" class=\"todo\"></textarea><br />" +
					//"<input type=\"button\" value=\"gravar\" style=\"margin:5px 0 20px 630px;\" onclick=\"gravarComentario('"+obj.metodo+"');\" />" +
					//"<script  type=\"text/javascript\">carregarComentario('"+obj.metodo+"');</script>"+ //TODO: problemas
				//"</div>" +
			"</dd></div>");
		$('.linkScreenshot').colorbox({retinaImage:true, retinaUrl:true});
		try{
			$.get(url+relativepath+'surefire-reports/'+obj.metodo+'.txt',
				function(data){
					var infoTeste = jQuery.parseJSON(data);
					jQuery("[name='"+infoTeste.id+"']").val(infoTeste.status);
				}
		    );
		}catch(err){}
		if(obj.status != "sucesso"){
			fetchStack(obj.classe, obj.metodo.replace(/\./g,""));
		}
		allPanels = $('.accordion > div > dd').last().hide();
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
	  
	function fetchBuffer(){
		jQuery.ajaxSetup({cache:false});
		jQuery.get(url+relativepath+'teststream.txt',
			function(data){
    	  		jQuery('#stream').html(data);
    	  		if (data.length > 0) {
    	  			parseBuffer(jQuery("#stream").html());            	  
    	  		}else{
    	  			jQuery('#stream').html("");
    	  			jQuery('#execucao').html("");
    	  		}
        	}
		);
	}
  
  function fetchStack(classe, metodo){
      $.ajaxSetup({cache:false});
      $.get(url+relativepath+'surefire-reports/'+classe+'.txt',
          function(data){
    	  	jQuery("#"+metodo).html(data);
          }
      );
  }
  
  function mostrarFalhas(){
	  jQuery(".containerPassou").hide();
	  jQuery(".containerFalhou").show();
  }
  
  function mostrarSucessos(){	  
	  jQuery(".containerPassou").show();
	  jQuery(".containerFalhou").hide();
  }
  
  function mostrarTodos(){
	  jQuery(".containerPassou").show();
	  jQuery(".containerFalhou").show();
  }
  
  fetchBuffer();
  window.setInterval(function(){
	  //Carregar conteúdo
	  fetchBuffer();
	  
	  //Atualizar totais
	  jQuery(".total").html(jQuery(".itemUiTest").length);
	  jQuery(".passou").html(jQuery(".itemPassou").length);
	  jQuery(".falhou").html(jQuery(".itemFalhou").length);
	  jQuery("#resultado").show();
	  atualizarQuadroFalhas();
	  	  	  
	  //Ordenar
	  if(jQuery("#ordenar").is(":checked")){
		  ordenar();
	  }

	  //Mostrar Loading de processamento da Build
  	  try{
		  buildInfo = jQuery.parseJSON(jQuery.ajax({ type:"GET", url:url+"/lastBuild/api/json", async:false }).responseText);
		  if(buildInfo.result === null){
			  if(buildInfo.id > versaoAtual){
				  jQuery("#execucao").children().remove();
				  jQuery("#buildInfoId").html(buildInfo.id);
				  versaoAtual = buildInfo.id;
				  bufferAtual=0;
			  }
			  jQuery(".reexec").fadeOut("slow");
			  jQuery(".loadingUiTest").show();
		  }else{
			  jQuery(".reexec").fadeIn("slow");
			  jQuery(".loadingUiTest").hide();
		  }
	  }catch(err){}
  },2000);
  
  	
	function ordenar(){
		jQuery('.containerTeste').sortElements(function(a, b){
		    return jQuery(a).attr('id') > jQuery(b).attr('id') ? 1 : -1;
		});
	}
    
  /**
   * jQuery.fn.sortElements
   * --------------
   * @param Function comparator:
   *   Exactly the same behaviour as [1,2,3].sort(comparator)
   *   
   * @param Function getSortable
   *   A function that should return the element that is
   *   to be sorted. The comparator will run on the
   *   current collection, but you may want the actual
   *   resulting sort to occur on a parent or another
   *   associated element.
   *   
   *   E.g. $('td').sortElements(comparator, function(){
   *      return this.parentNode; 
   *   })
   *   
   *   The <td>'s parent (<tr>) will be sorted instead
   *   of the <td> itself.
   */
  jQuery.fn.sortElements = (function(){
      var sort = [].sort;
      return function(comparator, getSortable) {
          getSortable = getSortable || function(){return this;};
          var placements = this.map(function(){
              var sortElement = getSortable.call(this),
                  parentNode = sortElement.parentNode,
                  // Since the element itself will change position, we have
                  // to have some way of storing its original position in
                  // the DOM. The easiest way is to have a 'flag' node:
                  nextSibling = parentNode.insertBefore(
                      document.createTextNode(''),
                      sortElement.nextSibling
                  );
              return function() {
                  if (parentNode === this) {
                      throw new Error(
                          "You can't sort elements if any one is a descendant of another."
                      );
                  }
                  // Insert before flag:
                  parentNode.insertBefore(this, nextSibling);
                  // Remove flag:
                  parentNode.removeChild(nextSibling);
              };
          });
          return sort.call(this, comparator).each(function(i){
              placements[i].call(getSortable.call(this));
          });
      };
  })();
})(jQuery)

function ajaxSubmit(obj){
	var stringAnterior = document.getElementById(obj.name).value;
	var jsonObj = {id: obj.name, status: obj.value}	
	jQuery.post(url+"uitestcapture/ajaxProcess", jsonObj, function( data ) {
		atualizarQuadroFalhas();
	});
}

function atualizarQuadroFalhas(){
	if(jQuery(".itemFalhou").length>0 && historico){
		  jQuery(".quadroFalhas").show();
		  jQuery(".analise").show();
		  jQuery(".comboAnalise").show();
	}else{
		jQuery(".quadroFalhas").hide();
		jQuery(".analise").hide();
		jQuery(".comboAnalise").hide();
	}

	jQuery(".quebraPendente").html(jQuery(".comboAnalise option[value=\'\']:selected").length); 
	jQuery(".quebraApp").html(jQuery(".comboAnalise option[value=\'failapp\']:selected").length); 
	jQuery(".quebraTeste").html(jQuery(".comboAnalise option[value=\'failtest\']:selected").length); 
	jQuery(".quebraBrittle").html(jQuery(".comboAnalise option[value=\'brittle\']:selected").length);
}

function ajaxRun(url){
	jQuery.post(url, '{}', function( data ) {
	});
}
