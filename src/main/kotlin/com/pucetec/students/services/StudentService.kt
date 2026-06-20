package com.pucetec.students.services

import com.pucetec.students.dto.StudentRequest
import com.pucetec.students.dto.StudentResponse
import com.pucetec.students.entities.Student
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.mappers.toEntity
import com.pucetec.students.mappers.toResponse
import com.pucetec.students.repositories.StudentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StudentService (
    private val studentRepository: StudentRepository
) {
    val logger = LoggerFactory.getLogger(javaClass)

    fun createStudent(request: StudentRequest): StudentResponse {
        logger.info("Creating student ${request.name}")

        if (request.name.isBlank()) {
            throw BlankNameException("Name cannot be blank")
        }

        val studentEntity = request.toEntity()
        val savedStudent = studentRepository.save(studentEntity)
        return savedStudent.toResponse()
    }

    fun getAllStudents(): List<StudentResponse> {
        logger.info("Get all student list")
        val savedStudents = studentRepository.findAll()
        return savedStudents.map { it.toResponse() }
    }

    fun getStudentById(id: Long): StudentResponse {
        logger.info("Getting student by id: $id")
        val student = studentRepository.findById(id).orElseThrow {
            StudentNotFoundException("Student with id $id not found")
        }
        return student.toResponse()
    }

    fun updateStudent(id: Long, request: StudentRequest): StudentResponse {
        logger.info("Updating student by id: $id")
        val existingStudent = studentRepository.findById(id).orElseThrow {
            StudentNotFoundException("Student with id $id not found")
        }

        if (request.name.isBlank()) {
            throw BlankNameException("Name cannot be blank")
        }

        val updatedStudent = Student(
            id = existingStudent.id,
            name = request.name,
            email = request.email
        )

        return studentRepository.save(updatedStudent).toResponse()
    }

    fun deleteStudent(id: Long) {
        logger.info("Deleting student by id: $id")
        val existingStudent = studentRepository.findById(id).orElseThrow {
            StudentNotFoundException("Student with id $id not found")
        }
        studentRepository.delete(existingStudent)
    }
}