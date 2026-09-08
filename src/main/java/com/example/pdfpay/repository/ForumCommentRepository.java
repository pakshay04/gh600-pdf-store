package com.example.pdfpay.repository;
import com.example.pdfpay.entity.ForumComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ForumCommentRepository extends JpaRepository<ForumComment,Long>{ List<ForumComment> findByPostIdAndHiddenFalseOrderByCreatedAtAsc(Long postId); long countByAuthorIdAndHiddenFalse(Long authorId); }
