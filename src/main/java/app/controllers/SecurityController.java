package app.controllers;

import app.daos.ISecurityDAO;
import app.daos.SecurityDAO;
import app.dtos.UserDTO;
import app.entities.Role;
import app.entities.User;
import app.exceptions.APIException;
import app.exceptions.PasswordValidationException;
import app.exceptions.TokenCreationException;
import app.exceptions.TokenValidationException;
import app.security.ITokenSecurity;
import app.security.TokenSecurity;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController {

    private static SecurityController instance;
    private final ITokenSecurity tokenSecurity;
    private final ISecurityDAO securityDAO;

    private SecurityController(EntityManagerFactory emf) {
        this.tokenSecurity = new TokenSecurity();
        this.securityDAO = SecurityDAO.getInstance(emf);
    }

    public static SecurityController getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new SecurityController(emf);
        }

        return instance;
    }

    @Override
    public void login(Context ctx) {
        try {
            UserDTO userDTO = ctx.bodyValidator(UserDTO.class).get();

            User verifiedUser = securityDAO.getVerifiedUser(userDTO.getUsername(), userDTO.getPassword());
            UserDTO verifiedUserDTO = new UserDTO(
                    verifiedUser.getUsername(),
                    verifiedUser.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet())
            );

            String token = createToken(verifiedUserDTO);

            ObjectNode responseJson = JsonNodeFactory.instance.objectNode();

            responseJson.put("username", verifiedUserDTO.getUsername());
            responseJson.put("token", token);

            ctx.status(HttpStatus.OK);
            ctx.json(responseJson);
        } catch (ValidationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getErrors().toString());
        } catch (EntityNotFoundException e) {
            throw new APIException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (PasswordValidationException e) {
            throw new APIException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Override
    public void register(Context ctx) {
        try {
            UserDTO userDTO = ctx.bodyValidator(UserDTO.class).get();

            User createdUser = securityDAO.createUser(userDTO.getUsername(), userDTO.getPassword());
            UserDTO createdUserDTO = new UserDTO(
                    createdUser.getUsername(),
                    createdUser.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet())
            );

            String token = createToken(createdUserDTO);

            ObjectNode responseJson = JsonNodeFactory.instance.objectNode();

            responseJson.put("username", createdUserDTO.getUsername());
            responseJson.put("token", token);

            ctx.status(HttpStatus.CREATED);
            ctx.json(responseJson);
        } catch (ValidationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getErrors().toString());
        } catch (EntityExistsException e) {
            throw new APIException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Override
    public UserDTO authenticate(Context ctx) {
        String authorization = ctx.header("Authorization");

        if (authorization == null) {
            throw new APIException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
        }

        String[] authorizationParts = authorization.split(" ");

        if (authorizationParts.length != 2 || !authorizationParts[0].equalsIgnoreCase("Bearer")) {
            throw new APIException(HttpStatus.UNAUTHORIZED, "Invalid Authorization header");
        }

        String token = authorizationParts[1];

        return validateToken(token);
    }

    @Override
    public String createToken(UserDTO userDTO) {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                Properties properties = getConfigProperties();

                ISSUER = properties.getProperty("ISSUER");
                TOKEN_EXPIRE_TIME = properties.getProperty("TOKEN_EXPIRE_TIME");
                SECRET_KEY = properties.getProperty("SECRET_KEY");
            }

            return tokenSecurity.createToken(userDTO, ISSUER, Long.parseLong(TOKEN_EXPIRE_TIME), SECRET_KEY);
        } catch (IOException | TokenCreationException e) {
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e.getCause());
        }
    }

    @Override
    public UserDTO validateToken(String token) {
        try {
            boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
            String SECRET_KEY = IS_DEPLOYED ? System.getenv("SECRET_KEY") : getConfigProperties().getProperty("SECRET_KEY");

            return tokenSecurity.validateToken(token, SECRET_KEY);
        } catch (TokenValidationException e) {
            throw new APIException(HttpStatus.UNAUTHORIZED, e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e.getCause());
        }
    }

    private Properties getConfigProperties() throws IOException {
        try (InputStream inputStream = SecurityController.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new IOException("Property file 'config.properties' not found in the classpath");
            }

            Properties properties = new Properties();
            properties.load(inputStream);

            return properties;
        }
    }
}
