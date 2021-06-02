package br.com.zup.edu.endpoints

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.RegisterPixKeyRequest
import br.com.zup.edu.RegisterPixKeyResponse
import br.com.zup.edu.clients.AccountResponse
import br.com.zup.edu.clients.InstituicaoResponse
import br.com.zup.edu.clients.ItauClient
import br.com.zup.edu.clients.TitularResponse
import br.com.zup.edu.models.enums.AccountTypeEnum
import br.com.zup.edu.utils.Util
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Happy Path de cada tipo de chave
 * dados inválidos para cada tipo de chave
 */


@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RegisterKeyPixEndpointTest{

    @Inject
    lateinit var itauClient: ItauClient

    @Inject
    lateinit var grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub

    companion object{
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

    // Teste de quando o tipo da chave for RANDOM
    @Test
    fun `deve cadastrar uma nova chave pix quando o tipo da chave for random`(){

        val response: RegisterPixKeyResponse = grpcClient.register(
            RegisterPixKeyRequest.newBuilder()
                .setIdClient(client_id.toString())
                .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
                .setKeyType(RegisterPixKeyRequest.Type.RANDOM)
                .build())

        with(response){
            assertEquals(client_id.toString(), clientId)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave pix quando o tipo da chave for RANDOM e o valor da chave for passada`(){

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(RegisterPixKeyRequest.newBuilder()
                .setIdClient(client_id.toString())
                .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
                .setKeyValue(UUID.randomUUID().toString())
                .setKeyType(RegisterPixKeyRequest.Type.RANDOM)
                .build())
        }
        with(error){
            assertEquals("INVALID_ARGUMENT: register.request: Chave pix inválida", message)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    // Teste de quando o tipo da chave for PHONE
    @Test
    fun `deve cadastrar uma nova chave pix quando o tipo da chave for PHONE`(){

        val response = grpcClient.register(RegisterPixKeyRequest.newBuilder()
            .setIdClient(client_id.toString())
            .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
            .setKeyValue("+1212121212")
            .setKeyType(RegisterPixKeyRequest.Type.PHONE)
            .build())

        with(response){
            assertEquals(client_id.toString(), clientId)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave pix quando o tipo da chave for PHONE e o valor da chave for invalida`(){

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(RegisterPixKeyRequest.newBuilder()
                .setIdClient(client_id.toString())
                .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
                .setKeyValue("123")
                .setKeyType(RegisterPixKeyRequest.Type.PHONE)
                .build())
        }
        with(error){
            assertEquals("INVALID_ARGUMENT: register.request: Chave pix inválida", message)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave pix quando o tipo da chave for PHONE e o valor da chave for nula`(){

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(RegisterPixKeyRequest.newBuilder()
                .setIdClient(client_id.toString())
                .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
                .setKeyType(RegisterPixKeyRequest.Type.PHONE)
                .build())
        }
        with(error){
            assertEquals("INVALID_ARGUMENT: register.request: Chave pix inválida", message)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    // Teste de quando o tipo da chave for EMAIL
    @Test
    fun `deve cadastrar uma nova chave pix quando o tipo da chave for email`(){

        val response = grpcClient.register(RegisterPixKeyRequest.newBuilder()
            .setIdClient(client_id.toString())
            .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
            .setKeyValue("lucas@email.com")
            .setKeyType(RegisterPixKeyRequest.Type.EMAIL)
            .build())

        with(response){
            assertEquals(client_id.toString(), clientId)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave pix quando o tipo da chave for email e o valor da chave for invalida`(){

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(RegisterPixKeyRequest.newBuilder()
                .setIdClient(client_id.toString())
                .setKeyValue("lucas")
                .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
                .setKeyType(RegisterPixKeyRequest.Type.EMAIL)
                .build())
        }
        with(error){
            assertEquals("INVALID_ARGUMENT: register.request: Chave pix inválida", message)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave pix quando o tipo da chave for email e o valor da chave for nula`(){

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(RegisterPixKeyRequest.newBuilder()
                .setIdClient(client_id.toString())
                .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
                .setKeyType(RegisterPixKeyRequest.Type.EMAIL)
                .build())
        }
        with(error){
            assertEquals("INVALID_ARGUMENT: register.request: Chave pix inválida", message)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    // Teste de quando o tipo da chave for EMAIL
    @Test
    fun `deve cadastrar uma nova chave pix quando o tipo da chave for CPF`(){

        val response = grpcClient.register(RegisterPixKeyRequest.newBuilder()
            .setIdClient(client_id.toString())
            .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
            .setKeyValue("23998987029")
            .setKeyType(RegisterPixKeyRequest.Type.CPF)
            .build())

        with(response){
            assertEquals(client_id.toString(), clientId)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave pix quando o tipo da chave for CPF e o valor da chave for invalida`(){

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(RegisterPixKeyRequest.newBuilder()
                .setIdClient(client_id.toString())
                .setKeyValue("cpf")
                .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
                .setKeyType(RegisterPixKeyRequest.Type.CPF)
                .build())
        }
        with(error){
            assertEquals("INVALID_ARGUMENT: register.request: Chave pix inválida", message)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave pix quando o tipo da chave for CPF e o valor da chave for nula`(){
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(RegisterPixKeyRequest.newBuilder()
                .setIdClient(client_id.toString())
                .setAccount(RegisterPixKeyRequest.AccountType.CONTA_CORRENTE)
                .setKeyType(RegisterPixKeyRequest.Type.CPF)
                .build())
        }
        with(error){
            assertEquals("INVALID_ARGUMENT: register.request: Chave pix inválida", message)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }


    @MockBean(ItauClient::class)
    fun enderecoMock(): ItauClient {
        return Mockito.mock(ItauClient::class.java)
    }


    @Factory
    internal class Client{

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub{
            return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
        }

    }

}