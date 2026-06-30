package com.pucetec.students.services

import com.pucetec.students.dto.SubjectRequest
import com.pucetec.students.entities.Professor
import com.pucetec.students.entities.Subject
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.ProfessorNotFound
import com.pucetec.students.exceptions.SubjectNotFound
import com.pucetec.students.repositories.ProfessorRepository
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
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class SubjectServiceTest {

    @Mock
    private lateinit var subjectRepository: SubjectRepository

    @Mock
    private lateinit var professorRepository: ProfessorRepository

    @InjectMocks
    private lateinit var subjectService: SubjectService

    private val professor = Professor(id = 1L, name = "Dr. Garcia", email = "garcia@test.com")

    @Test
    fun `createSubject should throw BlankNameException when name is blank`() {
        val request = SubjectRequest(name = "", code = "AE-101", professorId = 1L)
        assertThrows(BlankNameException::class.java) {
            subjectService.createSubject(request)
        }
    }

    @Test
    fun `createSubject should throw BlankNameException when code is blank`() {
        val request = SubjectRequest(name = "Arquitectura", code = "", professorId = 1L)
        assertThrows(BlankNameException::class.java) {
            subjectService.createSubject(request)
        }
    }

    @Test
    fun `createSubject should throw ProfessorNotFound when professor not found`() {
        val request = SubjectRequest(name = "Arquitectura", code = "AE-101", professorId = 99L)
        `when`(professorRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(ProfessorNotFound::class.java) {
            subjectService.createSubject(request)
        }
    }

    @Test
    fun `createSubject should return SubjectResponse when valid`() {
        val request = SubjectRequest(name = "Arquitectura", code = "AE-101", professorId = 1L)
        `when`(professorRepository.findById(1L)).thenReturn(Optional.of(professor))

        val savedSubject = Subject(id = 1L, name = "Arquitectura", code = "AE-101", professor = professor)
        `when`(subjectRepository.save(any())).thenReturn(savedSubject)

        val result = subjectService.createSubject(request)

        assertEquals(1L, result.id)
        assertEquals("Arquitectura", result.name)
        assertEquals("AE-101", result.code)
    }

    @Test
    fun `getAllSubjects should return list of subject responses`() {
        val subjects = listOf(
            Subject(id = 1L, name = "Arquitectura", code = "AE-101", professor = professor),
            Subject(id = 2L, name = "Matematicas", code = "MT-101", professor = professor)
        )
        `when`(subjectRepository.findAll()).thenReturn(subjects)

        val result = subjectService.getAllSubjects()

        assertEquals(2, result.size)
        assertEquals("Arquitectura", result[0].name)
    }

    @Test
    fun `getAllSubjects should return empty list when no subjects`() {
        `when`(subjectRepository.findAll()).thenReturn(emptyList())
        val result = subjectService.getAllSubjects()
        assertEquals(0, result.size)
    }

    @Test
    fun `getSubjectById should return subject when exists`() {
        val subject = Subject(id = 1L, name = "Arquitectura", code = "AE-101", professor = professor)
        `when`(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))

        val result = subjectService.getSubjectById(1L)

        assertEquals(1L, result.id)
        assertEquals("Arquitectura", result.name)
    }

    @Test
    fun `getSubjectById should throw SubjectNotFound when not exists`() {
        `when`(subjectRepository.findById(99L)).thenReturn(Optional.empty())
        assertThrows(SubjectNotFound::class.java) {
            subjectService.getSubjectById(99L)
        }
    }

    @Test
    fun `updateSubject should throw BlankNameException when name is blank`() {
        val request = SubjectRequest(name = "", code = "AE-101", professorId = 1L)
        assertThrows(BlankNameException::class.java) {
            subjectService.updateSubject(1L, request)
        }
    }

    @Test
    fun `updateSubject should throw BlankNameException when code is blank`() {
        val request = SubjectRequest(name = "Arquitectura", code = "", professorId = 1L)
        assertThrows(BlankNameException::class.java) {
            subjectService.updateSubject(1L, request)
        }
    }

    @Test
    fun `updateSubject should throw SubjectNotFound when subject not found`() {
        val request = SubjectRequest(name = "Arquitectura", code = "AE-101", professorId = 1L)
        `when`(subjectRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(SubjectNotFound::class.java) {
            subjectService.updateSubject(99L, request)
        }
    }

    @Test
    fun `updateSubject should throw ProfessorNotFound when professor not found`() {
        val existingSubject = Subject(id = 1L, name = "Arquitectura", code = "AE-101", professor = professor)
        `when`(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject))

        val request = SubjectRequest(name = "Arquitectura Updated", code = "AE-102", professorId = 99L)
        `when`(professorRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(ProfessorNotFound::class.java) {
            subjectService.updateSubject(1L, request)
        }
    }

    @Test
    fun `updateSubject should update and return subject when valid`() {
        val existingSubject = Subject(id = 1L, name = "Arquitectura", code = "AE-101", professor = professor)
        `when`(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject))

        val newProfessor = Professor(id = 2L, name = "Dr. Lopez", email = "lopez@test.com")
        val request = SubjectRequest(name = "Arquitectura Updated", code = "AE-102", professorId = 2L)
        `when`(professorRepository.findById(2L)).thenReturn(Optional.of(newProfessor))

        val updatedSubject = Subject(id = 1L, name = "Arquitectura Updated", code = "AE-102", professor = newProfessor)
        `when`(subjectRepository.save(any())).thenReturn(updatedSubject)

        val result = subjectService.updateSubject(1L, request)

        assertEquals(1L, result.id)
        assertEquals("Arquitectura Updated", result.name)
    }

    @Test
    fun `deleteSubject should throw SubjectNotFound when subject not found`() {
        `when`(subjectRepository.findById(99L)).thenReturn(Optional.empty())
        assertThrows(SubjectNotFound::class.java) {
            subjectService.deleteSubject(99L)
        }
    }

    @Test
    fun `deleteSubject should delete subject when exists`() {
        val subject = Subject(id = 1L, name = "Arquitectura", code = "AE-101", professor = professor)
        `when`(subjectRepository.findById(1L)).thenReturn(Optional.of(subject))

        subjectService.deleteSubject(1L)

        verify(subjectRepository).delete(subject)
    }
}