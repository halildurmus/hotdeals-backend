package com.halildurmus.hotdeals.comment;

import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.user.UserDTO;
import com.halildurmus.hotdeals.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

  @Autowired
  private CommentRepository repository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public List<CommentDTO> getCommentsByDealId(ObjectId dealId, Pageable pageable) {
    final List<Comment> comments = repository.findByDealIdOrderByCreatedAt(dealId, pageable)
        .getContent();
    final List<String> userIds = comments.stream().distinct().map((c) -> c.getPostedBy().toString())
        .collect(Collectors.toList());
    final List<User> users = userRepository.findAllByIdIn(userIds, null).getContent();
    final List<UserDTO> userDTOs = users.stream().map(
        (u) -> UserDTO.builder().id(u.getId()).uid(u.getUid()).avatar(u.getAvatar())
            .nickname(u.getNickname())
            .createdAt(u.getCreatedAt())
            .build()).collect(
        Collectors.toList());

    return comments.stream().map((c) -> CommentDTO.builder()
        .id(c.getId())
        .dealId(c.getDealId())
        .postedBy(userDTOs.stream()
            .filter(u -> u.getId().equals(c.getPostedBy().toString()))
            .findAny()
            .orElse(null))
        .message(c.getMessage())
        .createdAt(c.getCreatedAt())
        .build()).collect(Collectors.toList());
  }

}
