package com.github.jakz.billsplit.parser.json;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jakz.billsplit.data.Timestamp;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.pixbits.lib.json.JsonAdapter;

public class TimestampAdapter implements JsonAdapter<Timestamp>
{
  private final static Pattern timestampPattern = Pattern.compile("([0-9]{4})\\-([01]*[0-9])\\-([0-3]*[0-9])");

  @Override
  public JsonElement serialize(Timestamp obj, Type type, JsonSerializationContext context)
  {
    return new JsonPrimitive(String.format("%04d-%02d-%02d", obj.year(), obj.month(), obj.day()));
  }

  @Override
  public Timestamp deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
  {
    if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString())
      throw new JsonParseException("JSON Timestamp must be string");
    else 
    {
      Timestamp value = Timestamp.of(json.getAsString());
      
      String string = json.getAsString();
      
      Matcher matcher = timestampPattern.matcher(string);
      
      if (value != null)
      {
        return value;
      }
      else
        throw new JsonParseException("Wrong timestamp format: "+string);
    }
  }

}
