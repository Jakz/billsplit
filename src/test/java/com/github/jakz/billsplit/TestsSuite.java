package com.github.jakz.billsplit;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.jakz.billsplit.data.Category;
import com.github.jakz.billsplit.data.Group;
import com.github.jakz.billsplit.data.Person;
import com.github.jakz.billsplit.data.Timestamp;
import com.github.jakz.billsplit.parser.json.CategoryAdapter;
import com.github.jakz.billsplit.parser.json.ExpenseAmountsAdapter;
import com.github.jakz.billsplit.parser.json.TimestampAdapter;
import com.github.jakz.billsplit.parser.json.WeightedGroupAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  AmountTests.class,
  MultiAmountTests.class,
  WeightedGroupTests.class,
  DebtTests.class,
  SettlerTests.class,
  
  
  ExchangeRatesTests.class,
  
  
  JsonTimestampTests.class,
  JsonExpenseAmountsTests.class
})
public class TestsSuite
{
  public static Group group(String... names)
  {
    Set<Person> people = Arrays.stream(names)
        .map(Person::new)
        .collect(Collectors.toSet());
      
    return new Group(people);
  }
  
  public static Timestamp randomTimestamp()
  {
    int year = ThreadLocalRandom.current().nextInt(1980, 2020);
    int month = ThreadLocalRandom.current().nextInt(1, 13);
    int day = ThreadLocalRandom.current().nextInt(1, 29);
    
    return Timestamp.of(year, month, day);
  }
  
  public static Gson json(Group group)
  {
    Environment env = new Environment(group);
    
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(Timestamp.class, new TimestampAdapter());
    builder.registerTypeAdapter(Category.class, new CategoryAdapter());
    builder.registerTypeAdapter(ExpenseAmounts.class, new ExpenseAmountsAdapter(env));
    builder.registerTypeAdapter(WeightedGroup.class, new WeightedGroupAdapter(env));
    builder.setPrettyPrinting();
    
    return builder.create();
  }
}