package ProjectManager.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project {
    private String name;
    private Map<String, Task> tasks;
    private List<Task> criticalPath;

    public Project(String name) {
        this.name = name;
        this.tasks = new HashMap<>();
        this.criticalPath = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public void addTaskWithDependencies(String taskId, int duration, List<String> dependencyIds) {
        Task task = new Task(taskId, duration);
        for (String dependencyId : dependencyIds) {
            Task dependencyTask = tasks.get(dependencyId.trim());
            if (dependencyTask != null) {
                task.addDependency(dependencyTask);
            }
        }
        addTask(task);
    }

    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public String getName() {
        return name;
    }

    public List<Task> getCriticalPath() {
        return criticalPath;
    }

    public void setCriticalPath(List<Task> criticalPath) {
        this.criticalPath = criticalPath;
    }

    public void displayTasks() {
        System.out.println("Task ID\t\tDuration\tDependencies");
        for (Task task : getAllTasks()) {
            StringBuilder dependencies = new StringBuilder();
            for (Task dep : task.getDependencies()) {
                dependencies.append(dep.getTaskId()).append(" ");
            }
            System.out.printf("%s\t\t%d\t\t%s%n", task.getTaskId(), task.getDuration(), dependencies.toString().trim());
        }
    }
}