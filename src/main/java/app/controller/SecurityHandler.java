package app.controller;

import app.dto.user.UserDTOResponse;
import app.dto.user.UserLoginDTORequest;
import app.enums.UserRole;
import app.exceptions.NotAuthorizedException;
import app.exceptions.SecurityValidationException;
import app.exceptions.TokenVerificationException;
import app.security.TokenSecurity;
import app.service.SecurityService;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SecurityHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TokenSecurity tokenSecurity = new TokenSecurity();
    private final SecurityService securityService;

    public SecurityHandler(SecurityService securityService) {
        this.securityService = securityService;
    }

    //#region login
    public void login(Context ctx) {
        ObjectNode returnObject = objectMapper.createObjectNode(); // for sending json messages back to the client
        try {
            UserLoginDTORequest userLoginDTORequest = ctx.bodyAsClass(UserLoginDTORequest.class);

            UserDTOResponse userDTOResponse = securityService.login(userLoginDTORequest);

            if (userDTOResponse != null) {
                String token = createToken(userDTOResponse);

                ctx.status(HttpStatus.OK).json(returnObject
                        .put("token", token)
                        .put("id", userDTOResponse.id())
                        .put("name", userDTOResponse.name())
                        .put("email", userDTOResponse.email())
                        .put("phone", userDTOResponse.phone())
                        .put("userRole", userDTOResponse.userRole().toString()));
            } else {
                ctx.status(HttpStatus.UNAUTHORIZED);
            }


        } catch (EntityNotFoundException | ValidationException e) {
            ctx.status(401);
            System.out.println(e.getMessage());
            ctx.json(returnObject.put("msg", e.getMessage()));
        }
    }

    private String createToken(UserDTOResponse user) {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }
            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            log.error("Could not create token", e);
            throw new SecurityValidationException(500, "Could not create token");
        }
    }

    //#endregion

    //#region authentication
    public void authenticate(Context ctx) {
        // This is a preflight request => no need for authentication
        if (ctx.method().toString().equals("OPTIONS")) {
            ctx.status(200);
            return;
        }
        // If the endpoint is not protected with roles or is open to ANYONE role, then skip
        Set<UserRole> allowedRoles = ctx.routeRoles().stream()
                .filter(role -> role instanceof UserRole)
                .map(role -> (UserRole) role)
                .collect(Collectors.toSet());

        if (isEndpointOpenForAnyone(allowedRoles)) return;

        // If there is no token we do not allow entry
        UserDTOResponse verifiedTokenUser = validateAndGetUserFromToken(ctx);
        ctx.attribute("user", verifiedTokenUser); // -> ctx.attribute("user") in ApplicationConfig beforeMatched filter
    }



    private UserDTOResponse validateAndGetUserFromToken(Context ctx) {
        String token = getToken(ctx);
        UserDTOResponse verifiedTokenUser = verifyToken(token);
        if (verifiedTokenUser == null) {
            throw new UnauthorizedResponse("Invalid user or token"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }
        return verifiedTokenUser;
    }

    private static String getToken(Context ctx) {
        String header = ctx.header("Authorization");
        if (header == null) {
            throw new UnauthorizedResponse("Authorization header is missing"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }

        // If the Authorization Header was malformed, then no entry
        String token = header.split(" ")[1];
        if (token == null) {
            throw new UnauthorizedResponse("Authorization header is malformed"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }
        return token;
    }

    private UserDTOResponse verifyToken(String token) {
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET = IS_DEPLOYED ? System.getenv("SECRET_KEY") : Utils.getPropertyValue("SECRET_KEY", "config.properties");

        try {
            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                throw new NotAuthorizedException(403, "Token is not valid");
            }
        } catch (NotAuthorizedException | TokenVerificationException e) {
            log.error("Could not create token", e);
            throw new SecurityValidationException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }
    //#endregion

    //#region authorization
    public void authorize(Context ctx) {
        Set<UserRole> allowedRoles = ctx.routeRoles().stream()
                .filter(role -> role instanceof UserRole)
                .map(role -> (UserRole) role)
                .collect(Collectors.toSet());

        // 1. Check if the endpoint is open to all (either by not having any roles or having the ANYONE role set
        if (isEndpointOpenForAnyone(allowedRoles))return;
        // 2. Get user and ensure it is not null
        UserDTOResponse user = ctx.attribute("user");
        if (user == null) {
            throw new ForbiddenResponse("No user was added from the token");
        }
        // 3. See if any role matches
        if (!userHasAllowedRole(user, allowedRoles))
            throw new ForbiddenResponse("User was not authorized with roles: " + user.userRole() + ". Needed roles are: " + allowedRoles);
    }

    private static boolean userHasAllowedRole(UserDTOResponse user, Set<UserRole> allowedRoles) {
        return allowedRoles.contains(user.userRole());
    }

    private boolean isEndpointOpenForAnyone(Set<UserRole> userRoles) {
        // If the endpoint is not protected with any roles:
        return userRoles.contains(UserRole.ANYONE);
    }
    //#endregion
}
