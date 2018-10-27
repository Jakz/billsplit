package com.github.jakz.billsplit.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.jakz.billsplit.Environment;
import com.github.jakz.billsplit.Expense;
import com.github.jakz.billsplit.ExpenseAmounts;
import com.github.jakz.billsplit.ExpenseSet;
import com.github.jakz.billsplit.WeightedGroup;
import com.github.jakz.billsplit.data.Category;
import com.github.jakz.billsplit.data.Timestamp;
import com.github.jakz.billsplit.parser.json.CategoryAdapter;
import com.github.jakz.billsplit.parser.json.ExpenseAmountsAdapter;
import com.github.jakz.billsplit.parser.json.ExpenseDeserializer;
import com.github.jakz.billsplit.parser.json.ExpenseSetDeserializer;
import com.github.jakz.billsplit.parser.json.TimestampAdapter;
import com.github.jakz.billsplit.parser.json.WeightedGroupAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonParser implements Parser
{
  @Override
  public ExpenseSet parse(Path path, Environment environment) throws IOException
  {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(Timestamp.class, new TimestampAdapter());
    builder.registerTypeAdapter(Category.class, new CategoryAdapter());
    builder.registerTypeAdapter(ExpenseAmounts.class, new ExpenseAmountsAdapter(environment));
    builder.registerTypeAdapter(WeightedGroup.class, new WeightedGroupAdapter(environment));
    builder.registerTypeAdapter(ExpenseSet.class, new ExpenseSetDeserializer(environment));
    builder.registerTypeAdapter(Expense.class, new ExpenseDeserializer(environment));

    builder.setPrettyPrinting();
    
    Gson gson = builder.create();
    
    try (BufferedReader rdr = Files.newBufferedReader(Paths.get("expenses.json")))
    {
      return gson.fromJson(rdr, ExpenseSet.class);
    } 
  }
}
