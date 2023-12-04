/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aidan
 */
public class userInfo {
    // counters to keep track of roles
    private static int smCount = 1;
    private static int pmCount = 1;
    
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;
    
    public userInfo(String id, String name, String email, String password, String role){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    public String getID(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }
    
    public String getRole(){
        return role;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    // changes the role of the user
    public void setRole(String role) {
        if (role.equals("sm") || role.equals("pm")) {
            this.role = role;
            updateUserInfoFile(this);
            System.out.println("Role updated successfully.");
        } else {
            System.out.println("INVALID ROLE.");
        }
    }
    
    // changes ID after changing role for user
    public void setID(String id) {
        this.id = id;
        updateUserInfoFile(this);
    }
    
    // reads through userInfo txt file to see last ID after splitting string of ID to see last 3 digits
    public static String getNextID(String role) {
        try (BufferedReader br = new BufferedReader(new FileReader("userInfo.txt"))) {
            String line;
            int maxId = 0;

            while ((line = br.readLine()) != null) {
                String[] userInfoParts = line.split(",");
                if (userInfoParts.length >= 1 && userInfoParts[0].startsWith(role.toUpperCase())) {
                    // parseInt is shorter to write compared to Integer.valueOf() in this case
                    // .substring to ignore first 2 characters. in this case it is SM or PM
                    int idNumber = Integer.parseInt(userInfoParts[0].substring(2)); 
                    maxId = Math.max(maxId, idNumber); // maxId for respective roles updated based on last 3 digits
                }
            }

            if (role.equals("sm")) {
                smCount = maxId + 1;
                return String.format("%s%03d", role.toUpperCase(), smCount);
            } else if (role.equals("pm")) {
                pmCount = maxId + 1;
                return String.format("%s%03d", role.toUpperCase(), pmCount);
            } else {
                System.out.println("Invalid role.");
                return null;
            }
        } catch (IOException e) {
            System.out.println("ERROR READING USER INFO FILE.");
            return null;
        }
    }
    
    // changes user ID role and ID; since if need to change role, it will change ID too 
    public static void updateRoleAndID(userInfo user, String newRole) {
        String newID = userInfo.getNextID(newRole);

        if (newID != null) {
            // update user role and ID
            user.setRole(newRole);
            user.setID(newID);

            updateUserInfoFile(user);
            System.out.println("Role and ID updated successfully.");
        } else {
            System.out.println("ERROR UPDATING ROLE AND ID.");
        }
    }
    
    // updates the userInfo in the txt file
    public static void updateUserInfoFile(userInfo user) {
        try {
            
            Path uinfo = Paths.get("userInfo.txt");

            List<String> newLines = new ArrayList<>();
            
            BufferedReader br = new BufferedReader(new FileReader(uinfo.toFile()));

            try {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] userInfoParts = line.split(",");
                    if (userInfoParts[2].equals(user.getEmail())) {
                        newLines.add(user.toString());
                    } else {
                        newLines.add(line);
                    }
                }

            } catch (IOException e) {
                System.out.println("ERROR READING USER INFO FILE.");
                return;
            }
            
            Files.write(uinfo, newLines);

            System.out.println("User information updated successfully.");
        } catch (IOException e) {
            System.out.println("ERROR UPDATING USER INFO FILE.");
        }
    }
    
    // search user in txt file
    public static userInfo getUserInfoByID(String uid) {
        try (BufferedReader br = new BufferedReader(new FileReader("userInfo.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] userInfoParts = line.split(",");
                if (userInfoParts.length >= 3 && userInfoParts[0].equals(uid)){
                    // Found matching user, create and return userInfo object
                    return new userInfo(userInfoParts[0], userInfoParts[1], userInfoParts[2], userInfoParts[3], userInfoParts[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR READING USER INFO FILE.");
        }

        return null; // User not found
    }
    
    // search user in txt file by email and password
    public static userInfo getUserInfoByEmailAndPassword(String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader("userInfo.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] userInfoParts = line.split(",");
                if (userInfoParts.length >= 3 && userInfoParts[2].equals(email) && userInfoParts[3].equals(password)) {
                    // Found matching user, create and return userInfo object
                    return new userInfo(userInfoParts[0], userInfoParts[1], userInfoParts[2], userInfoParts[3], userInfoParts[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR READING USER INFO FILE.");
        }

        return null; // User not found
    }
    
    // see if it is valid email format
    public static boolean validEmail(String email){
        String regexEmailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regexEmailPattern);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches() == true){
            return true;
        }else{
            System.out.println("INVALID EMAIL ADDRESS.");
            System.out.println();
            return false;
        }
    }
    
    // see if there is same name in the user txt file
    public static boolean duplicateName(String name) {
        try (BufferedReader br = new BufferedReader(new FileReader("userInfo.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] userInfoParts = line.split(",");
                if (userInfoParts.length >= 2 && userInfoParts[1].equals(name)) {
                    return true; // Found a duplicate name
                }
            }
        } catch (FileNotFoundException e){
            
        } catch (IOException e) {
            System.out.println("ERROR READING USER INFO FILE.");
        }

        return false; 
    }
    
    // see if there is same email in user txt file
    public static boolean duplicateEmail(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader("userInfo.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] userInfoParts = line.split(",");
                if (userInfoParts.length >= 3 && userInfoParts[2].equals(email)) {
                    return true; // found duplicate email
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR READING USER INFO FILE.");
        }

        return false; 
    }
    
    // see if password follows guidelines
    public static boolean validPassword(String password){
        String regexPasswordPattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
        Pattern pattern = Pattern.compile(regexPasswordPattern);
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches() == true){
            return true;
        }else{
            System.out.println("INVALID PASSWORD, must contain at least ONE NUMERIC character, ONE LOWERCASE and UPPERCASE character,");
            System.out.println("ONE SPECIAL SYMBOL and must be between 8-20 characters in LENGTH.");
            System.out.println();
            return false;
        }
    }
    
    @Override
    public String toString() {
        return id + "," + name + "," + email + "," + password + "," + role;
    }
    
    // save user info into file
    public void saveFile(){
        
        boolean append = false;

        try { // try catch statement is just to prevent first line in txt file from being empty
            if (Files.size(Paths.get("userInfo.txt")) > 0) {
                append = true;
            }
        } catch (IOException e) {
            System.out.println("USER INFO FILE NOT FOUND. CREATING USER INFO FILE...");
        }
        
        try (FileWriter fw = new FileWriter("userInfo.txt", append)){
            String saveInfo = this.toString() + System.lineSeparator();
            fw.write(saveInfo);
        }catch (IOException e){
            System.out.println("ERROR SAVING USER INFO.");
        }
    }
    
    // prints out everything in user info file (all user details)
    public static void printInfo(){
        try (BufferedReader br = new BufferedReader(new FileReader("userInfo.txt"))){
            String lines;
            
            while ((lines = br.readLine()) != null){ // read each lines until it is not empty
                System.out.println(lines);
            }
            
        }catch (IOException e){
            System.out.println("ERROR READING USER INFO FILE.");
        }
    }
    
    // login for user
    public static boolean userLogin(String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader("userInfo.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] userInfoParts = line.split(",");
                if (userInfoParts.length >= 3 && userInfoParts[2].equals(email) && userInfoParts[3].equals(password)) {
                    return true; // Successful login
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR READING USER INFO FILE.");
        }

        return false; // Invalid login
    }
}
