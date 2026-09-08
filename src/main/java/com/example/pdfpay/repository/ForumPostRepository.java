package com.example.pdfpay.repository;
import com.example.pdfpay.entity.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ForumPostRepository extends JpaRepository<ForumPost,Long>{ List<ForumPost> findTop100ByHiddenFalseOrderByCreatedAtDesc(); long countByAuthorIdAndHiddenFalse(Long authorId); }
