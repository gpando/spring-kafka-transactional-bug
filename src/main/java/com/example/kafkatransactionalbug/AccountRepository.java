package com.example.kafkatransactionalbug;

import org.springframework.data.jpa.repository.JpaRepository;
 
public interface AccountRepository extends JpaRepository<Account, Long> {

}
