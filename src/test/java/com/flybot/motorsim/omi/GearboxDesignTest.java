package com.flybot.motorsim.omi;

import com.flybot.apps.motorsim.GearboxDesignPanel;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GearboxDesignTest extends Application
{
  public static void main(String[] pArgs)
  {
    launch(pArgs);
  }

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {
    StackPane pane = new StackPane();
    Scene scene = new Scene(pane);
    
    pane.getChildren().add(getNode());
    
    pPrimaryStage.setScene(scene);
    pPrimaryStage.setWidth(800);
    pPrimaryStage.setHeight(600);
    pPrimaryStage.show();
  }

  protected Node getNode()
  {
    return new GearboxDesignPanel();
  }
}
