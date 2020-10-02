package com.gk.jaikisaan.controllers;

import com.gk.jaikisaan.converters.AccountDetailsConverter;
import com.gk.jaikisaan.models.account.AccountDetails;
import com.gk.jaikisaan.services.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/login")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private final LoginService loginService;
    @Autowired
    private AccountDetailsConverter accountDetailsConverter;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }
    /*
    This api used to create account in organization. Stored against username
    input parameter  :: @accountDetails  --> AccountDetails Object
    output           :: Created Status
    */
    @PostMapping("/createaccount")
    public ResponseEntity<String> createAccount(@RequestBody AccountDetails accountDetails) {
        LOGGER.info("create account service called ");

        if (accountDetails.getUsertype().equalsIgnoreCase("admin") || accountDetails.getUsertype().equalsIgnoreCase("farmer") || accountDetails.getUsertype().equalsIgnoreCase("vendor")) {
            AccountDetails _accountDetail = accountDetailsConverter.accountDetailRequestConverter(accountDetails);
            ResponseEntity<String> accountCreateStatus = loginService.createAccount(_accountDetail);
            return accountCreateStatus;
        } else
            return new ResponseEntity<>("Please choose proper user type", HttpStatus.NOT_ACCEPTABLE);
    }
    /*
        This api used to delete account from organization.
        input parameter  :: @username   --> username of account
        output           :: deleted Status
    */
    @DeleteMapping(value = {"/deleteaccount"})
    public ResponseEntity<String> deleteAccount(@RequestParam("username") String username) {

        if (username != null) {
            ResponseEntity<String> accountCreateStatus = loginService.deleteAccount(username);
            return accountCreateStatus;
        } else
            return new ResponseEntity<>("Please choose proper user type", HttpStatus.NOT_ACCEPTABLE);
    }

    /*
    This api used to update whole account details in organization. Stored against username
    input parameter  :: @accountDetails  --> AccountDetails Object
    output           :: updated Status
    */
    @PutMapping("/updateaccount")
    public ResponseEntity<String> updateAccount(@RequestBody AccountDetails accountDetails) {
        ResponseEntity<String> updateAccountStatus = null;

            if (accountDetails.getUsername() != null)
                updateAccountStatus = loginService.updateAccount(accountDetails);
            else
                updateAccountStatus = new ResponseEntity<>("Please check request. User name is empty", HttpStatus.NO_CONTENT);
            return updateAccountStatus;

        }

    /*
     This api used to delete account from organization.
     input parameter  :: @username   --> username of account ,@password  -->new password
     output           :: updated Status
   */
    @PutMapping("/changepassword")
    public ResponseEntity<String> changePassword(@RequestParam String username,@RequestParam String password) {
        ResponseEntity<String> updateAccountStatus = null;
        if (username != null) {
            updateAccountStatus=loginService.changePassword(username,password);
            return updateAccountStatus;
        } else {
            return new ResponseEntity<>(" Username is not entered, Please try again ",HttpStatus.BAD_REQUEST);
        }

    }

    /*
     This api used to verify all pending accounts account from organization.
     input parameter  :: @username   --> username of account only for admins
     output           :: verified Status
   */
    @PutMapping("/verifyallaccounts")
    public ResponseEntity<String> verifyAllAccount(@RequestParam String username){
        ResponseEntity<String> verifyAllAccountStatus=loginService.verifyAllAccount(username);
    return verifyAllAccountStatus;
    }
    /*
      This api used to list down verified/unverified accounts account from organization.
      input parameter  :: @verifiedAccounts      --> it's optional, without this parameter will give only verified accounts
      output           :: verified Status
       */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String,String>>> getUserList(@RequestParam Optional<Boolean> verifiedAccounts){
       if(verifiedAccounts.isPresent())
        return loginService.userslist(verifiedAccounts.get());
       else
        return loginService.userslist(true);
    }
    @GetMapping("/getmessage")
    public ResponseEntity<String> getmessage(){
            return new ResponseEntity<>("gopi",HttpStatus.OK);
    }
}

