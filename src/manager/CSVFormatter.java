package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class CSVFormatter {
    /*private static Integer idEpic; */

    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }

    public static String toString(Task task) {
        // превратить таску в строку csv
        Type type = Type.TASK;
        Integer idEpic = null;
        if (task instanceof Epic) {
            type = Type.EPIC;
        }
        if (task instanceof Subtask) {
            type = Type.SUBTASK;
            idEpic = ((Subtask) task).getEpicId();
        }
        return task.getId() + "," + type + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + (idEpic != null ? idEpic : "");
    }

    public static Task fromString(String line) {
        // пользуемся split
        // парсим значения в массиве, который получается после сплита
        // в зависимости от типа таски создать соответствующий объект
        try {
            String[] split = line.split(",");
            int id = Integer.parseInt(split[0]);
            Type type = Type.valueOf(split[1]);
            String name = split[2];
            Status status = Status.valueOf(split[3]);
            String description = split[4];
            if (type.equals(Type.TASK)) {
                return new Task(id, name, description, status);
            }
            if (type.equals(Type.EPIC)) {
                return new Epic(id, name, description, status, null);
            } else {
                int idEpic = Integer.parseInt(split[5]);
                return new Subtask(id, name, description, status, idEpic);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка формата строки");
            return null;
        }
    }
}
