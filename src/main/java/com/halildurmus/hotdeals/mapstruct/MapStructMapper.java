package com.halildurmus.hotdeals.mapstruct;

import com.halildurmus.hotdeals.category.Category;
import com.halildurmus.hotdeals.category.DTO.CategoryGetDTO;
import com.halildurmus.hotdeals.category.DTO.CategoryPostDTO;
import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.comment.DTO.CommentGetDTO;
import com.halildurmus.hotdeals.comment.DTO.CommentPostDTO;
import com.halildurmus.hotdeals.report.deal.DTO.DealReportPostDTO;
import com.halildurmus.hotdeals.report.deal.DealReport;
import com.halildurmus.hotdeals.report.user.DTO.UserReportPostDTO;
import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.store.DTO.StoreGetDTO;
import com.halildurmus.hotdeals.store.DTO.StorePostDTO;
import com.halildurmus.hotdeals.store.Store;
import com.halildurmus.hotdeals.user.DTO.UserGetDTO;
import com.halildurmus.hotdeals.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  Category categoryPostDTOCategory(CategoryPostDTO categoryPostDTO);

  CategoryGetDTO categoryToCategoryGetDTO(Category category);

  Comment commentPostDTOToComment(CommentPostDTO commentPostDTO);

  CommentGetDTO commentToCommentGetDTO(Comment comment);

  DealReport dealReportPostDTOToDealReport(DealReportPostDTO dealReportPostDTO);

  Store storePostDTOStore(StorePostDTO storePostDTO);

  StoreGetDTO storeToStoreGetDTO(Store store);

  User userGetDTOToUser(UserGetDTO userGetDTO);

  UserGetDTO userToUserGetDTO(User user);

  UserReport userReportPostDTOToUserReport(UserReportPostDTO userReportPostDTO);

}