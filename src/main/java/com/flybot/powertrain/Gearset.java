package com.flybot.powertrain;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.flybot.sci.Distance;

public class Gearset
{
  private final Gear mDrivingGear;
  private final Gear mDrivenGear;
  private final DoubleProperty mEfficiency;
  private final DoubleProperty mRatio;
  private final ObjectProperty<Distance> mCenterCenter;
  
  public Gearset(Gear pDrivingGear, Gear pDrivenGear, double pEfficiency)
  {
    mDrivingGear = pDrivingGear;
    mDrivenGear = pDrivenGear;
    mEfficiency = new SimpleDoubleProperty(pEfficiency);
    mRatio = new SimpleDoubleProperty();
    mCenterCenter = new SimpleObjectProperty<>();
    
    mDrivingGear.getNumTeethProperty().addListener(e->{
      mRatio.set(mDrivingGear.getNumTeethProperty().get()/mDrivenGear.getNumTeethProperty().get());
      mCenterCenter.set(new Distance(Distance.INCH, 0d)/*Distance.from(Distance.EUnits.INCHES, 0)*/);
    });
    mDrivenGear.getDiameter().addListener(e->{
      mRatio.set(mDrivingGear.getNumTeethProperty().get()/mDrivenGear.getNumTeethProperty().get());
      mCenterCenter.set(
          new Distance(Distance.METER, mDrivingGear.getDiameter().get().si()/2 + mDrivenGear.getDiameter().get().si()/2)
          /*Distance.from(Distance.EUnits.METERS, 
          mDrivingGear.getDiameter().get().mMeters/2 + 
          mDrivenGear.getDiameter().get().mMeters/2)*/);
    });
  }
  
  /**
   * @return the gear ratio, as defined by 
   * (# Teeth on Driving Gear / # Teeth on Driven Gear)
   */
  public ReadOnlyDoubleProperty getRatio()
  {
    return ReadOnlyDoubleProperty.readOnlyDoubleProperty(mRatio);
  }
  
  /**
   * @return the distance between the centers of the gears
   */
  public ReadOnlyObjectProperty<Distance> getCenterCenter()
  {
    return mCenterCenter;
  }
  
  /**
   * @return the estimated efficiency of the gear set
   */
  public double getEfficiency()
  {
    return mEfficiency.get();
  }
}
