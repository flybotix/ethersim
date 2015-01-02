package com.flybot.powertrain;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class GearboxInputPanel
{
  private static final int sSTAGES = 4;
  GearTextField[][] mTextFields = new GearTextField[sSTAGES][2]; // [row][col]
  public GearboxInputPanel()
  {
    GridPane gl = new GridPane();
    gl.add(new Label("Driving"), 1, 0);
    gl.add(new Label("Driven"), 2, 0);
    for(int i = 1; i <= sSTAGES; i++) gl.add(new Label("Stage " + i), 0, 1);
    
    for(int r = 0; r < sSTAGES; r++)
    {
      for(int c = 0; c < 2; c++)
      {
        mTextFields[r][c] = new GearTextField();
        gl.add(mTextFields[r][c], c+1, r+1);
      }
    }
  }
  
  private void recalculate()
  {
    
  }

  private class GearTextField extends HBox
  {
    private final TextField mField = new TextField("1");
    private GearTextField()
    {
      mField.addEventFilter(KeyEvent.KEY_PRESSED, e->{
        if(!isNumber(e.getText())) e.consume();
      }); // Validate #'s only
      mField.textProperty().addListener(e->{
        if(Integer.parseInt(mField.getText()) < 1)
        {
          mField.setText("1");
        }
        else
        {
          recalculate();
        }
      });
      
      Button plus = new Button("+");
      Button minus = new Button("-");
      plus.setOnAction(e->mField.setText(Integer.toString(Integer.parseInt(mField.getText())+1)));
      minus.setOnAction(e->mField.setText(Integer.toString(Integer.parseInt(mField.getText())-1)));
    }
  }
  
  private static boolean isNumber(String pNum)
  {
    boolean result = true;
    for(int i = 0; i < pNum.length(); i++)
    {
      result &= Character.isDigit(pNum.charAt(i));
      if(!result) break;
    }
    return result;
  }
}
