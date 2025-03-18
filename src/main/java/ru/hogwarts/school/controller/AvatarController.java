package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/avatars")
public class AvatarController {
    private final AvatarService avatarService;

    @Autowired
    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<Avatar> getAvatar(@PathVariable Long studentId) {
        Avatar avatar = avatarService.findAvatar(studentId);
        return avatar != null ? ResponseEntity.ok(avatar) : ResponseEntity.notFound().build();
    }

    //Загрузка аватара
    @PostMapping(value = "/{studentId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long studentId, @RequestParam MultipartFile file) throws IOException {
        avatarService.uploadAvatar(studentId, file);
        return ResponseEntity.ok("Avatar uploaded successfully.");
    }

    @GetMapping("/{studentId}/from-db")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long studentId) {
        Avatar avatar = avatarService.findAvatar(studentId);
        if (avatar == null || avatar.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(avatar.getMediaType()))
                .body(avatar.getData());
    }

    @GetMapping("/{studentId}/from-file")
    public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable Long studentId) throws IOException {
        Avatar avatar = avatarService.findAvatar(studentId);
        if (avatar == null || avatar.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(avatar.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileContent = Files.readAllBytes(file.toPath());
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(avatar.getMediaType()))
                .body(fileContent);
    }
}
