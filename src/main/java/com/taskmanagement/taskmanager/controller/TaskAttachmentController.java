package com.taskmanagement.taskmanager.controller;

import com.taskmanagement.taskmanager.dto.response.TaskAttachmentResponse;
import com.taskmanagement.taskmanager.entity.TaskAttachment;
import com.taskmanagement.taskmanager.repository.TaskAttachmentRepository;
import com.taskmanagement.taskmanager.service.TaskAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskAttachmentController {

    private final TaskAttachmentService taskAttachmentService;

    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<List<TaskAttachmentResponse>> uploadAttachments(
            @PathVariable Long taskId,
            @RequestParam("file")MultipartFile[] files
            ){
        return ResponseEntity.ok(taskAttachmentService.uploadFiles(taskId, files));
    }

    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws MalformedURLException {


        TaskAttachment attachment = taskAttachmentService.getById(id);

        Path filePath = Paths.get(
//                "uploads/tasks",
                "D:/upload_files",
                attachment.getTask().getId().toString(),
                attachment.getStoredFilename()
        );

        Resource resource = new FileSystemResource(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getOriginalFilename() + "\"")
                .body(resource);

    }
}
