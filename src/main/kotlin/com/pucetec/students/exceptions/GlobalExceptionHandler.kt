package com.pucetec.students.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler (BlankNameException::class)
    fun handleBlankNameException(e: BlankNameException): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(

            message = e.message ?: "Nombre en blanco - ERROR ",
            source = "StudentService"
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response)


    }
    @ExceptionHandler (StudentNotFoundException::class)
    fun handleStudentNotFoundException(e: StudentNotFoundException): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(
            message = e.message ?: "Student not found",
            source = "StudentService"
        )

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response)
    }
    @ExceptionHandler(SubjectNotFound::class)
    open fun handleSubjectNotFound(
        e: SubjectNotFound
    ): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(
            message = e.message ?: "Materia no encontrada - ERROR",
            source = "SubjectService"
        )
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response)
    }
    @ExceptionHandler(ProfessorNotFound::class)
    open fun handleProfessorNotFound(
        e: ProfessorNotFound
    ): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(
            message = e.message ?: "Profesor no encontrada - ERROR",
            source = "ProfessorService"
        )
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response)
    }

    @ExceptionHandler(EnrollmentNotFound::class)
    open fun handleEnrollmentNotFound(
        e: EnrollmentNotFound
    ): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(
            message = e.message ?: "Enrollment no encontrado - ERROR",
            source = "EnrollmentService"
        )
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response)
    }
}

data class ExceptionResponse (
    val message: String,
    val source: String,
)