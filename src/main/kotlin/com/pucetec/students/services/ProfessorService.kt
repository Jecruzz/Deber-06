package com.pucetec.students.services

import com.pucetec.students.dto.ProfessorRequest
import com.pucetec.students.dto.ProfessorResponse
import com.pucetec.students.entities.Professor
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.ProfessorNotFound
import com.pucetec.students.mappers.toEntity
import com.pucetec.students.mappers.toResponse
import com.pucetec.students.repositories.ProfessorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProfessorService(
    private val professorRepository: ProfessorRepository
) {
    fun createProfessor(request: ProfessorRequest): ProfessorResponse {
        if (request.name.isBlank()) {
            throw BlankNameException("Name cannot be blank")
        }
        val professor = request.toEntity()
        return professorRepository.save(professor).toResponse()
    }

    fun getAllProfessors(): List<ProfessorResponse> {
        return professorRepository.findAll().map { it.toResponse() }
    }

    fun getProfessorById(id: Long): ProfessorResponse {
        val professor = professorRepository.findById(id).orElseThrow {
            ProfessorNotFound("Professor with id $id not found")
        }
        return professor.toResponse()
    }

    fun updateProfessor(id: Long, request: ProfessorRequest): ProfessorResponse {
        if (request.name.isBlank()) {
            throw BlankNameException("Name cannot be blank")
        }
        val existingProfessor = professorRepository.findById(id).orElseThrow {
            ProfessorNotFound("Professor with id $id not found")
        }
        val updatedProfessor = Professor(
            id = existingProfessor.id,
            name = request.name,
            email = request.email,
            subject = existingProfessor.subject
        )
        return professorRepository.save(updatedProfessor).toResponse()
    }

    fun deleteProfessor(id: Long) {
        val professor = professorRepository.findById(id).orElseThrow {
            ProfessorNotFound("Professor with id $id not found")
        }
        professorRepository.delete(professor)
    }
}

