package br.com.zup.edu.dto

import br.com.zup.edu.RegisterPixKeyRequest.AccountType
import br.com.zup.edu.RegisterPixKeyRequest.Type
import br.com.zup.edu.models.Account
import br.com.zup.edu.models.KeyModel
import br.com.zup.edu.models.enums.AccountTypeEnum
import br.com.zup.edu.models.enums.KeyTypeEnum
import br.com.zup.edu.utils.validations.ValidPixKeyByKeyType
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.Validated
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Validated
@Introspected
@ValidPixKeyByKeyType
class KeyRequest (@field:Size(max = 77, message = "O valor da chave tem que ser menor que 77")
                  val keyValue: String?,
                  @field:NotBlank(message = "O id do cliente não pode ser vazio")
                  val idClient: String?,
                  @field:NotBlank(message = "O tipo da conta não pode ser vazio")
                  val account: AccountTypeEnum,
                  @field:NotBlank(message = "O tipo da chave não pode ser vazio")
                  val keyType: KeyTypeEnum) {

    fun toModel(@Valid associatedAccount: Account): KeyModel{
        return KeyModel(
            idClient = UUID.fromString(this.idClient),
            keyType = this.keyType,
            keyValue = if(this.keyType.name == Type.RANDOM.name) UUID.randomUUID().toString() else this.keyValue!!,
            accountType = account,
            account = associatedAccount
        )
    }
}