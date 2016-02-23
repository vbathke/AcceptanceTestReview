package br.vbathke.helper;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonParseSingleQuote{

	static JsonNode df;
	
	public JsonParseSingleQuote(String data){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		try {
			df = mapper.readValue(data, JsonNode.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String get(String field){
		String retorno = "";
		try{
			retorno = df.get(field).getTextValue();
		}catch(Exception e){}
		return retorno;
	}

}