package com.flybot.apps.spinz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Spinz extends Application
{
  public static void main(String[] pArgs)
  {
    launch(pArgs);
  }

  @Override
  public void start(Stage pStage) throws Exception
  {
     Scene scene = new Scene(new SPanel());
     pStage.setScene(scene);
     pStage.setWidth(1280);
     pStage.setHeight(720);
     pStage.setTitle("Simple Calculator");
     pStage.show();
  }
  
}
