package com.example.pdfpay.repository;
import com.example.pdfpay.entity.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface ForumVoteRepository extends JpaRepository<ForumVote,Long>{ boolean existsByUserIdAndPostId(Long userId,Long postId); long countByPostId(Long postId); void deleteByUserIdAndPostId(Long userId,Long postId); }
