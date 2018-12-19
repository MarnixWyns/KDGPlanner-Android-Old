package tech.cloverfield.kdgplanner.business.domain;

import java.util.List;

public class Student {
    private List<ClassEnrollment> classEnrollment;
    private String firstName;
    private String lastName;

    public Student(List<ClassEnrollment> classEnrollment, String firstName, String lastName) {
        this.classEnrollment = classEnrollment;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    public String getName(){
        return String.format("%s %s", lastName, firstName);
    }

    public String getClassFormatted(){
        return classEnrollment.toString();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
