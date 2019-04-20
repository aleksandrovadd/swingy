package swingy.mvc.views;

public interface IView
{
    public void ChooseCharacter() throws Exception;
    public void drawGameObjects();
    public void viewRepaint();
    public void scrollPositionManager();
    public void updateData();
    public void addLog(String text);

    public boolean simpleDialog(String message);
    public String getViewType();
    public void close();
}