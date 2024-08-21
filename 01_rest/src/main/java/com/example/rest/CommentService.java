package com.example.rest;

import com.example.rest.dto.CommentDto;
import com.example.rest.entity.Article;
import com.example.rest.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.mbeans.SparseUserDatabaseMBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.plaf.PanelUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    //CREATE
    public CommentDto create(Long articleId, CommentDto dto) {
        Optional<Article> articleOptional =
                articleRepository.findById(articleId);
        if (articleOptional.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Comment newcomment = new Comment();
        newcomment.setContent(dto.getContent());
        newcomment.setWriter(dto.getWriter());
        newcomment.setArticle(articleOptional.get());

        return CommentDto.fromEntity(commentRepository.save(newcomment));
    }

    // READ ALL
    public List<CommentDto> readAll(Long articleId) {
        Optional<Article> optionalArticle =
                articleRepository.findById(articleId);
        if (optionalArticle.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        // Comment의 리스트니까
        List<Comment> comments = commentRepository
                .findAllByArticle(optionalArticle.get());
        // CommentDto의 리스트로
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment: comments) {
            commentDtoList.add(CommentDto.fromEntity(comment));
        }
        return commentDtoList;
    }
    //UPDATE
    public CommentDto update(
            Long articleId,
            Long commentId,
            CommentDto dto
    ) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        Comment target = commentRepository.findById(commentId)
//        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Comment target = optionalComment.get();
        // 만약 댓글이 URL 상의 게시글의 댓글인지 확인하고 싶다면
        if (!articleId.equals(target.getArticle().getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        target.setContent(dto.getContent());
        target.setWriter(dto.getWriter());
        return CommentDto.fromEntity(commentRepository.save(target));

    }
    //DELETE
    public void delete(
            Long articleId,
            Long commentId
    ) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Comment target = optionalComment.get();
        if (!articleId.equals(target.getArticle().getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        commentRepository.delete(target);


    }


}
