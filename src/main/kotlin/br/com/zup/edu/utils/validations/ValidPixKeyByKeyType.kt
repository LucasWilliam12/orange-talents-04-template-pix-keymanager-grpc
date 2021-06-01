package br.com.zup.edu.utils.validations

import br.com.zup.edu.dto.KeyRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.Constraint
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidPixKeyByKeyTypeValidator::class])
annotation class ValidPixKeyByKeyType(
    val message: String = "Chave pix inválida",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)

@Singleton
class ValidPixKeyByKeyTypeValidator: ConstraintValidator<ValidPixKeyByKeyType, KeyRequest>{
    val logger = LoggerFactory.getLogger(ValidPixKeyByKeyTypeValidator::class.java)
    override fun isValid(
        value: KeyRequest?,
        annotationMetadata: AnnotationValue<ValidPixKeyByKeyType>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value?.keyType == null){
            return false
        }

        logger.info("key type: ${value.keyType}")
        logger.info("${value.keyValue.isNullOrBlank()}")

        logger.info("key value: ${value.keyValue}")
        logger.info("retorno: ${value.keyType.isValid(value.keyValue)}")
        return value.keyType.isValid(value.keyValue)
    }

}