package controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import main.ClientMain;

public class HUDController implements ScreenController {
	
  private Screen screen;
  private ClientMain app;

 public HUDController() {}
  
  public HUDController(ClientMain app, Nifty nifty)
  {
    this.app = app;
    this.screen = nifty.getScreen("hud");
  }
  
  public void bind(Nifty newNifty, Screen newScreen)
  {
    this.screen = newScreen;
    update();
  }
  
  public void onEndScreen() {}
  
  public void onStartScreen() {}
  
  public void update()
  {
    TextRenderer credits_renderer = (TextRenderer)this.screen.findElementByName("hud_layer").findElementByName("panel_right").findElementByName("panel_right").findElementByName("text_01").getRenderer(TextRenderer.class);
    if (this.app == null)
    {
      credits_renderer.setText("Some Info");
      return;
    }
    int credits = 0;
    credits_renderer.setText("some text info");
  }
}
