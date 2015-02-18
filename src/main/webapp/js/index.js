  var allPanels = $('.accordion > dd').hide();
  var bufferAtual=0;
  var novoBuffer=0;
  function parseBuffer(text){
      var lines = text.split(/\n/);
      novoBuffer = lines.length -1;
      if(bufferAtual < novoBuffer){
          for(var i=bufferAtual; i<novoBuffer; i++){
        	var obj = jQuery.parseJSON(lines[i]);
      		if(obj.status == "sucesso"){
      			$("#execucao").append("<dt><a href=\"\">"+obj.metodo+" <img src=\""+pluginpath+"images/passed.gif\" /> </a></dt>");
      		}else{
      			$("#execucao").append("<dt><a href=\"\">"+obj.metodo+" <img src=\""+pluginpath+"images/failed.png\" /> </a></dt>");
      		}
            $("#execucao").append("" +
            		"<dd>" +
            			"<div>" +
            				"<h2>Screenshot:</h2>" +
            				"<img src=\""+relativepath+"screenshots/"+obj.metodo+".png\" />" +
            				((obj.status != "sucesso") ? "<h2>Stacktrace:</h2><pre class=\"stack\" id=\""+obj.metodo.replace(/\./g,"")+"\"></pre>" : "") +
            			"</div>" +
            		"</dd>");

      		if(obj.status != "sucesso"){
      			fetchStack(obj.classe, obj.metodo.replace(/\./g,""));
      		}
          }
          allPanels = $('.accordion > dd').hide();
          bufferAtual=novoBuffer;
          $('.accordion > dt > a').click(function() {
              $this = $(this);
              $target =  $this.parent().next();
              if(!$target.hasClass('active')){
                   allPanels.removeClass('active').slideUp();
                   $target.addClass('active').slideDown();
              }
              return false;
          });
      }
  }
  function fetchBuffer(){
      $.ajaxSetup({cache:false});
      $.get(relativepath+'teststream.txt',
          function(data){
              $('#stream').html(data);
              parseBuffer($("#stream").html());
          }
      );
  }
  
  function fetchStack(classe, metodo){
      $.ajaxSetup({cache:false});
      $.get(relativepath+'surefire-reports/'+classe+'.txt',
          function(data){
              $("#"+metodo).html(data);
              console.log(metodo);
              console.log(data);
          }
      );
  }
  
  fetchBuffer();
  window.setInterval(function(){
	  fetchBuffer();
  },2000);