package ProjectManager.Service;

import ProjectManager.Model.Project;
import ProjectManager.Model.Task;

import java.util.*;

public class CPMCalculator {
    private Project project;

    public CPMCalculator(Project project) {
        this.project = project;
    }

    public void calculateCPM() {
        List<Task> allTasks = new ArrayList<>(project.getAllTasks());

        // Forward pass - Calculate earliest start and finish times
        calculateForwardPass(allTasks);

        // Backward pass - Calculate latest start and finish times
        calculateBackwardPass(allTasks);

        // Calculate slack and identify critical path
        List<Task> criticalPath = identifyCriticalPath(allTasks);
        project.setCriticalPath(criticalPath);

        // Display results
        displayResults(allTasks, criticalPath);
    }

    private void calculateForwardPass(List<Task> tasks) {
        // Find tasks with no dependencies (start tasks)
        List<Task> startTasks = tasks.stream()
                .filter(task -> task.getDependencies().isEmpty())
                .toList();

        // Calculate earliest times for all tasks
        for (Task task : startTasks) {
            calculateEarliestTimes(task);
        }
    }

    private void calculateEarliestTimes(Task task) {
        // If task has dependencies, wait for them to complete
        int maxDependencyFinish = task.getDependencies().stream()
                .mapToInt(Task::getEarliestFinish)
                .max()
                .orElse(0);

        task.setEarliestStart(maxDependencyFinish);
        task.setEarliestFinish(maxDependencyFinish + task.getDuration());

        // Process all tasks that depend on this one
        for (Task t : project.getAllTasks()) {
            if (t.getDependencies().contains(task)) {
                calculateEarliestTimes(t);
            }
        }
    }

    private void calculateBackwardPass(List<Task> tasks) {
        // Find the maximum finish time
        int maxFinish = tasks.stream()
                .mapToInt(Task::getEarliestFinish)
                .max()
                .orElse(0);

        // Initialize latest finish times for all tasks
        for (Task task : tasks) {
            task.setLatestFinish(maxFinish);
        }

        // Find end tasks (tasks that no other tasks depend on)
        List<Task> endTasks = findEndTasks(tasks);

        // Calculate latest times for all tasks starting from end tasks
        for (Task task : endTasks) {
            task.setLatestFinish(task.getEarliestFinish());
            calculateLatestTimes(task);
        }
    }

    private List<Task> findEndTasks(List<Task> tasks) {
        Set<Task> allDependencies = new HashSet<>();
        tasks.forEach(task -> allDependencies.addAll(task.getDependencies()));
        return tasks.stream()
                .filter(task -> !allDependencies.contains(task))
                .toList();
    }

    private void calculateLatestTimes(Task task) {
        task.setLatestStart(task.getLatestFinish() - task.getDuration());
        task.setSlack(task.getLatestStart() - task.getEarliestStart());

        // Process dependencies
        for (Task dependency : task.getDependencies()) {
            int newLatestFinish = Math.min(
                    dependency.getLatestFinish(),
                    task.getLatestStart()
            );
            if (newLatestFinish < dependency.getLatestFinish()) {
                dependency.setLatestFinish(newLatestFinish);
                calculateLatestTimes(dependency);
            }
        }
    }

    private List<Task> identifyCriticalPath(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getSlack() == 0)
                .sorted(Comparator.comparingInt(Task::getEarliestStart))
                .toList();
    }

    private void displayResults(List<Task> allTasks, List<Task> criticalPath) {
        System.out.println("\nCPM Analysis Results:");
        System.out.println("=====================");

        System.out.println("\nAll Tasks Analysis:");
        System.out.println("Task ID | Duration | Early Start | Early Finish | Late Start | Late Finish | Slack | Dependencies");
        System.out.println("--------------------------------------------------------------------------------------------------");

        for (Task task : allTasks) {
            String dependencies = task.getDependencies().stream()
                    .map(Task::getTaskId)
                    .reduce((d1, d2) -> d1 + ", " + d2)
                    .orElse("None");

            System.out.printf("%-8s| %-9d| %-12d| %-13d| %-11d| %-12d| %-5d| %-15s%n",
                    task.getTaskId(),
                    task.getDuration(),
                    task.getEarliestStart(),
                    task.getEarliestFinish(),
                    task.getLatestStart(),
                    task.getLatestFinish(),
                    task.getSlack(),
                    task.getDependencies()
            );
        }

        System.out.println("\nCritical Path:");
        System.out.println("Project Duration: " + criticalPath.get(criticalPath.size() - 1).getEarliestFinish());
        System.out.println("Path: " + String.join(" -> ",
                criticalPath.stream().map(Task::getTaskId).toList()));
    }
}