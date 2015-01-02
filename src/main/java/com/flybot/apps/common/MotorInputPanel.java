package com.flybot.apps.common;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import com.flybot.apps.OmisThrownTogether;
import com.flybot.fxwidgets.BorderedTitlePane;
import com.flybot.powertrain.EMotorProperty;
import com.flybot.powertrain.Motor;

public class MotorInputPanel extends OmisThrownTogether
{
  private Region mLayout;
  private Map<EMotorProperty, Control> mMotorFields = new HashMap<>();
  private ComboBox<Motor> motors;
  public MotorInputPanel()
  {
    initCommon();
    GridPane grid = createSimpleGrid(
        EnumSet.allOf(EMotorProperty.class), mMotorFields, 
        k->new TextField(Double.toString(k.mDefault)), 
        u->u.mUnits);

    if(sMotors != null && sMotors.isEmpty() == false)
    {
      motors = createComboBox(sMotors, EnumSet.allOf(EMotorProperty.class), mMotorFields);
      grid.add(new Label("Motors: "), 0, EMotorProperty.values().length);
      grid.add(motors, 1, EMotorProperty.values().length);
    }
    mLayout = new BorderedTitlePane(getAppName(), grid);
  }
  
  public ComboBox<Motor> getSelectedMotorProperty()
  {
    return motors;
  }
  
  public Map<EMotorProperty, Control> getFields()
  {
    return mMotorFields;
  }

  @Override
  protected String getAppName()
  {
    return "Motor Input Panel";
  }

  @Override
  public void init()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void destroy()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Region getDisplay()
  {
    return mLayout;
  }
}
