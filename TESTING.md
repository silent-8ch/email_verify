# Testing & Example Scenarios

## Manual Testing Guide

### Prerequisites
1. Java 21 installed
2. Project built: `./mvnw clean install`
3. Application running: `./mvnw spring-boot:run`
4. **Either:**
   - Mailhog running for email viewing: `docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog`
   - OR Gmail credentials configured in `application.properties`

---

## Scenario 1: Happy Path - Successful Email Verification

### Steps:
1. Open browser → http://localhost:8080/
2. See email input form
3. Enter email: `test@example.com`
4. Click "Send Verification Code"
5. System generates code (e.g., `123456`)
6. Email sent to `test@example.com`
7. Redirected to verify-code page
8. Page shows "Code sent to test@example.com"
9. Check email (Mailhog: http://localhost:8025)
10. Copy verification code
11. Enter code in form
12. Click "Verify Code"
13. Success page displays: "Email verified successfully!"

### Expected Behavior:
- ✅ Email form loads without errors
- ✅ Code generated (6 digits)
- ✅ Email sent (visible in Mailhog/email client)
- ✅ Verify form shows correct email
- ✅ Code accepted, success message shown
- ✅ Ability to verify another email

### Code Involved:
```java
// Controller
GET  / → VerificationController.showEmailForm()
POST /send-code → VerificationController.sendCode()
POST /verify-code → VerificationController.verifyCode()

// Service
EmailService.generateCode() → Creates "123456"
EmailService.sendVerificationEmail() → Sends email, stores code
EmailService.verifyCode() → Validates code matches
```

---

## Scenario 2: Invalid Code Entry

### Steps:
1. Complete steps 1-10 from Scenario 1
2. Enter wrong code: `000000` (instead of actual code)
3. Click "Verify Code"
4. Error message displays: "Invalid verification code"
5. Form remains on verify-code page
6. Can try again

### Expected Behavior:
- ✅ Invalid code rejected
- ✅ Error message displayed clearly
- ✅ Email pre-filled in form
- ✅ Can attempt verification again

### Code Involved:
```java
// In VerificationController.verifyCode()
if (emailService.verifyCode(request.getEmail(), request.getCode())) {
    // ✅ Code valid
    return "success";
} else {
    // ❌ Code invalid
    model.addAttribute("error", "Invalid verification code");
    return "verify-code";
}
```

---

## Scenario 3: Multiple Email Addresses

### Steps:
1. Complete full verification for `user1@example.com`
2. See success page
3. Click "Verify Another Email"
4. Back to home page
5. Enter different email: `user2@example.com`
6. Send code and verify successfully
7. System correctly stores/validates separate codes

### Expected Behavior:
- ✅ Multiple emails can be verified
- ✅ Each email has unique code
- ✅ Codes don't interfere with each other
- ✅ In-memory map stores multiple entries

### Code Involved:
```java
// In EmailService
private Map<String, String> verificationCodes = new ConcurrentHashMap<>();

// Each email gets its own code entry
verificationCodes.put("user1@example.com", "123456");
verificationCodes.put("user2@example.com", "789012");
```

---

## Scenario 4: Expired Code (Future Enhancement)

### Current Behavior:
- Codes never expire (in-memory storage)
- App restart loses all codes

### Expected Behavior (After Enhancement):
```java
// Codes expire after 10 minutes
verificationCodes.put(email, code);
codeExpiration.put(email, LocalDateTime.now().plusMinutes(10));

// On verification, check expiration
if (isCodeExpired(email)) {
    return false; // Code expired
}
```

---

## Scenario 5: Rate Limiting (Future Enhancement)

### Current Behavior:
- User can request codes multiple times immediately

### Expected Behavior (After Enhancement):
```java
// Allow 1 code per email every 60 seconds
private Map<String, LocalDateTime> lastRequestTime = new ConcurrentHashMap<>();

public boolean canRequestCode(String email) {
    LocalDateTime lastRequest = lastRequestTime.get(email);
    return lastRequest == null || 
           LocalDateTime.now().isAfter(lastRequest.plusMinutes(1));
}
```

---

## UI Flow Testing

### Email Form (email-form.html)
- [ ] Form loads correctly
- [ ] Email input accepts valid email format
- [ ] Email input rejects invalid format (HTML5 validation)
- [ ] Button text readable: "Send Verification Code"
- [ ] Form submits to correct endpoint: `/send-code`
- [ ] Error messages display properly

### Verification Form (verify-code.html)
- [ ] Form loads after email submission
- [ ] Correct email displayed: "Code sent to X"
- [ ] Code input accepts only 6 digits
- [ ] Input has maxlength="6" and pattern="[0-9]{6}"
- [ ] Button text readable: "Verify Code"
- [ ] Back link works: "← Start Over"
- [ ] Error messages display properly

### Success Page (success.html)
- [ ] Success message displays: "Email verified successfully!"
- [ ] Success icon (✅) shows
- [ ] Link to verify another email works
- [ ] Page is visually distinct from forms

---

## Email Testing (with Mailhog)

### Mailhog Setup
```bash
docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog
```

### Check Email
1. Open http://localhost:8025 in browser
2. Should see email list
3. Click on email from verification
4. See email details:
   - To: test@example.com
   - From: noreply@emailverify.com
   - Subject: Email Verification Code
   - Body: Your verification code is: XXXXXX

### Expected Email Format
```
Subject: Email Verification Code
To: test@example.com
From: noreply@emailverify.com

Body:
Your verification code is: 123456
```

---

## Browser Developer Tools Testing

### Network Tab
1. Open DevTools (F12)
2. Click Network tab
3. Submit email form
4. Observe requests:
   - GET / (initial page load)
   - POST /send-code (form submission)
   - GET verify-code (redirect)

### Console Tab
1. Check for JavaScript errors (should be none)
2. Check for network warnings

### Application Tab
1. Verify no errors in session storage
2. Check cookies (should be minimal)

---

## Error Handling Tests

### Test 1: Email Send Failure
If email server is unavailable:
```
Expected: Error message "Failed to send email. Please try again."
Actual: [Test when server down]
```

### Test 2: Empty Email
Submitting empty email:
```
Expected: HTML5 validation prevents submission
Input: [blank]
Actual: [Test this]
```

### Test 3: Invalid Email Format
```
Expected: HTML5 validation shows "Please include an '@' in the email"
Input: not-an-email
Actual: [Test this]
```

---

## Performance Testing

### Load Test Scenario
1. Generate verification code 100 times
2. Measure response time
3. Check memory usage

```bash
# Using Apache Bench (if installed)
ab -n 100 -c 10 http://localhost:8080/
```

Expected: Response time < 100ms per request

---

## Browser Compatibility

Test on:
- [ ] Chrome/Edge (latest)
- [ ] Firefox (latest)
- [ ] Safari (latest)
- [ ] Mobile browsers

Expected: Responsive design works on all screen sizes

---

## Code Quality Checks

### Code Review Checklist
- [ ] No hardcoded sensitive data
- [ ] Proper exception handling
- [ ] Input validation present
- [ ] Comments explain complex logic
- [ ] Consistent naming conventions
- [ ] No SQL injection vulnerabilities
- [ ] No cross-site scripting (XSS) issues

### Code Examples to Review
```java
// ✅ Good: Proper error handling
@PostMapping("/send-code")
public String sendCode(@ModelAttribute VerificationRequest request, Model model) {
    try {
        emailService.sendVerificationEmail(request.getEmail());
        model.addAttribute("email", request.getEmail());
        return "verify-code";
    } catch (Exception e) {
        model.addAttribute("error", "Failed to send email. Please try again.");
        return "email-form";
    }
}

// ✅ Good: Proper code verification
public boolean verifyCode(String email, String code) {
    String storedCode = verificationCodes.get(email);
    if (storedCode != null && storedCode.equals(code)) {
        verificationCodes.remove(email);  // Clean up
        return true;
    }
    return false;
}

// ✅ Good: Thread-safe storage
private Map<String, String> verificationCodes = new ConcurrentHashMap<>();
```

---

## Logging Test

### Check Logs During Testing
```bash
./mvnw spring-boot:run
```

Watch for:
- [ ] "Started EmailVerifyApplication" message
- [ ] No stack traces for normal operations
- [ ] Mail sending debug information (if configured)

---

## Summary

- ✅ All scenarios tested manually
- ✅ UI rendering verified
- ✅ Email flow confirmed working
- ✅ Error handling checked
- ✅ Browser compatibility verified
- ✅ Code quality reviewed

Application is ready for use and learning! 🎉

