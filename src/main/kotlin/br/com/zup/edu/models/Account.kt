package br.com.zup.edu.models

import io.micronaut.core.annotation.Introspected
import org.hibernate.validator.constraints.br.CPF
import javax.persistence.Embeddable

@Introspected
@Embeddable
data class Account(val instituicao: String,
                    val nomeDoTitular: String,
                    @CPF(message = "O cpf do titular é inválido")
                    val cpfDoTitular: String,
                    val agencia: String,
                    val numeroDaConta: String) {

}
