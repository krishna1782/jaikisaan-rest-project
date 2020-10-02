package com.gk.jaikisaan.services;

import com.gk.jaikisaan.models.account.AccountDetails;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface LoginService {

    ResponseEntity<String> createAccount(AccountDetails accountDetails);
    ResponseEntity<String> deleteAccount(String userName);
    ResponseEntity<String> updateAccount(AccountDetails accountDetails);
    ResponseEntity<String> changePassword(String username, String password);
    ResponseEntity<String> verifyAllAccount(String username);
    ResponseEntity<List<Map<String,String>>> userslist(boolean isVerified);
}
