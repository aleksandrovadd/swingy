package swingy.mvc.views.console;

import swingy.bd.DataBase;
import swingy.mvc.models.CharacterBuilder;
import swingy.mvc.models.Character;

import java.util.List;
import java.util.Scanner;

import static swingy.util.Constants.*;

class ConsoleChooseCharacter {

    private List<String> names;
    private Character character;
    private Scanner scanner;

    ConsoleChooseCharacter(Scanner scanner) {
        this.scanner = scanner;
    }

    Character getCharacter() throws Exception {
        int value;

        DataBase.getDataBase().connectDb();
        names = DataBase.getDataBase().getNames();

        while (character == null) {
            System.out.println("0) Exit\n1) Select previously created character\n2) Create new character");
            value = getValidValue();
            switch (value) {
                case EXIT:
                    System.exit(0);
                case CHOOSE_PREVIOUS_CHARACTER:
                    previousCharactersManager();
                    break;
                case CHOOSE_CREATE_CHARACTER:
                    chooseCharacterType();
                    break;
            }
        }
        return character;
    }

    private void previousCharactersManager() throws Exception {
        int index;
        int value;

        while (true) {
            index = 0;
            if (names.size() == 0) {
                System.out.println("You don't have characters, create them.");
                return;
            }
            System.out.println("0) come back");
            for (String name : names)
                System.out.println(++index + ") " + name);

            if ((value = getValidValue()) == 0)
                break;
            else if (value <= index) {
                System.out.println(DataBase.getDataBase().getCharacter(names.get(value - 1)).getInfo());
                System.out.printf("\nMake choice: 1) %s   2) %s   3) %s", SELECT_STR, REMOVE_STR, CANCEL_STR);
                int choice = getValidValue();
                if (choice == SELECT) {
                    character = DataBase.getDataBase().getCharacter(names.get(value - 1));
                }
                else if (choice == REMOVE) {
                    try {
                        DataBase.getDataBase().remove( names.get(value - 1) );
                        names.remove(value - 1);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (choice == SELECT || choice == CANCEL) {
                    break;
                }
            }
        }
    }

    private void chooseCharacterType() {
        int value;
        while (true) {
            System.out.printf("0) %s\n1) %s\n2) %s\n3) %s\n", COME_BACK_STR, WARRIOR_TYPE, MAGE_TYPE, ROGUE_TYPE);
            value = getValidValue();
            switch (value) {
                case COME_BACK:
                    return;
                case WARRIOR:
                    createNewCharacter(WARRIOR_TYPE);
                    break;
                case MAGE:
                    createNewCharacter(MAGE_TYPE);
                     break;
                case ROGUE:
                     createNewCharacter(ROGUE_TYPE);
                     break;
                     default:
                         break;
            }
        }
    }

    private void createNewCharacter(String type) {
        String characterName = "";
        String error = "";
        Character newCharacter = CharacterBuilder.buildByType(type);
        System.out.println(newCharacter.getInfo() + "\nCreate character?  1) Yes   2) No");
        int value = getValidValue();
        if (value == YES) {
            while (error != null) {
                System.out.print("Enter name: ");
                while (characterName.equals("")) {
                    characterName = scanner.nextLine();
                }
                error = CharacterBuilder.setName(newCharacter, characterName);
                for (String name : names) {
                    if (name.equals(characterName)) {
                        error = "Character with such name already exists";
                    }
                }
                if (error != null) {
                    System.err.println(error);
                }
                characterName = "";
            }
            try {
                DataBase.getDataBase().addNewCharacter(newCharacter);
                this.names.add(newCharacter.getName());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getValidValue() {
        int value;
        do {
            while (!scanner.hasNextInt()) {
                System.out.println("That's not a number!");
                scanner.next();
            }
            value = scanner.nextInt();
        } while (value < 0);
        return value;
    }
}