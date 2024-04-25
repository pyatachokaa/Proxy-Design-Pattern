import java.util.HashMap;
import java.util.Map;

interface DocumentService {
    void uploadDocument(String document, String user);
    String downloadDocument(String document, String user);
    void editDocument(String document, String user, String content);
    String searchDocument(String query, String user);
}

class RealDocumentService implements DocumentService {
    private Map<String, String> documents = new HashMap<>();

    @Override
    public void uploadDocument(String document, String user) {
        documents.put(document, "Content of " + document);
        System.out.println("Document '" + document + "' uploaded by user '" + user + "'");
    }

    @Override
    public String downloadDocument(String document, String user) {
        System.out.println("Document '" + document + "' downloaded by user '" + user + "'");
        return documents.getOrDefault(document, "");
    }

    @Override
    public void editDocument(String document, String user, String content) {
        documents.put(document, content);
        System.out.println("Document '" + document + "' edited by user '" + user + "'");
    }

    @Override
    public String searchDocument(String query, String user) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : documents.entrySet()) {
            if (entry.getValue().contains(query)) {
                result.append(entry.getKey()).append(", ");
            }
        }
        System.out.println("User '" + user + "' searched for '" + query + "'");
        return result.toString();
    }
}

class ProxyDocumentService implements DocumentService {
    private RealDocumentService realDocumentService = new RealDocumentService();
    private Map<String, String> userSessions = new HashMap<>();

    public boolean authenticate(String user, String password) {
        // Dummy authentication for demonstration purposes
        return user.equals("admin") && password.equals("admin123");
    }

    @Override
    public void uploadDocument(String document, String user) {
        if (userSessions.containsKey(user)) {
            realDocumentService.uploadDocument(document, user);
        } else {
            System.out.println("User '" + user + "' is not authenticated.");
        }
    }

    @Override
    public String downloadDocument(String document, String user) {
        if (userSessions.containsKey(user)) {
            return realDocumentService.downloadDocument(document, user);
        } else {
            System.out.println("User '" + user + "' is not authenticated.");
            return "";
        }
    }

    @Override
    public void editDocument(String document, String user, String content) {
        if (userSessions.containsKey(user)) {
            realDocumentService.editDocument(document, user, content);
        } else {
            System.out.println("User '" + user + "' is not authenticated.");
        }
    }

    @Override
    public String searchDocument(String query, String user) {
        if (userSessions.containsKey(user)) {
            return realDocumentService.searchDocument(query, user);
        } else {
            System.out.println("User '" + user + "' is not authenticated.");
            return "";
        }
    }

    public void login(String user, String password) {
        if (authenticate(user, password)) {
            userSessions.put(user, password);
            System.out.println("User '" + user + "' logged in successfully.");
        } else {
            System.out.println("Invalid credentials for user '" + user + "'.");
        }
    }

    public void logout(String user) {
        userSessions.remove(user);
        System.out.println("User '" + user + "' logged out.");
    }
}

class Test8 {
    public static void main(String[] args) {
        ProxyDocumentService proxy = new ProxyDocumentService();
        proxy.login("admin", "admin123");

        proxy.uploadDocument("Document1", "admin");
        proxy.editDocument("Document1", "admin", "Updated content");
        String content = proxy.downloadDocument("Document1", "admin");
        System.out.println("Downloaded content: " + content);

        String searchResult = proxy.searchDocument("content", "admin");
        System.out.println("Search result: " + searchResult);

        proxy.logout("admin");
    }
}

