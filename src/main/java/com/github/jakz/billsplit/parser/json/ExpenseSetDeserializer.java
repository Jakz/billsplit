package com.github.jakz.billsplit.parser.json;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.github.jakz.billsplit.Environment;
import com.github.jakz.billsplit.Expense;
import com.github.jakz.billsplit.ExpenseSet;
import com.github.jakz.billsplit.data.Group;
import com.github.jakz.billsplit.data.Person;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import com.pixbits.lib.json.JsonAdapter;

public class ExpenseSetDeserializer implements JsonAdapter<ExpenseSet>
{
  private final Environment env;
  
  public ExpenseSetDeserializer(Environment env)
  {
    this.env = env;
  }
  
  @Override
  public JsonElement serialize(ExpenseSet src, Type typeOfSrc, JsonSerializationContext context)
  {
    return null;
  }

  @Override
  public ExpenseSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    JsonArray people = json.getAsJsonObject().get("people").getAsJsonArray();
    JsonArray data = json.getAsJsonObject().get("expenses").getAsJsonArray();
    
    Group group = new Group(
        StreamSupport.stream(people.spliterator(), false)
          .map(e -> e.getAsString())
          .map(Person::new)
          .collect(Collectors.toSet()
        )
    );
    
    env.setGroup(group);
    
    List<Expense> expenses = context.deserialize(data, new TypeToken<List<Expense>>(){}.getType());
    
    return new ExpenseSet(expenses);
  }
}
