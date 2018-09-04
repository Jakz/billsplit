package com.github.jakz.billsplit.json;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jakz.billsplit.DefaultCategory;
import com.github.jakz.billsplit.Environment;
import com.github.jakz.billsplit.Expense;
import com.github.jakz.billsplit.ExpenseAmounts;
import com.github.jakz.billsplit.Share;
import com.github.jakz.billsplit.WeightedGroup;
import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Category;
import com.github.jakz.billsplit.data.Group;
import com.github.jakz.billsplit.data.Person;
import com.github.jakz.billsplit.data.Timestamp;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonUnserializer implements JsonDeserializer<Expense>
{
  private final Environment env;
  
  public JsonUnserializer(Environment env)
  {
    this.env = env;
  }
  
  @Override
  public Expense deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
  {
    JsonArray jarray = json.getAsJsonArray();
    
    Timestamp timestamp = context.deserialize(jarray.get(0), Timestamp.class);
    ExpenseAmounts amounts = context.deserialize(jarray.get(1), ExpenseAmounts.class);
    WeightedGroup group = context.deserialize(jarray.get(2), WeightedGroup.class);
    Category category = context.deserialize(jarray.get(3), Category.class);
    String title = context.deserialize(jarray.get(4), String.class);
    
    return new Expense(amounts, timestamp, group, category, title);
  }

}
