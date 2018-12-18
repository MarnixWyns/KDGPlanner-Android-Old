package tech.cloverfield.kdgplanner;

import org.junit.Test;

import tech.cloverfield.kdgplanner.business.domain.Student;
import tech.cloverfield.kdgplanner.controller.KDGPlannerController;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    public KDGPlannerController controller;

    @Test
    public void studentsFormat() throws Exception {
        Student s = controller.addStudent();
        assertEquals("INF204A", s.getClassFormatted());
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}