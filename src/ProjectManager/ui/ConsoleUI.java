package ProjectManager.ui;

import ProjectManager.Service.ProjectService;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final ProjectService projectService;
    private final Scanner scanner ;

    public ConsoleUI() {
        this.projectService = new ProjectService();
        this.scanner = new Scanner(System.in);
    }

    public void init() {
        System.out.println("Welcome to Project Critical Path Calculator");
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();
        projectService.createProject(projectName);

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Add task");
            System.out.println("2. Add task dependencies");
            System.out.println("3. Calculate critical path");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    addTaskDependencies();
                    break;
                case 3:
                    projectService.calculateCriticalPath();
                    break;
                case 4:
                    System.out.println("Exiting the program. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void addTask() {
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine();
        System.out.print("Enter task duration: ");
        int duration = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        projectService.addTask(taskId, duration);
        System.out.println("Task added successfully.");
    }

    private void addTaskDependencies() {
        System.out.print("Enter task ID for which to add dependencies: ");
        String taskId = scanner.nextLine();
        System.out.print("Enter dependencies (comma-separated): ");
        String dependenciesInput = scanner.nextLine();
        List<String> dependencies = Arrays.asList(dependenciesInput.split(",\\s*"));
        projectService.addDependencies(taskId, dependencies);
        System.out.println("Dependencies added successfully.");
    }

    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        ui.init();
    }
}