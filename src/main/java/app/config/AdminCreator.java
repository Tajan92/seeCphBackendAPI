package app.config;

import app.dao.UserDAO;
import app.entities.users.Admin;
import app.enums.UserRole;
import app.utils.PasswordUtil;
import app.utils.Utils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public final class AdminCreator {

    public static void createAdminIfMissing(UserDAO userDAO) {
        String email = Utils.getOptionalConfig("ADMIN_EMAIL");
        String password = Utils.getOptionalConfig("ADMIN_PASSWORD");

        if (email == null || password == null) {
            log.warn("Environment variables ADMIN_EMAIL / ADMIN_PASSWORD not configured - skipping admin creation");
            return;
        }
        if (userDAO.checkIfEmailExist(email)) {
            log.info("Admin {} already exists - skipping creation", email);
            return;
        }

        String name = Utils.getOptionalConfig("ADMIN_NAME");
        String phone = Utils.getOptionalConfig("ADMIN_PHONE");

        Admin admin = Admin.builder()
                .name(name != null ? name : "Admin")
                .email(email)
                .phone(phone != null ? phone : "00000000")
                .hashedPassword(PasswordUtil.hashPassword(password))
                .userRole(UserRole.ADMIN)
                .build();

        userDAO.create(admin);
        log.info("Created admin {}", email);
    }
}