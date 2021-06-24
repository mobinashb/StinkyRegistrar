package domain;
import domain.exceptions.EnrollmentRulesViolationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
	private String id;
	private String name;

	private Map<Term, List<TranscriptRecord>> transcript;
	private List<EnrollOffering> currentTerm;

	public Student(String id, String name) {
		this.id = id;
		this.name = name;
		this.transcript = new HashMap<>();
		this.currentTerm = new ArrayList<>();
	}
	
	public void takeCourse(Course c, int section) {
		currentTerm.add(new EnrollOffering(c, section));
	}

	public Map<Term, List<TranscriptRecord>> getTranscript() {
		return transcript;
	}

	public void addTranscriptRecord(Course course, Term term, double grade) {
	    if (!transcript.containsKey(term))
	        transcript.put(term, new ArrayList<>());
	    transcript.get(term).add(new TranscriptRecord(course, grade));
    }

    public List<EnrollOffering> getCurrentTerm() {
        return currentTerm;
    }

    public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}

	public boolean checkGPALimit(int unitsRequested) {
		if ((getGpa() < 12 && unitsRequested > 14) ||
				(getGpa() < 16 && unitsRequested > 16) ||
				(unitsRequested > 20))
			return false;
		return true;
	}

	public double getGpa() {
		double points = 0;
		int totalUnits = 0;
		for (Map.Entry<Term, List<TranscriptRecord>> tr : this.transcript.entrySet()) {
			for (TranscriptRecord r : tr.getValue()) {
				points += r.getGrade() * r.getCourse().getUnits();
				totalUnits += r.getCourse().getUnits();
			}
		}
		return points / totalUnits;
	}
}
