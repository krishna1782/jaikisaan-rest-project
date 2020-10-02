package com.gk.jaikisaan.models.account;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accountdeatils")
public class AccountDetails {
    @Id
    private String id;

    private String username;
    private String password;
    private String usertype;
    private String adminkey;
    private UserDetails userDetails;
    private boolean isAccountVerified;

    public AccountDetails() {
    }

    //Basic Account Details
    public AccountDetails(String username, String usertype) {
        this.username = username;
        this.usertype = usertype;
    }


    //Basic Account Details with usertype
    public AccountDetails(String username, String password, String usertype) {
        this.username = username;
        this.password = password;
        this.usertype = usertype;
    }

    // admin account details constructor
    public AccountDetails(String username, String password, String usertype, String adminkey, UserDetails userDetails, boolean isAccountVerified) {
        this.username = username;
        this.password = password;
        this.usertype = usertype;
        this.adminkey = adminkey;
        this.userDetails = userDetails;
        this.isAccountVerified = isAccountVerified;
    }

    // farmer/vendor account details constructor
    public AccountDetails(String username, String password, String usertype, UserDetails userDetails, boolean isAccountVerified) {
        this.username = username;
        this.password = password;
        this.usertype = usertype;
        this.userDetails = userDetails;
        this.isAccountVerified = isAccountVerified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getAdminkey() {
        return adminkey;
    }

    public void setAdminkey(String adminkey) {
        this.adminkey = adminkey;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public boolean isAccountVerified() {
        return isAccountVerified;
    }

    public void setAccountVerified(boolean accountVerified) {
        isAccountVerified = accountVerified;
    }
}
