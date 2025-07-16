package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected static Map<Integer, Task> tasks = new HashMap<>();
    protected static Map<Integer, Subtask> subtasks = new HashMap<>();
    protected static Map<Integer, Epic> epics = new HashMap<>();
    protected static Integer generatorId = 1;
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    // МЕТОДЫ ДЛЯ ЗАДАЧИ
    @Override
    public ArrayList<Task> getTasks() { // получение списка всех задач

        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task createTask(Task task) {  // создание задачи
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    private Integer getNextId() { // генерация нового id
        return generatorId++;
    }

    @Override
    public Task updateTask(Task task) { // обновление задачи
        if (tasks.containsValue(task)) {
            tasks.put(task.getId(), task);
        }
        return task;
    }

    @Override
    public Task deleteTask(Integer id) { // удаление задачи
        historyManager.remove(id);
        return tasks.remove(id);
    }

    @Override
    public Task getTask(Integer id) { // получение задачи по id
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteAllTasks() { // удаление всех задач
        ArrayList<Integer> idList = new ArrayList<>(tasks.keySet());
        tasks.clear();
        historyManager.removeSomeTasksFromHistory(idList);
    }

    // МЕТОДЫ ДЛЯ ЭПИКА
    @Override
    public ArrayList<Epic> getEpics() { // получение списка всех эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic createEpic(Epic epic) {  // создание эпика
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }


    @Override
    public Epic updateEpic(Epic epic) { // обновление эпика
        if (epics.containsValue(epic)) {
            Epic oldEpic = epics.get(epic.getId());
            oldEpic.setName(epic.getName());
            oldEpic.setDescription(epic.getDescription());
        }
        return epic;
    }

    @Override
    public Epic deleteEpic(Integer id) { // удаление эпика
        historyManager.remove(id);
        Epic epic = epics.get(id);
        ArrayList<Integer> subtaskId = epic.getSubtaskId();
        for (Integer subId : subtaskId) {
            subtasks.remove(subId);
        }
        return epics.remove(id);
    }

    @Override
    public Epic getEpic(Integer id) { // получение эпика по id
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void deleteAllEpics() { // удаление всех эпиков
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtId = epic.getSubtaskId();
            if (subtId != null) {
                historyManager.removeSomeTasksFromHistory(subtId);
            }
        }
        ArrayList<Integer> idList = new ArrayList<>(epics.keySet());
        historyManager.removeSomeTasksFromHistory(idList);
        subtasks.clear();
        epics.clear();
    }


    private void updateStatusOfEpic() { // обновление статуса эпика
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
    @Override
    public ArrayList<Subtask> getSubtasks() { // получение списка всех задач
        return new ArrayList<>(subtasks.values());
    }


    @Override
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

    @Override
    public Subtask updateSubtask(Subtask subtask) { // обновление подзадачи
        // обновляем подзадачу:
        if (subtasks.containsValue(subtask)) {
            subtasks.put(subtask.getId(), subtask);
            // обновляем статус эпика:
            updateStatusOfEpic();
        }
        return subtask;
    }

    @Override
    public Subtask deleteSubtask(Integer id) { // удаление подзадачи по id и ее удаление из эпика
        historyManager.remove(id);
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        ArrayList<Integer> subt = epic.getSubtaskId();
        subt.remove(id);
        updateStatusOfEpic();
        return subtasks.remove(id);
    }

    @Override
    public Subtask getSubtask(Integer id) { // получение подзадачи по id
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksOfEpic(Integer epicId) { // получение списка всех подзадач определенного эпика
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subt = new ArrayList<>();
        ArrayList<Integer> subtId = epic.getSubtaskId();
        for (Integer id : subtId) {
            subt.add(subtasks.get(id));
        }
        return new ArrayList<>(subt);
    }

    @Override
    public void deleteAllSubtasks() { // удаление всех подзадач и из эпика тоже
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtId = epic.getSubtaskId();
            historyManager.removeSomeTasksFromHistory(subtId);
            subtId.clear();
        }
        subtasks.clear();
        updateStatusOfEpic();
    }


}

