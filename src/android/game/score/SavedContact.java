package android.game.score;

import java.util.LinkedList;
import java.util.List;

public class SavedContact {
    protected String id, displayName, hasPhone;
    protected List<String> phoneNumbers = new LinkedList<String>();
    protected List<String> emails = new LinkedList<String>();

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof SavedContact)) {
            return false;
        }

        SavedContact sc = (SavedContact)o;
        return id.equals(sc.id) && displayName.equals(sc.displayName);
    }

    @Override
    public int hashCode() {
        //danger: craptastic. do not rely upon for anything.
        return id.hashCode() * 17 + displayName.hashCode() * 37;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(id);
        sb.append(",").append(displayName);
        if(hasPhone.equals("1")) {
            for(String number : phoneNumbers) {
                sb.append(",").append(number);
            }
        }
        for(String addr : emails) {
            sb.append(",").append(addr);
        }
        return sb.toString();
    }
}
