package domain;

import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
	public void enroll(Student s, List<EnrollOffering> courses) throws EnrollmentRulesViolationException {
        Map<Term, List<TranscriptRecord>> transcript = s.getTranscript();
        checkAlreadyPassedCourse(courses, transcript);
        checkPassedPrerequisites(courses, transcript);
        checkConflictingExamTimes(courses);
        checkTakenTwiceCourse(courses);
        checkGPALimit(courses, transcript);
        for (EnrollOffering o : courses)
			s.takeCourse(o.getCourse(), o.getSection());
	}

    private void checkGPALimit(List<EnrollOffering> courses, Map<Term, List<TranscriptRecord>> transcript) throws EnrollmentRulesViolationException {
        int unitsRequested = 0;
        for (EnrollOffering o : courses)
            unitsRequested += o.getCourse().getUnits();
        double points = 0;
        int totalUnits = 0;
        for (Map.Entry<Term, List<TranscriptRecord>> tr : transcript.entrySet()) {
            for (TranscriptRecord r : tr.getValue()) {
                points += r.getGrade() * r.getCourse().getUnits();
                totalUnits += r.getCourse().getUnits();
            }
		}
        double gpa = points / totalUnits;
        if ((gpa < 12 && unitsRequested > 14) ||
                (gpa < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
    }

    private void checkTakenTwiceCourse(List<EnrollOffering> courses) throws EnrollmentRulesViolationException {
        for (EnrollOffering o : courses) {
            for (EnrollOffering o2 : courses) {
                if (o == o2)
                    continue;
                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
		}
    }

    private void checkConflictingExamTimes(List<EnrollOffering> courses) throws EnrollmentRulesViolationException {
        for (EnrollOffering o : courses) {
            for (EnrollOffering o2 : courses) {
                if (o == o2)
                    continue;
                if (o.getExamTime().equals(o2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
            }
        }
    }

    private void checkPassedPrerequisites(List<EnrollOffering> courses, Map<Term, List<TranscriptRecord>> transcript) throws EnrollmentRulesViolationException {
	    for (EnrollOffering o : courses) {
            String violatingPre = o.getCourse().getViolatingPrerequisite(transcript);
            if (!violatingPre.equals(""))
                throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", violatingPre, o.getCourse().getName()));
        }
    }

    private void checkAlreadyPassedCourse(List<EnrollOffering> courses, Map<Term, List<TranscriptRecord>> transcript) throws EnrollmentRulesViolationException {
        for (EnrollOffering o : courses) {
            for (Map.Entry<Term, List<TranscriptRecord>> tr : transcript.entrySet()) {
                for (TranscriptRecord r : tr.getValue()) {
                    if (r.getCourse().equals(o.getCourse()) && r.getGrade() >= 10)
                        throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName()));
                }
            }
        }
    }
}
