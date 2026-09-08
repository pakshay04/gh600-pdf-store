package com.example.pdfpay.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="forum_votes", uniqueConstraints=@UniqueConstraint(name="uk_vote_user_post", columnNames={"user_id","post_id"}), indexes=@Index(name="idx_vote_post", columnList="post_id"))
public class ForumVote {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false, fetch=FetchType.LAZY) private UserAccount user;
 @ManyToOne(optional=false, fetch=FetchType.LAZY) private ForumPost post;
 @Column(nullable=false) private Instant createdAt;
 public Long getId(){return id;} public UserAccount getUser(){return user;} public void setUser(UserAccount v){user=v;} public ForumPost getPost(){return post;} public void setPost(ForumPost v){post=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
