package todo;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class Main {
    private static Scanner scanner = new Scanner(System.in, "ISO-8859-1");
    private static String user, todoName;
    private static int todoID;
    private static boolean afterNextInt = false;

    public static void main(String[] args) throws SQLException {
        start();
    }

    private static Connection dbOpen() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/todo?useUnicode=yes&characterEncoding=UTF-8", "root",
                    "");
            return connection;
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void start() throws SQLException {
        System.out.println("\nTo-Do-List\n----------------------");
        Connection connection = dbOpen();
        PreparedStatement stmt = connection.prepareStatement("SELECT user FROM user");
        ResultSet result = stmt.executeQuery();
        if (!result.next()) {
            createUser(connection);
        } else {
            System.out.println("1.Create user\n2.Enter existing user");
            int userSelection = 0;
            boolean invalidInput = true;
            while (invalidInput) {
                try {
                    userSelection = scanner.nextInt();
                    if (userSelection > 0 && userSelection < 3) {
                        afterNextInt = true;
                        invalidInput = false;
                    } else {
                        System.out.println("Invalid input");
                        invalidInput = true;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input");
                    scanner.nextLine();
                }
            }
            if (userSelection == 1) {
                createUser(connection);
            } else {
                System.out.print("\n! enter = abort\nEnter user: ");
                if (afterNextInt) {
                    scanner.nextLine();
                    afterNextInt = false;
                }
                user = scanner.nextLine();
                if (user.equals("")) {
                    start();
                }
                stmt = connection
                        .prepareStatement(
                                "SELECT user FROM user WHERE user=?;");
                stmt.setString(1, user);
                result = stmt.executeQuery();
                if (result.next()) {
                    openTodo(connection);
                } else {
                    System.out.println("user does`t exist\n----------------------");
                    start();
                }
            }
        }
    }

    private static void createUser(Connection connection) throws SQLException {
        try {
            System.out.print("\ncreate User!");
            if (afterNextInt) {
                System.out.print("\n! enter = abort");
                scanner.nextLine();
                afterNextInt = false;
            }
            System.out.print("\nname: ");
            user = scanner.nextLine();
            if (user.equals("")) {
                start();
            }
            PreparedStatement stmt = connection
                    .prepareStatement("INSERT INTO user values(?, now(), NULL);");
            stmt.setString(1, user);
            stmt.executeUpdate();
            openTodo(connection);
        } catch (SQLException e) {
            System.out.println("user exist!\n----------------------");
            start();
        }
    }

    private static void openTodo(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT todoId,name FROM todo WHERE user=?");
        stmt.setString(1, user);
        ResultSet result = stmt.executeQuery();
        if (!result.next()) {
            System.out.println("\nTo-Do´s " + user + "\n----------------------");
            System.out.println("No To-Do´s found");
            makeTodoEntry(connection);
        } else {
            System.out.println("\nTo-Do´s " + user + "\n----------------------");
            System.out.println("# " + result.getString(2) + "");
            while (result.next()) {
                System.out.println("# " + result.getString(2) + " ");
            }
            makeTodoEntry(connection);
        }
    }

    private static void makeTodoEntry(Connection connection) throws SQLException {
        System.out.print("----------------------\n! enter = back | upd = update: " + user + "| del = delete: " + user);
        System.out.print("\nadd To-Do: ");
        if (afterNextInt) {
            scanner.nextLine();
            afterNextInt = false;
        }
        String entrie = scanner.nextLine();
        if (entrie.equals("del")) {
            PreparedStatement stmt = connection
                    .prepareStatement("DELETE FROM user WHERE user = ? LIMIT 1;");
            stmt.setString(1, user);
            stmt.executeUpdate();
            System.out.println("User deleted\n----------------------");
            user = null;
            start();
        } else if (entrie.equals("upd")) {
            System.out.print("\n! enter = abort\nnew name 4 " + user + ": ");
            entrie = scanner.nextLine();
            if (entrie.equals("")) {
                openTodo(connection);
            }
            PreparedStatement stmt = connection
                    .prepareStatement("UPDATE user SET user = ? WHERE user = ? LIMIT 1;");
            stmt.setString(1, entrie);
            stmt.setString(2, user);
            stmt.executeUpdate();
            System.out.println(user + " updated to " + entrie + "\n----------------------");
            user = entrie;
            openTodo(connection);
        } else if (entrie.equals("")) {
            todoID = 0;
            todoName = null;
            user = null;
            start();
        } else {
            PreparedStatement stmt = connection
                    .prepareStatement("SELECT todoId, name FROM todo WHERE user=? AND name=?");
            stmt.setString(1, user);
            stmt.setString(2, entrie);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                todoID = result.getInt(1);
                todoName = result.getString(2);
            }
            if (entrie.equals(todoName)) {
                openTasks(connection);
            } else {
                stmt = connection
                        .prepareStatement("INSERT INTO todo values(NULL,?, ?, now(),NULL);");
                stmt.setString(1, entrie);
                stmt.setString(2, user);
                stmt.executeUpdate();
                if (todoID == 0) {
                    stmt = connection
                            .prepareStatement("SELECT todoId, name FROM todo WHERE user=? AND name=?");
                    stmt.setString(1, user);
                    stmt.setString(2, entrie);
                    result = stmt.executeQuery();
                    while (result.next()) {
                        todoID = result.getInt(1);
                        todoName = result.getNString(2);
                    }
                }
                openTasks(connection);
            }
        }
    }

    private static void openTasks(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT tasksid, task FROM tasks WHERE todoid=?");
        stmt.setInt(1, todoID);
        ResultSet result = stmt.executeQuery();
        if (!result.next()) {
            System.out.println("\nTasks´s # " + todoName + " - " + user + "\n----------------------");
            System.out.println("No Tasks´s found");
            makeTaskEntry(connection);
        } else {
            System.out.println("\nTasks´s # " + todoName + " - " + user + "\n----------------------");
            System.out.println("- " + result.getString(2));
            while (result.next()) {
                System.out.println("- " + result.getString(2));
            }
            makeTaskEntry(connection);
        }
    }

    private static void makeTaskEntry(Connection connection) throws SQLException {
        String task = null;
        System.out
                .print("----------------------\n! enter = back | upd = update: " + todoName + " | del = delete: "
                        + todoName + " | l = delete last entrie");
        System.out.print("\nadd Task: ");
        String entrie = scanner.nextLine();
        if (entrie.equals("l")) {
            PreparedStatement stmt = connection
                    .prepareStatement(
                            "DELETE FROM tasks WHERE  tasksId = (SELECT MAX(tasksId) FROM tasks WHERE todoId = ? LIMIT 1);");
            stmt.setInt(1, todoID);
            stmt.executeUpdate();
            System.out.println("Task deleted\n----------------------");
            openTasks(connection);
        } else if (entrie.equals("upd")) {
            System.out.print("\n! enter = back\nnew name 4 " + todoName + ": ");
            entrie = scanner.nextLine();
            if (entrie.equals("")) {
                openTasks(connection);
            }
            PreparedStatement stmt = connection
                    .prepareStatement("UPDATE todo SET name = ? WHERE user = ? AND todoId = ? LIMIT 1;");
            stmt.setString(1, entrie);
            stmt.setString(2, user);
            stmt.setInt(3, todoID);
            stmt.executeUpdate();
            System.out.println(todoName + " updated to" + entrie + "\n----------------------");
            todoName = entrie;
            openTasks(connection);
        } else if (entrie.equals("del")) {
            PreparedStatement stmt = connection
                    .prepareStatement("DELETE FROM todo WHERE todoId = ? LIMIT 1;");
            stmt.setInt(1, todoID);
            stmt.executeUpdate();
            todoName = null;
            todoID = 0;
            System.out.println("Task deleted\n----------------------");
            openTodo(connection);
        } else if (entrie.equals("")) {
            todoName = null;
            todoID = 0;
            openTodo(connection);
        } else {
            PreparedStatement stmt = connection
                    .prepareStatement("SELECT tasksId, task FROM tasks WHERE todoId=? AND task=?");
            stmt.setInt(1, todoID);
            stmt.setString(2, entrie);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                task = result.getString(2);
            }
            if (entrie.equals(task)) {
                System.out.println("Taks exist!");
                openTasks(connection);
            } else {
                stmt = connection
                        .prepareStatement("INSERT INTO tasks values(NULL,?, ?, now(),NULL);");
                stmt.setString(1, entrie);
                stmt.setInt(2, todoID);
                stmt.executeUpdate();
                openTasks(connection);
            }
        }
    }
}