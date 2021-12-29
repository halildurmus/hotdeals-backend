package com.halildurmus.hotdeals.mapstruct;

import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.comment.DTO.CommentGetDTO;
import com.halildurmus.hotdeals.comment.DTO.CommentPostDTO;
import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.user.UserGetDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  Comment commentPostDtoToComment(CommentPostDTO commentPostDTO);

  CommentGetDTO commentToCommentGetDto(Comment comment);

  User userGetDtoToUser(UserGetDTO userGetDTO);

  UserGetDTO userToUserGetDto(User user);

}