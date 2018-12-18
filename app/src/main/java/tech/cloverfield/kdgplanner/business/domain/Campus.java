package tech.cloverfield.kdgplanner.business.domain;

public enum Campus {
    GROENPLAATS("Groenplaats","GR"),
    STADSWAAG("Stadswaag","SW"),
    POTHOEK("Pothoek","PH");

    private String longName;
    private String abbreviation;

    Campus(String longName,String abbreviation) {
        this.longName = longName;
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getLongName() {
        return longName;
    }
}
