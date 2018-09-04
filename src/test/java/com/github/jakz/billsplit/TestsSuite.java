package com.github.jakz.billsplit;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  AmountTests.class,
  MultiAmountTests.class,
  WeightedGroupTests.class,
  ExchangeRatesTests.class,
  JsonTests.class
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
}