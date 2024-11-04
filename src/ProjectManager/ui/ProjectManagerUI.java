package ProjectManager.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ProjectManager.Model.Task;
import java.util.*;

public class ProjectManagerUI extends Application {

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private TableView<Task> table;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Project Manager");

        // Create UI elements
        Label taskNameLabel = new Label("Task Name:");
        TextField taskNameField = new TextField();

        Label durationLabel = new Label("Duration:");
        TextField durationField = new TextField();

        Label dependenciesLabel = new Label("Dependencies (comma-separated):");
        TextField dependenciesField = new TextField();

        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> addTask(taskNameField, durationField, dependenciesField));

        Button calculateButton = new Button("Calculate Critical Path");
        calculateButton.setOnAction(e -> showCriticalPathWindow());

        // Set up the table
        table = new TableView<>();
        setupTable();

        // Layout
        GridPane inputLayout = new GridPane();
        inputLayout.setPadding(new Insets(10));
        inputLayout.setVgap(8);
        inputLayout.setHgap(10);
        inputLayout.add(taskNameLabel, 0, 0);
        inputLayout.add(taskNameField, 1, 0);
        inputLayout.add(durationLabel, 0, 1);
        inputLayout.add(durationField, 1, 1);
        inputLayout.add(dependenciesLabel, 0, 2);
        inputLayout.add(dependenciesField, 1, 2);
        inputLayout.add(addButton, 1, 3);
        inputLayout.add(calculateButton, 1, 4);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(inputLayout, table);

        Scene scene = new Scene(vbox, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTable() {
        table.setItems(tasks);
        table.setEditable(false);

        TableColumn<Task, String> nameColumn = new TableColumn<>("Task Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("taskId"));
        nameColumn.setMinWidth(100);

        TableColumn<Task, Integer> durationColumn = new TableColumn<>("Duration");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        TableColumn<Task, Integer> earliestStartColumn = new TableColumn<>("Earliest Start");
        earliestStartColumn.setCellValueFactory(new PropertyValueFactory<>("earliestStart"));

        TableColumn<Task, Integer> earliestFinishColumn = new TableColumn<>("Earliest Finish");
        earliestFinishColumn.setCellValueFactory(new PropertyValueFactory<>("earliestFinish"));

        TableColumn<Task, Integer> latestStartColumn = new TableColumn<>("Latest Start");
        latestStartColumn.setCellValueFactory(new PropertyValueFactory<>("latestStart"));

        TableColumn<Task, Integer> latestFinishColumn = new TableColumn<>("Latest Finish");
        latestFinishColumn.setCellValueFactory(new PropertyValueFactory<>("latestFinish"));

        TableColumn<Task, Integer> slackColumn = new TableColumn<>("Slack");
        slackColumn.setCellValueFactory(new PropertyValueFactory<>("slack"));

        TableColumn<Task, String> dependenciesColumn = new TableColumn<>("Dependencies");
        dependenciesColumn.setCellValueFactory(new PropertyValueFactory<>("dependencies"));

        table.getColumns().addAll(nameColumn, durationColumn, earliestStartColumn, earliestFinishColumn,
                latestStartColumn, latestFinishColumn, slackColumn, dependenciesColumn);
    }

    private void addTask(TextField nameField, TextField durationField, TextField dependenciesField) {
        String taskName = nameField.getText();
        int duration;
        String dependencies = dependenciesField.getText();

        // Basic validation
        try {
            duration = Integer.parseInt(durationField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid duration", "Please enter a valid number for duration.");
            return;
        }

        // Create task and add it to the table
        Task newTask = new Task(taskName, duration, 0, duration, 0, duration, 0, dependencies);
        tasks.add(newTask);

        // Clear input fields
        nameField.clear();
        durationField.clear();
        dependenciesField.clear();
    }

    private void showCriticalPathWindow() {
        // Calculate critical path
        List<Task> criticalPath = calculateCriticalPath();

        // Create a new stage for displaying the critical path
        Stage criticalPathStage = new Stage();
        criticalPathStage.setTitle("Critical Path");

        TableView<Task> criticalPathTable = new TableView<>();
        setupCriticalPathTable(criticalPathTable, criticalPath);

        VBox vbox = new VBox();
        vbox.getChildren().add(criticalPathTable);

        Scene scene = new Scene(vbox, 600, 300);
        criticalPathStage.setScene(scene);
        criticalPathStage.initModality(Modality.APPLICATION_MODAL);
        criticalPathStage.show();
    }

    private List<Task> calculateCriticalPath() {
        // Reset earliest and latest times
        for (Task task : tasks) {
            task.reset();
        }

        // Calculate earliest start and finish
        for (Task task : tasks) {
            computeEarliest(task);
        }

        // Calculate latest start and finish (reverse order)
        for (int i = tasks.size() - 1; i >= 0; i--) {
            computeLatest(tasks.get(i));
        }

        // Determine critical path (tasks with zero slack)
        List<Task> criticalPath = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getSlack() == 0) {
                criticalPath.add(task);
            }
        }
        return criticalPath;
    }

    private void computeEarliest(Task task) {
        String[] dependencies = task.getDependencies().split(",");
        int maxFinish = 0;
        for (String dep : dependencies) {
            dep = dep.trim();
            for (Task t : tasks) {
                if (t.getTaskId().equals(dep)) {
                    maxFinish = Math.max(maxFinish, t.getEarliestFinish());
                }
            }
        }
        task.setEarliestStart(maxFinish);
        task.setEarliestFinish(maxFinish + task.getDuration());
    }

    private void computeLatest(Task task) {
        if (task.getLatestFinish() == 0) {
            task.setLatestFinish(task.getEarliestFinish());
        }
        task.setLatestStart(task.getLatestFinish() - task.getDuration());
        task.setSlack(task.getLatestStart() - task.getEarliestStart());
    }

    private void setupCriticalPathTable(TableView<Task> criticalPathTable, List<Task> criticalPath) {
        criticalPathTable.setEditable(false);

        TableColumn<Task, String> nameColumn = new TableColumn<>("Task Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("taskId"));
        nameColumn.setMinWidth(100);

        TableColumn<Task, Integer> durationColumn = new TableColumn<>("Duration");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        TableColumn<Task, Integer> earliestStartColumn = new TableColumn<>("Earliest Start");
        earliestStartColumn.setCellValueFactory(new PropertyValueFactory<>("earliestStart"));

        TableColumn<Task, Integer> earliestFinishColumn = new TableColumn<>("Earliest Finish");
        earliestFinishColumn.setCellValueFactory(new PropertyValueFactory<>("earliestFinish"));

        TableColumn<Task, Integer> latestStartColumn = new TableColumn<>("Latest Start");
        latestStartColumn.setCellValueFactory(new PropertyValueFactory<>("latestStart"));

        TableColumn<Task, Integer> latestFinishColumn = new TableColumn<>("Latest Finish");
        latestFinishColumn.setCellValueFactory(new PropertyValueFactory<>("latestFinish"));

        TableColumn<Task, Integer> slackColumn = new TableColumn<>("Slack");
        slackColumn.setCellValueFactory(new PropertyValueFactory<>("slack"));

        TableColumn<Task, String> dependenciesColumn = new TableColumn<>("Dependencies");
        dependenciesColumn.setCellValueFactory(new PropertyValueFactory<>("dependencies"));

        criticalPathTable.getColumns().addAll(nameColumn, durationColumn, earliestStartColumn, earliestFinishColumn,
                latestStartColumn, latestFinishColumn, slackColumn, dependenciesColumn);

        criticalPathTable.setItems(FXCollections.observableArrayList(criticalPath));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Task {
        private final String taskId;
        private final int duration;
        private int earliestStart;
        private int earliestFinish;
        private int latestStart;
        private int latestFinish;
        private int slack;
        private final String dependencies;

        public Task(String taskId, int duration, int earliestStart, int earliestFinish,
                    int latestStart, int latestFinish, int slack, String dependencies) {
            this.taskId = taskId;
            this.duration = duration;
            this.earliestStart = earliestStart;
            this.earliestFinish = earliestFinish;
            this.latestStart = latestStart;
            this.latestFinish = latestFinish;
            this.slack = slack;
            this.dependencies = dependencies;
        }

        public String getTaskId() {
            return taskId;
        }

        public int getDuration() {
            return duration;
        }

        public int getEarliestStart() {
            return earliestStart;
        }

        public int getEarliestFinish() {
            return earliestFinish;
        }

        public int getLatestStart() {
            return latestStart;
        }

        public int getLatestFinish() {
            return latestFinish;
        }

        public int getSlack() {
            return slack;
        }

        public String getDependencies() {
            return dependencies;
        }

        public void setEarliestStart(int earliestStart) {
            this.earliestStart = earliestStart;
        }

        public void reset() {
            this.earliestStart = 0;
            this.earliestFinish = this.duration;
            this.latestStart = 0;
            this.latestFinish = 0;
            this.slack = 0;
        }

        public void setLatestFinish(int latestFinish) {
            this.latestFinish = latestFinish;
        }

        public void setLatestStart(int latestStart) {
            this.latestStart = latestStart;
        }

        public void setSlack(int slack) {
            this.slack = slack;
        }

        public void setEarliestFinish(int i) {

        }
    }
}