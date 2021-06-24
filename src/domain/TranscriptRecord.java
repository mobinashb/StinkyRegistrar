package domain;

public class TranscriptRecord {
    private Course course;
    private Double grade;


    public TranscriptRecord(Course course, Double grade) {
        this.course = course;
        this.grade = grade;
    }

    public Course getCourse() {
        return course;
    }

    public Double getGrade() {
        return grade;
    }
}
