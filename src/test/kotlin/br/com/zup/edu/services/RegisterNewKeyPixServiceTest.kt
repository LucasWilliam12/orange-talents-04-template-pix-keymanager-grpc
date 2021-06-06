package br.com.zup.edu.services

import br.com.zup.edu.clients.ItauClient
import br.com.zup.edu.dto.DeleteKeyRequest
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
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
        val key = KeyModel(
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
        )
    }

    @BeforeEach
    fun setup(){
        Mockito.`when`(itauClient
            .findUserByAccountTypeAndId(tipo = AccountTypeEnum.CONTA_CORRENTE.name,
                id = client_id.toString())).thenReturn(
            HttpResponse.ok(Util.createAccountResponse())
        )
        Mockito.`when`(itauClient
            .findUserById(client_id.toString())).thenReturn(
            HttpResponse.ok(Util.createAccountBasicResponse())
        )

    }

    @AfterEach
    fun down(){
        repository.deleteAll()
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
        repository.save(key)

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

    @Test
    fun `deve remover uma chave pix quando os dados informados forem validos`(){
        val newKey = repository.save(key)

        val response = RegisterNewKeyPixService(repository, itauClient)
            .delete(DeleteKeyRequest(clientId = client_id.toString(),
                pixId = newKey.pixId.toString()))

        assertTrue(response.deleted)
        assertEquals(key.idClient.toString(), response.clientId.toString())
    }

    @Test
    fun `deve retornar um erro quando o usuario informado nao existir`(){
        val newKey = repository.save(key)
        val error = assertThrows<Exception> {
            RegisterNewKeyPixService(repository, itauClient)
                .delete(DeleteKeyRequest(clientId = "126dfef4-7901-44fb-84e2-a2cefb157890",
                    pixId = newKey.pixId.toString()))
        }

        assertEquals("Cliente informado não foi encontrado", error.message)
    }

    @Test
    fun `deve retornar um erro quando a chave nao for encontrada`(){
        val newKey = repository.save(key)
        val error = assertThrows<Exception> {
            RegisterNewKeyPixService(repository, itauClient)
                .delete(DeleteKeyRequest(clientId = newKey.idClient.toString(),
                    pixId = UUID.randomUUID().toString()))
        }

        assertEquals("Nada foi encontrado", error.message)
    }

    @MockBean(ItauClient::class)
    fun enderecoMock(): ItauClient {
        return Mockito.mock(ItauClient::class.java)
    }

}