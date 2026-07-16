package com.familylink.backend.family.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import com.familylink.backend.family.FamilyRole;

@Data
public class JoinFamilyRequest {

    @NotBlank(message = "Invite-код обязателен")
    @Pattern(regexp = "^FAM-[A-Z2-9]{6}$", message = "Некорректный формат invite-кода")
    private String inviteCode;

    @NotNull(message = "Роль обязательна")
    private FamilyRole role;
}