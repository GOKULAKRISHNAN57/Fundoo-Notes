package com.fundoonotes.repository;

import com.fundoonotes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserId(Long userId);

    List<Note> findByUserIdAndArchivedFalseAndTrashedFalse(Long userId);

    List<Note> findByUserIdAndPinnedTrueAndTrashedFalse(Long userId);

    List<Note> findByUserIdAndArchivedTrueAndTrashedFalse(Long userId);

    List<Note> findByUserIdAndTrashedTrue(Long userId);

    List<Note> findByUserIdAndTitleContainingIgnoreCaseAndTrashedFalse(Long userId, String keyword);
}
