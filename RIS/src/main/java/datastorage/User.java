/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datastorage;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import datastorage.Perfevel;

/**
 *
 * @author 14048
 */
public class User {

    public String userID;
    public String fullName;
    private String email;
    public String username;
    private int role;
    private boolean enabled;
    public Button placeholder = new Button("placeholder");
    public String roleVal;
    private Image pfp;
    public Double time;

    public Image getPfp() {
        return pfp;
    }

    public ImageView getPfpView() {
        ImageView pfpView = new ImageView(pfp);
        pfpView.setPreserveRatio(true);
        pfpView.setFitHeight(30);
        return pfpView;
    }

    public void setPfp(Image pfp) {
        this.pfp = pfp;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public Label getEnabledLabel() {
        Label val = new Label(enabled + "");
        if (enabled) {
            val.setId("colorGreen");
        } else {
            val.setId("colorRed");
        }
        return val;
    }

    public String getRoleVal() {
        return roleVal;
    }

    public User() {
        this.userID = "";
        this.email = "";
        this.fullName = "";
        this.role = 0;
        this.roleVal = "";
    }

    public User(String userID, String email, String fullName, String username, int role, boolean enabled, String roleVal) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.role = role;
        this.enabled = enabled;
        this.roleVal = roleVal;
    }

    public User(String userID, String fullName, int role) {
        this.userID = userID;
        this.fullName = fullName;
        this.role = role;
    }
    
    public User(String userID, String fullName, String username, String roleVal) {
        this.userID = userID;
        this.fullName = fullName;
        this.username = username;
        this.roleVal = roleVal;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    public Double getTime() {
        return time;
    }

    public void SetTime(Double time) {
        this.time = time;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Button getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(Button placeholder) {
        this.placeholder = placeholder;
    }

}
