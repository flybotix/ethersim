package com.flybot.motor;

import com.flybot.apps.simplecalc.SimpleCalculator;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class EtherSimOmi extends Application
{
  private HBox mPanel;
  
  public static void main(String[] pArgs)
  {
    launch(pArgs);
  }
  
  @Override
  public void start(Stage pStage) throws Exception
  {
    EtherSimOmi omi = new EtherSimOmi();

    Scene scene = new Scene(omi.getPanel());
    pStage.setScene(scene);
    pStage.setWidth(1280);
    pStage.setHeight(720);
    pStage.setTitle("Simple Calculator");
    pStage.show();    
  }

  private Parent getPanel()
  {
    return mPanel;
  }
}
