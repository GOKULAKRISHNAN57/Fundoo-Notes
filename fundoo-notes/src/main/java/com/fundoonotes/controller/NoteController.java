package com.fundoonotes.controller;

import com.fundoonotes.dto.request.NoteRequestDto;
import com.fundoonotes.dto.response.NoteResponseDto;
import com.fundoonotes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(
            @Valid @RequestBody NoteRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.createNote(requestDto, token));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getAllNotes(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.getAllNotes(token));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponseDto> updateNote(
            @PathVariable Long noteId,
            @Valid @RequestBody NoteRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.updateNote(noteId, requestDto, token));
    }


    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long noteId,
            @RequestHeader("Authorization") String token) {
        noteService.deleteNote(noteId, token);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{noteId}/pin")
    public ResponseEntity<NoteResponseDto> pinNote(
            @PathVariable Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.pinNote(noteId, token));
    }


    @PatchMapping("/{noteId}/archive")
    public ResponseEntity<NoteResponseDto> archiveNote(
            @PathVariable Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.archiveNote(noteId, token));
    }

    @PatchMapping("/{noteId}/trash")
    public ResponseEntity<NoteResponseDto> trashNote(
            @PathVariable Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.trashNote(noteId, token));
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoteResponseDto>> searchNotes(
            @RequestParam String keyword,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.searchNotes(keyword, token));
    }
}
