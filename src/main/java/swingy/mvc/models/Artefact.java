package swingy.mvc.models;

public class Artefact {

    private int value;
    private String type;

    public Artefact(String type, int value) {
        this.type = type;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
