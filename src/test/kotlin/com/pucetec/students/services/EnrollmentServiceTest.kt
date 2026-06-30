package com.pucetec.students.services

import com.pucetec.students.dto.EnrollmentRequest
import com.pucetec.students.dto.EnrollmentStatusRequest
import com.pucetec.students.entities.Enrollment
import com.pucetec.students.entities.Professor
import com.pucetec.students.entities.Student
import com.pucetec.students.entities.Subject
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.EnrollmentNotFound
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.exceptions.SubjectNotFound
import com.pucetec.students.repositories.EnrollmentRepository
import com.pucetec.students.repositories.StudentRepository
import com.pucetec.students.repositories.SubjectRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class EnrollmentServiceTest {

    @Mock
    private lateinit var enrollmentRepository: EnrollmentRepository
    @Mock
    private lateinit var studentRepository: StudentRepository
    @Mock
    private lateinit var subjectRepository: SubjectRepository

    @InjectMocks
    private lateinit var enrollmentService: EnrollmentService

    private val student = Student(id = 1L, name = "Ana Torres", email = "ana@test.com")
    private val professor = Professor(id = 1L, name = "Dr. Garcia", email = "garcia@test.com")
    private val subject = Subject(id = 1L, name = "Arquitectura", code = "AE-101", professor = professor)

    @Test
    fun `createEnrollment should throw StudentNotFoundException when student not found`() {
        val request = EnrollmentRequest(studentId = 99L, subjectId = 1L)
        `when`(studentRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            enrollmentService.createEnrollment(request)
        }
    }

    @Test
    fun `createEnrollment should throw SubjectNotFound when subject not found`() {
        val request = EnrollmentRequest(studentId = 1L, subjectId = 99L)
        `when`(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        `when`(subjectRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SubjectNotFound::class.java) {
            enrollmentService.createEnrollment(request)
        }
    }

    @Test
    fun `createEnrollment should return EnrollmentResponse when valid`() {
        val request = EnrollmentRequest(studentId = 1L, subjectId = 1L)
        `when`(studentRepository.findById(1L)).thenReturn(Optional.of(student))
        `when`(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))

        val savedEnrollment = Enrollment(id = 1L, status = "INSCRITO", createdAt = LocalDateTime.now(), student = student, subject = subject)
        `when`(enrollmentRepository.save(any())).thenReturn(savedEnrollment)

        val result = enrollmentService.createEnrollment(request)

        assertEquals(1L, result.id)
        assertEquals("INSCRITO", result.status)
        assertEquals("Ana Torres", result.student.name)
    }

    @Test
    fun `getAllEnrollments should return list of enrollment responses`() {
        val enrollments = listOf(
            Enrollment(id = 1L, status = "INSCRITO", createdAt = LocalDateTime.now(), student = student, subject = subject)
        )
        `when`(enrollmentRepository.findAll()).thenReturn(enrollments)

        val result = enrollmentService.getAllEnrollments()

        assertEquals(1, result.size)
        assertEquals("INSCRITO", result[0].status)
    }

    @Test
    fun `getEnrollmentById should return enrollment when exists`() {
        val enrollment = Enrollment(id = 1L, status = "INSCRITO", createdAt = LocalDateTime.now(), student = student, subject = subject)
        `when`(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment))

        val result = enrollmentService.getEnrollmentById(1L)

        assertEquals(1L, result.id)
        assertEquals("INSCRITO", result.status)
    }

    @Test
    fun `getEnrollmentById should throw EnrollmentNotFound when not exists`() {
        `when`(enrollmentRepository.findById(99L)).thenReturn(Optional.empty())
        assertThrows(EnrollmentNotFound::class.java) {
            enrollmentService.getEnrollmentById(99L)
        }
    }

    @Test
    fun `updateEnrollment should throw BlankNameException when status is blank`() {
        val request = EnrollmentStatusRequest(status = "")
        assertThrows(BlankNameException::class.java) {
            enrollmentService.updateEnrollment(1L, request)
        }
    }

    @Test
    fun `updateEnrollment should throw EnrollmentNotFound when enrollment not found`() {
        val request = EnrollmentStatusRequest(status = "APROBADO")
        `when`(enrollmentRepository.findById(99L)).thenReturn(Optional.empty())
        assertThrows(EnrollmentNotFound::class.java) {
            enrollmentService.updateEnrollment(99L, request)
        }
    }

    @Test
    fun `updateEnrollment should update and return enrollment when valid`() {
        val existingEnrollment = Enrollment(id = 1L, status = "INSCRITO", createdAt = LocalDateTime.now(), student = student, subject = subject)
        `when`(enrollmentRepository.findById(1L)).thenReturn(Optional.of(existingEnrollment))

        val request = EnrollmentStatusRequest(status = "APROBADO")
        val updatedEnrollment = Enrollment(id = 1L, status = "APROBADO", createdAt = existingEnrollment.createdAt, student = student, subject = subject)
        `when`(enrollmentRepository.save(any())).thenReturn(updatedEnrollment)

        val result = enrollmentService.updateEnrollment(1L, request)

        assertEquals(1L, result.id)
        assertEquals("APROBADO", result.status)
    }

    @Test
    fun `deleteEnrollment should throw EnrollmentNotFound when enrollment not found`() {
        `when`(enrollmentRepository.findById(99L)).thenReturn(Optional.empty())
        assertThrows(EnrollmentNotFound::class.java) {
            enrollmentService.deleteEnrollment(99L)
        }
    }

    @Test
    fun `deleteEnrollment should delete enrollment when exists`() {
        val enrollment = Enrollment(id = 1L, status = "INSCRITO", createdAt = LocalDateTime.now(), student = student, subject = subject)
        `when`(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment))

        enrollmentService.deleteEnrollment(1L)

        verify(enrollmentRepository).delete(enrollment)
    }
}