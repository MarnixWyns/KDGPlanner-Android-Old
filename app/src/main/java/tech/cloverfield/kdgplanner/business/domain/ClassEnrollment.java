package tech.cloverfield.kdgplanner.business.domain;

import android.annotation.SuppressLint;

class ClassEnrollment {

    private StudyField studyField;
    private int year;
    private int classNumber;
    private String classLetter;


    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%s%d0%d%s", studyField.getAbbreviation(), year, classNumber, classLetter);
    }
}
