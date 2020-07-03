package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferBalanceRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

@Override
public void sendMoney(TransferBalanceRequest transferBalanceRequest) {
	String fromAccountNumber = transferBalanceRequest.getFromAccountNumber();
    String toAccountNumber = transferBalanceRequest.getToAccountNumber();
    BigDecimal amount = transferBalanceRequest.getAmount();
    System.out.println(fromAccountNumber+":"+toAccountNumber+":"+amount);
    Account fromAccount = accounts.get(fromAccountNumber);
    Account toAccount = accounts.get(toAccountNumber);
    if(fromAccount.getBalance().compareTo(BigDecimal.ONE) == 1
            && fromAccount.getBalance().compareTo(amount) == 1){
    	if(amount.intValue()>1){
    		fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
	        if(fromAccount.getBalance().intValue() >1){
	        	toAccount.setBalance(toAccount.getBalance().add(amount));
	        }
    	}
    }
}

}
