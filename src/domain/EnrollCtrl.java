package domain;

import java.util.ArrayList;
import java.util.List;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {

    List<String> errors = new ArrayList<>();

	public void enroll(Student student, List<EnrollOffering> enrollOfferings) {
        checkAlreadyPassedCourse(enrollOfferings, student);
        checkPassedPrerequisites(enrollOfferings, student);
        checkConflictingExamTimes(enrollOfferings);
        checkTakenTwiceCourse(enrollOfferings);
        checkGPALimit(student, enrollOfferings);
        if(errors.size() == 0) enrollOfferings.forEach(o -> student.takeCourse(o.getCourse(), o.getSection()));
        else showErrors();
	}

    private void checkGPALimit(Student student, List<EnrollOffering> enrollOfferings) {
        int unitsRequested = enrollOfferings.stream().mapToInt(o -> o.getCourse().getUnits()).sum();
        if (!student.checkGPALimit(unitsRequested))
            errors.add(String.format("%s: Number of units (%d) requested does not match GPA of %f", ErrorsTag.GPA_LIMIT_ERROR, unitsRequested, student.getGpa()));
    }

    private void checkTakenTwiceCourse(List<EnrollOffering> enrollOfferings) {
        for (EnrollOffering offering : enrollOfferings) {
            for (EnrollOffering offering2 : enrollOfferings) {
                if (offering == offering2)
                    continue;
                if (offering.getCourse().equals(offering2.getCourse()))
                    errors.add(String.format("%s: %s is requested to be taken twice", ErrorsTag.TAKEN_TWICE_ERROR, offering.getCourse().getName()));
            }
		}
    }

    private void checkConflictingExamTimes(List<EnrollOffering> enrollOfferings) {
        for (EnrollOffering offering : enrollOfferings) {
            for (EnrollOffering offering2 : enrollOfferings) {
                if (offering == offering2)
                    continue;
                if (offering.getExamTime().equals(offering2.getExamTime()))
                    errors.add(String.format("%s: Two offerings %s and %s have the same exam time", ErrorsTag.EXAM_TIMES_ERROR, offering, offering2));
            }
        }
    }

    private void checkPassedPrerequisites(List<EnrollOffering> enrollOfferings, Student student) {
	    for (EnrollOffering offering : enrollOfferings) {
            String violatingPre = offering.getCourse().getViolatingPrerequisite(student);
            if (!violatingPre.equals(""))
                errors.add(String.format("%s: The student has not passed %s as a prerequisite of %s", ErrorsTag.PASSED_PREREQUISITES_ERROR, violatingPre, offering.getCourse().getName()));
        }
    }

    private void checkAlreadyPassedCourse(List<EnrollOffering> enrollOfferings, Student student){
        for (EnrollOffering offering : enrollOfferings) {
            if (student.hasPassed(offering.getCourse()))
                errors.add(String.format("%s: The student has already passed %s", ErrorsTag.ALREADY_PASSED_ERROR, offering.getCourse().getName()));
        }
    }

    private void showErrors(){
        errors.forEach(err -> System.out.println(err));
    }

    public List<String> getErrors() {
        return errors;
    }
}
