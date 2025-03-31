/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.service;

import com.maven.vintage_project.config.JWT;
import com.maven.vintage_project.model.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.MediaType;
import static jdk.nashorn.tools.ShellFunctions.input;
import net.coobird.thumbnailator.Thumbnails;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author herma
 */
public class UserService {
    private User layer = new User();
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,10}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final String PHONE_REGEX = "^(\\+?[0-9]{1,3})?[ -]?[0-9]{6,14}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final String UPLOAD_DIR = "C:\\wildfly\\standalone\\deployments\\uploads\\";


    
    //Még nincsen benne semmiben
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
    }

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
                toAdd.put("profilePicture", actualUser.getProfilePicture());
                
                
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
            user.put("profile_picture", modelResult.getProfilePicture());
            
            toReturn.put("result", user);
        }
        
        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
    
    public JSONObject changePassword(Integer userId, String newPassword, Integer creator){
        JSONObject toReturn = new JSONObject();
        String status = "success";
        int statusCode = 200;
        
        if(userId == creator){
            Boolean modelResult = layer.changePassword(userId, newPassword, creator);
            if(!modelResult){
                status = "ModelException";
                statusCode = 500;
            }
        } else{
            status = "PermissionError";
            statusCode = 417;
        }
        
        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
    
    public JSONObject uploadAndResizeProfilePicture(Integer userId, MultipartFormDataInput input) {
    JSONObject responseJson = new JSONObject();

    try {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> fileParts = uploadForm.get("file");

        if (fileParts == null || fileParts.isEmpty()) {
            responseJson.put("status", 400);
            responseJson.put("error", "No file uploaded");
            return responseJson;
        }

        InputPart filePart = fileParts.get(0);
        InputStream fileInputStream = filePart.getBody(InputStream.class, null);

        // Fájlnév kinyerése regexszel
        String contentDisposition = filePart.getHeaders().getFirst("Content-Disposition");
        Pattern pattern = Pattern.compile("filename=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(contentDisposition);
        String fileNameFromHeader = matcher.find() ? matcher.group(1) : "default.jpg";
        String extension = fileNameFromHeader.substring(fileNameFromHeader.lastIndexOf('.') + 1).toLowerCase();

        // Kiterjesztés ellenőrzése
        if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("jpeg")) {
            responseJson.put("status", 415);
            responseJson.put("error", "Only PNG and JPG files are allowed.");
            return responseJson;
        }

        // Fájlnév generálása
        String fileName = "profile_" + userId + "_" + System.currentTimeMillis() + "." + extension;
        String filePath = UPLOAD_DIR + File.separator + fileName;
        File tempFile = new File(filePath);

        // Fájl mentése
        Files.copy(fileInputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Átméretezés
        File resizedFile = new File(filePath);
        Thumbnails.of(tempFile)
                  .size(300, 300)
                  .outputFormat(extension)
                  .toFile(resizedFile);

        // Adatbázis frissítése
        Boolean success = User.updateProfilePicture(userId, filePath);

        String url = "/webresources/user/uploads/" + fileName;
        if (success) {
            responseJson.put("status", 200);
            responseJson.put("message", "File uploaded and resized successfully");
            responseJson.put("url", url);
            //responseJson.put("url", "/api/user/uploads/" + fileName); // Opcionális URL visszaadás
        } else {
            responseJson.put("status", 404);
            responseJson.put("error", "User not found");
        }

        return responseJson;
    } catch (Exception e) {
        responseJson.put("status", 500);
        responseJson.put("error", e.getMessage());
        return responseJson;
    }
}

    
    public File getProfilePicture(String fileName) {
        File file = new File(UPLOAD_DIR + fileName);
        return file.exists() ? file : null;
    }

    public JSONObject sendEmail(String to, String subject, String template, Map<String, String> variables) {
        JSONObject responseJson = new JSONObject();

        try {
            // Meghívjuk a User Model metódusát az email elküldésére
            String htmlBody = User.loadEmailTemplate(template, variables);
            Boolean success = User.sendEmail(to, subject, htmlBody);

            if (success) {
                responseJson.put("status", 200);
                responseJson.put("message", "Email successfully sent.");
            } else {
                responseJson.put("status", 500);
                responseJson.put("error", "Failed to send email.");
            }

            return responseJson;
        } catch (Exception e) {
            responseJson.put("status", 500);
            responseJson.put("error", e.getMessage());
            return responseJson;
        } 
    }
    
    public JSONObject updateUser(Integer modifierId, Integer targetUserId, User u) {
        JSONObject responseJson = new JSONObject();
        
        try {
            User modifier = User.findById(modifierId);
            User target = User.findById(targetUserId); // cél user ellenőrzése is

            if (modifier == null || target == null) {
                responseJson.put("status", 404);
                responseJson.put("error", "Modifier or target user not found");
                return responseJson;
            }

            // Jogosultságellenőrzés
            if (modifierId.equals(targetUserId)) {
                // Saját magát módosítja - engedjük
            } else if (!modifier.getIsAdmin()) {
                // Más profilját próbálja módosítani, de nem admin - tiltjuk
                responseJson.put("status", 403);
                responseJson.put("error", "Only admins can update other users");
                return responseJson;
            }

            // Meghívjuk az adatbázisban lévő tárolt eljárást
            Boolean success = User.updateUser(modifierId, targetUserId, u);
            System.out.println(modifierId + " " + targetUserId + " " + u);
            
            if (Boolean.TRUE.equals(success)) {
                responseJson.put("status", 200);
                responseJson.put("message", "User updated successfully");
            } else {
                responseJson.put("status", 500);
                responseJson.put("error", "Database update failed");
            }

        } catch (Exception e) {
            responseJson.put("status", 500);
            responseJson.put("error", e.getMessage());
            e.printStackTrace(); // hibakereséshez hasznos
        }

        return responseJson;    
    }
    
    
}
