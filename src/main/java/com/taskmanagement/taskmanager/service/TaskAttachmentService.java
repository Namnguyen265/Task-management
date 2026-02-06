package com.taskmanagement.taskmanager.service;

import com.taskmanagement.taskmanager.dto.response.TaskAttachmentResponse;
import com.taskmanagement.taskmanager.entity.Task;
import com.taskmanagement.taskmanager.entity.TaskAttachment;
import com.taskmanagement.taskmanager.entity.User;
import com.taskmanagement.taskmanager.mapper.TaskAttachmentMapper;
import com.taskmanagement.taskmanager.repository.TaskAttachmentRepository;
import com.taskmanagement.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TaskAttachmentService {

    private final TaskRepository taskRepository;
    private final TaskAttachmentRepository attachmentRepository;
    private final AuthService authService;

    private static final String UPLOAD_DIR = "D:/upload_files/";

    public TaskAttachment getById(Long id){
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task attachment not found with id = " + id));
    }

    public List<TaskAttachmentResponse> uploadFiles(Long taskId, MultipartFile[] files) {

        User currentUser = authService.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        List<TaskAttachment> savedAttachments = new ArrayList<>();

        for (MultipartFile file : files) {

            validateFile(file);

            String storedFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();

            try {
                Path uploadPath = Paths.get(UPLOAD_DIR + taskId);
                Files.createDirectories(uploadPath);


                Path filePath = uploadPath.resolve(storedFilename);
                file.transferTo(filePath.toFile());
            } catch (IOException e){
                throw new RuntimeException("Upload file thất bại", e);
            }


            TaskAttachment attachment = new TaskAttachment();
            attachment.setTask(task);
            attachment.setOriginalFilename(file.getOriginalFilename());
            attachment.setStoredFilename(storedFilename);
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setUploadedBy(currentUser);
            attachment.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            savedAttachments.add(attachmentRepository.save(attachment));
        }

        return savedAttachments.stream()
                .map(TaskAttachmentMapper::toResponse)
                .toList();
    }

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("File type not allowed: " + file.getContentType());
        }

        // ví dụ giới hạn 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("File size exceeds 10MB");
        }
    }

    public Resource download(Long attachmentId) throws MalformedURLException{

        TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(()-> new RuntimeException("Attachment not found"));

        Path filePath = Paths.get(
//                "uploads/tasks",
                "D:/upload_files",
                attachment.getTask().getId().toString(),
                attachment.getStoredFilename()
        );
        return new UrlResource(filePath.toUri());
    }
}
