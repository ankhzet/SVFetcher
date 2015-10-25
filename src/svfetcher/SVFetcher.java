/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package svfetcher;

import javafx.application.Application;
import svfetcher.app.SVFetcherApp;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class SVFetcher extends Application {

  public static void main(String[] args) throws Exception {
    launch(SVFetcherApp.class, args);
  }

}
