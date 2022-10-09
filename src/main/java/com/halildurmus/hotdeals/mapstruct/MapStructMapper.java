package com.halildurmus.hotdeals.mapstruct;

import com.halildurmus.hotdeals.category.Category;
import com.halildurmus.hotdeals.category.dto.CategoryGetDTO;
import com.halildurmus.hotdeals.category.dto.CategoryPostDTO;
import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.comment.dto.CommentGetDTO;
import com.halildurmus.hotdeals.comment.dto.CommentPostDTO;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.deal.dto.DealGetDTO;
import com.halildurmus.hotdeals.deal.dto.DealPostDTO;
import com.halildurmus.hotdeals.report.comment.CommentReport;
import com.halildurmus.hotdeals.report.comment.dto.CommentReportPostDTO;
import com.halildurmus.hotdeals.report.deal.DealReport;
import com.halildurmus.hotdeals.report.deal.dto.DealReportPostDTO;
import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.report.user.dto.UserReportPostDTO;
import com.halildurmus.hotdeals.store.Store;
import com.halildurmus.hotdeals.store.dto.StoreGetDTO;
import com.halildurmus.hotdeals.store.dto.StorePostDTO;
import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.user.dto.UserBasicDTO;
import com.halildurmus.hotdeals.user.dto.UserExtendedDTO;
import com.halildurmus.hotdeals.user.dto.UserPostDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  Category categoryPostDTOToCategory(CategoryPostDTO categoryPostDTO);

  CategoryGetDTO categoryToCategoryGetDTO(Category category);

  CategoryPostDTO categoryToCategoryPostDTO(Category category);

  Comment commentPostDTOToComment(CommentPostDTO commentPostDTO);

  CommentGetDTO commentToCommentGetDTO(Comment comment);

  CommentPostDTO commentToCommentPostDTO(Comment comment);

  CommentReport commentReportPostDTOToCommentReport(CommentReportPostDTO commentReportPostDTO);

  CommentReportPostDTO commentReportToCommentReportPostDTO(CommentReport commentReport);

  Deal dealPostDTOToDeal(DealPostDTO dealPostDTO);

  DealGetDTO dealToDealGetDTO(Deal deal);

  DealPostDTO dealToDealPostDTO(Deal deal);

  DealReport dealReportPostDTOToDealReport(DealReportPostDTO dealReportPostDTO);

  DealReportPostDTO dealReportToDealReportPostDTO(DealReport dealReport);

  Store storePostDTOToStore(StorePostDTO storePostDTO);

  StoreGetDTO storeToStoreGetDTO(Store store);

  StorePostDTO storeToStorePostDTO(Store store);

  User userPostDTOToUser(UserPostDTO userPostDTO);

  UserBasicDTO userToUserBasicDTO(User user);

  UserExtendedDTO userToUserExtendedDTO(User user);

  UserPostDTO userToUserPostDTO(User user);

  UserReport userReportPostDTOToUserReport(UserReportPostDTO userReportPostDTO);
}
