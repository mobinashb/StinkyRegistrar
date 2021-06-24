package domain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Course {
	private String id;
	private String name;
	private int units;
	
	List<Course> prerequisites;

	public Course(String id, String name, int units) {
		this.id = id;
		this.name = name;
		this.units = units;
		prerequisites = new ArrayList<Course>();
	}
	
	public void addPre(Course c) {
		getPrerequisites().add(c);
	}

	public Course withPre(Course... pres) {
		prerequisites.addAll(Arrays.asList(pres));
		return this;
	}

	public List<Course> getPrerequisites() {
		return prerequisites;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" {");
		for (Course pre : getPrerequisites()) {
			sb.append(pre.getName());
			sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public int getUnits() {
		return units;
	}

	public String getId() {
		return id;
	}

	public boolean equals(Object obj) {
		Course other = (Course)obj;
		return id.equals(other.id);
	}

    public String getViolatingPrerequisite(Map<Term, List<TranscriptRecord>> transcript) {
        for (Course pre : getPrerequisites()) {
			if (!checkPassedCourse(transcript, pre)) return pre.getName();
        }
        return "";
    }

	private boolean checkPassedCourse(Map<Term, List<TranscriptRecord>> transcript, Course course) {
		for (Map.Entry<Term, List<TranscriptRecord>> tr : transcript.entrySet()) {
			for (TranscriptRecord r : tr.getValue()) {
				if (r.getCourse().equals(course) && r.getGrade() >= 10)
					return true;
			}
		}
		return false;
	}
}
