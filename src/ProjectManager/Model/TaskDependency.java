package ProjectManager.Model;

import java.util.List;

public class TaskDependency {
    private String task;
    private List<String> dependencies;

    public TaskDependency(String task, List<String> dependencies) {
        this.task = task;
        this.dependencies = dependencies;
    }

    public String getTask() { return task; }
    public List<String> getDependencies() { return dependencies; }
}
