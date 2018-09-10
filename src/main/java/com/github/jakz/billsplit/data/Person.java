package com.github.jakz.billsplit.data;

public class Person implements Comparable<Person>
{
  private final String nickname;
  private Group group;
  
  public Person(String nickname)
  {
    this.nickname = nickname;
  }
  
  public String nickname() { return nickname; }
  
  public void group(Group group) { this.group = group; }
  public Group group() { return group; }

  @Override
  public int compareTo(Person o) { return nickname.compareTo(o.nickname); }
  
  public String toString() { return nickname; }
  public int hashCode() { return nickname.hashCode(); }
  public boolean equals(Object o) { return o instanceof Person && ((Person)o).nickname.equals(nickname); }
}
