package org.erp.giahungquan_be.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "The old password is required")
    private String oldPassword;

    @NotBlank(message = "The new password is required")
    @Pattern(regexp = "^[A-Z](?=.*[!@#$%^&*()_+])[A-Za-z0-9!@#$%^&*()_+]{7,}$", 
             message = "New password must start with an uppercase letter, contain at least one special character, and be at least 8 characters long.")
    private String newPassword;
}
