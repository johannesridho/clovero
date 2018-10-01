package clovero.dashboard.admin;

import lombok.Data;

@Data
public class ChangePasswordForm {
    private String currentPassword;
    private String password;
    private String rePassword;
}
