package swingy.mvc.views.console;

import swingy.mvc.Controller;
import swingy.mvc.models.Monster;
import swingy.mvc.views.IView;

import java.util.*;

import static swingy.util.Constants.*;

public class ConsoleView implements IView
{
    private Controller controller;
    private Scanner scanner;
    private ArrayList<char[]> map;
    private int numAttributes;
    private String type;

    public ConsoleView(Controller controller) {
        this.controller = controller;
        scanner = new Scanner(System.in);
        map = new ArrayList<>();
        numAttributes = 0;
        type = CONSOLE_STR;
    }

    @Override
    public void ChooseCharacter() throws Exception {
        controller.setCharacter(new ConsoleChooseCharacter(scanner).getCharacter());
    }

    @Override
    public void drawObjects() {
        drawMap();
        System.out.println("\n0) " + EXIT_STR + "\n\n    1) " + NORTH_STR + "\n2) " + WEST_STR +
                "    3) " + EAST_STR + "\n    4) " + SOUTH_STR + "\n " + GUI_STR + " - for gui-mode");
        controller.keyPressed(getValidValue());
    }

    @Override
    public void reDraw(){
        this.drawObjects();
    }

    @Override
    public boolean yesNoDialog(String message) {
        System.out.println(message + "\n 1) Yes  2) No");

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
    public String getViewType() { return type; }

    @Override
    public void close() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void drawMap() {
        char[] mapBuff = new char[controller.getSizeMap()];
        Arrays.fill(mapBuff, '.');

        map.clear();
        for (int i = 0; i < controller.getSizeMap(); i++) {
            map.add(mapBuff.clone());
        }

        map.get(controller.getCharacter().getPosition().y)[controller.getCharacter().getPosition().x] = 'C';
        for (Monster monster : controller.getMonsters()) {
            map.get(monster.getPosition().y)[monster.getPosition().x] = 'M';
        }

        numAttributes = 0;
        for (char[] str : map) {
            System.out.println("   " + String.valueOf(str) + getAttributes(numAttributes++));
        }
    }

    private int getValidValue() {
        String inputString;

        while (true) {
            inputString = EMPTY_STRING;
            while (inputString.equals(EMPTY_STRING)) {
                try {
                    inputString = scanner.nextLine();
                } catch (Exception e) {
                    System.err.println("CTRL+D is bad!");
                    System.exit(0);
                }
            }
            if (inputString.equals(GUI_STR)) {
                return -2;
            } else if (!inputString.matches(DIGITS_PATTERN)) {
                System.out.println("That's not a number!");
            } else {
                break;
            }
        }
        int inputValue = -3;
        switch (Integer.parseInt(inputString) ) {
            case NORTH:
                inputValue = KEY_NORTH;
                break;
            case WEST:
                inputValue = KEY_WEST;
                break;
            case EAST:
                inputValue = KEY_EAST;
                break;
            case SOUTH:
                inputValue = KEY_SOUTH;
                break;
            case EXIT:
                exit();
                break;
            case GUI_SWITCH:
                inputValue = GUI_SWITCH;
                break;
        }
        return inputValue;
    }

    private String getAttributes(int numAttributes) {

        String attributes = "    ";

        switch (numAttributes) {
            case NUM_ATTRIBUTES_NAME:
                attributes += "Name: " + controller.getCharacter().getName();
                break;
            case NUM_ATTRIBUTES_TYPE:
                attributes += "Type: " + controller.getCharacter().getType();
                break;
            case NUM_ATTRIBUTES_LEVEL:
                attributes += "Level: " + controller.getCharacter().getLevel();
                break;
            case NUM_ATTRIBUTES_LOCATION:
                attributes += "Location [" + controller.getCharacter().getPosition().x + ", "
                    + controller.getCharacter().getPosition().y + "]";
                break;
            case NUM_ATTRIBUTES_EXP:
                attributes += "Exp: " + controller.getCharacter().getExp() + "/" +
                    controller.getCharacter().getNecessaryExp();
                break;
            case NUM_ATTRIBUTES_ATTACK:
                attributes += "Attack: " + controller.getCharacter().getAttack();
                break;
            case NUM_ATTRIBUTES_DEFENSE:
                attributes += "Defense: " + controller.getCharacter().getDefense();
                break;
            case NUM_ATTRIBUTES_HP:
                attributes += "Hp: " + controller.getCharacter().getHitP() + "/"
                    + controller.getCharacter().getMaxHp();
                break;
            case NUM_ATTRIBUTES_ARTEFACT:
                if (controller.getCharacter().getArtefact() != null && !controller.getCharacter().getArtefact().getType().equals(EMPTY_STRING) ) {
                attributes += "Artefact-" + controller.getCharacter().getArtefact().getType() + ": " +
                        controller.getCharacter().getArtefact().getValue();
                }
                break;
                default:
                    return EMPTY_STRING;
        }

        return attributes;
    }

    private void exit() {
        controller.saveCharacter();
        System.exit(0);
    }
}
