package com.halildurmus.hotdeals.mapstruct;

import com.halildurmus.hotdeals.category.Category;
import com.halildurmus.hotdeals.category.DTO.CategoryGetDTO;
import com.halildurmus.hotdeals.category.DTO.CategoryPostDTO;
import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.comment.DTO.CommentGetDTO;
import com.halildurmus.hotdeals.comment.DTO.CommentPostDTO;
import com.halildurmus.hotdeals.deal.DTO.DealGetDTO;
import com.halildurmus.hotdeals.deal.DTO.DealPostDTO;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.report.deal.DTO.DealReportPostDTO;
import com.halildurmus.hotdeals.report.deal.DealReport;
import com.halildurmus.hotdeals.report.user.DTO.UserReportPostDTO;
import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.store.DTO.StoreGetDTO;
import com.halildurmus.hotdeals.store.DTO.StorePostDTO;
import com.halildurmus.hotdeals.store.Store;
import com.halildurmus.hotdeals.user.DTO.UserBasicDTO;
import com.halildurmus.hotdeals.user.DTO.UserExtendedDTO;
import com.halildurmus.hotdeals.user.DTO.UserPostDTO;
import com.halildurmus.hotdeals.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  Category categoryPostDTOToCategory(CategoryPostDTO categoryPostDTO);

  CategoryGetDTO categoryToCategoryGetDTO(Category category);

  CategoryPostDTO categoryToCategoryPostDTO(Category category);

  Comment commentPostDTOToComment(CommentPostDTO commentPostDTO);

  CommentGetDTO commentToCommentGetDTO(Comment comment);

  CommentPostDTO commentToCommentPostDTO(Comment comment);

  Deal dealPostDTOToDeal(DealPostDTO dealPostDTO);

  DealGetDTO dealToDealGetDTO(Deal deal);

  DealPostDTO dealToDealPostDTO(Deal deal);

  DealReport dealReportPostDTOToDealReport(DealReportPostDTO dealReportPostDTO);

  DealReportPostDTO dealReportToDealReportPostDTO(DealReport dealReport);

  Store storePostDTOToStore(StorePostDTO storePostDTO);

  StoreGetDTO storeToStoreGetDTO(Store store);

  StorePostDTO storeToStorePostDTO(Store store);

  User userBasicDTOToUser(UserBasicDTO userBasicDTO);

  User userPostDTOToUser(UserPostDTO userPostDTO);

  UserBasicDTO userToUserBasicDTO(User user);

  UserExtendedDTO userToUserExtendedDTO(User user);

  UserPostDTO userToUserPostDTO(User user);

  UserReport userReportPostDTOToUserReport(UserReportPostDTO userReportPostDTO);

}