package com.familylink.backend.family.dto;

import com.familylink.backend.family.FamilyRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateFamilyRequest {

    @NotBlank(message = "Название семьи обязательно")
    @Size(min = 2, max = 100, message = "Название семьи от 2 до 100 символов")
    private String name;

    @NotNull(message = "Роль обязательна")
    private FamilyRole creatorRole;  // с какой ролью создатель вступает в семью
}