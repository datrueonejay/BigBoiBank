package com.bank.databasehelper;

import com.bank.database.DatabaseInserter;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.AccountTypesContains;
import com.bank.generics.RolesContains;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class DatabaseInsertHelper extends DatabaseInserter {
    
  /**
   * Insert a new account into the Account table.
   * @param name the name of the account.
   * @param balance the balance currently in account.
   * @param typeId the id of the type of the account.
   * @return accountId of inserted account, -1 otherwise
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static int insertAccount(String name, BigDecimal balance, int typeId) throws 
      ConnectionFailedException {
    // gets the the id's of the types of accounts in the database
    List<Integer> accountTypeIds = DatabaseSelectHelper.getAccountTypesIds();
    // ensure the name, balance, and typeId are valid
    if (name != null && name.length() > 0 && balance != null 
        && balance.compareTo(BigDecimal.ZERO) >= 0 && accountTypeIds.contains((Integer) typeId)) {
      // set the balance to have two decimal places
      balance = balance.setScale(2, BigDecimal.ROUND_HALF_UP);
      // Connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // try to add the account to the database if you successfully connected to the database
      if (connection != null) {
        // Create new Account and get it's id
        int id = DatabaseInserter.insertAccount(name, balance, typeId, connection);
        try {  
          // try to close the connection to the database
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
        // return the id of the inserted account if successful
        return id;
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    // return -1 if the given parameters were not valid
    } else {
      return -1;
    }
  }
  
  /**
   * Insert an accountType into the accountType table.
   * @param name The name of the type of account. name must be either chequing, saving, or tfsa 
   *        (case insensitive) or it will not be added.
   * @param interestRate The interest rate for this type of account. 0 <= interestRate < 1 or it 
   *        will not be added.
   * @return True if successful, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean insertAccountType(String name, BigDecimal interestRate) throws 
      ConnectionFailedException {
    // set the the account was added as false by default
    boolean added = false;
    // ensure that the interestRate and name are valid
    if (interestRate.compareTo(BigDecimal.ONE) < 0 && interestRate.compareTo(BigDecimal.ZERO) >= 0 
        && AccountTypesContains.contains(name)) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection exists
      if (connection != null) {
        // check if the account type is already in the database
        List<Integer> accountIds = DatabaseSelectHelper.getAccountTypesIds();
        // variable to check if this account name is unique
        boolean unique = true;
        // loop through each account type and if the Account exists, set that it is not unique
        for (Integer id : accountIds) {
          if (DatabaseSelectHelper.getRole(id).equals(name.toUpperCase())) {
            unique = false;
          }
        }
        // try to add a new account type to the database if its not there, seeing if it worked
        if (unique) {
          added = DatabaseInserter.insertAccountType(name.toUpperCase(), interestRate, connection);
        }
        try {
          // try to close the connection 
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    } 
    return added;
  }
  
  /**
   * Insert a new user into the Users table, if all given info is valid.
   * @param name The name of the user.
   * @param age The age of the user.
   * @param address The address of the user. 
   * @param roleId The id of the role of the user.
   * @param password The password of the user.
   * @return The id of the user if added successfully and -1 otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static int insertNewUser(String name, int age, String address, int roleId, String password)
       throws ConnectionFailedException { 
    // set the default id to return as -1
    int id = -1;
    // get the id's of the available roles in the database
    List<Integer> roleIds = DatabaseSelectHelper.getRoles();
    // ensure the name, age, address, roleId, and password are all valid
    if (name != null && name.length() > 0 && age > 0 && address != null && address.length() <= 100 
        && roleIds.contains(roleId) && password != null) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection exists
      if (connection != null) {
        // insert the user and get the Id
        id = DatabaseInserter.insertNewUser(name, age, address, roleId, password, connection);
        try {
          // try to close the connection
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    }
    // return the id of the added user or -1 if not added 
    return id;
  }
  
  /**
   * Insert a Role into the Roles table.
   * @param role The name of the type of Role. name must be either admin, teller, or customer (case
   *        insensitive) or it will not be added.
   * @return True if successful, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean insertRole(String role) throws ConnectionFailedException {
    // set that the role was inserted as false for default
    boolean added = false;
    // ensure the role is in the enum
    if (RolesContains.contains(role)) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection exists
      if (connection != null) {
        // check if the role is already in the database
        List<Integer> roleIds = DatabaseSelectHelper.getAccountTypesIds();
        // variable to check if this account name is unique
        boolean unique = true;
        // loop through each roleId and if the Account exists, set that it is not unique
        for (Integer id : roleIds) {
          if (DatabaseSelectHelper.getRole(id).equals(role.toUpperCase())) {
            unique = false;
          }
        }
        // try to add a new role to the database if its not there, seeing if it worked
        if (unique) {
          added = DatabaseInserter.insertRole(role.toUpperCase(), connection);
        }
        try {
          // try to close the connection
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    } 
    // return whether it was added successfully
    return added;
  }
  
  /**
   * Insert an accountType into the accountType table.
   * @param userId The id of a User in the database.
   * @param accountId The id of an Account in the database.
   * @return True if successful, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean insertUserAccount(int userId, int accountId) throws 
      ConnectionFailedException {
    // set that the userAccount was added as false by default
    boolean added = false;
    // check that the userId and accountId exist in the table
    try {
      // ensure the user Id exists
      DatabaseSelectHelper.getUserDetails(userId);
      // ensure the account Id exists
      DatabaseSelectHelper.getAccountDetails(accountId);
      // check if the user already does not have this account added
      if (!DatabaseSelectHelper.getAccountIds(userId).contains(accountId)) {
        // connect to the database
        Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
        // ensure the connection was successful
        if (connection != null) {
          // add a new userAccount to the database, seeing if it worked
          added = DatabaseInserter.insertUserAccount(userId, accountId, connection);
          connection.close();
        } else {
          // throw Connection FailedException if the database was not connected to
          throw new ConnectionFailedException("Unable to connect to the database.");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // return if the account was added properly
    return added;
  }
  
}
