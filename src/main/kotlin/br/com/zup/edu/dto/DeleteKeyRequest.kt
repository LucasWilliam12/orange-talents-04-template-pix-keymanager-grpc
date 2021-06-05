package br.com.zup.edu.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
class DeleteKeyRequest(
    @field:NotNull(message = "O clientId não pode ser vazio")
    @field:NotBlank(message = "O clientId não pode ser em branco")
    val clientId: String,
    @field:NotNull(message = "O pixId não pode ser vazio")
    @field:NotBlank(message = "O pixId não pode ser em branco")
    val pixId: String
)