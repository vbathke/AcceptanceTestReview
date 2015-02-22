(function($){

var allPanels = $('.accordion > dd').hide();
var bufferAtual=0;
var novoBuffer=0;
var builInfo;
  
  function parseBuffer(text){
      var lines = text.split(/\n/);
      novoBuffer = lines.length -1;
      if(bufferAtual < novoBuffer){
          for(var i=bufferAtual; i<novoBuffer; i++){
        	var obj = jQuery.parseJSON(lines[i]);
        	var comboAnalise="<select><option></option><option>Quebra de teste</option><option>Quebra de app</option><option>Intermitência</option></select>";
      		if(obj.status == "sucesso"){
      			$("#execucao").append("<dt class=\"containerPassou\"><div class=\"analise\"></div><img src=\"images/passed.gif\" class=\"itemUiTest itemPassou\" /> <a href=\"\">"+obj.metodo+" </a></dt>");
      		}else{
      			$("#execucao").append("<dt class=\"containerFalhou\"><div class=\"analise\">"+comboAnalise+"</div><img src=\"images/failed.png\" class=\"itemUiTest itemFalhou\" /> <a href=\"\">"+obj.metodo+" </a></dt>");
      		}
            $("#execucao").append("" +
            		"<dd>" +
            			"<div class=\"infoUiTest\">" +
            				"<div class=\"screenshot\"><h2>Screenshot:</h2>" +
            					"<a href=\""+url+relativepath+"screenshots/"+obj.metodo+".png\" title=\""+obj.metodo+"\" class=\"linkScreenshot\">" +
            						"<img src=\""+url+relativepath+"screenshots/"+obj.metodo+".png\" />" +
            					"</a>" +
            				"</div>" +
            				((obj.status != "sucesso") ? "<div class=\"stackUiTest\"><h2>Stacktrace:</h2><pre class=\"stackUiTest\" id=\""+obj.metodo.replace(/\./g,"")+"\"></pre></div>" : "") +
            			"</div>" +
            		"</dd>");
            $('.linkScreenshot').colorbox({retinaImage:true, retinaUrl:true});
            
      		if(obj.status != "sucesso"){
      			fetchStack(obj.classe, obj.metodo.replace(/\./g,""));
      		}
            allPanels = $('.accordion > dd').last().hide();
            $('.accordion > dt > a').click(function() {
                $this = $(this);
                $target =  $this.parent().next();
                if(!$target.hasClass('active')){
                	$('.active').removeClass('active').slideUp();
                     $target.addClass('active').slideDown();
                }
                return false;
            });
          }
          
          bufferAtual=novoBuffer;
      }
  }
  function fetchBuffer(){
      $.ajaxSetup({cache:false});
      $.get(url+relativepath+'teststream.txt',
          function(data){
              $('#stream').html(data);
              if (data.length > 0) {
                  parseBuffer($("#stream").html());            	  
              }else{
            	  console.log("stream empty");
            	  $('#stream').html("");
            	  $('#execucao').html("");
              }
          }
      );
  }
  
  function fetchStack(classe, metodo){
      $.ajaxSetup({cache:false});
      $.get(url+relativepath+'surefire-reports/'+classe+'.txt',
          function(data){
              $("#"+metodo).html(data);
          }
      );
  }
  
  function mostrarFalhas(){
	  $(".containerPassou").hide();
	  $(".containerFalhou").show();
  }
  
  function mostrarSucessos(){	  
	  $(".containerPassou").show();
	  $(".containerFalhou").hide();
  }
  
  function mostrarTodos(){
	  $(".containerPassou").show();
	  $(".containerFalhou").show();
  }
  
  fetchBuffer();
  window.setInterval(function(){
	  //Carregar conteúdo
	  fetchBuffer();
	  
	  //Atualizar totais
	  $(".total").html($(".itemUiTest").length);
	  $(".passou").html($(".itemPassou").length);
	  $(".falhou").html($(".itemFalhou").length);
	  $("#resultado").show();
	  
	  //Ordenar
	  if($("#ordenar").is(":checked")){
		  ordenar();
	  }

	  //Mostrar Loading de processamento da Build
	  buildInfo = $.parseJSON($.ajax({ type:"GET", url: url+"/lastBuild/api/json", async:false }).responseText);
	  if(buildInfo.result === null){
		  $(".loadingUiTest").show();
	  }else{
		  $(".loadingUiTest").hide();		  
	  }
  },2000);
  
	function ordenar(){
		$('dt').sortElements(function(a, b){
		    return $(a).text() > $(b).text() ? 1 : -1;
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
  
function ajaxSubmit(postUrl){
	$.post(postUrl, {name: "Foo"}).done(function(data){
		console.log( "Data Loaded: " + data );}
	);
}
})(jQuery)
