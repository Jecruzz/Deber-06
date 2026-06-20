package com.pucetec.students.services

import com.pucetec.students.dto.SubjectRequest
import com.pucetec.students.dto.SubjectResponse
import com.pucetec.students.entities.Subject
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.ProfessorNotFound
import com.pucetec.students.exceptions.SubjectNotFound
import com.pucetec.students.mappers.toEntity
import com.pucetec.students.mappers.toResponse
import com.pucetec.students.repositories.ProfessorRepository
import com.pucetec.students.repositories.SubjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SubjectService (
    private val subjectRepository: SubjectRepository,
    private val professorRepository: ProfessorRepository,
) {
    fun createSubject(request: SubjectRequest): SubjectResponse {
        if (request.name.isBlank() || request.code.isBlank()) {
            throw BlankNameException("Subject name and code cannot be blank")
        }
        val professor = professorRepository.findById(request.professorId).orElseThrow {
            ProfessorNotFound("Profesor con id ${request.professorId} no encontrado")
        }

        val subjectEntity = request.toEntity(professor)
        val savedSubject = subjectRepository.save(subjectEntity)
        return savedSubject.toResponse()
    }

    fun getAllSubjects(): List<SubjectResponse> {
        return subjectRepository.findAll().map { it.toResponse() }
    }

    fun getSubjectById(id: Long): SubjectResponse {
        val subject = subjectRepository.findById(id).orElseThrow {
            SubjectNotFound("Subject with id $id not found")
        }
        return subject.toResponse()
    }

    fun updateSubject(id: Long, request: SubjectRequest): SubjectResponse {
        if (request.name.isBlank() || request.code.isBlank()) {
            throw BlankNameException("Subject name and code cannot be blank")
        }
        val existingSubject = subjectRepository.findById(id).orElseThrow {
            SubjectNotFound("Subject with id $id not found")
        }
        val professor = professorRepository.findById(request.professorId).orElseThrow {
            ProfessorNotFound("Profesor con id ${request.professorId} no encontrado")
        }

        val updatedSubject = Subject(
            id = existingSubject.id,
            name = request.name,
            code = request.code,
            professor = professor
        )

        return subjectRepository.save(updatedSubject).toResponse()
    }

    fun deleteSubject(id: Long) {
        val existingSubject = subjectRepository.findById(id).orElseThrow {
            SubjectNotFound("Subject with id $id not found")
        }
        subjectRepository.delete(existingSubject)
    }
}