package domain;

import java.util.List;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
	public void enroll(Student s, List<EnrollOffering> enrollOfferings) throws EnrollmentRulesViolationException {
        checkAlreadyPassedCourse(enrollOfferings, s);
        checkPassedPrerequisites(enrollOfferings, s);
        checkConflictingExamTimes(enrollOfferings);
        checkTakenTwiceCourse(enrollOfferings);
        checkGPALimit(s, enrollOfferings);
        enrollOfferings.forEach(o -> s.takeCourse(o.getCourse(), o.getSection()));
	}

    private void checkGPALimit(Student s, List<EnrollOffering> enrollOfferings) throws EnrollmentRulesViolationException {
        int unitsRequested = enrollOfferings.stream().mapToInt(o -> o.getCourse().getUnits()).sum();
        if (!s.checkGPALimit(unitsRequested)) {
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, s.getGpa()));
        }
    }

    private void checkTakenTwiceCourse(List<EnrollOffering> enrollOfferings) throws EnrollmentRulesViolationException {
        for (EnrollOffering offering : enrollOfferings) {
            for (EnrollOffering offering2 : enrollOfferings) {
                if (offering == offering2)
                    continue;
                if (offering.getCourse().equals(offering2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", offering.getCourse().getName()));
            }
		}
    }

    private void checkConflictingExamTimes(List<EnrollOffering> enrollOfferings) throws EnrollmentRulesViolationException {
        for (EnrollOffering offering : enrollOfferings) {
            for (EnrollOffering offering2 : enrollOfferings) {
                if (offering == offering2)
                    continue;
                if (offering.getExamTime().equals(offering2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", offering, offering2));
            }
        }
    }

    private void checkPassedPrerequisites(List<EnrollOffering> enrollOfferings, Student s) throws EnrollmentRulesViolationException {
	    for (EnrollOffering offering : enrollOfferings) {
            String violatingPre = offering.getCourse().getViolatingPrerequisite(s);
            if (!violatingPre.equals(""))
                throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", violatingPre, offering.getCourse().getName()));
        }
    }

    private void checkAlreadyPassedCourse(List<EnrollOffering> enrollOfferings, Student s) throws EnrollmentRulesViolationException {
        for (EnrollOffering offering : enrollOfferings) {
            if (s.hasPassed(offering.getCourse()))
                        throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", offering.getCourse().getName()));
        }
    }
}
