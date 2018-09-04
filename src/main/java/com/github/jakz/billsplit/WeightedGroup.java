package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.github.jakz.billsplit.data.Person;
import com.pixbits.lib.lang.Pair;

public class WeightedGroup
{
  private final List<Share<Float>> people;
  
  public WeightedGroup(Person... people)
  {
    this(Arrays.asList(people));     
  }
  
  public WeightedGroup(Collection<Person> people)
  {
    this(people, people.size());
  }
  
  public WeightedGroup(Iterable<Person> people, int size)
  {
    this.people = new ArrayList<>();
    
    float sum = 0.0f;
    float dx = 1.0f / size;
    
    Iterator<Person> it = people.iterator();
    
    while (it.hasNext())
    {
      Person person = it.next();
      
      if (it.hasNext())
      {
        this.people.add(new Share<>(person, dx));
        sum += dx;
      }
      else
      {
        this.people.add(new Share<>(person, 1.0f - sum));
      }
    }
  }
  
  public Share<Float> get(int index) { return people.get(index); }
  public Share<Float> get(String name) { return people.stream().filter(s -> s.person.nickname().equals(name)).findFirst().get(); } 
  
  public int size() { return people.size(); }
  
  public float weight(String person) { return people.stream().filter(s -> s.person.nickname().equals(person)).findFirst().get().value; }
  public float weight(Person person) { return people.stream().filter(s -> s.person == person).findFirst().get().value; }
  
  public Stream<Share<Float>> stream()
  {
    return people.stream();
  }
}
