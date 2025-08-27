package movieService.controller;

// LoginSession.java
public class LoginSession {
    private static String currentId;

    public static void setCurrentId(String id) {
        currentId = id;
    }

    public static String getCurrentId() {
        return currentId;
    }
}
