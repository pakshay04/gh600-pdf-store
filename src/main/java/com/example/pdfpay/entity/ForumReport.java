package com.example.pdfpay.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="forum_reports", indexes=@Index(name="idx_report_status", columnList="status"))
public class ForumReport {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false, fetch=FetchType.LAZY) private UserAccount reporter;
 @ManyToOne(fetch=FetchType.LAZY) private ForumPost post;
 @ManyToOne(fetch=FetchType.LAZY) private ForumComment comment;
 @Column(nullable=false,length=500) private String reason;
 @Column(nullable=false,length=20) private String status="OPEN";
 @Column(nullable=false) private Instant createdAt;
 public Long getId(){return id;} public UserAccount getReporter(){return reporter;} public void setReporter(UserAccount v){reporter=v;} public ForumPost getPost(){return post;} public void setPost(ForumPost v){post=v;} public ForumComment getComment(){return comment;} public void setComment(ForumComment v){comment=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
