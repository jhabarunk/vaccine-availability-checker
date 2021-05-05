# CoWIN vaccination slot availability for 18+ using Java

## Java executable JAR to check the available slots for Covid-19 Vaccination Centers for 18+ using CoWIN API in India and send mail alert when available

### Prerequisites software
    1. Maven 3.x.x
    2. Java 1.8

### Config
* Update mail.username in application.properties with gmail email id to be used to send mail to users 
* Update mail.password in application.properties with the gmail App password. Follow steps given in **Create & use App Passwords** section in [Sign in with App Passwords](#1)

### Steps to generate executable JAR
    1. mvn clean package

### Running the JAR
    1. java -jar ./target/vaccine-slot-checker-executable.jar <pincode> <date> <to_email_id> <next N days>
       N: Checker will check slots starting from input date till next N days
       Ex: java -jar ./target/vaccine-slot-checker-executable.jar 400076 15-05-2021 abc@gmail.com 7



### **Note**: Executable will check for vaccine slot every 15 minutes

[1]: https://support.google.com/mail/answer/185833?hl=en
