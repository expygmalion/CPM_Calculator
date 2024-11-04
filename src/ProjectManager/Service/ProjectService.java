package ProjectManager.Service;

import ProjectManager.Model.Project;
import ProjectManager.Model.Task;
import ProjectManager.Model.TaskDependency;

import java.util.ArrayList;
import java.util.List;

public class ProjectService {
    private Project currentProject;

    public void createProject(String name) {
        currentProject = new Project(name);
    }

    public void addTask(String taskId, int duration) {
        if (currentProject != null) {
            Task task = new Task(taskId, duration);
            currentProject.addTask(task);
        }
    }

    public void addDependencies(String taskId, List<String> dependencyIds) {
        if (currentProject != null) {
            Task task = currentProject.getTask(taskId);
            if (task != null) {
                for (String dependencyId : dependencyIds) {
                    Task dependency = currentProject.getTask(dependencyId);
                    if (dependency != null) {
                        task.addDependency(dependency);
                    }
                }
            }
        }
    }

    public void calculateCriticalPath() {
        if (currentProject != null) {
            CPMCalculator calculator = new CPMCalculator(currentProject);
            calculator.calculateCPM();
        }
    }

    public Project getCurrentProject() {
        return currentProject;
    }
}
