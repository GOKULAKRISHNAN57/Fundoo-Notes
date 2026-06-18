package com.fundoonotes.service.impl;

import com.fundoonotes.dto.request.NoteRequestDto;
import com.fundoonotes.dto.response.NoteResponseDto;
import com.fundoonotes.entity.Note;
import com.fundoonotes.entity.User;
import com.fundoonotes.exception.NoteNotFoundException;
import com.fundoonotes.exception.UserNotFoundException;
import com.fundoonotes.repository.NoteRepository;
import com.fundoonotes.repository.UserRepository;
import com.fundoonotes.service.NoteService;
import com.fundoonotes.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;

    public NoteServiceImpl(NoteRepository noteRepository,
                           UserRepository userRepository,
                           TokenUtil tokenUtil) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public NoteResponseDto createNote(NoteRequestDto requestDto, String token) {
        User user = getUserFromToken(token);
        log.info("Creating note for user id: {}", user.getId());

        Note note = new Note();
        note.setTitle(requestDto.getTitle());
        note.setDescription(requestDto.getDescription());
        note.setUser(user);

        Note savedNote = noteRepository.save(note);
        log.debug("Note created with id: {}", savedNote.getId());

        return mapToNoteResponse(savedNote);
    }

    @Override
    public List<NoteResponseDto> getAllNotes(String token) {
        User user = getUserFromToken(token);
        log.info("Fetching active notes for user id: {}", user.getId());

        return noteRepository.findByUserIdAndArchivedFalseAndTrashedFalse(user.getId())
                .stream()
                .map(this::mapToNoteResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NoteResponseDto updateNote(Long noteId, NoteRequestDto requestDto, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());

        note.setTitle(requestDto.getTitle());
        note.setDescription(requestDto.getDescription());

        return mapToNoteResponse(noteRepository.save(note));
    }

    @Override
    public void deleteNote(Long noteId, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        noteRepository.delete(note);
        log.info("Note deleted with id: {}", noteId);
    }

    @Override
    public NoteResponseDto pinNote(Long noteId, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        note.setPinned(!note.isPinned()); // toggle pin
        return mapToNoteResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponseDto archiveNote(Long noteId, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        note.setArchived(!note.isArchived()); // toggle archive
        return mapToNoteResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponseDto trashNote(Long noteId, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        note.setTrashed(!note.isTrashed()); // toggle trash
        return mapToNoteResponse(noteRepository.save(note));
    }

    @Override
    public List<NoteResponseDto> searchNotes(String keyword, String token) {
        User user = getUserFromToken(token);
        log.info("Searching notes for user id: {} with keyword: {}", user.getId(), keyword);

        return noteRepository.findByUserIdAndTitleContainingIgnoreCaseAndTrashedFalse(user.getId(), keyword)
                .stream()
                .map(this::mapToNoteResponse)
                .collect(Collectors.toList());
    }

    // Helper: get User from JWT token
    private User getUserFromToken(String token) {
        Long userId = tokenUtil.getUserIdFromToken(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for the provided token"));
    }

    // Helper: get Note and verify it belongs to user
    private Note getNoteForUser(Long noteId, Long userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + noteId));

        if (!note.getUser().getId().equals(userId)) {
            throw new NoteNotFoundException("Note not found for this user");
        }
        return note;
    }

    // Helper: map Note entity to response DTO
    private NoteResponseDto mapToNoteResponse(Note note) {
        NoteResponseDto dto = new NoteResponseDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setDescription(note.getDescription());
        dto.setPinned(note.isPinned());
        dto.setArchived(note.isArchived());
        dto.setTrashed(note.isTrashed());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }
}
