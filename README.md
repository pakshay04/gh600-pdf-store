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
