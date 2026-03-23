# Email Verify (Spring Boot Demo)

This is a simple Spring Boot project for learning email verification.

## What it does

- Shows a web form to enter an email address
- Sends a 6-digit verification code to that email
- Lets the user enter the code on a second page
- Shows a success message when the code matches

This is a demo app only. Verification codes are kept in memory and are not saved to a database.

## Tech used

- Java 21
- Spring Boot
- Thymeleaf
- Spring Mail
- Maven

## Run locally

```bash
./mvnw spring-boot:run
```

Then open:

- http://localhost:8080

## Email setup note

To send real emails, configure Gmail SMTP (or another provider) in `src/main/resources/application.properties`.
For local testing without real delivery, you can use MailHog.

