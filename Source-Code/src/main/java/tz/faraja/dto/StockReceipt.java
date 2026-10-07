package tz.faraja.dto;
import jakarta.validation.constraints.*;
public record StockReceipt(@Min(1) @Max(1000000) int quantity, @NotBlank @Size(max=120) String reference)  {
}
