package com.flybot.apps;

import javafx.scene.layout.Region;

public interface IApp
{
   /**
    * Do not assume that the app is displayed prior to this method being called
    */
   public void init();
   
   public void destroy();
   
   public Region getDisplay();

}
