package manager;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());
            String[] split = content.split(System.lineSeparator());
            for (String line : split) {
                Task task = CSVFormatter.fromString(line);
                if (task != null) {
                    if (task.getId() >= generatorId) {
                        generatorId = task.getId() + 1;
                    }
                    if (task instanceof Epic) {
                        epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        Subtask subtask = (Subtask) task;
                        subtasks.put(task.getId(), subtask);
                        // добавляем ее в эпик:
                        Epic epic = epics.get(subtask.getEpicId());
                        ArrayList<Integer> subtId = epic.getSubtaskId();
                        subtId.add(subtask.getId());
                    } else {
                        tasks.put(task.getId(), task);
                    }
                }
            }

        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
        }
        return taskManager;
    }

    void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSVFormatter.getHeader());
            writer.newLine();
            for (Task task : getTasks()) {
                writer.write(CSVFormatter.toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(CSVFormatter.toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(CSVFormatter.toString(subtask));
                writer.newLine();
            }
        } catch (IOException | ClassCastException | ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }


    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Task deleteTask(Integer id) {
        Task task = super.deleteTask(id);
        save();
        return task;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }


    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Epic deleteEpic(Integer id) {
        Epic epic = super.deleteEpic(id);
        save();
        return epic;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Subtask deleteSubtask(Integer id) {
        Subtask subtask = super.deleteSubtask(id);
        save();
        return subtask;
    }


    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

}
