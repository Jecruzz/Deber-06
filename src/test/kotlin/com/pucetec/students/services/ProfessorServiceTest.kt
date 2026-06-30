package com.pucetec.students.services

import com.pucetec.students.dto.ProfessorRequest
import com.pucetec.students.entities.Professor
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.ProfessorNotFound
import com.pucetec.students.repositories.ProfessorRepository
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
class ProfessorServiceTest {

    @Mock
    private lateinit var professorRepository: ProfessorRepository

    @InjectMocks
    private lateinit var professorService: ProfessorService

    @Test
    fun `createProfessor should throw BlankNameException when name is blank`() {
        val request = ProfessorRequest(name = "", email = "test@test.com")
        assertThrows(BlankNameException::class.java) {
            professorService.createProfessor(request)
        }
    }

    @Test
    fun `createProfessor should return ProfessorResponse when name is valid`() {
        val request = ProfessorRequest(name = "Dr. Garcia", email = "garcia@test.com")
        val savedProfessor = Professor(id = 1L, name = "Dr. Garcia", email = "garcia@test.com")

        `when`(professorRepository.save(any())).thenReturn(savedProfessor)

        val result = professorService.createProfessor(request)

        assertEquals(1L, result.id)
        assertEquals("Dr. Garcia", result.name)
        assertEquals("garcia@test.com", result.email)
    }

    @Test
    fun `createProfessor should return ProfessorResponse when email is null`() {
        val request = ProfessorRequest(name = "Dr. Garcia", email = null)
        val savedProfessor = Professor(id = 1L, name = "Dr. Garcia", email = null)

        `when`(professorRepository.save(any())).thenReturn(savedProfessor)

        val result = professorService.createProfessor(request)

        assertEquals(1L, result.id)
        assertEquals("Dr. Garcia", result.name)
        assertEquals(null, result.email)
    }

    @Test
    fun `getAllProfessors should return list of professor responses`() {
        val professors = listOf(
            Professor(id = 1L, name = "Dr. Garcia", email = "garcia@test.com"),
            Professor(id = 2L, name = "Dr. Lopez", email = "lopez@test.com")
        )
        `when`(professorRepository.findAll()).thenReturn(professors)

        val result = professorService.getAllProfessors()

        assertEquals(2, result.size)
        assertEquals("Dr. Garcia", result[0].name)
    }

    @Test
    fun `getAllProfessors should return empty list when no professors`() {
        `when`(professorRepository.findAll()).thenReturn(emptyList())
        val result = professorService.getAllProfessors()
        assertEquals(0, result.size)
    }

    @Test
    fun `getProfessorById should return professor when exists`() {
        val professor = Professor(id = 1L, name = "Dr. Garcia", email = "garcia@test.com")
        `when`(professorRepository.findById(1L)).thenReturn(Optional.of(professor))

        val result = professorService.getProfessorById(1L)

        assertEquals(1L, result.id)
        assertEquals("Dr. Garcia", result.name)
    }

    @Test
    fun `getProfessorById should throw ProfessorNotFound when not exists`() {
        `when`(professorRepository.findById(99L)).thenReturn(Optional.empty())
        assertThrows(ProfessorNotFound::class.java) {
            professorService.getProfessorById(99L)
        }
    }

    @Test
    fun `updateProfessor should throw BlankNameException when name is blank`() {
        val request = ProfessorRequest(name = "", email = "test@test.com")
        assertThrows(BlankNameException::class.java) {
            professorService.updateProfessor(1L, request)
        }
    }

    @Test
    fun `updateProfessor should throw ProfessorNotFound when professor not found`() {
        val request = ProfessorRequest(name = "Dr. Garcia Updated", email = "garcia@test.com")
        `when`(professorRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(ProfessorNotFound::class.java) {
            professorService.updateProfessor(99L, request)
        }
    }

    @Test
    fun `updateProfessor should update and return professor when valid`() {
        val existingProfessor = Professor(id = 1L, name = "Dr. Garcia", email = "garcia@test.com")
        `when`(professorRepository.findById(1L)).thenReturn(Optional.of(existingProfessor))

        val request = ProfessorRequest(name = "Dr. Garcia Updated", email = "garcia.updated@test.com")
        val updatedProfessor = Professor(id = 1L, name = "Dr. Garcia Updated", email = "garcia.updated@test.com")

        `when`(professorRepository.save(any())).thenReturn(updatedProfessor)

        val result = professorService.updateProfessor(1L, request)

        assertEquals(1L, result.id)
        assertEquals("Dr. Garcia Updated", result.name)
        assertEquals("garcia.updated@test.com", result.email)
    }

    @Test
    fun `deleteProfessor should throw ProfessorNotFound when professor not found`() {
        `when`(professorRepository.findById(99L)).thenReturn(Optional.empty())
        assertThrows(ProfessorNotFound::class.java) {
            professorService.deleteProfessor(99L)
        }
    }

    @Test
    fun `deleteProfessor should delete professor when exists`() {
        val professor = Professor(id = 1L, name = "Dr. Garcia", email = "garcia@test.com")
        `when`(professorRepository.findById(1L)).thenReturn(Optional.of(professor))

        professorService.deleteProfessor(1L)

        verify(professorRepository).delete(professor)
    }
}