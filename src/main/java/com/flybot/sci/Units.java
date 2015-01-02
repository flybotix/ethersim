package com.flybot.sci;

public abstract class Units
{
  public abstract String getLabel();
  public abstract double getSiConversion();
  
  public String toString()
  { 
    return getLabel(); 
  }
}
