package manager;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Integer generatorId = 1;
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks = new TreeSet<>();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean isCrossing(Task t1, Task t2) {
        return !(t1.getEndTime().isBefore(t2.getStartTime()) || t2.getEndTime().isBefore(t1.getStartTime()));
    }


    // МЕТОДЫ ДЛЯ ЗАДАЧИ
    @Override
    public ArrayList<Task> getTasks() { // получение списка всех задач
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task createTask(Task task) throws ManagerSaveException { // создание задачи
        if (prioritizedTasks.isEmpty()) {
            task.setId(getNextId());
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            return task;
        } else {
            Set<Task> notCrossingTasks = prioritizedTasks.stream()
                    .filter(prioritizedTask -> !isCrossing(prioritizedTask, task))
                    .collect(Collectors.toSet());
            if (notCrossingTasks.isEmpty()) {
                throw new ManagerSaveException("Задача пересекается с другими");
            } else {
                task.setId(getNextId());
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
                return task;
            }
        }
    }


    private Integer getNextId() { // генерация нового id
        return generatorId++;
    }

    @Override
    public Task updateTask(Task task) { // обновление задачи
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            Set<Task> crossingTasks = prioritizedTasks.stream()
                    .filter(prioritizedTask -> !prioritizedTask.equals(task))
                    .filter(prioritizedTask -> isCrossing(prioritizedTask, task))
                    .collect(Collectors.toSet());
            if (!crossingTasks.isEmpty()) {
                throw new ManagerSaveException("Задача пересекается с другими");
            } else {
                tasks.put(task.getId(), task);
                prioritizedTasks.remove(oldTask);
                prioritizedTasks.add(task);
            }
        }
        return task;
    }

    @Override
    public Task deleteTask(Integer id) { // удаление задачи
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
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
        idList.stream()
                .forEach(id -> prioritizedTasks.remove(tasks.get(id)));
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
        epic.getSubtaskId().stream()
                .forEach(subtasks::remove);
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
        List<Integer> allSubtaskId = epics.values().stream()
                .flatMap(epic -> epic.getSubtaskId().stream())
                .collect(Collectors.toList());
        historyManager.removeSomeTasksFromHistory(allSubtaskId);
        historyManager.removeSomeTasksFromHistory(new ArrayList<>(epics.keySet()));
        allSubtaskId.stream()
                .map(subtasks::get)
                .forEach(prioritizedTasks::remove);
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

    public void updateStartTimeOfEpic() {
        LocalDateTime minStartTime = LocalDateTime.MAX;
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtId = epic.getSubtaskId();
            for (Integer id : subtId) {
                Subtask subtask = subtasks.get(id);
                LocalDateTime startTimeSub = subtask.getStartTime();
                if (startTimeSub.isBefore(minStartTime)) {
                    minStartTime = startTimeSub;
                }
            }
            epic.setStartTime(minStartTime);
        }
    }

    public void updateDurationOfEpic() {
        for (Epic epic : epics.values()) {
            Duration durationEpic = Duration.ZERO;
            ArrayList<Integer> subtId = epic.getSubtaskId();
            for (Integer id : subtId) {
                Subtask subtask = subtasks.get(id);
                Duration duration = subtask.getDuration();
                durationEpic = durationEpic.plus(duration);
            }
            epic.setDuration(durationEpic);
        }
    }

    public void updateEndTimeOfEpic() {
        LocalDateTime maxEndTime = null;
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtId = epic.getSubtaskId();
            for (Integer id : subtId) {
                Subtask subtask = subtasks.get(id);
                LocalDateTime endTimeSub = subtask.getEndTime();
                if (maxEndTime == null || endTimeSub.isAfter(maxEndTime)) {
                    maxEndTime = endTimeSub;
                }
            }
            epic.setEndTime(maxEndTime);
        }
    }

    // МЕТОДЫ ДЛЯ ПОДЗАДАЧ
    // получение списка всех подзадач
    @Override
    public ArrayList<Subtask> getSubtasks() { // получение списка всех задач
        return new ArrayList<>(subtasks.values());
    }


    @Override
    public Subtask createSubtask(Subtask subtask) throws ManagerSaveException {  // создание подзадачи и добавление в эпик
        // создаем подзадачу:
        if (prioritizedTasks.isEmpty()) {
            if (epics.containsKey(subtask.getEpicId())) {
                subtask.setId(getNextId());
                subtasks.put(subtask.getId(), subtask);
                // добавляем ее в эпик:
                Epic epic = epics.get(subtask.getEpicId());
                ArrayList<Integer> subtId = epic.getSubtaskId();
                subtId.add(subtask.getId());
                updateStatusOfEpic();
                updateStartTimeOfEpic();
                updateDurationOfEpic();
                updateEndTimeOfEpic();
                prioritizedTasks.add(subtask);
            }
            return subtask;
        } else {
            Set<Task> notCrossingTasks = prioritizedTasks.stream()
                    .filter(prioritizedTask -> !isCrossing(prioritizedTask, subtask))
                    .collect(Collectors.toSet());
            if (notCrossingTasks.isEmpty()) {
                throw new ManagerSaveException("Подзадача пересекается с другими");
            } else {
                if (epics.containsKey(subtask.getEpicId())) {
                    subtask.setId(getNextId());
                    subtasks.put(subtask.getId(), subtask);
                    // добавляем ее в эпик:
                    Epic epic = epics.get(subtask.getEpicId());
                    ArrayList<Integer> subtId = epic.getSubtaskId();
                    subtId.add(subtask.getId());
                    updateStatusOfEpic();
                    updateStartTimeOfEpic();
                    updateDurationOfEpic();
                    updateEndTimeOfEpic();
                    prioritizedTasks.add(subtask);
                }
                return subtask;
            }
        }
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) { // обновление подзадачи
        // обновляем подзадачу:
        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            Set<Task> crossingTasks = prioritizedTasks.stream()
                    .filter(prioritizedTask -> !prioritizedTask.equals(subtask))
                    .filter(prioritizedTask -> isCrossing(prioritizedTask, subtask))
                    .collect(Collectors.toSet());
            if (!crossingTasks.isEmpty()) {
                throw new ManagerSaveException("Подзадача пересекается с другими");
            } else {
                subtasks.put(subtask.getId(), subtask);
                // обновляем статус эпика:
                updateStatusOfEpic();
                updateStartTimeOfEpic();
                updateDurationOfEpic();
                updateEndTimeOfEpic();
                prioritizedTasks.remove(oldSubtask);
                prioritizedTasks.add(subtask);
            }
        }
        return subtask;
    }


    @Override
    public Subtask deleteSubtask(Integer id) { // удаление подзадачи по id и ее удаление из эпика
        historyManager.remove(id);
        prioritizedTasks.remove(subtasks.get(id));
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        ArrayList<Integer> subt = epic.getSubtaskId();
        subt.remove(id);
        updateStatusOfEpic();
        updateStartTimeOfEpic();
        updateDurationOfEpic();
        updateEndTimeOfEpic();
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
        List<Subtask> subt = epic.getSubtaskId().stream()
                .map(id -> subtasks.get(id))
                .collect(Collectors.toList());
        return new ArrayList<>(subt);
    }


    @Override
    public void deleteAllSubtasks() { // удаление всех подзадач и из эпика тоже
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtId = epic.getSubtaskId();
            historyManager.removeSomeTasksFromHistory(subtId);
            subtId.stream()
                    .forEach(id -> prioritizedTasks.remove(subtasks.get(id)));
            subtId.clear();
        }
        subtasks.clear();
        updateStatusOfEpic();
        updateStartTimeOfEpic();
        updateDurationOfEpic();
        updateEndTimeOfEpic();
    }

}
