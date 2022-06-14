package com.Bsep.dto;

import com.Bsep.validator.BlackList;
import com.Bsep.validator.FieldMatch;
import com.Bsep.validator.ValidPassword;

import javax.validation.constraints.NotBlank;

@FieldMatch(first = "newPassword", second = "confirmPassword", message = "The password fields must match")
public class ChangePasswordDTO {

    @NotBlank(message = "Old password is mandatory")
    private String oldPassword;

    @NotBlank(message = "New password is mandatory")
    @ValidPassword
    @BlackList
    private String newPassword;

    @NotBlank(message = "Confirm password is mandatory")
    private String confirmPassword;

    public ChangePasswordDTO() {
    }

    public ChangePasswordDTO(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
