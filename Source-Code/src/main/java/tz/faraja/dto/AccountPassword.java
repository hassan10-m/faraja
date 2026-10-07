package tz.faraja.dto;
import jakarta.validation.constraints.*;
public record AccountPassword(@NotBlank String currentPassword, @NotBlank @Size(min=12,max=72) String newPassword)  {
}
