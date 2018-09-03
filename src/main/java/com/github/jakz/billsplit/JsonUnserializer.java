package com.github.jakz.billsplit;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonUnserializer implements JsonDeserializer<Expense>
{
  private Group group ;
  
  
  private Person getOrAllocatePerson(String nickname)
  {
    //Person person = 
  }
  
  
  @Override
  public Expense deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
  {
    if (!json.isJsonArray()) throw new JsonParseException("Expense root element must be an array");
    
  }

}
