# JavaBanker

A simple Java-based banking application that connects to an MS Access database (`.accdb`).

## Features
- Select and manage from three preset bank accounts  
- Deposit, withdrawal, and transfer operations  
- Transaction logs stored in the MS Access database  
- Graphical user interface built with Java Swing  
- Reads from and writes to the MS Access database

## Files
- `BankingApp.java` - Main source code
- `BankingDB.accdb` - Access database file
- `read.txt` - Instructions or notes

## Requirements
- Java JDK 8+
- MS Access driver installed (for `.accdb` connectivity)

## Run Instructions
For detailed steps to compile and run the application, please refer to `read.txt`.

## What I learned
- Designing a user-friendly Java Swing interface for banking operations.  
- Establishing reliable JDBC connections with MS Access to log transactions.  
- Handling concurrent account updates and ensuring data consistency during deposits, withdrawals, and transfers.  
- Implementing predefined account management with persistent transaction history.  
- Managing external dependencies and troubleshooting database connectivity issues.
