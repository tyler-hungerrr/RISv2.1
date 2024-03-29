/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datastorage;

import java.io.InputStream;
import datastorage.User;

/**
 *
 * @author tyler
 */
public class Perfevel {
    
    private int role_id, apptID;
    private String radtime;
    private String techtime;
    private String rectime;
    public Double time;
    public String userID;
    public String fullName;
    public String username;
    public String roleVal;
    
    public Perfevel(int role_id,int apptID, String radtime, String techtime, String rectime) {
        this.role_id = role_id;
        this.apptID = apptID;
        this.radtime = radtime;
        this.techtime = techtime;
        this.rectime = rectime;
    }
    
    public Perfevel(String radtime) {
        this.radtime = radtime;
    }
    
    public String getRoleVal() {
        return roleVal;
    }
    
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
     public Double getTime() {
        return time;
    }

    public void SetTime(Double time) {
        this.time = time;
    }
    
      public String getRadTime() {
        return radtime;
    }

    public void SetRadTime(String radtime) {
        this.radtime = radtime;
    }
    
    public String getTechTime() {
        return techtime;
    }

    public void setTechTime(String techtime) {
        this.techtime = techtime;
    }
    
    public String getRecTime() {
        return rectime;
    }

    public void setRecTime(String rectime) {
        this.rectime = rectime;
    }
    
    public int getRoleId() {
        return role_id;
    }

    public void setRoleId(int role_id) {
        this.role_id = role_id;
    }
    public int getApptId() {
        return apptID;
    }

    public void setApptId(int apptID) {
        this.apptID = apptID;
    }
}
