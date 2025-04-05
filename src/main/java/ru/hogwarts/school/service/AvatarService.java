package ru.hogwarts.school.service;

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
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId).orElse(null);
    }

    public Avatar saveAvatar(Avatar avatar) {
        return avatarRepository.save(avatar);
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

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
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return avatarRepository.findAllAvatars(pageRequest);
    }
}
