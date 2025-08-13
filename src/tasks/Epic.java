package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskId;
    private LocalDateTime endTime;

    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration, ArrayList<Integer> subtaskId) {
        super(name, description, status, startTime, duration);
        this.subtaskId = new ArrayList<>();
        this.endTime = getStartTime().plus(getDuration());
    }

    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.subtaskId = new ArrayList<>();
        this.endTime = getStartTime().plus(getDuration());
    }

    public Epic(Integer id, String name, String description, Status status, LocalDateTime startTime, Duration duration, ArrayList<Integer> subtaskId) {
        super(id, name, description, status, startTime, duration);
        this.subtaskId = new ArrayList<>();
        this.endTime = getStartTime().plus(getDuration());
    }

    public Epic(Integer id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", startTime=" + getStartTime() + '\'' +
                ", duration=" + getDuration() + '\'' +
                ", subtaskId=" + subtaskId + '\'' +
                ", endTime=" + endTime +
                '}';
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}
