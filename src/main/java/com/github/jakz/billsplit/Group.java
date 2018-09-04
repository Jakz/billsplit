package com.github.jakz.billsplit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Group implements Iterable<Person>
{
  private final Set<Person> persons;
  
  public Group()
  {
    persons = new TreeSet<>();
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
  
  public Person forName(String nickname)
  {
    return persons.stream()
      .filter(p -> p.nickname().equals(nickname))
      .findFirst()
      .orElse(null);
  }
  
  public Stream<Person> stream() { return persons.stream(); }
  public int size() { return persons.size(); }
  
  @Override
  public Iterator<Person> iterator() { return persons.iterator(); }
  
  
  
}
