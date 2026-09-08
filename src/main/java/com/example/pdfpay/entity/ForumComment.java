package com.example.pdfpay.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="forum_comments", indexes=@Index(name="idx_comment_post", columnList="post_id"))
public class ForumComment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) private ForumPost post;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) private UserAccount author;
    @Column(nullable=false, length=3000) private String body;
    @Column(nullable=false) private Instant createdAt;
    @Column(nullable=false) private boolean hidden=false;
    public Long getId(){return id;} public ForumPost getPost(){return post;} public void setPost(ForumPost v){post=v;} public UserAccount getAuthor(){return author;} public void setAuthor(UserAccount v){author=v;}
    public String getBody(){return body;} public void setBody(String v){body=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} public boolean isHidden(){return hidden;} public void setHidden(boolean v){hidden=v;}
}
