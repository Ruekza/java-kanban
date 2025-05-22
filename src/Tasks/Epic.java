package Tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskId;

    public Epic(String name, String description, Status status, ArrayList<Integer> subtaskId) {
        super(name, description, status);
        this.subtaskId = new ArrayList<>();
    }

    public Epic(Integer id, String name, String description, Status status, ArrayList<Integer> subtaskId) {
        super(id, name, description, status);
        this.subtaskId = subtaskId;
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", subtaskId=" + subtaskId +
                '}';
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }
}
