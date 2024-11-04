package ProjectManager.Model;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String taskId;
    private int duration;
    private List<Task> dependencies;
    private int earliestStart;
    private int earliestFinish;
    private int latestStart;
    private int latestFinish;
    private int slack;

    public Task(String taskId, int duration) {
        this.taskId = taskId;
        this.duration = duration;
        this.dependencies = new ArrayList<>();
        reset(); // Initialize values upon creation
    }

    public void addDependency(Task task) {
        dependencies.add(task);
    }

    // Getters and setters
    public String getTaskId() {
        return taskId;
    }

    public int getDuration() {
        return duration;
    }

    public List<Task> getDependencies() {
        return dependencies;
    }

    public int getEarliestStart() {
        return earliestStart;
    }

    public void setEarliestStart(int earliestStart) {
        this.earliestStart = earliestStart;
        calculateEarliestFinish(); // Automatically calculate earliest finish when start is set
    }

    public int getEarliestFinish() {
        return earliestFinish;
    }

    public void setEarliestFinish(int earliestFinish) {
        this.earliestFinish = earliestFinish; // Set the earliest finish directly
        // Calculate the earliest start based on the earliest finish
        this.earliestStart = earliestFinish - duration; // Adjust earliest start based on finish
    }

    private void calculateEarliestFinish() {
        this.earliestFinish = this.earliestStart + this.duration;
    }

    public int getLatestStart() {
        return latestStart;
    }

    public void setLatestStart(int latestStart) {
        this.latestStart = latestStart;
        calculateLatestFinish(); // Automatically calculate latest finish when start is set
    }

    public int getLatestFinish() {
        return latestFinish;
    }

    private void calculateLatestFinish() {
        this.latestFinish = this.latestStart + this.duration;
    }

    public int getSlack() {
        return slack;
    }

    public void setSlack(int slack) {
        this.slack = slack;
    }

    public void reset() {
        this.earliestStart = 0;
        this.earliestFinish = 0; // Will be recalculated
        this.latestStart = 0; // Will be calculated later
        this.latestFinish = 0; // Will be calculated later
        this.slack = 0; // Will be calculated later
    }

    @Override
    public String toString() {
        return String.format("Task %s (Duration: %d, Earliest Start: %d, Earliest Finish: %d, Latest Start: %d, Latest Finish: %d, Slack: %d)",
                taskId, duration, earliestStart, earliestFinish, latestStart, latestFinish, slack);
    }

    public void setLatestFinish(int newLatestFinish) {
        // Update the latestFinish value for this task
        this.latestFinish = newLatestFinish;

        // Adjust earliestStart for dependent tasks based on this task's new latestFinish
        for (Task dependentTask : getDependentTasks()) {
            // Calculate the new latest finish time for the dependent task
            int adjustedLatestFinish = newLatestFinish - dependentTask.getDuration();
            // Set the dependent task's latest finish if the adjusted finish is earlier
            dependentTask.setLatestFinish(Math.min(dependentTask.getLatestFinish(), adjustedLatestFinish));
        }
    }

    // Helper method to get dependent tasks of this task
    private List<Task> getDependentTasks() {
        List<Task> dependents = new ArrayList<>();
        for (Task t : dependencies) {
            dependents.add(t);
        }
        return dependents;
    }
}