package br.com.zup.edu.endpoints

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.RegisterPixKeyRequest
import br.com.zup.edu.RegisterPixKeyResponse
import br.com.zup.edu.exceptions.HandlerException
import br.com.zup.edu.services.RegisterNewKeyPixService
import br.com.zup.edu.utils.toModel
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@HandlerException
@Singleton
class RegisterKeyPixEndpoint(@Inject val registerKeyPixService: RegisterNewKeyPixService): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {
    override fun register(request: RegisterPixKeyRequest, responseObserver: StreamObserver<RegisterPixKeyResponse>) {

        val logger = LoggerFactory.getLogger(RegisterKeyPixEndpoint::class.java)
        logger.info("Cadastrando uma chave pix:.")
        val newKey = registerKeyPixService.register(request.toModel())
        responseObserver.onNext(RegisterPixKeyResponse.newBuilder()
            .setClientId(newKey.idClient.toString())
            .setPixId(newKey.pixId.toString()).build())
        responseObserver.onCompleted()
        logger.info("Chave pix criada com sucesso.")

    }

}