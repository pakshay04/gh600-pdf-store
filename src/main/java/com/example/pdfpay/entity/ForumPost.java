package com.example.pdfpay.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="forum_posts", indexes=@Index(name="idx_post_created", columnList="createdAt"))
public class ForumPost {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) private UserAccount author;
    @Column(nullable=false, length=140) private String title;
    @Column(nullable=false, length=5000) private String body;
    @Column(nullable=false) private Instant createdAt;
    @Column(nullable=false) private boolean hidden=false;
    @Column(length=300) private String tags;
    @ManyToOne(fetch=FetchType.LAZY) private ForumComment acceptedComment;
    public Long getId(){return id;} public String getTags(){return tags;} public void setTags(String v){tags=v;} public ForumComment getAcceptedComment(){return acceptedComment;} public void setAcceptedComment(ForumComment v){acceptedComment=v;} public UserAccount getAuthor(){return author;} public void setAuthor(UserAccount v){author=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getBody(){return body;} public void setBody(String v){body=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} public boolean isHidden(){return hidden;} public void setHidden(boolean v){hidden=v;}
}
