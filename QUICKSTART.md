# Quick Start Guide

## 1. Install Java 21

**macOS (using Homebrew):**
```bash
brew install openjdk@21
```

**Verify installation:**
```bash
java -version
```

## 2. Build the Project

```bash
cd /Users/phlusko/Projects/email_verify
./mvnw clean install
```

## 3. Configure Email (Choose One)

### Option A: Test with Mailhog (Easiest)
```bash
# Using Docker (if you have it installed)
docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog

# Then view emails at http://localhost:8025
# The application is already configured to use localhost:1025
```

### Option B: Use Real Email (Gmail)
Edit `src/main/resources/application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

See: https://support.google.com/accounts/answer/185833 for app password

## 4. Run the Application

```bash
cd /Users/phlusko/Projects/email_verify
./mvnw spring-boot:run
```

## 5. Test the App

1. Open browser: http://localhost:8080
2. Enter your email
3. Click "Send Verification Code"
4. View email (in Mailhog UI or your email client)
5. Copy the 6-digit code
6. Enter the code on the verification form
7. See success message!

## Project Files Created

```
✅ Controller:           src/main/java/com/p4u1/email_verify/controller/VerificationController.java
✅ Service:             src/main/java/com/p4u1/email_verify/service/EmailService.java
✅ Model:               src/main/java/com/p4u1/email_verify/model/VerificationRequest.java
✅ Email Form:          src/main/resources/templates/email-form.html
✅ Verify Code Form:    src/main/resources/templates/verify-code.html
✅ Success Page:        src/main/resources/templates/success.html
✅ Dependencies:        pom.xml (updated)
✅ Configuration:       src/main/resources/application.properties (updated)
```

## Application URLs

- **Home:** http://localhost:8080/
- **Send Code:** http://localhost:8080/send-code (POST)
- **Verify Code:** http://localhost:8080/verify-code (POST)

## Key Features

✅ Send verification code to email
✅ Random 6-digit code generation
✅ Code verification on form submission
✅ Success notification
✅ In-memory code storage (no database)
✅ Modern, responsive UI
✅ Error handling

Enjoy! 🎉

