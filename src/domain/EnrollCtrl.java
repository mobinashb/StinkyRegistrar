package domain;

import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
	public void enroll(Student s, List<EnrollOffering> enrollOfferings) throws EnrollmentRulesViolationException {
        Map<Term, List<TranscriptRecord>> transcript = s.getTranscript();
        checkAlreadyPassedCourse(enrollOfferings, transcript);
        checkPassedPrerequisites(enrollOfferings, transcript);
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
        for (EnrollOffering o : enrollOfferings) {
            for (EnrollOffering o2 : enrollOfferings) {
                if (o == o2)
                    continue;
                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
		}
    }

    private void checkConflictingExamTimes(List<EnrollOffering> enrollOfferings) throws EnrollmentRulesViolationException {
        for (EnrollOffering o : enrollOfferings) {
            for (EnrollOffering o2 : enrollOfferings) {
                if (o == o2)
                    continue;
                if (o.getExamTime().equals(o2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
            }
        }
    }

    private void checkPassedPrerequisites(List<EnrollOffering> enrollOfferings, Map<Term, List<TranscriptRecord>> transcript) throws EnrollmentRulesViolationException {
	    for (EnrollOffering o : enrollOfferings) {
            String violatingPre = o.getCourse().getViolatingPrerequisite(transcript);
            if (!violatingPre.equals(""))
                throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", violatingPre, o.getCourse().getName()));
        }
    }

    private void checkAlreadyPassedCourse(List<EnrollOffering> enrollOfferings, Map<Term, List<TranscriptRecord>> transcript) throws EnrollmentRulesViolationException {
        for (EnrollOffering o : enrollOfferings) {
            for (Map.Entry<Term, List<TranscriptRecord>> tr : transcript.entrySet()) {
                for (TranscriptRecord r : tr.getValue()) {
                    if (r.getCourse().equals(o.getCourse()) && r.getGrade() >= 10)
                        throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName()));
                }
            }
        }
    }
}
