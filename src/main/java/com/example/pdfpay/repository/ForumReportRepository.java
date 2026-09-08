package com.example.pdfpay.repository;
import com.example.pdfpay.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ForumReportRepository extends JpaRepository<ForumReport,Long>{ List<ForumReport> findTop200ByStatusOrderByCreatedAtDesc(String status); }
