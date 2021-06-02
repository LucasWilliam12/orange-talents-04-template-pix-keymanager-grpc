package br.com.zup.edu.services

import br.com.zup.edu.clients.ItauClient
import br.com.zup.edu.dto.KeyRequest
import br.com.zup.edu.models.Account
import br.com.zup.edu.models.KeyModel
import br.com.zup.edu.models.enums.AccountTypeEnum
import br.com.zup.edu.models.enums.KeyTypeEnum
import br.com.zup.edu.repositories.KeyRepository
import br.com.zup.edu.utils.Util
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RegisterNewKeyPixServiceTest{

    @Inject
    lateinit var repository: KeyRepository
    @Inject
    lateinit var itauClient: ItauClient

    companion object {
        val client_id = UUID.randomUUID()
    }

    @BeforeEach
    fun setup(){
        Mockito.`when`(itauClient
            .findUserByAccountTypeAndId(tipo = AccountTypeEnum.CONTA_CORRENTE.name,
                id = client_id.toString())).thenReturn(
            HttpResponse.ok(Util.createAccountResponse())
        )
    }

    @Test
    fun `deve retornar uma nova chave`(){
        val keyValue = UUID.randomUUID().toString()
        val response: KeyModel = RegisterNewKeyPixService(itauClient = itauClient, repository = repository).register(KeyRequest(
            keyValue = keyValue,
            idClient = client_id.toString(),
            account = AccountTypeEnum.CONTA_CORRENTE,
            keyType = KeyTypeEnum.PHONE
        ))
        println(response)
        assertEquals(keyValue.toString(), response.keyValue.toString())
        assertEquals(client_id.toString(), response.idClient.toString())
        assertEquals(AccountTypeEnum.CONTA_CORRENTE, response.accountType)
        assertNotNull(response.pixId)
    }

    @Test
    fun `nao deve retornar uma nova chave quando o id do cliente nao for informado`(){
        val error = assertThrows<Exception> {
            RegisterNewKeyPixService(itauClient = itauClient, repository = repository).register(KeyRequest(
                keyValue = UUID.randomUUID().toString(),
                idClient = "",
                account = AccountTypeEnum.CONTA_CORRENTE,
                keyType = KeyTypeEnum.PHONE
            ))
        }

        assertEquals("Cliente informado não foi encontrado", error.message)
    }

    @Test
    fun `nao deve retornar uma nova chave quando ja houver uma outra chave igual cadastrada`(){
        repository.save(KeyModel(
            keyType = KeyTypeEnum.EMAIL,
            keyValue = "lucas@email.com",
            idClient = client_id,
            accountType = AccountTypeEnum.CONTA_CORRENTE,
            account = Account(
                instituicao = "Itau",
                nomeDoTitular = "Lucas William",
                cpfDoTitular = "23998987029",
                agencia = "1234",
                numeroDaConta = "12345"
            )
        ))

        val error = assertThrows<Exception> {
            RegisterNewKeyPixService(itauClient = itauClient, repository = repository).register(KeyRequest(
                keyValue = "lucas@email.com",
                idClient = client_id.toString(),
                account = AccountTypeEnum.CONTA_CORRENTE,
                keyType = KeyTypeEnum.EMAIL,
            ))
        }

        assertEquals("A chave pix informada já existe", error.message)
    }

    @MockBean(ItauClient::class)
    fun enderecoMock(): ItauClient {
        return Mockito.mock(ItauClient::class.java)
    }

}