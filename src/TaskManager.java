import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private Integer generatorId = 1;

    // МЕТОДЫ ДЛЯ ЗАДАЧИ
    public ArrayList<Task> getTasks() { // получение списка всех задач
        return new ArrayList<>(tasks.values());
    }

    public Task createTask(Task task) {  // создание задачи
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    private Integer getNextId() { // генерация нового id
        return generatorId++;
    }

    public Task updateTask(Task task) { // обновление задачи
        if (tasks.containsValue(task)) {
            tasks.put(task.getId(), task);
        }
        return task;
    }

    public Task deleteTask(Integer id) { // удаление задачи
        return tasks.remove(id);
    }

    public Task getTask(Integer id) { // получение задачи по id
        return tasks.get(id);
    }

    public void deleteAllTasks() { // удаление всех задач
        tasks.clear();
    }

    // МЕТОДЫ ДЛЯ ЭПИКА
    public ArrayList<Epic> getEpics() { // получение списка всех эпиков
        return new ArrayList<>(epics.values());
    }

    public Epic createEpic(Epic epic) {  // создание эпика
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) { // обновление эпика
        if (epics.containsValue(epic)) {
            Epic oldEpic = epics.get(epic.getId());
            oldEpic.setName(epic.getName());
            oldEpic.setDescription(epic.getDescription());
        }
        return epic;
    }

    public Epic deleteEpic(Integer id) { // удаление эпика
        Epic epic = epics.get(id);
        ArrayList<Integer> subtaskId = epic.getSubtaskId();
        for (Integer subId : subtaskId) {
            subtasks.remove(subId);
        }
        return epics.remove(id);
    }

    public Epic getEpic(Integer id) { // получение эпика по id
        return epics.get(id);
    }

    public void deleteAllEpics() { // удаление всех эпиков
        subtasks.clear();
        epics.clear();
    }

    private void updateStatusOfEpic() {// обновление статуса эпика
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtId = epic.getSubtaskId();
            boolean isAllSubtaskDone = true;
            boolean isAllSubtaskNew = true;
            for (Integer id : subtId) {
                Subtask subtask = subtasks.get(id);
                if (subtask.getStatus() != Status.DONE) {
                    isAllSubtaskDone = false;
                }
                if (subtask.getStatus() != Status.NEW) {
                    isAllSubtaskNew = false;
                }
            }
            if (isAllSubtaskDone) {
                epic.setStatus(Status.DONE);
            } else if (isAllSubtaskNew) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }


    // МЕТОДЫ ДЛЯ ПОДЗАДАЧ
    // получение списка всех подзадач
    public ArrayList<Subtask> getSubtasks() { // получение списка всех задач
        return new ArrayList<>(subtasks.values());
    }


    public Subtask createSubtask(Subtask subtask) {  // создание подзадачи и добавление в эпик
        // создаем подзадачу:

        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(getNextId());
            subtasks.put(subtask.getId(), subtask);
            // добавляем ее в эпик:
            Epic epic = epics.get(subtask.getEpicId());
            ArrayList<Integer> subtId = epic.getSubtaskId();
            subtId.add(subtask.getId());
            updateStatusOfEpic();
        }
        return subtask;
    }

    public Subtask updateSubtask(Subtask subtask) { // обновление подзадачи
        // обновляем подзадачу:
        if (subtasks.containsValue(subtask)) {
            subtasks.put(subtask.getId(), subtask);
            // обновляем статус эпика:
            updateStatusOfEpic();
        }
        return subtask;
    }

    public Subtask deleteSubtask(Integer id) { // удаление подзадачи по id и ее удаление из эпика
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        ArrayList<Integer> subt = epic.getSubtaskId();
        subt.remove(id);
        updateStatusOfEpic();
        return subtasks.remove(id);
    }

    public Subtask getSubtask(Integer id) { // получение подзадачи по id
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getAllSubtasksOfEpic(Integer epicId) { // получение списка всех подзадач определенного эпика
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subt = new ArrayList<>();
        ArrayList<Integer> subtId = epic.getSubtaskId();
        for (Integer id : subtId) {
            subt.add(subtasks.get(id));
        }
        return new ArrayList<>(subt);
    }

    public void deleteAllSubtasks() { // удаление всех подзадач и из эпика тоже
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtId = epic.getSubtaskId();
            subtId.clear();
        }
        subtasks.clear();
        updateStatusOfEpic();
    }

}

