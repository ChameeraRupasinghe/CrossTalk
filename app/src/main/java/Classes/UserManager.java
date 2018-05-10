package Classes;

public class UserManager {

    public static String[] arrangeAlphabeticalOrder(String userID1, String userID2) {
        String[] userArray = new String[2];
        if (userID1.compareTo(userID2) > 0) {
            userArray[0] = userID2;
            userArray[1] = userID1;

        }

        else {
            userArray[0] = userID1;
            userArray[1] = userID2;
        }

        return userArray;
    }
}
