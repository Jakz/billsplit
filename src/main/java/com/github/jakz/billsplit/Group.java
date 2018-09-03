package com.github.jakz.billsplit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Group
{
  private final Set<Person> persons;
  
  public Group()
  {
    persons = new HashSet<>();
  }
  
  public Group(Set<Person> persons)
  {
    this.persons = persons;
  }
  
  public Group(Person... persons)
  {
    this();
    addPerson(persons);
  }
  
  public void addPerson(Person... persons)
  {
    this.persons.addAll(Arrays.asList(persons));
  }
  
  public Group byExcluding(Person person)
  {
    Set<Person> npersons = new HashSet<>(persons);
    npersons.remove(person);
    return new Group(npersons);
  }
  
  public Stream<Person> stream() { return persons.stream(); }
  
  
}
