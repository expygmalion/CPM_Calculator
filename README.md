# SAAD Critical Path Project Manager

## Overview
SAAD Critical Path Project Manager is a JavaFX application designed to visualize the critical path of a project by calculating task dependencies, durations, and slack times. Tasks on the critical path have zero slack, indicating they are essential to the project timeline.

## Features
- **Critical Path Calculation**: Automatically computes critical paths.
- **Dependency Management**: Specify task dependencies.
- **Slack Calculation**: Identifies tasks with zero slack.
- **JavaFX GUI**: Simple, user-friendly interface.

## Code Structure
### Packages
- **`ProjectManager.ui`**: Contains the user interface code.

- **`ProjectManager.models`**: (Optional) For task data classes.

### Classes and Key Methods
- **`ProjectManagerUI`**: Main GUI class with methods for initializing and displaying tasks.
- **`ConsoleUI`**: A UI that features the implementation of the application in console.
- **`Task`**: Represents a task, managing dependencies, start/finish times, and slack.

## Getting Started
To run the app:
1. Clone the repository.
   ```terminal
   git clone https://github.com/username/SAAD_Critical_Path.git
   
2. Compile and run
  ```terminal
   cd %path%
      javac ProjectManagerUI.java
      java ProjectManagerUI
```
## Sample Run: 

```bacs
Welcome to Project Critical Path Calculator
Enter project name: Project A

Options:
1. Add task
2. Add task dependency
3. Calculate critical path
4. Exit
Choose option: 1
Enter task ID: T1
Enter task duration: 3
Task added successfully.
```
