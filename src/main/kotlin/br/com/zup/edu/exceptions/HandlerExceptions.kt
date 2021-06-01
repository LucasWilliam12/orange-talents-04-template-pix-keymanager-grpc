package br.com.zup.edu.exceptions

import io.grpc.Status
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
                    observer.onError(Status.ALREADY_EXISTS.withDescription(e.message).asRuntimeException())
                }
                is IllegalArgumentException -> {
                    observer.onError(Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException())
                }
                is ValidationException -> {
                    observer.onError(Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException())
                }
                else -> {
                    println("Olá")
                    e.printStackTrace()
                    observer.onError(Status.UNKNOWN.withDescription(e.message).asException())
                }

            }
        }
    }

}