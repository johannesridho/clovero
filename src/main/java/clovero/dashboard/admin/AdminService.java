package clovero.dashboard.admin;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class AdminService {

    AdminRepository adminRepository;
    PasswordEncoder passwordEncoder;

    public Admin changePassword(Admin admin, String newPassword) {
        admin.setPassword(passwordEncoder.encode(newPassword));

        return adminRepository.save(admin);
    }

    public Optional<Admin> find(final String username) {
        return adminRepository.findByUsername(username);
    }

    public boolean isPasswordMatch(String savedPassword, String currentPassword) {
        return passwordEncoder.matches(currentPassword, savedPassword);
    }
}
