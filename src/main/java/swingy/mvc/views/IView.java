package swingy.mvc.views;

public interface IView
{
    public void    ChooseHero() throws Exception;
    public void    drawGameObjects();
    public void    viewRepaint();
    public void    scrollPositionManager();
    public void    updateData();
    public void    addLog(String text);

    public boolean simpleDialog(String message);
    public String  get_Type();
    public void    close();
}