package com.pucetec.students.services

import com.pucetec.students.dto.StudentRequest
import com.pucetec.students.entities.Student
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.repositories.StudentRepository
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
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class StudentServiceTest {

    @Mock
    private lateinit var studentRepository: StudentRepository

    @InjectMocks
    private lateinit var studentService: StudentService

    @Test
    fun `createStudent should throw BlankNameException when name is blank`() {
        val request = StudentRequest(name = "", email = "test@test.com")
        assertThrows(BlankNameException::class.java) {
            studentService.createStudent(request)
        }
    }

    @Test
    fun `createStudent should return valid StudentResponse when name is not blank`() {
        val request = StudentRequest(name = "Ana", email = "ana@test.com")
        val savedStudent = Student(id = 1L, name = "Ana", email = "ana@test.com")

        `when`(studentRepository.save(any())).thenReturn(savedStudent)

        val response = studentService.createStudent(request)

        assertEquals(1L, response.id)
        assertEquals("Ana", response.name)
        assertEquals("ana@test.com", response.email)
    }

    @Test
    fun `createStudent should return valid StudentResponse with empty email when email is null`() {
        val request = StudentRequest(name = "test", email = null)
        val savedStudent = Student(id = 1L, name = "test", email = null)

        `when`(studentRepository.save(any())).thenReturn(savedStudent)

        val response = studentService.createStudent(request)

        assertEquals(1L, response.id)
        assertEquals("test", response.name)
        assertEquals(null, response.email)
    }

    @Test
    fun `getAllStudents should return list of student responses`() {
        val students = listOf(
            Student(id = 1L, name = "Ana", email = "ana@test.com"),
            Student(id = 2L, name = "Bob", email = "bob@test.com")
        )
        `when`(studentRepository.findAll()).thenReturn(students)

        val result = studentService.getAllStudents()

        assertEquals(2, result.size)
        assertEquals("Ana", result[0].name)
        assertEquals("Bob", result[1].name)
    }

    @Test
    fun `getAllStudents should return empty list when no students`() {
        `when`(studentRepository.findAll()).thenReturn(emptyList())
        val result = studentService.getAllStudents()
        assertEquals(0, result.size)
    }

    @Test
    fun `getStudentById should return student when exists`() {
        val student = Student(id = 1L, name = "Ana", email = "ana@test.com")
        `when`(studentRepository.findById(1L)).thenReturn(Optional.of(student))

        val result = studentService.getStudentById(1L)

        assertEquals(1L, result.id)
        assertEquals("Ana", result.name)
    }

    @Test
    fun `getStudentById should throw StudentNotFoundException when not exists`() {
        `when`(studentRepository.findById(99L)).thenReturn(Optional.empty())
        assertThrows(StudentNotFoundException::class.java) {
            studentService.getStudentById(99L)
        }
    }

    @Test
    fun `updateStudent should throw StudentNotFoundException when student not found`() {
        val request = StudentRequest(name = "Ana Updated", email = "ana@test.com")
        `when`(studentRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            studentService.updateStudent(99L, request)
        }
    }

    @Test
    fun `updateStudent should throw BlankNameException when name is blank`() {
        val existingStudent = Student(id = 1L, name = "Ana", email = "ana@test.com")
        `when`(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent))

        val request = StudentRequest(name = "", email = "ana@test.com")

        assertThrows(BlankNameException::class.java) {
            studentService.updateStudent(1L, request)
        }
    }

    @Test
    fun `updateStudent should update and return student when valid`() {
        val existingStudent = Student(id = 1L, name = "Ana", email = "ana@test.com")
        `when`(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent))

        val request = StudentRequest(name = "Ana Updated", email = "ana.updated@test.com")
        val updatedStudent = Student(id = 1L, name = "Ana Updated", email = "ana.updated@test.com")
        `when`(studentRepository.save(any())).thenReturn(updatedStudent)

        val result = studentService.updateStudent(1L, request)

        assertEquals(1L, result.id)
        assertEquals("Ana Updated", result.name)
        assertEquals("ana.updated@test.com", result.email)
    }

    @Test
    fun `deleteStudent should throw StudentNotFoundException when student not found`() {
        `when`(studentRepository.findById(99L)).thenReturn(Optional.empty())
        assertThrows(StudentNotFoundException::class.java) {
            studentService.deleteStudent(99L)
        }
    }

    @Test
    fun `deleteStudent should delete student when exists`() {
        val student = Student(id = 1L, name = "Ana", email = "ana@test.com")
        `when`(studentRepository.findById(1L)).thenReturn(Optional.of(student))

        studentService.deleteStudent(1L)

        verify(studentRepository).delete(student)
    }
}