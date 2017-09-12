package com.github.jakz.billsplit;

public class Person
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
}
