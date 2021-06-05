package br.com.zup.edu.services

import br.com.zup.edu.DeletePixKeyResponse
import br.com.zup.edu.clients.AccountResponse
import br.com.zup.edu.clients.ItauClient
import br.com.zup.edu.dto.DeleteKeyRequest
import br.com.zup.edu.dto.KeyRequest
import br.com.zup.edu.exceptions.PixIdNotFound
import br.com.zup.edu.exceptions.PixKeyAlreadyExistsException
import br.com.zup.edu.models.KeyModel
import br.com.zup.edu.repositories.KeyRepository
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RegisterNewKeyPixService (@Inject val repository: KeyRepository, @Inject val itauClient: ItauClient) {

    @Transactional
    fun register(@Valid request: KeyRequest): KeyModel{

        val logger = LoggerFactory.getLogger(RegisterNewKeyPixService::class.java)

        val account = clientExists(request.account.name, request.idClient!!)

        val newKey = request.toModel(account.toModel())

        // Verificando se já tem uma chave com o mesmo valor
        if(repository.existsByKeyValue(newKey.keyValue)){
            logger.error("chave já cadasrada")
            throw PixKeyAlreadyExistsException("A chave pix informada já existe")
        }

        // Tentando salvar o objeto
        repository.save(newKey)

        return newKey
    }

    @Transactional
    fun delete(@Valid request: DeleteKeyRequest): DeletePixKeyResponse{

        val pixId: UUID =  UUID.fromString(request.pixId)
        val clientId: UUID =  UUID.fromString(request.clientId)

        // Verificando se o cliente realmente existe
        val response = itauClient.findUserById(request.clientId)
        if(response.body() == null){
            throw IllegalArgumentException("Cliente informado não foi encontrado")
        }

        repository.findByPixIdAndIdClient(pixId, clientId).orElseThrow{ PixIdNotFound("Nada foi encontrado") }

        repository.deleteByPixId(pixId)

        return DeletePixKeyResponse.newBuilder().setClientId(clientId.toString()).setDeleted(true).setPixId(pixId.toString()).build()
    }

    fun clientExists(tipo: String, id: String): AccountResponse{
        // Verificando se o cliente realmente existe
        try {
            val response = itauClient.findUserByAccountTypeAndId(tipo, id)
            return response.body()!!
        }catch(e: Exception){
            throw IllegalArgumentException("Cliente informado não foi encontrado")
        }
    }
}
