package swingy.mvc.views.console;

import swingy.bd.DataBase;
import swingy.mvc.models.heroBuilder.DirectorHero;
import swingy.mvc.models.Character;

import java.util.List;
import java.util.Scanner;

import static swingy.util.Constants.*;

public class ConsoleChooseHero {

    private DirectorHero builder;
    private List<String> names;
    private Character hero;
    private Scanner scanner;

    public ConsoleChooseHero(Scanner scanner) {
        builder = new DirectorHero();
        this.scanner = scanner;
    }

    public Character getHero() throws Exception {
        int value;

        DataBase.getDb().connectDb();
        names = DataBase.getDb().getNames();

        while (hero == null) {
            System.out.println("0) Exit\n1) Select previous created hero\n2) Create new hero");
            value = getValidValue();
            switch (value) {
                case EXIT:
                    System.exit(0);
                case CHOOSE_OLD_CHARACTER:
                    oldHeroesManager();
                    break;
                case CHOOSE_CREATE_CHARACTER:
                    heroCreator();
                    break;
            }
        }
        return hero;
    }

    private void oldHeroesManager() throws Exception {
        int index;
        int value;

        while (true) {
            index = 0;
            if (names.size() == 0) {
                System.out.println("You haven't heroes, create them.");
                return;
            }
            System.out.println("0) come back");
            for (String name : names)
                System.out.println(++index + ") " + name);

            if ((value = this.getValidValue()) == 0)
                break;
            else if (value <= index) {
                System.out.println(DataBase.getDb().getHero(names.get(value - 1)).getInfo());
                System.out.println("\nMake choice: 1) Select   2) Remove   3) Cancel");
                int choice = this.getValidValue();
                if (choice == SELECT) {
                    hero = DataBase.getDb().getHero(names.get(value - 1));
                }
                else if (choice == REMOVE) {
                    try {
                        DataBase.getDb().remove( names.get(value - 1) );
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

    private void heroCreator() {
        int value;

        while (true) {
            System.out.println("0) come back\n1) Human\n2) Ork\n3) Elf");
            if ((value = getValidValue()) == 0) {
                break;
            }
            if ((value > 0 && value < 4)) {
                switch (value) {
                    case HUMAN: createNewCharacter("Human"); break;
                    case ORC: createNewCharacter("Ork");   break;
                    case ELF: createNewCharacter("Elf");   break;
                }
            }
        }
    }

    private void createNewCharacter(String type) {
        String characterName = "";
        String error = "";
        Character newCharacter = builder.buildByType(type);
        System.out.println(newCharacter.getInfo() + "\nCreate character?  1) Yes   2) No");
        int value = getValidValue();
        if (value == YES) {
            while (error != null) {
                System.out.print("Enter name: ");
                while (characterName.equals("")) {
                    characterName = scanner.nextLine();
                }
                error = builder.setName(newCharacter, characterName);
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
                DataBase.getDb().addNewCharacter(newCharacter);
                this.names.add(newCharacter.getName());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getValidValue() {
        String str = "";
        while (true) {
            while (str.equals("")) {
                str = scanner.nextLine();
            }
            if (!str.matches("^[0-9]+")) {
                System.err.println("Enter a valid value!");
            } else {
                return Integer.parseInt(str);
            }
        }
    }
}