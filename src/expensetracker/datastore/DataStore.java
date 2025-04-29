package expensetracker.datastore;

import expensetracker.models.User;
import expensetracker.ds.LinkedList;

public class DataStore {
    private LinkedList<User> users = new LinkedList<>();

    public boolean register(User u) {
        for (User ex : users)
            if (ex.getId().equals(u.getId()))
                return false;
        users.add(u);
        return true;
    }

    public User authenticate(String id, String pw) {
        for (User u : users)
            if (u.getId().equals(id) && u.checkPw(pw))
                return u;
        return null;
    }
}
