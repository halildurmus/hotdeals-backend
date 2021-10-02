package com.halildurmus.hotdeals.comment;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

public interface CommentService {

  List<CommentDTO> getCommentsByDealId(ObjectId dealId, Pageable pageable);

}
