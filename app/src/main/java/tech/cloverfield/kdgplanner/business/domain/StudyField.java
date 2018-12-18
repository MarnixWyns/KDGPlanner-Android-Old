package tech.cloverfield.kdgplanner.business.domain;

public enum StudyField {
    TI("Toegepaste Informatica","TI"),
    EPM("Event & Project Management","EPM"),
    IOM("Logistiek Management","LOM"),
    IOR("Internationaal Ondernemen","IOR");

    private String fieldName;
    private String abbreviation;

    StudyField(String fieldName, String abbreviation) {
        this.fieldName = fieldName;
        this.abbreviation = abbreviation;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
