/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.service;

import com.maven.vintage_project.config.JWT;
import com.maven.vintage_project.model.User;
import java.util.List;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author herma
 */
public class UserService {
    private User layer = new User();
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,10}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    public static boolean isValidPassword(String password) {
        if(password.length() < 8) {
            return false;
        }
        
        boolean hasNum = false;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasSpecialChar = false;
        
        for(char c : password.toCharArray()) {
            if(Character.isDigit(c)) {
                hasNum = true;
            } else if(Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if(Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if("!@#$%^&*()_+-=[]{}|;':,.<>?/`~".indexOf(c) != -1) {
                hasSpecialChar = true;
            }
        }
        
        return hasNum && hasUpperCase && hasLowerCase && hasSpecialChar;
    }
    
    public JSONObject login(String email, String password) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        int statusCode = 200;

        if (isValidEmail(email)) {
            User modelResult = layer.login(email, password);

            if (modelResult == null) {
                status = "modelException";
                statusCode = 500;
            } else {
                if (modelResult.getId() == null) {
                    status = "userNotFound";
                    statusCode = 417;
                } else {
                    JSONObject result = new JSONObject();
                    result.put("id", modelResult.getId());
                    result.put("username", modelResult.getUsername());
                    result.put("firstName", modelResult.getFirstname());
                    result.put("lastName", modelResult.getLastname());
                    result.put("email", modelResult.getEmail());
                    result.put("isAdmin", modelResult.getIsAdmin());
                    result.put("isDeleted", modelResult.getIsDeleted());
                    result.put("jwt", JWT.createJWT(modelResult));

                    toReturn.put("result", result);
                    
                }
            }

        } else {
            status = "invalidEmail";
            statusCode = 417;
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
    
    public JSONObject registerUser(User u) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        int statusCode = 200;
        
        if (!isValidEmail(u.getEmail())) {
        status = "InvalidEmail";
        statusCode = 417;
    } else if (!isValidPassword(u.getPassword())) {
        status = "InvalidPassword";
        statusCode = 417;
    } else if (isUserExists(u.getEmail())) {
        status = "UserAlreadyExists";
        statusCode = 417;
    } else {
        boolean registerUser = layer.registerUser(u);
        if (!registerUser) {
            status = "fail";
            statusCode = 417;
        }
    }
        

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
        
    }
    
    private boolean isUserExists(String email) {
    // EZ ellenörzi hogy létezik e a felhasználó
    return User.isUserExists(email);
}
    
    public JSONObject registerAdmin(User u, String jwt) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        int statusCode = 200;

        //A user aki meghívja ezt, admin-e 1.
        //valid-e a jelszó 2.
        //valid-e az email cím 3.
        //Az email cím benne van-e a db-ben 4.
        if (JWT.isAdmin(jwt)) {
            if (isValidPassword(u.getPassword())) {
                if (isValidEmail(u.getEmail())) {
                    boolean userIsExists = User.isUserExists(u.getEmail());
                    if (User.isUserExists(u.getEmail()) == null) {
                        status = "ModelException";
                        statusCode = 500;
                    } else if (userIsExists == true) {
                        status = "UserAlreadyExists";
                        statusCode = 417;
                    } else {
                        boolean registerAdmin = layer.registerAdmin(u);
                        if (registerAdmin == false) {
                            status = "fail";
                            statusCode = 417;
                        }
                    }
                } else {
                    status = "InvalidEmail";
                    statusCode = 417;
                }
            } else {
                status = "InvalidPassword";
                statusCode = 417;
            }
        } else {
            status = "PersmissonError";
            statusCode = 417;
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
    
    
    public JSONObject getAllUser() {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        int statusCode = 200;
        try {
            List<User> modelResult = layer.getAllUser();
            if (modelResult == null) {
            status = "ModelException";
            statusCode = 500;
        } else if (modelResult.isEmpty()) {
            status = "NoUsersFound";
            statusCode = 417;
        } else {
            JSONArray result = new JSONArray();
            
            for(User actualUser : modelResult){
                JSONObject toAdd = new JSONObject();

                toAdd.put("id", actualUser.getId());
                toAdd.put("username", actualUser.getUsername());
                toAdd.put("firstName", actualUser.getFirstname());
                toAdd.put("lastName", actualUser.getLastname());
                toAdd.put("email", actualUser.getEmail());
                toAdd.put("phoneNumber", actualUser.getPhoneNumber());
                toAdd.put("password", actualUser.getPassword());
                toAdd.put("isAdmin", actualUser.getIsAdmin());
                toAdd.put("isDeleted", actualUser.getIsDeleted());
                toAdd.put("createdAt", actualUser.getCreatedAt());
                toAdd.put("deletedAt", actualUser.getDeletedAt());
                
                result.put(toAdd);
            }
            
            toReturn.put("result", result);
            System.out.println("getAllUser() meghívva");
        }
        } catch (Exception ex) {
            ex.printStackTrace();
            status = "ModelException";
            statusCode = 500;
        }
        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
    
    public JSONObject getUserById(Integer id){
        JSONObject toReturn = new JSONObject();
        String status = "success";
        int statusCode = 200;
        User modelResult = new User(id);
        
        if(modelResult.getEmail() == null){
            status = "UserNotFound";
            statusCode = 417;
        }else{
            JSONObject user = new JSONObject();
            
            user.put("id", modelResult.getId());
            user.put("username", modelResult.getUsername());
            user.put("firstName", modelResult.getFirstname());
            user.put("lastName", modelResult.getLastname());
            user.put("email", modelResult.getEmail());
            user.put("phone", modelResult.getPhoneNumber());
            user.put("isAdmin", modelResult.getIsAdmin());
            user.put("isDeleted", modelResult.getIsDeleted());
            user.put("createdAt", modelResult.getCreatedAt());
            
            toReturn.put("result", user);
        }
        
        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
}
