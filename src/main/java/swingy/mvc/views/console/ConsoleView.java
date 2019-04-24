package swingy.mvc.views.console;

import swingy.mvc.Controller;
import swingy.mvc.models.Monster;
import swingy.mvc.views.IView;
import swingy.util.Constants;

import java.util.*;

public class ConsoleView implements IView
{
    private Controller controller;
    private Scanner scanner;
    private ArrayList<char[]> map;
    private int numStat;
    private String type;

    public ConsoleView(Controller controller) {
        this.controller = controller;
        scanner = new Scanner(System.in);
        map = new ArrayList<>();
        numStat = 0;
        type = Constants.CONSOLE_STR;
    }

    @Override
    public void ChooseCharacter() throws Exception {
        controller.setCharacter(new ConsoleChooseCharacter(scanner).getCharacter());
    }

    @Override
    public void drawGameObjects() {
        drawMap();
        System.out.println("\n0) Exit\n\n     1) North\n2) West     3) East\n     4) South\n\"gui\" - for gui-mode");
        controller.keyPressed(getValidValue());
    }

    @Override
    public void viewRepaint(){
        this.drawGameObjects();
    }

    @Override
    public boolean simpleDialog(String message) {
        System.out.println(message + "\n 1) Yes     2) No");

        int key;

        while ((key = getValidValue()) != 38 && key != 37) {
            System.err.println("Unknown value.");
        }

        return (key == 38);
    }

    @Override
    public void scrollPositionManager(){}

    @Override
    public void updateData(){}

    @Override
    public void addLog(String text) {
        System.out.println(text);
    }

    @Override
    public String getViewType() { return this.type; }

    @Override
    public void close() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void    drawMap() {
        char[] buff = new char[controller.getSizeMap()];
        Arrays.fill(buff, '.');

        map.clear();
        for (int i = 0; i < controller.getSizeMap(); i++)
            map.add( buff.clone() );

        map.get(controller.getCharacter().getPosition().y)[controller.getCharacter().getPosition().x] = 'C';
        for (Monster monster : controller.getMonsters())
            map.get(monster.getPosition().y)[monster.getPosition().x] = 'M';

        numStat = 0;
        for (char[] str : map) {
            System.out.println("     " + String.valueOf(str) + this.getStat(numStat++));
        }
    }

    private int getValidValue() {
        String str;

        while (true) {
            str = "";
            while (str.equals("")) {
                try {
                    str = scanner.nextLine();
                }
                catch (Exception e) {
                    System.err.println("CTRL+D is bad!");
                    System.exit(0);
                }
            }
            if (str.equals(Constants.GUI_STR))
                return -2;
            else if (!str.matches("^[0-9]+"))
                System.err.println("Enter a valid value !");
            else
                break;
        }
        int value = -3;
        switch (Integer.parseInt(str) ) {
            case 1:
                value = 38;
                break;
            case 2:
                value = 37;
                break;
            case 3:
                value = 39;
                break;
            case 4:
                value = 40;
                break;
            case 0:
                controller.saveCharacter(); System.exit(0);
                break;
            case -2:
                value = -2;
                break;
        }
        return value;
    }

    private String getStat(int numStat) {

        String stat = "       ";

        switch (numStat) {
            case 0:
                stat += "Name: " + controller.getCharacter().getName();
                break;
            case 1:
                stat += "Type: " + controller.getCharacter().getType();
                break;
            case 2:
                stat += "Level: " + controller.getCharacter().getLevel();
                break;
            case 3:
                stat += "Location [" + controller.getCharacter().getPosition().x + ", "
                    + controller.getCharacter().getPosition().y + "]";
                break;
            case 4:
                stat += "Exp: " + controller.getCharacter().getExp() + "/" +
                    controller.getCharacter().getNecessaryExp();
                break;
            case 5:
                stat += "Attack: " + controller.getCharacter().getAttack();
                break;
            case 6:
                stat += "Defense: " + controller.getCharacter().getDefense();
                break;
            case 7:
                stat += "Hp: " + controller.getCharacter().getHitP() + "/"
                    + controller.getCharacter().getMaxHp();
                break;
            case 8:
                if (controller.getCharacter().getArtifact() != null && !controller.getCharacter().getArtifact().getType().equals("") ) {
                stat += "Artifact-" + controller.getCharacter().getArtifact().getType() + ": " +
                        controller.getCharacter().getArtifact().getValue();
                }
                break;
                default:
                    return "";
        }

        return stat;
    }
}
