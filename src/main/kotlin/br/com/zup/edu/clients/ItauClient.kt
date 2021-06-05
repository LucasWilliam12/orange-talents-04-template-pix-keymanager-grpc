package br.com.zup.edu.clients
import br.com.zup.edu.models.Account
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${clients.itau.address}")
interface ItauClient {

    @Get("/api/v1/clientes/{id}/contas")
    fun findUserByAccountTypeAndId(@QueryValue tipo: String, @PathVariable id: String): HttpResponse<AccountResponse>

    @Get("/api/v1/clientes/{id}")
    fun findUserById(@PathVariable id: String): HttpResponse<AccounBasictResponse>
}

data class AccounBasictResponse(
    val id: String,
    val nome: String,
    val cpf: String,
    val instituicao: InstituicaoResponse
)

data class AccountResponse(val tipo: String,
                          val instituicao: InstituicaoResponse,
                          val agencia: String,
                          val numero: String,
                          val titular: TitularResponse){

    fun toModel(): Account {
        return Account(
            instituicao = this.instituicao.nome,
            nomeDoTitular = this.titular.nome,
            cpfDoTitular = this.titular.cpf,
            agencia = this.agencia,
            numeroDaConta = this.numero
        )
    }
}

data class InstituicaoResponse(val nome: String, val ispb: String){
}

data class TitularResponse(val id: String, val nome: String, val cpf: String)