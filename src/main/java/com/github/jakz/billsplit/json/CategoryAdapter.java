package com.github.jakz.billsplit.json;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

import com.github.jakz.billsplit.Category;
import com.github.jakz.billsplit.DefaultCategory;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.pixbits.lib.json.JsonAdapter;

public class CategoryAdapter implements JsonAdapter<Category>
{
  @Override
  public JsonElement serialize(Category src, Type type, JsonSerializationContext context)
  {
    if (src instanceof DefaultCategory)
      return new JsonPrimitive(((DefaultCategory)src).name().toLowerCase());
    else
      return new JsonPrimitive(src.caption());
  }

  @Override
  public Category deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
  {
    String string = json.getAsString();
    
    Optional<Category> category = Arrays.stream(DefaultCategory.values())
        .filter(c -> c.name().compareToIgnoreCase(string) == 0)
        .findFirst()
        .map(c -> c);
      
      if (!category.isPresent())
        throw new JsonParseException("Unknown category: "+string);
      else
        return category.get();
  }
}
