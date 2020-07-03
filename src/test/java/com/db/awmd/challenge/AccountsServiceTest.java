package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferBalanceRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;
  @Autowired
  private AccountsRepositoryInMemory accountsRepositoryInMemory;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }
  @Test
  public void transferBalancePositiveCase() throws Exception {
    Account account1 = new Account("1001", new BigDecimal(50000));
    Account account2 = new Account("2002", new BigDecimal(2000));
    TransferBalanceRequest transferBalanceRequest =
            new TransferBalanceRequest(
                    account1.getAccountId(),
                    account2.getAccountId(),
                    new BigDecimal(3000)
            );
    accountsRepositoryInMemory.sendMoney(transferBalanceRequest);
    assertThat(accountsService.getAccountsRepository().getAccount(account1.getAccountId())
            .getBalance())
            .isEqualTo(new BigDecimal(47000));
    assertThat(accountsService.getAccountsRepository().getAccount(account2.getAccountId())
            .getBalance())
            .isEqualTo(new BigDecimal(5000));
  }
}
