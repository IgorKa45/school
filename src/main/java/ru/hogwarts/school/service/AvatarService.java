package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;


import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class AvatarService {
    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar findAvatar(Long studentId) {
        logger.debug("Was invoked method for find avatar by student id = {}", studentId);
        return avatarRepository.findByStudentId(studentId).orElse(null);
    }

    public Avatar saveAvatar(Avatar avatar) {
        logger.debug("Saving avatar for student id = {}", avatar.getStudent().getId());
        return avatarRepository.save(avatar);
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload avatar for student id = {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with id = {}", studentId);
                    return new RuntimeException("Student not found");
                });
        // Генерируем путь для сохранения файла
        String filePath = "avatars/" + studentId + "_" + file.getOriginalFilename();
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);
        // Создаем папку avatars, если её нет
        File directory = new File("avatars/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Avatar avatar = new Avatar();
        avatar.setStudent(student);
        avatar.setFilePath(filePath);
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
    }

    public List<Avatar> getAllAvatars(Integer pageNumber, Integer pageSize) {
        logger.debug("Was invoked method for get all avatars");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return avatarRepository.findAllAvatars(pageRequest);
    }
}
