package com.pucetec.students.services

import com.pucetec.students.dto.EnrollmentRequest
import com.pucetec.students.dto.EnrollmentResponse
import com.pucetec.students.dto.EnrollmentStatusRequest
import com.pucetec.students.entities.Enrollment
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.EnrollmentNotFound
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.exceptions.SubjectNotFound
import com.pucetec.students.mappers.toEntity
import com.pucetec.students.mappers.toResponse
import com.pucetec.students.repositories.EnrollmentRepository
import com.pucetec.students.repositories.StudentRepository
import com.pucetec.students.repositories.SubjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EnrollmentService (
    private val studentRepository: StudentRepository,
    private val subjectRepository: SubjectRepository,
    private val enrollmentRepository: EnrollmentRepository
){
    fun createEnrollment(request: EnrollmentRequest): EnrollmentResponse {
        val student = studentRepository.findById(request.studentId).orElseThrow {
            StudentNotFoundException("Estudiante con id ${request.studentId} not found")
        }
        val subjectEntity = subjectRepository.findById(request.subjectId).orElseThrow {
            SubjectNotFound("Materia con id ${request.subjectId} not found")
        }

        val enrollment = request.toEntity(student, subjectEntity)
        return enrollmentRepository.save(enrollment).toResponse()
    }

    fun getAllEnrollments(): List<EnrollmentResponse> {
        return enrollmentRepository.findAll().map { it.toResponse() }
    }

    fun getEnrollmentById(id: Long): EnrollmentResponse {
        val enrollment = enrollmentRepository.findById(id).orElseThrow {
            EnrollmentNotFound("Enrollment with id $id not found")
        }
        return enrollment.toResponse()
    }

    fun updateEnrollment(id: Long, request: EnrollmentStatusRequest): EnrollmentResponse {
        if (request.status.isBlank()) {
            throw BlankNameException("Enrollment status cannot be blank")
        }
        val existingEnrollment = enrollmentRepository.findById(id).orElseThrow {
            EnrollmentNotFound("Enrollment with id $id not found")
        }

        val updatedEnrollment = Enrollment(
            id = existingEnrollment.id,
            status = request.status,
            createdAt = existingEnrollment.createdAt,
            student = existingEnrollment.student,
            subject = existingEnrollment.subject
        )

        return enrollmentRepository.save(updatedEnrollment).toResponse()
    }

    fun deleteEnrollment(id: Long) {
        val existingEnrollment = enrollmentRepository.findById(id).orElseThrow {
            EnrollmentNotFound("Enrollment with id $id not found")
        }
        enrollmentRepository.delete(existingEnrollment)
    }
}