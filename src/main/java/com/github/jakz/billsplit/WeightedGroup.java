package com.github.jakz.billsplit;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.pixbits.lib.lang.Pair;

public class WeightedGroup
{
  private Map<Person, Float> people;
  
  public WeightedGroup(Person people)
  {
    this.people = new TreeMap<>();
    this.people.put(people, 1.0f);
  }
  
  public WeightedGroup(Person... people)
  {
    float sum = 0.0f;
    float dx = 1.0f / people.length;
    Person last = people[people.length - 1];
    
    for (Person person : people)
    {
      if (person != last)
      {
        this.people.put(person, dx);
        sum += dx;
      }
      else
      {
        this.people.put(person, 1.0f - sum);
      }
    }     
  }
  
  public Stream<Pair<Person, Float>> stream()
  {
    return people.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue()));
  }
}
