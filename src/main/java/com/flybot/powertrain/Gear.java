package com.flybot.powertrain;

import com.flybot.sci.Distance;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Gear
{
  private final IntegerProperty mDiametricPitch;
  private final DoubleProperty mNumTeeth;
  private final ObjectProperty<Distance> mDiameter;
  
  public Gear(int pDiametricPitch)
  {
    this(pDiametricPitch, 1);
  }
  
  public Gear(int pDiametricPitch, int pNumTeeth)
  {
    mDiametricPitch = new SimpleIntegerProperty(pDiametricPitch);
    mNumTeeth = new SimpleDoubleProperty(pNumTeeth);
    mDiameter = new SimpleObjectProperty<>();
    mNumTeeth.addListener(e -> {
//      mDiameter.set(Distance.from(Distance.EUnits.INCHES, 
//          mNumTeeth.get()/(double)mDiametricPitch.get())); 
      mDiameter.set(new Distance(Distance.INCH, mNumTeeth.get() / (double)mDiametricPitch.get()));
    });
  }
  
  public void bindNumTeethProperty(IntegerProperty pToProperty)
  {
    mNumTeeth.bind(pToProperty);
  }
  
  public ReadOnlyDoubleProperty getNumTeethProperty()
  {
    return mNumTeeth;
  }
  
  public int getDiametricPitch()
  {
    return mDiametricPitch.get();
  }
  
  public ReadOnlyObjectProperty<Distance> getDiameter()
  {
    return mDiameter;
  }
}
