package br.com.zup.edu.exceptions

import br.com.zup.edu.DetailsError
import io.grpc.Status
import com.google.rpc.Status as GStatus
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.aop.Around
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Singleton
import javax.validation.ValidationException
import kotlin.annotation.AnnotationTarget.*

@Around
@Retention(AnnotationRetention.RUNTIME)
@Target(FUNCTION, CLASS, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class HandlerException()

@InterceptorBean(HandlerException::class)
@Singleton
class HandlerExceptions: MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any {
        return try{
            context.proceed()
        }catch (e: RuntimeException){
            val observer = context.parameterValues.filterIsInstance<StreamObserver<*>>()
                .firstOrNull() as StreamObserver
            when(e){
                is PixKeyAlreadyExistsException -> {
                    val status = GStatus.newBuilder()
                        .setCode(Status.ALREADY_EXISTS.code.value())
                        .setMessage(e.message)
                        .addDetails(com.google.protobuf.Any.pack(DetailsError.newBuilder().setCode(401).build()))
                        .build()
                    val statusProto = StatusProto.toStatusRuntimeException(status)
                    observer.onError(statusProto)
                }
                is IllegalArgumentException -> {
                    val status = GStatus.newBuilder()
                        .setCode(Status.INVALID_ARGUMENT.code.value())
                        .setMessage(e.message)
                        .addDetails(com.google.protobuf.Any.pack(DetailsError.newBuilder().setCode(401).build()))
                        .build()
                    val statusProto = StatusProto.toStatusRuntimeException(status)
                    observer.onError(statusProto)
                }
                is ValidationException -> {
                    val status = GStatus.newBuilder()
                        .setCode(Status.INVALID_ARGUMENT.code.value())
                        .setMessage(e.message)
                        .addDetails(com.google.protobuf.Any.pack(DetailsError.newBuilder().setCode(401).build()))
                        .build()
                    val statusProto = StatusProto.toStatusRuntimeException(status)
                    observer.onError(statusProto)
                }is PixIdNotFound -> {
                    val status = GStatus.newBuilder()
                        .setCode(Status.NOT_FOUND.code.value())
                        .setMessage(e.message)
                        .addDetails(com.google.protobuf.Any.pack(DetailsError.newBuilder().setCode(404).build()))
                        .build()
                    val statusProto = StatusProto.toStatusRuntimeException(status)
                    observer.onError(statusProto)
                }
                else -> {
                    observer.onError(Status.UNKNOWN.withDescription(e.message).asException())
                }

            }
        }
    }

}