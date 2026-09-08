# PDF Pay Download - Razorpay Test Mode

This is the complete project: PDF upload, seller-generated customer link, customer PDF/price page, Razorpay Test Mode checkout, server-side signature verification, and one-time temporary download token.

## Configure Razorpay
Set these in `src/main/resources/application.properties`:

```properties
razorpay.key.id=rzp_test_...
razorpay.key.secret=...
```

The secret must remain on the server.

## Run
```bash
mvn clean spring-boot:run
```

Seller: `http://localhost:8080/`

Customer: use the generated `/?pdf=ID` link.

## Payment flow
1. Spring Boot creates the Razorpay order.
2. Razorpay Checkout opens in the browser.
3. Browser sends payment ID/order ID/signature to Spring Boot.
4. Spring Boot verifies the signature.
5. Only after verification is a temporary download token issued.

This is Test Mode. Before production, add admin authentication, HTTPS, environment variables for secrets, webhook verification, persistent download-token storage, rate limits and stronger file/security controls.

## Community accounts and Google sign-in

TechCertHub now includes local email/password accounts, a community Q&A area, and a local/offline profanity filter. Signup does not require a paid moderation service. The filter is intentionally conservative and should be expanded as needed.

### Google sign-in (optional)
Google sign-in is free to use but requires creating a Google OAuth 2.0 Web application in Google Cloud. Set:

`GOOGLE_OAUTH_ENABLED=true`

`GOOGLE_CLIENT_ID=...`

`GOOGLE_CLIENT_SECRET=...`

For local development add this redirect URI to the Google OAuth client:

`http://localhost:8080/login/oauth2/code/google`

For production use your HTTPS domain with the same `/login/oauth2/code/google` path.

### Community moderation
Questions and answers are stored in H2 by default. The server checks titles, questions and answers against an offline blocklist before saving. No paid API is used. Admin moderation can be extended later with hide/report controls.
