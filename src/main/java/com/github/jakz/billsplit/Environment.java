package com.github.jakz.billsplit;

public class Environment
{
  public final Group group;
  
  public Environment(Group group)
  {
    this.group = group;
  }
  
  public Person person(String nickname)
  {
    return group.forName(nickname);
  }
}
