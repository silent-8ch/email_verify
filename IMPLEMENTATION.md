# Email Verification App - Spring Boot Demo

A simple email verification application built with Spring Boot to demonstrate core concepts like controllers, services, dependency injection, mail handling, and templating.

## Project Structure

```
src/
├── main/
│   ├── java/com/p4u1/email_verify/
│   │   ├── EmailVerifyApplication.java     (Main application entry point)
│   │   ├── controller/
│   │   │   └── VerificationController.java (Handles HTTP requests)
│   │   ├── model/
│   │   │   └── VerificationRequest.java    (Data model)
│   │   └── service/
│   │       └── EmailService.java           (Business logic)
│   └── resources/
│       ├── application.properties          (Configuration)
│       └── templates/
│           ├── email-form.html             (Initial form)
│           ├── verify-code.html            (Code verification form)
│           └── success.html                (Success page)
└── test/
    └── EmailVerifyApplicationTests.java
```

## Features Implemented

✅ **Email Form** - Users enter their email address
✅ **Code Generation** - Random 6-digit verification codes
✅ **Email Sending** - Integration with Java Mail API
✅ **Code Verification** - Validates entered codes
✅ **Success Notification** - Displays success message
✅ **In-Memory Storage** - No database needed for demo
✅ **Responsive UI** - Modern, user-friendly interface

## Setup Instructions

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Installation

1. **Clone/Navigate to the project directory:**
   ```bash
   cd /Users/phlusko/Projects/email_verify
   ```

2. **Build the project:**
   ```bash
   ./mvnw clean install
   ```

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the app:**
   Open your browser and navigate to: `http://localhost:8080`

## Email Configuration

### For Development/Testing (Current Setup)
The application is configured to use localhost SMTP (port 1025). To test emails:

**Option 1: Use Mailhog (Recommended for testing)**
```bash
# Install Mailhog (requires Go)
# https://github.com/mailhog/MailHog

# Or use Docker
docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog

# View emails at http://localhost:8025
```

**Option 2: Console Output**
Modify `application.properties` to log emails to console:
```properties
spring.mail.host=localhost
spring.mail.port=1025
```

### For Production (Gmail Example)
Update `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

**Note:** For Gmail, use an [App Password](https://support.google.com/accounts/answer/185833), not your regular password.

## Application Flow

1. **Landing Page (`/`)** - Shows email form
2. **Send Code (`/send-code`)** - Generates random code, sends email, redirects to verification form
3. **Verify Code (`/verify-code`)** - Shows form to enter verification code
4. **Verify Code Submission (`/verify-code`)** - Validates code against stored value
5. **Success (`/success`)** - Shows success message, option to verify another email

## Key Spring Boot Concepts Learned

### 1. **@SpringBootApplication**
   - Main entry point for Spring Boot applications
   - Enables auto-configuration and component scanning

### 2. **@Controller**
   - Marks class as a web controller
   - Handles HTTP requests and returns views

### 3. **@PostMapping / @GetMapping**
   - Routes HTTP POST and GET requests to methods
   - `@PostMapping("/send-code")` handles form submissions
   - `@GetMapping("/")` handles page loads

### 4. **@Service**
   - Marks class as a service component
   - Contains business logic
   - Managed by Spring for dependency injection

### 5. **@Autowired**
   - Dependency injection annotation
   - Spring automatically injects dependencies
   - Example: `JavaMailSender mailSender`

### 6. **Model**
   - Used to pass data from controller to view template
   - `model.addAttribute("email", email);`
   - Data accessible in Thymeleaf templates

### 7. **Thymeleaf Templating**
   - Server-side template engine for Java
   - `th:action`, `th:text`, `th:if`, `th:value` for dynamic content
   - Similar to JSP but cleaner syntax

### 8. **JavaMailSender**
   - Spring's abstraction for sending emails
   - `SimpleMailMessage` for basic emails
   - `MimeMessageHelper` for complex emails

## Code Walkthrough

### EmailService - The Business Logic
```java
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    
    private Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    
    // Generate 6-digit code
    public String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
    
    // Send email with code
    public void sendVerificationEmail(String email) {
        String code = generateCode();
        verificationCodes.put(email, code);
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }
    
    // Verify code matches
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }
}
```

### VerificationController - The Web Layer
```java
@Controller
public class VerificationController {
    @Autowired
    private EmailService emailService;
    
    @GetMapping("/")
    public String showEmailForm(Model model) {
        model.addAttribute("verificationRequest", new VerificationRequest());
        return "email-form";  // Returns email-form.html
    }
    
    @PostMapping("/send-code")
    public String sendCode(@ModelAttribute VerificationRequest request, Model model) {
        emailService.sendVerificationEmail(request.getEmail());
        model.addAttribute("email", request.getEmail());
        return "verify-code";  // Returns verify-code.html
    }
}
```

## Extending the Application

### Add Database Persistence
Replace `Map<String, String>` with a JPA repository and entity:
```java
@Entity
public class VerificationCode {
    @Id
    @GeneratedValue
    private Long id;
    
    private String email;
    private String code;
    private LocalDateTime expiresAt;
    // getters/setters
}
```

### Add Email Templates
Use Thymeleaf templates for dynamic email content:
```java
MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
String htmlContent = templateEngine.process("email-template", context);
helper.setText(htmlContent, true);
```

### Add Expiration to Codes
Store timestamp and check if code has expired:
```java
private Map<String, LocalDateTime> codeTimestamps = new ConcurrentHashMap<>();

public boolean isCodeExpired(String email) {
    LocalDateTime timestamp = codeTimestamps.get(email);
    return timestamp == null || LocalDateTime.now().isAfter(timestamp.plusMinutes(10));
}
```

### Add Rate Limiting
Prevent users from requesting too many codes:
```java
private Map<String, LocalDateTime> lastRequestTime = new ConcurrentHashMap<>();

public boolean canRequestCode(String email) {
    LocalDateTime lastRequest = lastRequestTime.get(email);
    return lastRequest == null || LocalDateTime.now().isAfter(lastRequest.plusMinutes(1));
}
```

## Troubleshooting

### Issue: "Failed to send email"
- Check email configuration in `application.properties`
- Verify SMTP server is running (especially if using Mailhog)
- Check firewall/network settings

### Issue: Codes not being received
- Ensure you're using a real email address or Mailhog
- Check Mailhog UI at http://localhost:8025 if using Docker
- Check application logs for errors

### Issue: Build fails
- Ensure Java 21+ is installed: `java -version`
- Run `./mvnw clean install` to download dependencies

## Dependencies Used

- **spring-boot-starter-web** - Web and REST support
- **spring-boot-starter-thymeleaf** - Template engine
- **spring-boot-starter-mail** - Email support
- **spring-boot-starter-validation** - Input validation

## Learning Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring MVC Tutorial](https://spring.io/guides/gs/serving-web-content/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/)
- [Spring Mail Integration](https://spring.io/guides/gs/sending-email/)

## Next Steps

1. Add database support (JPA/Hibernate)
2. Add user authentication
3. Add email templates with HTML formatting
4. Implement code expiration
5. Add rate limiting
6. Add logging and monitoring
7. Deploy to cloud platform (Heroku, AWS, etc.)

Enjoy learning Spring Boot! 🚀

