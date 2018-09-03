package com.github.jakz.billsplit.json;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jakz.billsplit.Expense;
import com.github.jakz.billsplit.Timestamp;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonUnserializer implements JsonDeserializer<Expense>
{
  
  private final Pattern timestampPattern = Pattern.compile("([0-9]{4})\\\\-([01][0-9])\\\\-([0-3][0-9])");
  
  private Timestamp parseTimestamp(String string) throws JsonParseException
  {
    Matcher matcher = timestampPattern.matcher(string);
    
    if (matcher.matches())
    {
      int year = Integer.parseInt(matcher.group(1));
      int month = Integer.parseInt(matcher.group(2));
      int day = Integer.parseInt(matcher.group(3));
      
      return Timestamp.of(year, month, day);
    }
    else
      throw new JsonParseException("Wrong timestamp format: "+string);
  }
  
  @Override
  public Expense deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
  {
    if (!json.isJsonArray()) throw new JsonParseException("Expense root element must be an array");

    JsonArray array = json.getAsJsonArray();
    
    Timestamp timestamp = parseTimestamp(array.get(0).getAsString());
    
    return null;
  }

}
