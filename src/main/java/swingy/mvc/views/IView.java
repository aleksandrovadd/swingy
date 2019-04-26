package swingy.mvc.views;

public interface IView {
    void ChooseCharacter() throws Exception;
    void drawObjects();
    void reDraw();
    void scrollPositionManager();
    void updateData();
    void addLog(String text);

    boolean simpleDialog(String message);
    String getViewType();
    void close();
}