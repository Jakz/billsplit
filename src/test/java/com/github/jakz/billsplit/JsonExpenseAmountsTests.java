package com.github.jakz.billsplit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Rule;
import org.junit.Test;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Group;
import com.github.jakz.billsplit.data.Timestamp;
import com.github.jakz.billsplit.parser.json.TimestampAdapter;
import com.github.jakz.billsplit.support.Repeat;
import com.github.jakz.billsplit.support.RepeatRule;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class JsonExpenseAmountsTests
{
  @Test
  public void testSingleDeserialization()
  {
    final Group group = TestsSuite.group("foo");
    final String json = "[ \"10.00 USD\", \"foo\" ]";
    final Gson gson = TestsSuite.json(group);
    final ExpenseAmounts amounts = gson.fromJson(json, ExpenseAmounts.class);
    
    assertEquals(amounts.size(), 1);
    assertEquals(amounts.get(0), new Share<>(group.forName("foo"), Amount.of("10.00 USD")));
  }
  
  @Test
  public void testSingleNestedDeserialization()
  {
    final Group group = TestsSuite.group("foo");
    final String json = "[ [ \"12.34 EUR\", \"foo\" ] ]";
    final Gson gson = TestsSuite.json(group);
    final ExpenseAmounts amounts = gson.fromJson(json, ExpenseAmounts.class);
    
    assertEquals(amounts.size(), 1);
    assertEquals(amounts.get(0), new Share<>(group.forName("foo"), Amount.of("12.34 EUR")));
  }
  
  @Test
  public void testDoubleDeserialization()
  {
    final Group group = TestsSuite.group("foo", "bar");
    final String json = "[ [ \"10.00 USD\", \"bar\" ], [ \"12.34 EUR\", \"foo\" ] ]";
    final Gson gson = TestsSuite.json(group);
    final ExpenseAmounts amounts = gson.fromJson(json, ExpenseAmounts.class);
    
    assertEquals(amounts.size(), 2);
    assertEquals(amounts.get(0), new Share<>(group.forName("bar"), Amount.of("10.00 USD")));
    assertEquals(amounts.get(1), new Share<>(group.forName("foo"), Amount.of("12.34 EUR")));

  }
}


