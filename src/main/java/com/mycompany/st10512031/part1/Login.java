/*
 * Login.java
 * Part 1: Registration and Login for QuickChat.
 * Author: Student
 * Student Number: ST10512031
 * Date: 2026
 *
 * References:
 *   [1] J. Farrell, Java Programming, 10th ed. Boston, MA, USA:
 *       Cengage Learning, 2023.
 *
 * Code patterns adapted from [1]:
 *   - Class structure and private fields ........ Ch. 4, Sec. 4.3
 *   - Named constants with final ................. Ch. 2, Sec. 2.1
 *   - if...else decisions ........................ Ch. 5, Sec. 5.2
 *   - Relational and equality operators .......... Ch. 2, Sec. 2.3
 *   - for loops .................................. Ch. 6, Sec. 6.4
 *   - char data type and charAt() ................ Ch. 2, Sec. 2.5
 *   - Character class methods .................... Ch. 7, Sec. 7.2
 *   - String methods (contains, length, equals) .. Ch. 7, Sec. 7.4
 */

package com.mycompany.st10512031.part1;

public class Login {

    // -------- Fields (private per [1], Ch. 4, Sec. 4.3) --------
    private String registeredUsername;
    private String registeredPassword;
    private String userFirstName;
    private String userLastName;

    // -------- Named constants (final, per [1], Ch. 2, Sec. 2.1) --------
    private final int MAX_USERNAME_LENGTH = 5;
    private final int MIN_PASSWORD_LENGTH = 8;

    /*
     * RESEARCH-BASED CONSTANT
     * Regex pattern for a South African cell phone number.
     * Must start with the international code "+27" and be
     * followed by exactly 9 digits.
     */
    private final String CELL_PHONE_PATTERN = "^\\+?[0-9]{10,13}$";

    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= MAX_USERNAME_LENGTH;
    }

    public boolean checkPasswordComplexity(String password) {

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i = i + 1) {
            char currentChar = password.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                hasCapital = true;
            }
            if (Character.isDigit(currentChar)) {
                hasNumber = true;
            }
            if (!Character.isLetterOrDigit(currentChar)) {
                hasSpecial = true;
            }
        }

        return hasCapital && hasNumber && hasSpecial;
    }

    public boolean checkCellPhoneNumber(String cellPhone) {
        return cellPhone.matches(CELL_PHONE_PATTERN);
    }

    public String registerUser(String username, String password, String cellPhone,
                               String firstName, String lastName) {

        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your "
                    + "username contains an underscore and is no more than five "
                    + "characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the "
                    + "password contains at least eight characters, a capital letter, "
                    + "a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellPhone)) {
            return "Cell phone number incorrectly formatted or does not contain "
                    + "international code.";
        }

        registeredUsername = username;
        registeredPassword = password;
        userFirstName = firstName;
        userLastName = lastName;

        // Using standard String concatenation (Chapter 7)
        return """
               Username successfully captured.
               Password successfully captured.
               Cell phone number successfully added.
               User registered successfully.""";
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return enteredUsername.equals(registeredUsername)
                && enteredPassword.equals(registeredPassword);
    }

    public String returnLoginStatus(boolean loginSuccess) {
        if (loginSuccess) {
            return "Welcome " + userFirstName + ", " + userLastName
                    + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    public String getRegisteredUsername() {
        return registeredUsername;
    }
}