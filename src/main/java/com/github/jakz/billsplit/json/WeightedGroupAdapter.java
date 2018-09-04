package com.github.jakz.billsplit.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.github.jakz.billsplit.Environment;
import com.github.jakz.billsplit.WeightedGroup;
import com.github.jakz.billsplit.data.Person;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.pixbits.lib.json.JsonAdapter;

public class WeightedGroupAdapter implements JsonAdapter<WeightedGroup>
{
  private static final String ALL_SPLIT_EQUALLY = "split-equally";
  
  private final Environment env;
  
  public WeightedGroupAdapter(Environment env)
  {
    this.env = env;
  }

  @Override
  public JsonElement serialize(WeightedGroup src, Type type, JsonSerializationContext context)
  {
    // TODO
    return null;
  }

  @Override
  public WeightedGroup deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
  {
    if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
    {
      String string = json.getAsString();
      
      /* "split-equally" */
      if (json.getAsString().equals(ALL_SPLIT_EQUALLY))
        return new WeightedGroup(env.group, env.group.size());
      
      Person specific = env.person(string);
      
      /* "PersonName" */
      if (specific != null)
        return new WeightedGroup(specific);
    }
    else if (json.isJsonArray())
    {
      JsonArray array = json.getAsJsonArray();
      
      String splitType = array.get(0).getAsString();
      
      /* [ "split-equally", "Foo", "Bar" ] */
      if (splitType.equals(ALL_SPLIT_EQUALLY))
      {
        List<Person> persons = new ArrayList<>();
        
        for (int i = 1; i < array.size(); ++i)
        {
          Person person = env.person(array.get(i).getAsString());
          if (person == null)
            throw new JsonParseException("Unknown person: "+array.get(i).getAsString());
          
          persons.add(person);
        }
        
        return new WeightedGroup(persons);
      }
    }
    
    throw new JsonParseException("Unknown expense split mode");
  }
}
