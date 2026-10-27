package app.controller;

import app.dto.user.ChangePasswordDTORequest;
import app.dto.user.UserDTOResponse;
import app.dto.user.UserLoginDTORequest;
import app.dto.user.UserLoginDTOResponse;
import app.enums.UserRole;
import app.exceptions.SecurityValidationException;
import app.security.TokenSecurity;
import app.service.SecurityService;
import app.utils.UserValidator;
import app.utils.Utils;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class SecurityHandler {

    private final TokenSecurity tokenSecurity = new TokenSecurity();
    private final SecurityService securityService;
    private final String issuer;
    private final String secretKey;
    private final long tokenExpireMillis;

    public SecurityHandler(SecurityService securityService) {
        this.securityService = securityService;
        this.issuer = Utils.getConfig("ISSUER");
        this.secretKey = Utils.getConfig("SECRET_KEY");
        this.tokenExpireMillis = Long.parseLong(Utils.getConfig("TOKEN_EXPIRE_TIME"));
    }

    public void login(Context ctx) {
        UserLoginDTORequest request = ctx.bodyValidator(UserLoginDTORequest.class)
                .check(r -> r.email() != null && !r.email().isBlank(), "Email is required")
                .check(r -> r.password() != null && !r.password().isBlank(), "Password is required")
                .get();

        UserDTOResponse user = securityService.login(request);

        String token = createToken(user);
        ctx.status(HttpStatus.OK).json(new UserLoginDTOResponse(token, user));
    }

    public void changePassword(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        ChangePasswordDTORequest dto = ctx.bodyValidator(ChangePasswordDTORequest.class)
                .check(u -> u.password() != null && !u.password().isBlank(), "Current password is required")
                .check(u -> !u.newPassword().equals(u.password()), "New password must be different from the current password")
                .check(u -> UserValidator.passwordsMustMatch(u.newPassword(), u.newPasswordCheck()), "Passwords do not match")
                .check(u -> UserValidator.isPasswordLongEnough(u.newPassword()), "Password must be at least 8 characters long")
                .check(u -> UserValidator.passwordMustContainNumber(u.newPassword()), "Password must contain a number")
                .check(u -> UserValidator.shouldRejectPasswordWithoutSpecialCharacter(u.newPassword()), "Password must contain a special character")
                .get();
        securityService.changePassword(currentUser.id(), dto);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void healthCheck(@NotNull Context ctx) {
        ctx.status(200).json("{\"msg\": \"API is up and running\"}");
    }

    public void authenticate(Context ctx) {
        if (ctx.method().toString().equals("OPTIONS")) {
            ctx.status(200);
            return;
        }
        if (isStaticFile(ctx)) return;
        if (isEndpointOpenForAnyone(getAllowedRoles(ctx))) return;

        String token = getUsersToken(ctx);
        ctx.attribute("user", tokenSecurity.verifyAndGetUser(token, issuer, secretKey));
    }

    public void authorize(Context ctx) {
        if (isStaticFile(ctx)) return;

        Set<UserRole> allowedRoles = getAllowedRoles(ctx);
        if (isEndpointOpenForAnyone(allowedRoles)) return;

        UserDTOResponse user = ctx.attribute("user");
        if (user == null) {
            throw new SecurityValidationException(401, "Not authenticated");
        }
        if (!allowedRoles.contains(user.userRole())) {
            throw new SecurityValidationException(403, "You do not have access to this resource");
        }
    }

    private String createToken(UserDTOResponse user) {
        return tokenSecurity.createToken(user, issuer, tokenExpireMillis, secretKey);
    }

    private static String getUsersToken(Context ctx) {
        String header = ctx.header("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new SecurityValidationException(401, "Missing Authorization");
        }
        String token = header.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            throw new SecurityValidationException(401, "Missing Authorization");
        }
        return token;
    }

    private static Set<UserRole> getAllowedRoles(Context ctx) {
        return ctx.routeRoles().stream()
                .filter(UserRole.class::isInstance)
                .map(UserRole.class::cast)
                .collect(Collectors.toSet());
    }

    private static boolean isEndpointOpenForAnyone(Set<UserRole> allowedRoles) {
        return allowedRoles.contains(UserRole.ANYONE);
    }

    private static boolean isStaticFile(Context ctx) {
        return ctx.path().startsWith("/images/");
    }
}
