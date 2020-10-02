package com.gk.jaikisaan.services.impl;

import com.gk.jaikisaan.converters.AccountDetailsConverter;
import com.gk.jaikisaan.models.account.AccountDetails;
import com.gk.jaikisaan.services.LoginService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.internal.connection.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class LoginSericeImpl implements LoginService {

    private final static Logger LOGGER = LoggerFactory.getLogger(LoginSericeImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AccountDetailsConverter accountDetailsConverter;
    @Override
    public ResponseEntity<List<Map<String,String>>> userslist(boolean isVerified){

        try {
            Query query=new Query();
            if(isVerified){
                query.addCriteria(Criteria.where("isAccountVerified").is(true));
                query.fields().include("username").include("usertype").exclude("_id");
            }
            else{
                query.addCriteria(Criteria.where("isAccountVerified").is(false));
                query.fields().include("username").include("usertype").exclude("_id");
            }
            System.out.println("Query "+query);
            List<AccountDetails> dbResponse=mongoTemplate.find(query,AccountDetails.class);
            List<Map<String,String>> userNamesList=new ArrayList<Map<String,String>>();
            dbResponse.forEach(k->{
                Map<String ,String> user=new HashMap<>();
                user.put("username",k.getUsername());
                user.put("usertype",k.getUsertype());
                userNamesList.add(user);
            });
            return new ResponseEntity<>(userNamesList,HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public ResponseEntity<String> createAccount(AccountDetails accountDetail) {

        try {
            LOGGER.info("checking with existing accounts");
        // Getting list of usernames
            Query query = new Query();
            query.addCriteria(Criteria.where("username").is(accountDetail.getUsername()));
            boolean existOrNot=(mongoTemplate.find(query,AccountDetails.class).stream().count()) > 0;
            System.out.println("exists or not "+existOrNot);

           if (existOrNot) {
                LOGGER.info("account already exists");

                return new ResponseEntity<>(" "+accountDetail.getUsertype()+" Account already exists with this username "+accountDetail.getUsername()+". Please visit login page or wait till account get verified", HttpStatus.OK);
            } else {
                LOGGER.info("account submitted");
                //creating account
                mongoTemplate.save(accountDetail);
                return new ResponseEntity<>("your "+accountDetail.getUsertype().toUpperCase()+" account submitted with username " + accountDetail.getUsername() + ".Account Will get approved soon. Please Check email/message for approval status", HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("something went wrong please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<String> deleteAccount(String userName){
      try{
          Query query=new Query();
          query.addCriteria(Criteria.where("username").is(userName));
          AccountDetails _accountDetails=new AccountDetails();

          boolean existsOrNot=(mongoTemplate.find(query,AccountDetails.class).stream().count()) >0;
            if(existsOrNot) {
                DeleteResult deleteResult = mongoTemplate.remove(query, AccountDetails.class);
                System.out.println("out "+deleteResult.getDeletedCount()+"out2 "+deleteResult.toString());
                if(deleteResult.getDeletedCount()>0)
                    return new ResponseEntity<>(userName+" Account deleted succefully. ",HttpStatus.OK);
                else
                    return new ResponseEntity<>("Unable to delete "+userName+".  Please try again. ",HttpStatus.NOT_FOUND);
            }
          //mongoTemplate.findAndRemove(query,AccountDetails.class);}
            else{
                return new ResponseEntity<>(userName+" Username inCorrect Please check  and try again ",HttpStatus.NOT_FOUND);
            }

      }
      catch(Exception e){
          return new ResponseEntity<>(userName+" Account not there. ",HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<String> updateAccount(AccountDetails newaccountDetail) {

        try {
                Query findQuery=new Query();
                findQuery.addCriteria(Criteria.where("username").is(newaccountDetail.getUsername()));
                AccountDetails dbs_AccountDetails=mongoTemplate.findOne(findQuery,AccountDetails.class);
                if(dbs_AccountDetails != null){
                    accountDetailsConverter.accountDetailRequestConverter(dbs_AccountDetails,newaccountDetail);
                    if(dbs_AccountDetails!=null)
                        mongoTemplate.save(dbs_AccountDetails);
                    return new ResponseEntity<>(""+dbs_AccountDetails.getUsername()+" information updated successfully", HttpStatus.OK);
                }
                else
                    return new ResponseEntity<>(newaccountDetail.getUsername()+" username not present, Please enter proper details", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<String> changePassword(String username,String password){
        if(username != null){
            Query findFieldQuery=new Query();
            findFieldQuery.addCriteria(Criteria.where("username").is(username));
            Update update=new Update();
            update.set("password",password);
            UpdateResult updateResult=mongoTemplate.updateFirst(findFieldQuery,update,AccountDetails.class);
            if(updateResult.getModifiedCount() == 1)
                return new ResponseEntity<>("password updated successfully",HttpStatus.OK);
            else if(updateResult.getModifiedCount() == 0)
                return new ResponseEntity<>(username+" is not present ,Please enter correct details",HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>("Username is not entered, Please try again",HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<>(" Username is not entered, Please try again ",HttpStatus.BAD_REQUEST);

    }

    @Override
    public ResponseEntity<String> verifyAllAccount(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        AccountDetails accountDetails = mongoTemplate.findOne(query, AccountDetails.class);
        if(accountDetails == null)
            return new ResponseEntity<>("Account not present", HttpStatus.NOT_FOUND);
        else if ((accountDetails!=null) && (accountDetails.getUsertype().equalsIgnoreCase("admin")) && (accountDetails.isAccountVerified()) ) {
            Query searchQuery = new Query();
            searchQuery.addCriteria(Criteria.where("isAccountVerified").is(false).orOperator(Criteria.where("usertype").is("farmer"),Criteria.where("usertype").is("vendor")));
            Update updateQuery = new Update();
            updateQuery.set("isAccountVerified", true);
             System.out.println("search query "+searchQuery+"\n update query ::"+updateQuery);
            UpdateResult result = mongoTemplate.updateMulti(searchQuery, updateQuery, AccountDetails.class);
            if (result.getMatchedCount() > 0)
                return new ResponseEntity<>("All "+result.getMatchedCount()+" got accounts verified", HttpStatus.OK);
            else if (result.getMatchedCount() == 0)
                return new ResponseEntity<>("all Accounts got verified already . nothing pending", HttpStatus.OK);
            else
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
         }
        else {
            return new ResponseEntity<>("Unauthorised access to perform action", HttpStatus.UNAUTHORIZED);
        }
    }

}
