package com.halildurmus.hotdeals.mapstruct;

import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.comment.CommentGetDTO;
import com.halildurmus.hotdeals.comment.CommentPostDTO;
import com.halildurmus.hotdeals.post.Post;
import com.halildurmus.hotdeals.post.PostDTO;
import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.user.UserGetDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  Comment commentPostDtoToComment(CommentPostDTO commentPostDTO);

  CommentGetDTO commentToCommentGetDto(Comment comment);

  Post postDtoToPost(PostDTO postDTO);

  PostDTO postToPostDto(Post post);

  User userGetDtoToUser(UserGetDTO userGetDTO);

  UserGetDTO userToUserGetDto(User user);

}