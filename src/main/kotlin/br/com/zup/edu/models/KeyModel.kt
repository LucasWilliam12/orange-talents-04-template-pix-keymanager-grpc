package br.com.zup.edu.models

import br.com.zup.edu.RegisterPixKeyRequest
import br.com.zup.edu.models.enums.KeyTypeEnum
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.Validated
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Introspected
data class KeyModel(@field:Column(nullable = true, unique = true)
                    @field:Size(max = 77, message = "O valor da chave tem que ser menor que 77")
                    val keyValue: String,
                    @field:NotNull(message = "O id do cliente não pode ser vazio")
                    @field:Column(nullable = false)
                    val idClient: UUID,
                    @field:NotNull(message = "O tipo da conta não pode ser vazio")
                    @field:Column(nullable = false)
                    @Enumerated
                    val accountType: RegisterPixKeyRequest.AccountType,
                    @field:NotNull(message = "O tipo da chave não pode ser vazio")
                    @Enumerated
                    val keyType: KeyTypeEnum,
                    @Embedded
                    @NotNull(message = "A conta associada não pode ser vazia")
                    @field:Column(nullable = false)
                    val account: Account) {

    @Id
    var pixId: UUID = UUID.randomUUID()
    val createdAt: LocalDateTime = LocalDateTime.now()
}